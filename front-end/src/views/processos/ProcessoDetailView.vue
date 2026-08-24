<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import ProcessoSeletivoService from '../../repositories/ProcessoSeletivoService'
import type { ProcessoSeletivo } from '../../types/ProcessoSeletivo'

const route = useRoute()
const router = useRouter()

const processo = ref<ProcessoSeletivo | null>(null)
const loading = ref(false)
const error = ref(false)

/**
 * Verifica se um ID é válido.
 */
function temId(id: unknown): boolean {
  return (
    id !== undefined &&
    id !== null &&
    String(id).trim() !== ''
  )
}

/**
 * Normaliza o objeto recebido da API.
 *
 * IMPORTANTE:
 * O backend está retornando:
 *
 * {
 *   nome: "...",
 *   descricao: "...",
 *   qtdVagas: 1,
 *   status: "CADASTRADO",
 *   idd: 2
 * }
 *
 * Portanto:
 *
 * id = id ?? idd
 *
 * O frontend NÃO cria o ID.
 * Apenas usa o identificador fornecido pelo backend.
 */
function normalizarProcesso(data: any): ProcessoSeletivo {
  if (!data || typeof data !== 'object') {
    throw new Error(
      'A API não retornou os dados do processo seletivo.',
    )
  }

  const id = data.id ?? data.idd

  if (!temId(id)) {
    console.error(
      'Processo recebido sem ID:',
      data,
    )

    throw new Error(
      'O processo seletivo foi encontrado, mas a API não retornou um ID.',
    )
  }

  return {
    ...data,
    id,
  } as ProcessoSeletivo
}

/**
 * Busca o processo seletivo pelo ID da URL.
 *
 * Rota:
 *
 * /processos/:id
 */
async function carregarProcesso() {
  const idParam = Array.isArray(route.params.id)
    ? route.params.id[0]
    : route.params.id

  /*
   * Verifica se existe ID na rota.
   */
  if (!temId(idParam)) {
    console.error(
      'ID do processo seletivo não informado:',
      idParam,
    )

    processo.value = null
    error.value = true

    return
  }

  const id = Number(idParam)

  /*
   * Verifica se o ID é numérico.
   */
  if (!Number.isFinite(id)) {
    console.error(
      'ID do processo seletivo inválido:',
      idParam,
    )

    processo.value = null
    error.value = true

    return
  }

  loading.value = true
  error.value = false
  processo.value = null

  try {
    console.log(
      'Buscando processo seletivo com ID:',
      id,
    )

    const response =
      await ProcessoSeletivoService.getById(id)

    console.log(
      'Resposta completa do Service:',
      response,
    )

    /*
     * O Service pode estar retornando:
     *
     * response.data
     *
     * ou diretamente:
     *
     * response
     */
    const data = response?.data ?? response

    console.log(
      'Dados do processo:',
      data,
    )

    processo.value = normalizarProcesso(data)

    console.log(
      'Processo normalizado:',
      processo.value,
    )
  } catch (err) {
    console.error(
      'Erro ao carregar processo seletivo:',
      err,
    )

    processo.value = null
    error.value = true
  } finally {
    loading.value = false
  }
}

/**
 * Abre a edição do processo atual.
 */
function editar() {
  const id = processo.value?.id

  if (!temId(id)) {
    console.error(
      'Não foi possível editar: processo sem ID.',
    )

    return
  }

  console.log(
    'Editando processo com ID:',
    id,
  )

  void router.push(`/processos/${id}/editar`)
}

/**
 * Volta para a listagem.
 */
function voltar() {
  void router.push('/processos')
}

/**
 * Formata o status.
 */
function formatStatus(status: string) {
  const labels: Record<string, string> = {
    CADASTRADO: 'Cadastrado',
    FINALIZADO: 'Finalizado',
    CANCELADO: 'Cancelado',
  }

  return labels[status] ?? status
}

/**
 * Define a cor do status.
 */
function statusColor(status: string) {
  const colors: Record<string, string> = {
    CADASTRADO: 'blue',
    FINALIZADO: 'green',
    CANCELADO: 'red',
  }

  return colors[status] ?? 'grey'
}

/**
 * Carrega o processo:
 *
 * - ao abrir a página;
 * - quando o ID da rota mudar.
 */
watch(
  () => route.params.id,
  () => {
    void carregarProcesso()
  },
  {
    immediate: true,
  },
)
</script>

<template>
  <v-container>
    <!-- ========================================= -->
    <!-- CABEÇALHO                                -->
    <!-- ========================================= -->

    <div
      class="d-flex align-center justify-space-between mb-6"
    >
      <div>


        <h1 class="text-h4 font-weight-bold">
          {{
            processo?.nome ||
            'Detalhes do Processo'
          }}
        </h1>
      </div>

      <div class="d-flex ga-2">
        <button
          class="processo-button processo-button--outlined"
          type="button"
          @click="voltar"
        >
          Voltar
        </button>

        <button
          v-if="processo"
          class="processo-button"
          color="primary"
          type="button"
          @click="editar"
        >
          <span class="processo-button-icon" aria-hidden="true">✎</span>
          Editar
        </button>
      </div>
    </div>

    <!-- ========================================= -->
    <!-- LOADING                                  -->
    <!-- ========================================= -->

    <v-progress-linear
      v-if="loading"
      indeterminate
      color="primary"
      class="mb-4"
    />

    <!-- ========================================= -->
    <!-- ERRO                                     -->
    <!-- ========================================= -->

    <v-alert
      v-else-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
    >
      Não foi possível carregar as informações deste
      processo seletivo.

      <template #append>
        <button
          class="processo-button processo-button--text"
          type="button"
          @click="carregarProcesso"
        >
          Tentar novamente
        </button>
      </template>
    </v-alert>

    <!-- ========================================= -->
    <!-- PROCESSO                                 -->
    <!-- ========================================= -->

    <template v-else-if="processo">
      <v-card
        variant="outlined"
        class="mb-6"
      >
        <v-card-item>

        </v-card-item>

        <v-divider />

        <v-card-text>
          <v-row>
            <!-- ID -->
            <v-col
              cols="12"
              md="6"
            >
             
            </v-col>

            <!-- VAGAS -->
            <v-col
              cols="12"
              md="6"
            >
              <div
                class="text-subtitle-2 text-grey-darken-1"
              >
                Quantidade de Vagas
              </div>

              <div
                class="text-body-1 font-weight-medium"
              >
                {{ processo.qtdVagas }}
              </div>
            </v-col>

            <!-- STATUS -->
            <v-col
              cols="12"
              md="6"
            >
              <div
                class="text-subtitle-2 text-grey-darken-1"
              >
                Status
              </div>

              <v-chip
                size="small"
                :color="
                  statusColor(processo.status)
                "
                variant="flat"
                class="mt-1"
              >
                {{ formatStatus(processo.status) }}
              </v-chip>
            </v-col>

            <!-- DESCRIÇÃO -->
            <v-col cols="12">
              <div
                class="text-subtitle-2 text-grey-darken-1"
              >
                Descrição
              </div>

              <p class="text-body-1 mt-1">
                {{
                  processo.descricao ||
                  'Nenhuma descrição informada.'
                }}
              </p>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- ========================================= -->
      <!-- BALÕES DO DEV D                         -->
      <!-- ========================================= -->

      <v-card variant="outlined">
        <v-card-title
          class="d-flex align-center justify-space-between pa-4"
        >
          <span class="text-h6 font-weight-bold">
            Balões Informativos
          </span>

          <v-chip
            size="x-small"
            color="secondary"
            variant="outlined"
          >
            Área do Dev D
          </v-chip>
        </v-card-title>

        <v-divider />

        <v-card-text class="pa-4">
          <v-alert
            type="info"
            variant="tonal"
            icon="mdi-information"
          >
            Área reservada para a listagem e criação
            de balões informativos.

            <span class="d-block mt-2">
              Processo ID:
              <code>{{ processo.id }}</code>
            </span>
          </v-alert>
        </v-card-text>
      </v-card>
    </template>

    <!-- ========================================= -->
    <!-- SEM PROCESSO                             -->
    <!-- ========================================= -->

    <v-alert
      v-else
      type="info"
      variant="tonal"
    >
      Nenhum processo seletivo foi carregado.
    </v-alert>
  </v-container>
</template>
<style scoped>
/* =========================================================
   PROCESSO DETAIL VIEW
   ========================================================= */

.processo-detail-view {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 8px 0 32px;
}

:deep(.v-btn) {
  min-height: 40px;
  border-radius: 8px;
  padding-inline: 16px;
  font-weight: 600;
  letter-spacing: normal;
  text-transform: none;
  transition: background-color 0.2s ease, border-color 0.2s ease,
    box-shadow 0.2s ease, transform 0.2s ease;
}

:deep(.v-btn:not(.v-btn--variant-outlined)) {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #ffffff;
  box-shadow: 0 2px 5px rgb(29 78 216 / 18%);
}

:deep(.v-btn:not(.v-btn--variant-outlined):hover) {
  background: linear-gradient(135deg, #1d4ed8, #1e40af);
  box-shadow: 0 5px 12px rgb(29 78 216 / 25%);
  transform: translateY(-1px);
}

:deep(.v-btn--variant-outlined) {
  border-color: #d1d5db;
  color: #374151;
}

:deep(.v-btn--variant-outlined:hover) {
  border-color: #9ca3af;
  background: #f9fafb;
}

:deep(.v-btn:focus-visible) {
  outline: 3px solid rgb(37 99 235 / 30%);
  outline-offset: 2px;
}

/* =========================================================
   CABEÇALHO
   ========================================================= */

.processo-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
  padding: 0 4px;
}

.processo-header-content {
  min-width: 0;
}

.processo-eyebrow {
  margin: 0 0 6px;

  font-size: 12px;
  font-weight: 600;
  line-height: 1.2;

  text-transform: uppercase;
  letter-spacing: 0.08em;

  color: #6b7280;
}

.processo-title {
  margin: 0;

  font-size: 30px;
  font-weight: 700;
  line-height: 1.2;

  color: #111827;

  overflow-wrap: anywhere;
}

.processo-actions {
  display: flex;
  align-items: center;
  gap: 8px;

  flex-shrink: 0;
}

/* =========================================================
   CARD PRINCIPAL
   ========================================================= */

.processo-card {
  width: 100%;

  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgb(15 23 42 / 5%);

  overflow: hidden;
}

/* =========================================================
   CABEÇALHO DO CARD
   ========================================================= */

.processo-card-header {
  padding: 20px 24px;
}

.processo-card-title {
  margin: 0;

  font-size: 20px;
  font-weight: 700;
  line-height: 1.3;

  color: #111827;

  overflow-wrap: anywhere;
}

.processo-card-divider {
  height: 1px;
  width: 100%;

  background: #e5e7eb;
}

/* =========================================================
   GRID DE INFORMAÇÕES
   ========================================================= */

.processo-info-grid {
  display: grid;

  grid-template-columns: repeat(2, minmax(0, 1fr));

  gap: 16px;

  padding: 24px;
}

.processo-info {
  display: flex;
  flex-direction: column;
  gap: 6px;

  min-width: 0;

  padding: 16px;

  background: #f8fafc;

  border: 1px solid #e5e7eb;
  border-radius: 10px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease,
    transform 0.2s ease;
}

.processo-info:hover {
  border-color: #bfdbfe;
  box-shadow: 0 4px 12px rgb(15 23 42 / 6%);
  transform: translateY(-1px);
}

.processo-info--wide {
  grid-column: 1 / -1;
}

.processo-info-label {
  font-size: 12px;
  font-weight: 600;

  text-transform: uppercase;
  letter-spacing: 0.06em;

  color: #6b7280;
}

.processo-info-value {
  margin: 0;

  font-size: 16px;
  font-weight: 600;
  line-height: 1.5;

  color: #111827;

  overflow-wrap: anywhere;
}

.processo-description {
  margin: 0;

  font-size: 15px;
  line-height: 1.6;

  color: #374151;

  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.processo-button{
  border: 0;
  border-radius: 8px;
  padding: 9px 14px;
  font-weight: 600;
  cursor: pointer;
  background: #e5e7eb;
  color: #111827;
}

/* =========================================================
   ID
   ========================================================= */

.processo-id {
  display: inline-flex;
  align-items: center;

  width: fit-content;

  padding: 4px 8px;

  border-radius: 6px;

  background: #e5e7eb;

  color: #374151;

  font-family:
    ui-monospace,
    SFMono-Regular,
    Menlo,
    Monaco,
    Consolas,
    "Liberation Mono",
    monospace;

  font-size: 13px;
}

/* =========================================================
   STATUS
   ========================================================= */

.processo-status {
  display: inline-flex;
  align-items: center;

  width: fit-content;

  min-height: 28px;

  padding: 4px 10px;

  border-radius: 999px;

  font-size: 12px;
  font-weight: 600;
}

.processo-status--cadastrado {
  background: #dbeafe;
  color: #1d4ed8;
}

.processo-status--finalizado {
  background: #dcfce7;
  color: #15803d;
}

.processo-status--cancelado {
  background: #fee2e2;
  color: #b91c1c;
}

/* =========================================================
   BALÕES INFORMATIVOS
   ========================================================= */

.baloes-panel {
  width: 100%;

  background: #ffffff;

  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgb(15 23 42 / 5%);

  overflow: hidden;
}

.baloes-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 16px;

  padding: 20px 24px;
}

.baloes-title {
  margin: 0;

  font-size: 18px;
  font-weight: 700;

  color: #111827;
}

.baloes-divider {
  height: 1px;

  background: #e5e7eb;
}

.baloes-content {
  padding: 24px;
}

/* =========================================================
   ÁREA RESERVADA PARA DEV D
   ========================================================= */

.baloes-placeholder {
  display: grid;

  grid-template-columns: repeat(3, minmax(0, 1fr));

  gap: 16px;
}

.balao-slot {
  display: flex;
  align-items: center;
  justify-content: center;

  min-height: 120px;

  padding: 16px;

  background: #f8fafc;

  border: 1px dashed #cbd5e1;
  border-radius: 10px;

  color: #64748b;

  text-align: center;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.balao-slot:hover {
  background: #f1f5f9;
  border-color: #94a3b8;
}

/* =========================================================
   ESTADOS
   ========================================================= */

.processo-loading {
  width: 100%;
  margin-bottom: 16px;
}

.processo-error {
  width: 100%;
  margin-bottom: 16px;
}

/* =========================================================
   RESPONSIVIDADE
   ========================================================= */

@media (max-width: 768px) {
  .processo-detail-view {
    padding-inline: 12px;
  }

  .processo-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .processo-actions {
    width: 100%;
  }

  .processo-actions > * {
    flex: 1;
  }

  .processo-actions :deep(.v-btn) {
    width: 100%;
  }

  .processo-info-grid {
    grid-template-columns: 1fr;
  }

  .processo-info--wide {
    grid-column: auto;
  }

  .baloes-placeholder {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .processo-detail-view {
    padding-inline: 8px;
  }

  .processo-title {
    font-size: 24px;
  }

  .processo-card-header,
  .processo-info-grid,
  .baloes-header,
  .baloes-content {
    padding: 16px;
  }

  .processo-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .processo-actions > * {
    width: 100%;
  }

  .processo-actions :deep(.v-btn) {
    justify-content: center;
  }
}
  
</style>