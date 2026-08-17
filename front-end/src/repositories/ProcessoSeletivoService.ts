import HttpClient from './adapters/HttpClient'
import type { ProcessoSeletivo } from '../types/ProcessoSeletivo'

class ProcessoSeletivoService {
  async list() {
    return HttpClient.get('/processo-seletivo')
  }

  async getById(id: number) {
    return HttpClient.get(`/processo-seletivo/${id}`)
  }

  async create(body: ProcessoSeletivo) {
    return HttpClient.post('/processo-seletivo', body)
  }

  async update(id: number, body: ProcessoSeletivo) {
    return HttpClient.put(`/processo-seletivo/${id}`, body)
  }

  async delete(id: number) {
    return HttpClient.delete(`/processo-seletivo/${id}`)
  }
}

export default new ProcessoSeletivoService()