import { http } from '@/api/http'
import type { Page, Produto, ProdutoRequest } from '@/types/api'

export const produtosApi = {
  listar: (page = 0, size = 10) =>
    http
      .get<Page<Produto>>('/produtos', { params: { page, size, sort: 'descricao' } })
      .then((r) => r.data),

  criar: (dados: ProdutoRequest) => http.post<Produto>('/produtos', dados).then((r) => r.data),

  atualizar: (id: number, dados: ProdutoRequest) =>
    http.put<Produto>(`/produtos/${id}`, dados).then((r) => r.data),

  excluir: (id: number) => http.delete(`/produtos/${id}`).then(() => undefined),
}
