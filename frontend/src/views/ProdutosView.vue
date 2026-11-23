<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ApiException } from '@/api/http'
import { produtosApi } from '@/api/produtos'
import ProdutoForm from '@/components/ProdutoForm.vue'
import { rotuloTipo } from '@/constants/tipos'
import type { Page, Produto } from '@/types/api'
import { formatarMoeda } from '@/utils/formato'
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

const pagina = ref<Page<Produto> | null>(null)
const numeroPagina = ref(0)
const erro = ref('')
const formAberto = ref(false)
const emEdicao = ref<Produto | null>(null)

async function carregar() {
    try {
        pagina.value = await produtosApi.listar(numeroPagina.value)
        erro.value = ''
    } catch (e) {
        erro.value = e instanceof ApiException ? e.message : 'Erro ao carregar produtos'
    }
}

function novo() {
    emEdicao.value = null
    formAberto.value = true
}

function editar(produto: Produto) {
    emEdicao.value = produto
    formAberto.value = true
}

async function aoSalvar() {
    formAberto.value = false
    await carregar()
}

async function excluir(produto: Produto) {
    if (!confirm(`Excluir o produto ${produto.codigo} - ${produto.descricao}?`)) return
    try {
        await produtosApi.excluir(produto.id)
        if (pagina.value?.content.length === 1 && numeroPagina.value > 0) {
            numeroPagina.value--
        }
        await carregar()
    } catch (e) {
        erro.value = e instanceof ApiException ? e.message : 'Erro ao excluir o produto'
    }
}

function irParaPagina(n: number) {
    numeroPagina.value = n
    carregar()
}

onMounted(carregar)
</script>

<template>
    <section class="grid gap-4">
        <div class="flex items-center justify-between">
            <h2 class="text-2xl font-semibold tracking-tight">Produtos</h2>
            <Button type="button" @click="novo">Novo produto</Button>
        </div>

        <p v-if="erro" class="text-sm text-destructive">{{ erro }}</p>

        <Card v-if="formAberto">
            <CardHeader>
                <CardTitle>{{ emEdicao ? 'Editar produto' : 'Novo produto' }}</CardTitle>
            </CardHeader>
            <CardContent>
                <ProdutoForm :produto="emEdicao" @salvo="aoSalvar" @cancelar="formAberto = false" />
            </CardContent>
        </Card>

        <Card v-if="pagina && pagina.content.length">
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Código</TableHead>
                            <TableHead>Descrição</TableHead>
                            <TableHead>Tipo</TableHead>
                            <TableHead>Valor fornecedor</TableHead>
                            <TableHead>Estoque</TableHead>
                            <TableHead></TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        <TableRow v-for="p in pagina.content" :key="p.id">
                            <TableCell>{{ p.codigo }}</TableCell>
                            <TableCell>{{ p.descricao }}</TableCell>
                            <TableCell>{{ rotuloTipo(p.tipo) }}</TableCell>
                            <TableCell>{{ formatarMoeda(p.valorFornecedor) }}</TableCell>
                            <TableCell>{{ p.quantidadeEstoque }}</TableCell>
                            <TableCell class="flex gap-2">
                                <Button type="button" variant="outline" size="sm" @click="editar(p)">
                                    Editar
                                </Button>
                                <Button type="button" variant="destructive" size="sm" @click="excluir(p)">
                                    Excluir
                                </Button>
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
        <p v-else-if="pagina">Nenhum produto cadastrado.</p>

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
