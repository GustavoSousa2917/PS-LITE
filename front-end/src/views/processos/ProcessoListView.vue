<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import type { ProcessoSeletivo } from '../../types/ProcessoSeletivo'

const router = useRouter()

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const processosUrl = `${apiBaseUrl}/processo-seletivo`

const processos = ref<ProcessoSeletivo[]>([])
const loading = ref(false)
const error = ref(false)
const excluindoId = ref<ProcessoSeletivo['id'] | null>(null)

/**
 * Carrega todos os processos seletivos.
 */

 function normalizarProcesso(processo: ProcessoSeletivo): ProcessoSeletivo {
  return {
    ...processo,
    id: processo.id ?? processo.idd,
  }
}

async function carregarProcessos() {
  loading.value = true
  error.value = false

  try {
    const response = await fetch(processosUrl)

    if (!response.ok) {
      throw new Error(
        `Erro ao carregar processos. Status: ${response.status}`,
      )
    }

    const data = await response.json()
    console.log('PROCESSOS RECEBIDOS DA API:', data)

    /*
     * A API pode retornar:
     * - um array diretamente
     * - ou um objeto paginado contendo "content"
     */
   if (Array.isArray(data)) {
  processos.value = data.map(normalizarProcesso)
} else if (Array.isArray(data?.content)) {
  processos.value = data.content.map(normalizarProcesso)
} else {
  processos.value = []
}
  } catch (err) {
    console.error('Erro ao carregar processos seletivos:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

/**
 * Abre a tela de criação.
 *
 * /processos/novo
 */
function novoProcesso() {
  void router.push('/processos/novo')
}

/**
 * Abre o detalhe de um processo específico.
 *
 * /processos/:id
 */
function visualizarProcesso(id: ProcessoSeletivo['id']) {
  if (!temId(id)) {
    console.error('Não foi possível visualizar: processo sem ID.')
    return
  }

  console.log('Visualizando processo com ID:', id)

  void router.push(`/processos/${id}`)
}

/**
 * Abre a tela de edição de um processo específico.
 *
 * /processos/:id/editar
 */
function editarProcesso(id: ProcessoSeletivo['id']) {
  if (!temId(id)) {
    console.error('Não foi possível editar: processo sem ID.')
    return
  }

  console.log('Editando processo com ID:', id)

  void router.push(`/processos/${id}/editar`)
}

/**
 * Verifica se o processo possui um ID válido.
 */
function temId(id: ProcessoSeletivo['id']) {
  return (
    id !== undefined &&
    id !== null &&
    String(id).trim() !== ''
  )
}

/**
 * Exclui um processo específico.
 *
 * DELETE /processo-seletivo/:id
 */
async function excluirProcesso(id: ProcessoSeletivo['id']) {
  if (!temId(id)) {
    console.error('Não foi possível excluir: processo sem ID.')
    return
  }

  const confirmar = window.confirm(
    'Deseja realmente excluir este processo seletivo?',
  )

  if (!confirmar) {
    return
  }

  excluindoId.value = id
  error.value = false

  console.log('Excluindo processo com ID:', id)

  try {
    const response = await fetch(`${processosUrl}/${id}`, {
      method: 'DELETE',
    })

    /*
     * O contrato da API utiliza HTTP 204
     * quando a exclusão é realizada com sucesso.
     */
    if (!response.ok) {
      throw new Error(
        `Não foi possível excluir o processo. Status: ${response.status}`,
      )
    }

    /*
     * Atualiza a listagem depois da exclusão.
     */
    await carregarProcessos()
  } catch (err) {
    console.error('Erro ao excluir processo seletivo:', err)
    error.value = true
  } finally {
    excluindoId.value = null
  }
}

/**
 * Converte o status recebido pela API
 * para o texto exibido na interface.
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
 * Define a classe visual de cada status.
 */
function statusClass(value: string) {
  const classes: Record<string, string> = {
    CADASTRADO: 'status-badge--cadastrado',
    FINALIZADO: 'status-badge--finalizado',
    CANCELADO: 'status-badge--cancelado',
  }

  return classes[value] ?? ''
}

onMounted(() => {
  void carregarProcessos()
})
</script>

<template>
  <section class="listagem-view">
    <header class="page-header">
      <div>

        <h1>Processos seletivos</h1>

        <p class="subtitle">
          Gerencie os processos seletivos cadastrados.
        </p>
      </div>

      <button
        class="primary-button"
        type="button"
        @click="novoProcesso"
      >
        + Novo processo
      </button>
    </header>

    <!-- Loading -->
    <div
      v-if="loading"
      class="state-box"
    >
      <div
        class="spinner"
        aria-hidden="true"
      ></div>

      <span>
        Carregando processos seletivos...
      </span>
    </div>

    <!-- Erro -->
    <div
      v-else-if="error"
      class="state-box error-box"
    >
      <span>
        Não foi possível carregar os processos seletivos.
      </span>

      <button
        class="secondary-button"
        type="button"
        @click="carregarProcessos"
      >
        Tentar novamente
      </button>
    </div>

    <!-- Lista vazia -->
    <div
      v-else-if="processos.length === 0"
      class="state-box empty-box"
    >
      <span>
        Nenhum processo seletivo cadastrado ainda.
      </span>

      <button
        class="primary-button"
        type="button"
        @click="novoProcesso"
      >
        Criar o primeiro processo
      </button>
    </div>

    <!-- Lista -->
    <div
      v-else
      class="table-card"
    >
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Vagas</th>
            <th>Status</th>
            <th class="col-actions">
              Ações
            </th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="processo in processos"
            :key="processo.id ?? processo.nome"
          >
            <td class="col-nome">
              {{ processo.nome }}
            </td>

            <td>
              {{ processo.qtdVagas }}
            </td>

            <td>
              <span
                class="status-badge"
                :class="statusClass(processo.status)"
              >
                {{ formatStatus(processo.status) }}
              </span>
            </td>

            <td class="col-actions">
              <div class="actions-group">
                <!-- VISUALIZAR -->
                <button
                  class="icon-button"
                  type="button"
                  :disabled="!temId(processo.id)"
                  :title="
                    temId(processo.id)
                      ? `Visualizar ID ${processo.id}`
                      : 'Processo sem ID'
                  "
                  @click="visualizarProcesso(processo.id)"
                >
                  Visualizar
                </button>

                <!-- EDITAR -->
                <button
                  class="icon-button"
                  type="button"
                  :disabled="!temId(processo.id)"
                  :title="
                    temId(processo.id)
                      ? `Editar ID ${processo.id}`
                      : 'Processo sem ID'
                  "
                  @click="editarProcesso(processo.id)"
                >
                  Editar
                </button>

                <!-- DELETAR -->
                <button
                  class="icon-button icon-button--danger"
                  type="button"
                  :disabled="
                    !temId(processo.id) ||
                    excluindoId === processo.id
                  "
                  :title="
                    temId(processo.id)
                      ? `Excluir ID ${processo.id}`
                      : 'Processo sem ID'
                  "
                  @click="excluirProcesso(processo.id)"
                >
                  {{
                    excluindoId === processo.id
                      ? 'Excluindo...'
                      : 'Deletar'
                  }}
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


.listagem-view {
  width: 100%;
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

h1 {
  margin: 0;
  color: #111827;
}

.subtitle {
  margin: 8px 0 0;
  color: #6b7280;
}

.table-card {
  width: 100%;
  overflow-x: auto;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

th {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
}

tbody tr:hover {
  background-color: #f9fafb;
}

tbody tr:last-child td {
  border-bottom: 0;
}

.col-nome {
  font-weight: 600;
  color: #111827;
}

.col-actions {
  text-align: right;
}

.actions-group {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.primary-button,
.secondary-button,
.icon-button {
  border: 0;
  border-radius: 8px;
  padding: 9px 14px;
  font-weight: 600;
  cursor: pointer;
}

.primary-button {
  background: #2563eb;
  color: #ffffff;
}

.secondary-button {
  background: #e5e7eb;
  color: #111827;
}

.icon-button {
  background: #f3f4f6;
  color: #111827;
}

.icon-button--danger {
  color: #b91c1c;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge--cadastrado {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-badge--finalizado {
  background: #dcfce7;
  color: #15803d;
}

.status-badge--cancelado {
  background: #fee2e2;
  color: #b91c1c;
}

.state-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  min-height: 120px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
}

.error-box {
  flex-direction: column;
  color: #b91c1c;
  background: #fef2f2;
  border-color: #fecaca;
}

.empty-box {
  flex-direction: column;
  color: #374151;
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid #d1d5db;
  border-top-color: #2563eb;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 640px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .actions-group {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>