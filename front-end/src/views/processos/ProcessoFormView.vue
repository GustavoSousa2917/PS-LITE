<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useForm } from 'vee-validate'
import * as z from 'zod'
import { toTypedSchema } from '@vee-validate/zod'

const route = useRoute()
const router = useRouter()

/**
 * Status aceitos pela API.
 *
 * O "as const" é importante para o TypeScript
 * não transformar os valores em string genérica.
 */
const STATUS_OPTIONS = [
  'CADASTRADO',
  'FINALIZADO',
  'CANCELADO',
] as const

type StatusProcesso = (typeof STATUS_OPTIONS)[number]

interface Processo {
  id?: number
  idd?: number
  nome: string
  descricao?: string | null
  qtdVagas: number
  status: StatusProcesso
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const processosUrl = `${apiBaseUrl}/processo-seletivo`

const processo = ref<Processo | null>(null)
const loading = ref(false)
const saving = ref(false)
const erro = ref('')
const notFound = ref(false)

/**
 * Identifica o modo da tela.
 *
 * /processos/novo          -> create
 * /processos/:id/editar    -> edit
 * /processos/:id?mode=...  -> conforme query
 */
const currentMode = computed<'create' | 'edit' | 'detail'>(() => {
  const mode = route.query.mode

  if (mode === 'edit') {
    return 'edit'
  }

  if (mode === 'detail') {
    return 'detail'
  }

  if (route.params.id) {
    return 'edit'
  }

  return 'create'
})

/**
 * Schema de validação.
 */
const schema = toTypedSchema(
  z.object({
    id: z.number().optional(),

    nome: z
      .string()
      .trim()
      .min(1, 'Nome é obrigatório.'),

    descricao: z
      .string()
      .trim()
      .optional()
      .default(''),

    qtdVagas: z
      .coerce
      .number({
        message: 'Quantidade de vagas é obrigatória.',
      })
      .min(
        0,
        'Quantidade de vagas deve ser maior ou igual a 0.',
      ),

    status: z.enum(STATUS_OPTIONS, {
      message: 'Status inválido.',
    }),
  }),
)

/**
 * Formulário.
 */
const {
  handleSubmit,
  errors,
  setValues,
  defineField,
} = useForm({
  validationSchema: schema,

  initialValues: {
    id: undefined,
    nome: '',
    descricao: '',
    qtdVagas: 0,
    status: 'CADASTRADO' as StatusProcesso,
  },
})

const [nome, nomeProps] = defineField('nome')
const [descricao, descricaoProps] = defineField('descricao')
const [qtdVagas, qtdVagasProps] = defineField('qtdVagas')
const [status, statusProps] = defineField('status')

/**
 * Formata o status para exibição.
 */
function formatStatus(value: string) {
  const labels: Record<string, string> = {
    CADASTRADO: 'Cadastrado',
    FINALIZADO: 'Finalizado',
    CANCELADO: 'Cancelado',
  }

  return labels[value] ?? value
}

/**
 * Retorna o ID da rota.
 *
 * O ID da rota é a fonte principal para edição.
 */
function getRouteId(): number | null {
  const idParam = Array.isArray(route.params.id)
    ? route.params.id[0]
    : route.params.id

  if (
    idParam === undefined ||
    idParam === null ||
    String(idParam).trim() === ''
  ) {
    return null
  }

  const id = Number(idParam)

  if (!Number.isFinite(id)) {
    return null
  }

  return id
}

/**
 * Volta para a listagem.
 */
function redirectToListagem() {
  void router.push('/processos')
}

/**
 * Vai para o detalhe.
 */
function redirectToDetalhe(id: number | string) {
  void router.push(`/processos/${id}`)
}

/**
 * Faz o parse seguro da resposta da API.
 */
async function parseJson(
  response: Response,
): Promise<any> {
  if (response.status === 204) {
    return null
  }

  const text = await response.text()

  if (!text) {
    return null
  }

  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

/**
 * Converte o status recebido da API para o tipo
 * aceito pelo formulário.
 */
function normalizarStatus(
  value: unknown,
): StatusProcesso {
  if (
    value === 'CADASTRADO' ||
    value === 'FINALIZADO' ||
    value === 'CANCELADO'
  ) {
    return value
  }

  return 'CADASTRADO'
}

/**
 * Carrega o processo para edição.
 *
 * IMPORTANTE:
 * O ID usado para buscar o processo vem da URL.
 *
 * Exemplo:
 * /processos/2/editar
 *
 * => GET /processo-seletivo/2
 */
async function loadProcesso() {
  const id = getRouteId()

  if (id === null) {
    return
  }

  loading.value = true
  erro.value = ''
  notFound.value = false

  try {
    const response = await fetch(
      `${processosUrl}/${id}`,
    )

    if (!response.ok) {
      if (response.status === 404) {
        notFound.value = true
        return
      }

      throw new Error(
        `Não foi possível carregar o processo. Status: ${response.status}`,
      )
    }

    const data = await parseJson(response)

    if (!data) {
      throw new Error(
        'A API não retornou os dados do processo.',
      )
    }

    /**
     * A API aparentemente usa "idd".
     *
     * Portanto:
     * - data.id pode não existir
     * - data.idd pode existir
     *
     * Mas para edição NÃO precisamos depender disso,
     * pois o ID já está na URL.
     */
    const processoCarregado: Processo = {
      id:
        data.id !== undefined
          ? Number(data.id)
          : data.idd !== undefined
            ? Number(data.idd)
            : id,

      idd:
        data.idd !== undefined
          ? Number(data.idd)
          : id,

      nome: data.nome ?? '',

      descricao: data.descricao ?? '',

      qtdVagas: Number(
        data.qtdVagas ?? 0,
      ),

      status: normalizarStatus(
        data.status,
      ),
    }

    processo.value = processoCarregado

    /**
     * Preenche o formulário.
     */
    setValues({
      id: id,

      nome: processoCarregado.nome,

      descricao:
        processoCarregado.descricao ?? '',

      qtdVagas:
        processoCarregado.qtdVagas,

      status:
        processoCarregado.status,
    })
  } catch (error) {
    console.error(
      'Erro ao carregar processo seletivo:',
      error,
    )

    erro.value =
      error instanceof Error
        ? error.message
        : 'Erro ao carregar processo.'
  } finally {
    loading.value = false
  }
}

/**
 * Salva o formulário.
 *
 * CREATE:
 * POST /processo-seletivo
 *
 * EDIT:
 * PUT /processo-seletivo/:id
 */
const submitForm = handleSubmit(
  async (values) => {
    saving.value = true
    erro.value = ''

    try {
      const routeId = getRouteId()

      const isEdit =
        currentMode.value === 'edit' &&
        routeId !== null

      /**
       * No EDIT, o ID vem exclusivamente da rota.
       *
       * Não usamos data.id nem values.id para
       * decidir qual registro será atualizado.
       */
      const payload = {
        nome: values.nome.trim(),

        descricao:
          values.descricao?.trim() ?? '',

        qtdVagas:
          Number(values.qtdVagas),

        status:
          values.status,
      }

      let url = processosUrl
      let method: 'POST' | 'PUT' = 'POST'

      if (isEdit) {
        url = `${processosUrl}/${routeId}`
        method = 'PUT'
      }

      console.log('SALVANDO PROCESSO:', {
        method,
        url,
        payload,
      })

      const response = await fetch(url, {
        method,

        headers: {
          'Content-Type': 'application/json',
        },

        body: JSON.stringify(payload),
      })

      const data = await parseJson(response)

      if (!response.ok) {
        const message =
          data?.message ||
          data?.error ||
          `Não foi possível salvar o processo. Status: ${response.status}`

        throw new Error(message)
      }

      /**
       * Se estamos editando, o ID já é conhecido:
       * routeId.
       *
       * Portanto não dependemos do backend devolver
       * "id" na resposta.
       */
      if (isEdit && routeId !== null) {
        console.log(
          'Processo atualizado com ID:',
          routeId,
        )

        redirectToDetalhe(routeId)
        return
      }

      /**
       * CREATE.
       *
       * O backend deve retornar o ID criado.
       *
       * Como sua API mostrou "idd", aceitamos:
       * - id
       * - idd
       * - processoId
       */
      const savedId =
        data?.id ??
        data?.idd ??
        data?.processoId

      if (
        savedId !== undefined &&
        savedId !== null &&
        String(savedId).trim() !== ''
      ) {
        redirectToDetalhe(savedId)
        return
      }

      /**
       * Se o POST não devolveu ID,
       * volta para a listagem.
       */
      redirectToListagem()
    } catch (error) {
      console.error(
        'Erro ao salvar processo:',
        error,
      )

      erro.value =
        error instanceof Error
          ? error.message
          : 'Erro ao salvar o processo.'
    } finally {
      saving.value = false
    }
  },
)

/**
 * Editar a partir da tela de detalhe.
 */
function editarDetalhe() {
  const id = getRouteId()

  if (id === null) {
    console.error(
      'Não foi possível editar: ID inválido.',
    )

    return
  }

  void router.push(
    `/processos/${id}/editar`,
  )
}

const isDetailMode = computed(
  () => currentMode.value === 'detail',
)

/**
 * Carrega quando a tela abre.
 */
onMounted(() => {
  if (
    currentMode.value === 'edit' ||
    currentMode.value === 'detail'
  ) {
    void loadProcesso()
  }
})

/**
 * Recarrega quando o ID da rota mudar.
 */
watch(
  () => route.params.id,
  () => {
    if (
      currentMode.value === 'edit' ||
      currentMode.value === 'detail'
    ) {
      void loadProcesso()
    }
  },
)
</script>

<template>
  <section class="processo-view">

    <!-- ========================= -->
    <!-- DETALHE -->
    <!-- ========================= -->

    <div
      v-if="isDetailMode"
      class="detail-page"
    >
      <header class="page-header">
        <div>
          <p class="eyebrow">
            Processo
          </p>

          <h1>
            {{
              processo?.nome ||
              'Detalhe do processo'
            }}
          </h1>
        </div>

        <button
          class="primary-button"
          type="button"
          @click="editarDetalhe"
        >
          Editar
        </button>
      </header>

      <div
        v-if="loading"
        class="state-box"
      >
        Carregando processo...
      </div>

      <div
        v-else-if="notFound"
        class="state-box error-box"
      >
        Processo não encontrado.
      </div>

      <div
        v-else-if="erro"
        class="state-box error-box"
      >
        {{ erro }}
      </div>

      <div
        v-else-if="processo"
        class="detail-content"
      >
        <div class="card-grid">

          <div class="info-card">
            <span class="label">
              Nome
            </span>

            <strong>
              {{ processo.nome }}
            </strong>
          </div>

          <div class="info-card">
            <span class="label">
              Vagas
            </span>

            <strong>
              {{ processo.qtdVagas }}
            </strong>
          </div>

          <div class="info-card wide">
            <span class="label">
              Status
            </span>

            <strong>
              {{ formatStatus(processo.status) }}
            </strong>
          </div>

          <div
            class="info-card wide description-card"
          >
            <span class="label">
              Descrição
            </span>

            <p>
              {{
                processo.descricao ||
                'Sem descrição informada.'
              }}
            </p>
          </div>

        </div>

        <div class="baloes-panel">
          <h2>
            Balões (D)
          </h2>

          <div class="baloes-placeholder">
            <div class="balao-slot">
              Reservado para balões
            </div>

            <div class="balao-slot">
              Reservado para balões
            </div>

            <div class="balao-slot">
              Reservado para balões
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========================= -->
    <!-- FORMULÁRIO -->
    <!-- ========================= -->

    <div
      v-else
      class="form-page"
    >
      <header class="page-header">
        <div>
          <p class="eyebrow">
            Processo
          </p>

          <h1>
            {{
              currentMode === 'edit'
                ? 'Editar processo'
                : 'Novo processo'
            }}
          </h1>
        </div>
      </header>

      <div
        v-if="loading"
        class="state-box"
      >
        Carregando processo...
      </div>

      <div
        v-else-if="notFound"
        class="state-box error-box"
      >
        Processo não encontrado.
      </div>

      <form
        v-else
        class="processo-form"
        @submit.prevent="submitForm"
      >

        <!-- NOME -->
        <div class="field-group">
          <label for="nome">
            Nome
          </label>

          <input
            id="nome"
            v-model="nome"
            v-bind="nomeProps"
            type="text"
            placeholder="Digite o nome do processo"
          />

          <small
            v-if="errors.nome"
            class="error-message"
          >
            {{ errors.nome }}
          </small>
        </div>

        <!-- DESCRIÇÃO -->
        <div class="field-group">
          <label for="descricao">
            Descrição
          </label>

          <textarea
            id="descricao"
            v-model="descricao"
            v-bind="descricaoProps"
            rows="4"
            placeholder="Descreva o processo"
          ></textarea>

          <small
            v-if="errors.descricao"
            class="error-message"
          >
            {{ errors.descricao }}
          </small>
        </div>

        <!-- VAGAS / STATUS -->
        <div class="field-row">

          <div class="field-group">
            <label for="qtdVagas">
              Quantidade de vagas
            </label>

            <input
              id="qtdVagas"
              v-model.number="qtdVagas"
              v-bind="qtdVagasProps"
              type="number"
              min="0"
            />

            <small
              v-if="errors.qtdVagas"
              class="error-message"
            >
              {{ errors.qtdVagas }}
            </small>
          </div>

          <div class="field-group">
            <label for="status">
              Status
            </label>

            <select
              id="status"
              v-model="status"
              v-bind="statusProps"
            >
              <option
                v-for="option in STATUS_OPTIONS"
                :key="option"
                :value="option"
              >
                {{ formatStatus(option) }}
              </option>
            </select>

            <small
              v-if="errors.status"
              class="error-message"
            >
              {{ errors.status }}
            </small>
          </div>

        </div>

        <!-- ERRO -->
        <div
          v-if="erro"
          class="form-alert error-box"
        >
          {{ erro }}
        </div>

        <!-- AÇÕES -->
        <div class="actions">

          <button
            type="button"
            class="secondary-button"
            @click="redirectToListagem"
          >
            Cancelar
          </button>

          <button
            type="submit"
            class="primary-button"
            :disabled="saving"
          >
            {{
              saving
                ? 'Salvando...'
                : currentMode === 'edit'
                  ? 'Salvar alterações'
                  : 'Criar processo'
            }}
          </button>

        </div>

      </form>
    </div>
  </section>
</template>

<style scoped>
template {
  background-color: white;
}
.processo-view {
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #6b7280;
}

h1,
h2 {
  margin: 0;
  color: #111827;
}

.processo-form {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.field-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

label {
  font-weight: 600;
  color: #374151;
}

input,
textarea,
select {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 12px;
  font: inherit;
  background: #fff;
  color: #111827;
}

input:focus,
textarea:focus,
select:focus {
  outline: 2px solid rgba(37, 99, 235, 0.2);
  border-color: #2563eb;
}

textarea {
  resize: vertical;
}

.error-message,
.error-box {
  color: #b91c1c;
}

.error-box {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 10px 12px;
  margin-top: 12px;
}

.primary-button,
.secondary-button {
  border: 0;
  border-radius: 8px;
  padding: 10px 16px;
  font-weight: 600;
  cursor: pointer;
}

.primary-button {
  background: #2563eb;
  color: #fff;
}

.primary-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.secondary-button {
  background: #e5e7eb;
  color: #111827;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.detail-content {
  display: grid;
  gap: 24px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.info-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-card.wide {
  grid-column: span 2;
}

.description-card p {
  margin: 0;
  color: #374151;
  line-height: 1.5;
}

.label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #6b7280;
}

.baloes-panel {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
}

.baloes-placeholder {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.balao-slot {
  min-height: 120px;
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  text-align: center;
  padding: 12px;
}

.state-box {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  color: #374151;
}

@media (max-width: 640px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .field-row,
  .card-grid,
  .baloes-placeholder {
    grid-template-columns: 1fr;
  }

  .info-card.wide {
    grid-column: span 1;
  }

  .actions {
    flex-direction: column-reverse;
  }

  .primary-button,
  .secondary-button {
    width: 100%;
  }
}
</style>