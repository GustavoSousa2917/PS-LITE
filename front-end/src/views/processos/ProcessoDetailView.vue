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
        <p
          class="text-caption text-uppercase text-grey-darken-1 mb-1"
        >
          Processo
        </p>

        <h1 class="text-h4 font-weight-bold">
          {{
            processo?.nome ||
            'Detalhes do Processo'
          }}
        </h1>
      </div>

      <div class="d-flex ga-2">
        <v-btn
          variant="outlined"
          type="button"
          @click="voltar"
        >
          Voltar
        </v-btn>

        <v-btn
          v-if="processo"
          color="primary"
          prepend-icon="mdi-pencil"
          type="button"
          @click="editar"
        >
          Editar
        </v-btn>
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
        <v-btn
          variant="text"
          @click="carregarProcesso"
        >
          Tentar novamente
        </v-btn>
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
          <v-card-title
            class="text-h5 font-weight-bold"
          >
            {{ processo.nome }}
          </v-card-title>
        </v-card-item>

        <v-divider />

        <v-card-text>
          <v-row>
            <!-- ID -->
            <v-col
              cols="12"
              md="6"
            >
              <div
                class="text-subtitle-2 text-grey-darken-1"
              >
                ID
              </div>

              <div
                class="text-body-1 font-weight-medium"
              >
                {{ processo.id }}
              </div>
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