import { http } from '@/api/http'
import type { LucroProduto, ProdutoPorTipo, ResumoMovimentacao, TipoProduto } from '@/types/api'

export const relatoriosApi = {
  porTipo: (tipo: TipoProduto) =>
    http.get<ProdutoPorTipo[]>('/produtos/por-tipo', { params: { tipo } }).then((r) => r.data),

  lucro: () => http.get<LucroProduto[]>('/produtos/lucro').then((r) => r.data),

  movimentacao: (inicio?: string, fim?: string) =>
    http
      .get<ResumoMovimentacao[]>('/produtos/movimentacao', { params: { inicio, fim } })
      .then((r) => r.data),
}
