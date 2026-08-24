import type { StatusProcessoSeletivo } from './StatusProcessoSeletivo'

export interface ProcessoSeletivo {
  data: ProcessoSeletivo | { id?: number | string |undefined; nome: string; descricao?: string | null | undefined; qtdVagas: number; status: StatusProcessoSeletivo } | null
  id?: number
  nome: string
  descricao?: string | null
  qtdVagas: number
  status: StatusProcessoSeletivo
}
