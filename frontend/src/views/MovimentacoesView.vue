<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ApiException } from '@/api/http'
import { movimentosApi } from '@/api/movimentos'
import { produtosApi } from '@/api/produtos'
import MovimentacaoForm from '@/components/MovimentacaoForm.vue'
import { rotuloMovimentacao } from '@/constants/tipos'
import type { Movimento, Page, Produto } from '@/types/api'
import { formatarDataHora, formatarMoeda } from '@/utils/formato'

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
    <section>
        <h2>Movimentações</h2>

        <p v-if="erro" class="erro">{{ erro }}</p>

        <MovimentacaoForm :produtos="produtos" @registrada="aoRegistrar" />

        <h3>Histórico</h3>
        <table v-if="pagina && pagina.content.length">
            <thead>
                <tr>
                    <th>Data</th>
                    <th>Produto</th>
                    <th>Tipo</th>
                    <th>Quantidade</th>
                    <th>Valor de venda</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="m in pagina.content" :key="m.id">
                    <td>{{ formatarDataHora(m.dataMovimentacao) }}</td>
                    <td>{{ m.codigoProduto }}</td>
                    <td>{{ rotuloMovimentacao(m.tipo) }}</td>
                    <td>{{ m.quantidade }}</td>
                    <td>{{ m.valorVenda === null ? '-' : formatarMoeda(m.valorVenda) }}</td>
                </tr>
            </tbody>
        </table>
        <p v-else-if="pagina">Nenhuma movimentação registrada.</p>

        <div v-if="pagina && pagina.totalPages > 1" class="paginacao">
            <button type="button" :disabled="pagina.first" @click="irParaPagina(numeroPagina - 1)">
                Anterior
            </button>
            <span>Página {{ pagina.number + 1 }} de {{ pagina.totalPages }}</span>
            <button type="button" :disabled="pagina.last" @click="irParaPagina(numeroPagina + 1)">
                Próxima
            </button>
        </div>
    </section>
</template>

<style scoped>
table {
    width: 100%;
    border-collapse: collapse;
}

th,
td {
    text-align: left;
    padding: 8px;
    border-bottom: 1px solid #e5e7eb;
}

.paginacao {
    display: flex;
    gap: 12px;
    align-items: center;
    margin-top: 16px;
}

.erro {
    color: #b91c1c;
}
</style>