<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ApiException } from '@/api/http'
import { movimentosApi } from '@/api/movimentos'
import { produtosApi } from '@/api/produtos'
import MovimentacaoForm from '@/components/MovimentacaoForm.vue'
import { rotuloMovimentacao } from '@/constants/tipos'
import type { Movimento, Page, Produto } from '@/types/api'
import { formatarDataHora, formatarMoeda } from '@/utils/formato'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'

const produtos = ref<Produto[]>([])
const pagina = ref<Page<Movimento> | null>(null)
const numeroPagina = ref(0)
const erro = ref('')

async function carregar() {
    try {
        const [listaProdutos, listaMovimentos] = await Promise.all([
            produtosApi.listarTodos(),
            movimentosApi.listar(numeroPagina.value),
        ])
        produtos.value = listaProdutos
        pagina.value = listaMovimentos
        erro.value = ''
    } catch (e) {
        erro.value = e instanceof ApiException ? e.message : 'Erro ao carregar os dados'
    }
}

async function aoRegistrar() {
    numeroPagina.value = 0
    await carregar()
}

function irParaPagina(n: number) {
    numeroPagina.value = n
    carregar()
}

onMounted(carregar)
</script>

<template>
    <section class="grid gap-4">
        <h2 class="text-2xl font-semibold tracking-tight">Movimentações</h2>

        <p v-if="erro" class="text-sm text-destructive">{{ erro }}</p>

        <Card>
            <CardHeader>
                <CardTitle>Registrar movimentação</CardTitle>
            </CardHeader>
            <CardContent>
                <MovimentacaoForm :produtos="produtos" @registrada="aoRegistrar" />
            </CardContent>
        </Card>

        <h3 class="text-xl font-semibold tracking-tight">Histórico</h3>

        <Card v-if="pagina && pagina.content.length">
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Data</TableHead>
                            <TableHead>Produto</TableHead>
                            <TableHead>Tipo</TableHead>
                            <TableHead>Quantidade</TableHead>
                            <TableHead>Valor de venda</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        <TableRow v-for="m in pagina.content" :key="m.id">
                            <TableCell>{{ formatarDataHora(m.dataMovimentacao) }}</TableCell>
                            <TableCell>{{ m.codigoProduto }}</TableCell>
                            <TableCell>{{ rotuloMovimentacao(m.tipo) }}</TableCell>
                            <TableCell>{{ m.quantidade }}</TableCell>
                            <TableCell>{{ m.valorVenda === null ? '-' : formatarMoeda(m.valorVenda) }}</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
        <p v-else-if="pagina">Nenhuma movimentação registrada.</p>

        <div v-if="pagina && pagina.totalPages > 1" class="flex items-center gap-3">
            <Button type="button" variant="outline" size="sm" :disabled="pagina.first" @click="irParaPagina(numeroPagina - 1)">
                Anterior
            </Button>
            <span class="text-sm text-muted-foreground">Página {{ pagina.number + 1 }} de {{ pagina.totalPages }}</span>
            <Button type="button" variant="outline" size="sm" :disabled="pagina.last" @click="irParaPagina(numeroPagina + 1)">
                Próxima
            </Button>
        </div>
    </section>
</template>
