import { http } from '@/api/http'
import type { MovimentacaoRequest, Movimento, Page } from '@/types/api'

export const movimentosApi = {
  listar: (page = 0, size = 10) =>
    http
      .get<Page<Movimento>>('/movimentos', {
        params: { page, size, sort: 'dataMovimentacao,desc' },
      })
      .then((r) => r.data),

  registrar: (dados: MovimentacaoRequest) =>
    http.post<Movimento>('/movimentos', dados).then((r) => r.data),
}
