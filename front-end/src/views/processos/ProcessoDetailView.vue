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
 * Busca o processo seletivo pelo ID presente na rota.
 *
 * Rota:
 * /processos/:id
 */
async function carregarProcesso() {
  const idParam = Array.isArray(route.params.id)
    ? route.params.id[0]
    : route.params.id

  if (
    idParam === undefined ||
    idParam === null ||
    String(idParam).trim() === ''
  ) {
    console.error('ID do processo seletivo inválido:', idParam)
    processo.value = null
    error.value = true
    return
  }

  const id = Number(idParam)

  if (!Number.isFinite(id)) {
    console.error('ID do processo seletivo inválido:', idParam)
    processo.value = null
    error.value = true
    return
  }

  loading.value = true
  error.value = false
  processo.value = null

  try {
    const response = await ProcessoSeletivoService.getById(id)

    processo.value = response.data as ProcessoSeletivo
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
 * Abre a tela de edição do processo atual.
 *
 * /processos/:id/editar
 */
function editar() {
  const id = processo.value?.id

  if (
    id === undefined ||
    id === null ||
    String(id).trim() === ''
  ) {
    console.error(
      'Não foi possível editar: processo sem ID.',
    )
    return
  }

  void router.push(`/processos/${id}/editar`)
}

/**
 * Volta para a listagem.
 */
function voltar() {
  void router.push('/processos')
}

/**
 * Converte o status da API para o texto exibido.
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
 * Define a cor do chip de status.
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
 * Recarrega o processo:
 * - quando a página é aberta;
 * - quando o :id da rota muda.
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
    <!-- Cabeçalho -->
    <div class="d-flex align-center justify-space-between mb-6">
      <div>
        <p
          class="text-caption text-uppercase text-grey-darken-1 mb-1"
        >
          Processo
        </p>

        <h1 class="text-h4 font-weight-bold">
          {{ processo?.nome || 'Detalhes do Processo' }}
        </h1>
      </div>

      <div class="d-flex ga-2">
        <v-btn
          variant="outlined"
          @click="voltar"
        >
          Voltar
        </v-btn>

        <v-btn
          v-if="processo"
          color="primary"
          prepend-icon="mdi-pencil"
          @click="editar"
        >
          Editar
        </v-btn>
      </div>
    </div>

    <!-- Loading -->
    <v-progress-linear
      v-if="loading"
      indeterminate
      color="primary"
      class="mb-4"
    />

    <!-- Erro -->
    <v-alert
      v-else-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
    >
      Não foi possível carregar as informações deste
      processo seletivo.
    </v-alert>

    <!-- Processo encontrado -->
    <template v-else-if="processo">
      <!-- Dados do processo -->
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
            <!-- Quantidade de vagas -->
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

            <!-- Status -->
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
                :color="statusColor(processo.status)"
                variant="flat"
                class="mt-1"
              >
                {{ formatStatus(processo.status) }}
              </v-chip>
            </v-col>

            <!-- Descrição -->
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

      <!-- Área reservada para o Dev D -->
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
            Área reservada para a listagem e criação de
            balões informativos.

            <span class="d-block mt-2">
              Processo ID:
              <code>{{ processo.id }}</code>
            </span>
          </v-alert>
        </v-card-text>
      </v-card>
    </template>
  </v-container>
</template>