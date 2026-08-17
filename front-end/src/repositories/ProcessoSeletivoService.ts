import HttpClient from './adapters/HttpClient'
import type { ProcessoSeletivo } from '../types/ProcessoSeletivo'

class ProcessoSeletivoService {
  async list() {
    try {
    return await HttpClient.get('/processo-seletivo')
    } catch (error) {
      console.error('Erro ao listar processos seletivos:', error)
      throw error
    }
  }

  async getById(id: number) {
    try {
      return await HttpClient.get(`/processo-seletivo/${id}`)
    } catch (error) {
      console.error('Erro ao obter processo seletivo:', error)
      throw error
    }
  }

  async create(body: ProcessoSeletivo) {
    try {
      return await HttpClient.post('/processo-seletivo', body)
    } catch (error) {
      console.error('Erro ao criar processo seletivo:', error)
      throw error
    }
  }

  async update(id: number, body: ProcessoSeletivo) {
    try {
      return await HttpClient.put(`/processo-seletivo/${id}`, body)
    } catch (error) {
      console.error('Erro ao atualizar processo seletivo:', error)
      throw error
    }
  }

  async delete(id: number) {
    try {
      return await HttpClient.delete(`/processo-seletivo/${id}`)
    } catch (error) {
      console.error('Erro ao excluir processo seletivo:', error)
      throw error
    }
  }
}

export default new ProcessoSeletivoService()