import axios, { AxiosError } from 'axios'
import type { ApiErrorBody } from '@/types/api'

export class ApiException extends Error {
  readonly status: number
  readonly detalhes: string[]

  constructor(status: number, message: string, detalhes: string[] = []) {
    super(message)
    this.status = status
    this.detalhes = detalhes
  }
}

export const http = axios.create({ baseURL: '/api' })

http.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiErrorBody>) => {
    const body = error.response?.data
    return Promise.reject(
      new ApiException(
        error.response?.status ?? 0,
        body?.message ?? 'Não foi possível conectar à API',
        body?.detalhes ?? [],
      ),
    )
  },
)
