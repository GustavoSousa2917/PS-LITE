import HttpClient from './adapters/HttpClient'
import type { ProcessoSeletivo } from '../types/ProcessoSeletivo'

class ProcessoSeletivoService {
  async list(): Promise<ProcessoSeletivo[]> {
    const response = await HttpClient.get('/processo-seletivo')
    if (response.data && Array.isArray(response.data.content)) {
      return response.data.content
    }
    if (Array.isArray(response.data)) {
      return response.data
    }
    return []
  }

  async getById(id: number | string): Promise<ProcessoSeletivo> {
    const response = await HttpClient.get(`/processo-seletivo/${id}`)
    return response.data
  }

  async create(body: Omit<ProcessoSeletivo, 'id'>): Promise<ProcessoSeletivo> {
    const response = await HttpClient.post('/processo-seletivo', body)
    return response.data
  }

  async update(id: number | string, body: Partial<ProcessoSeletivo>): Promise<ProcessoSeletivo> {
    const response = await HttpClient.put(`/processo-seletivo/${id}`, body)
    return response.data
  }

  async delete(id: number | string): Promise<void> {
    await HttpClient.delete(`/processo-seletivo/${id}`)
  }
}

export default new ProcessoSeletivoService()