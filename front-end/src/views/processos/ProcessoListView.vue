<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import ProcessoSeletivoService from '../../repositories/ProcessoSeletivoService'
import type { ProcessoSeletivo } from '../../types/ProcessoSeletivo'

const router = useRouter()

const processos = ref<ProcessoSeletivo[]>([])
const loading = ref(false)
const error = ref(false)

async function carregarProcessos() {
  loading.value = true
  error.value = false

  try {
    processos.value = await ProcessoSeletivoService.list()
  } catch (err) {
    console.error('Erro ao carregar processos seletivos:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

function novoProcesso() {
  router.push('/processos/novo')
}

function visualizarProcesso(id: number) {
  router.push(`/processos/${id}`)
}

function editarProcesso(id: number) {
  router.push(`/processos/${id}/editar`)
}

async function excluirProcesso(id: number) {
  const confirmar = window.confirm(
    'Deseja realmente excluir este processo seletivo?',
  )

  if (!confirmar) {
    return
  }

  try {
    await ProcessoSeletivoService.delete(id)

    await carregarProcessos()
  } catch (err) {
    console.error('Erro ao excluir processo seletivo:', err)
    error.value = true
  }
}

onMounted(() => {
  carregarProcessos()
})
</script>

<template>
  <v-container>
    <div class="d-flex align-center justify-space-between mb-6">
      <div>
        <h1 class="text-h4">Processos seletivos</h1>
        <p class="text-body-1 mt-2">
          Gerencie os processos seletivos cadastrados.
        </p>
      </div>

    </div>

    <!-- Loading -->
    <div v-if="loading">
      <v-progress-linear
        indeterminate
        color="primary"
      />

      <p class="text-body-1 mt-4">
        Carregando processos seletivos...
      </p>
    </div>

    <!-- Erro -->
    <v-alert
      v-else-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
    >
      Não foi possível carregar os processos seletivos.
    </v-alert>

    <!-- Empty -->
    <v-alert
      v-else-if="processos.length === 0"
      type="info"
      variant="tonal"
    >
      <div class="d-flex align-center justify-space-between">
        <span>
          Nenhum processo seletivo cadastrado. 
        </span>
        <v-button 
        color="white"
        background="green" 
        @click="novoProcesso">Novo Processo
        </v-button>
      </div>
    </v-alert>

    <!-- Lista -->
    <v-table v-else>
      <thead>
        <tr>
          <th>Nome</th>
          <th>Quantidade de vagas</th>
          <th>Status</th>
          <th class="text-right">Ações</th>
        </tr>
      </thead>
      <v-div>
        <v-button 
        color="white"
        background="green" 
        @click="novoProcesso">Novo Processo
        </v-button>
      </v-div>
      <tbody>
        <tr
          v-for="processo in processos"
          :key="processo.id"
        >
          <td>
            {{ processo.nome }}
          </td>

          <td>
            {{ processo.qtdVagas }}
          </td>

          <td>
            <v-chip
              size="small"
              variant="tonal"
            >
              {{ processo.status }}
            </v-chip>
          </td>

          <td class="text-right">
            <v-btn
              icon="mdi-eye"
              variant="text"
              size="small"
              aria-label="Visualizar processo"
              @click="processo.id !== undefined && visualizarProcesso(processo.id)"
            />

            <v-btn
              icon="mdi-pencil"
              variant="text"
              size="small"
              aria-label="Editar processo"
              @click="processo.id !== undefined && editarProcesso(processo.id)"
            />

            <v-btn
              icon="mdi-delete"
              variant="text"
              size="small"
              aria-label="Excluir processo"
              @click="processo.id !== undefined && excluirProcesso(processo.id)"
            />
          </td>
        </tr>
      </tbody>
    </v-table>
  </v-container>
</template>
<style>
    body {
        background-color: #f5f5f5;
    }
</style>