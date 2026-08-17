import type { StatusProcessoSeletivo } from './StatusProcessoSeletivo'

export interface ProcessoSeletivo {
  id?: number
  nome: string
  descricao: string
  qtdVagas: number
  status: StatusProcessoSeletivo
}

