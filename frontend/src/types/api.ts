export type TipoProduto =
  'ELETRONICO' | 'ELETRODOMESTICO' | 'MOVEL' | 'ROUPA' | 'ALIMENTO' | 'LIVRO' | 'MOVEIS'
export type TipoMovimentacao = 'ENTRADA' | 'SAIDA'

export interface Produto {
  id: number
  codigo: string
  descricao: string
  tipo: TipoProduto
  valorFornecedor: number
  quantidadeEstoque: number
}

export interface ApiErrorBody {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  detalhes: string[]
}

export interface Page<T> {
  content: T[]
  number: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface ProdutoRequest {
  codigo: string
  descricao: string
  tipo: TipoProduto
  valorFornecedor: number
}
