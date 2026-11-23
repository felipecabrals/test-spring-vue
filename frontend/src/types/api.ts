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
  quantidadeInicial?: number
}

export interface Movimento {
  id: number
  produtoId: number
  codigoProduto: string
  tipo: TipoMovimentacao
  quantidade: number
  valorVenda: number | null
  dataMovimentacao: string
}

export interface MovimentacaoRequest {
  produtoId: number
  tipo: TipoMovimentacao
  quantidade: number
  valorVenda: number | null
}

export interface ProdutoPorTipo {
  id: number
  codigo: string
  descricao: string
  tipo: TipoProduto
  quantidadeSaida: number
  quantidadeDisponivel: number
}

export interface LucroProduto {
  id: number
  codigo: string
  descricao: string
  quantidadeTotalSaida: number
  lucroTotal: number
}

export interface ResumoMovimentacao {
  id: number
  codigo: string
  descricao: string
  totalEntrada: number
  totalSaida: number
  saldoAtual: number
}
