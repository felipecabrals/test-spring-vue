import type { TipoProduto } from '@/types/api'

export const TIPOS_PRODUTO: { value: TipoProduto; label: string }[] = [
  { value: 'ELETRONICO', label: 'Eletrônico' },
  { value: 'ELETRODOMESTICO', label: 'Eletrodoméstico' },
  { value: 'MOVEL', label: 'Móvel' },
  { value: 'ROUPA', label: 'Roupa' },
  { value: 'ALIMENTO', label: 'Alimento' },
  { value: 'LIVRO', label: 'Livro' },
  { value: 'MOVEIS', label: 'Móveis' },
]

export const rotuloTipo = (tipo: TipoProduto) =>
  TIPOS_PRODUTO.find((t) => t.value === tipo)?.label ?? tipo
