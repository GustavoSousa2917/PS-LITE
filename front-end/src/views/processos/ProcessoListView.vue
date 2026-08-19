<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import ProcessoSeletivoService from '../../repositories/ProcessoSeletivoService'
import type { ProcessoSeletivo } from '../../types/ProcessoSeletivo'

const router = useRouter()

const processos = ref<ProcessoSeletivo[]>([])
const loading = ref(false)
const error = ref(false)
const excluindoId = ref<number | null>(null)

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

function visualizarProcesso(id: number | undefined) {
  if (id === undefined) {
    return
  }

  void router.push(`/processos/${id}`)
}

function editarProcesso(id: number | undefined) {
  if (id === undefined) {
    return
  }

  void router.push(`/processos/${id}/editar?mode=edit`)
}

async function excluirProcesso(id: number) {
  const confirmar = window.confirm(
    'Deseja realmente excluir este processo seletivo?',
  )

  if (!confirmar) {
    return
  }

  excluindoId.value = id

  try {
    await ProcessoSeletivoService.delete(id)
    await carregarProcessos()
  } catch (err) {
    console.error('Erro ao excluir processo seletivo:', err)
    error.value = true
  } finally {
    excluindoId.value = null
  }
}

function formatStatus(value: string) {
  const labels: Record<string, string> = {
    CADASTRADO: 'Cadastrado',
    FINALIZADO: 'Finalizado',
    CANCELADO: 'Cancelado'
  }
  return labels[value] ?? value
}

function statusClass(value: string) {
  const classes: Record<string, string> = {
    CADASTRADO: 'status-badge--cadastrado',
    FINALIZADO: 'status-badge--finalizado',
    CANCELADO: 'status-badge--cancelado'
  }
  return classes[value] ?? ''
}

onMounted(() => {
  carregarProcessos()
})
</script>

<template>
  <section class="listagem-view">
    <header class="page-header">
      <div>
        <p class="eyebrow">Processos</p>
        <h1>Processos seletivos</h1>
        <p class="subtitle">Gerencie os processos seletivos cadastrados.</p>
      </div>
      <button class="primary-button" type="button" @click="novoProcesso">
        + Novo processo
      </button>
    </header>

    <!-- Loading -->
    <div v-if="loading" class="state-box">
      <div class="spinner" aria-hidden="true" />
      <span>Carregando processos seletivos...</span>
    </div>

    <!-- Erro -->
    <div v-else-if="error" class="state-box error-box">
      <span>Não foi possível carregar os processos seletivos.</span>
      <button class="secondary-button" type="button" @click="carregarProcessos">
        Tentar novamente
      </button>
    </div>

    <!-- Empty -->
    <div v-else-if="processos.length === 0" class="state-box empty-box">
      <span>Nenhum processo seletivo cadastrado ainda.</span>
      <button class="primary-button" type="button" @click="novoProcesso">
        Criar o primeiro processo
      </button>
    </div>

    <!-- Lista -->
    <div v-else class="table-card">
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Vagas</th>
            <th>Status</th>
            <th class="col-actions">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="processo in processos" :key="processo.id">
            <td class="col-nome">{{ processo.nome }}</td>
            <td>{{ processo.qtdVagas }}</td>
            <td>
              <span class="status-badge" :class="statusClass(processo.status)">
                {{ formatStatus(processo.status) }}
              </span>
            </td>
            <td class="col-actions">
              <div class="actions-group">
                <button
                  class="icon-button"
                  type="button"
                  aria-label="Visualizar processo"
                  title="Visualizar"
                  @click="visualizarProcesso(processo.id)"
                >
                  <svg viewBox="0 0 20 20" width="16" height="16" fill="none">
                    <path d="M1 10s3-6 9-6 9 6 9 6-3 6-9 6-9-6-9-6Z" stroke="currentColor" stroke-width="1.5"/>
                    <circle cx="10" cy="10" r="2.5" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>

                <button
                  class="icon-button"
                  type="button"
                  aria-label="Editar processo"
                  title="Editar"
                  @click="editarProcesso(processo.id)"
                >
                  <svg viewBox="0 0 20 20" width="16" height="16" fill="none">
                    <path d="M13.5 3.5 16.5 6.5 6.5 16.5 3 17l0.5-3.5 10-10Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
                  </svg>
                </button>

                <button
                  class="icon-button icon-button--danger"
                  type="button"
                  aria-label="Excluir processo"
                  title="Excluir"
                  :disabled="excluindoId === processo.id"
                  @click="processo.id !== undefined && excluirProcesso(processo.id)"
                >
                  <svg viewBox="0 0 20 20" width="16" height="16" fill="none">
                    <path d="M4 6h12M8 6V4.5a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1V6M6 6l.6 10a1 1 0 0 0 1 .9h4.8a1 1 0 0 0 1-.9L14 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
  main {
    background-color: fffffff;
    padding: 0;
  }

  .table-card {
    overflow-x: auto;
  }
  .table {
    width: 100%;
    border-collapse: collapse;
  }
  .tbody tr:hover {
    background-color: var(--v-theme-surface-hover);
  }

</style>