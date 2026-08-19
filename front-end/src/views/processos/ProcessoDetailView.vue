<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import ProcessoSeletivoService from '../../repositories/ProcessoSeletivoService'
import type { ProcessoSeletivo } from '../../types/ProcessoSeletivo'

const route = useRoute()
const router = useRouter()

const processo = ref<ProcessoSeletivo | null>(null)
const loading = ref(false)
const error = ref(false)

async function carregarProcesso() {
  const id = Number(route.params.id)

  loading.value = true
  error.value = false

  try {
    const response = await ProcessoSeletivoService.getById(id)

    processo.value = response.data as ProcessoSeletivo
  } catch (err) {
    console.error('Erro ao carregar processo seletivo:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

function editar() {
  router.push(`/processos/${processo.value?.id}/editar`)
}

function voltar() {
  router.push('/processos')
}

onMounted(() => {
  carregarProcesso()
})
</script>

<template>
  <v-container>
    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4">
        Detalhes do processo
      </h1>

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

    <v-progress-linear
      v-if="loading"
      indeterminate
      class="mb-4"
    />

    <v-alert
      v-else-if="error"
      type="error"
      variant="tonal"
    >
      Não foi possível carregar o processo seletivo.
    </v-alert>

    <template v-else-if="processo">
      <v-card>
        <v-card-title>
          {{ processo.nome }}
        </v-card-title>

        <v-card-text>
          <p>
            <strong>Descrição:</strong>
            {{ processo.descricao }}
          </p>

          <p class="mt-3">
            <strong>Quantidade de vagas:</strong>
            {{ processo.qtdVagas }}
          </p>

          <p class="mt-3">
            <strong>Status:</strong>
            {{ processo.status }}
          </p>
        </v-card-text>
      </v-card>

      <!-- Área reservada para os balões -->
      <v-card class="mt-6">
        <v-card-title>
          Balões informativos
        </v-card-title>

        <v-card-text>
          Área reservada para a integração dos balões informativos.
        </v-card-text>
      </v-card>
    </template>
  </v-container>
</template>