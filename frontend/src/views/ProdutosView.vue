<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ApiException } from '@/api/http'
import { produtosApi } from '@/api/produtos'
import ProdutoForm from '@/components/ProdutoForm.vue'
import { rotuloTipo } from '@/constants/tipos'
import type { Page, Produto } from '@/types/api'
import { formatarMoeda } from '@/utils/formato'

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
    <section>
        <div class="cabecalho">
            <h2>Produtos</h2>
            <button type="button" @click="novo">Novo produto</button>
        </div>

        <p v-if="erro" class="erro">{{ erro }}</p>

        <ProdutoForm v-if="formAberto" :produto="emEdicao" @salvo="aoSalvar" @cancelar="formAberto = false" />

        <table v-if="pagina && pagina.content.length">
            <thead>
                <tr>
                    <th>Código</th>
                    <th>Descrição</th>
                    <th>Tipo</th>
                    <th>Valor fornecedor</th>
                    <th>Estoque</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="p in pagina.content" :key="p.id">
                    <td>{{ p.codigo }}</td>
                    <td>{{ p.descricao }}</td>
                    <td>{{ rotuloTipo(p.tipo) }}</td>
                    <td>{{ formatarMoeda(p.valorFornecedor) }}</td>
                    <td>{{ p.quantidadeEstoque }}</td>
                    <td class="acoes">
                        <button type="button" @click="editar(p)">Editar</button>
                        <button type="button" @click="excluir(p)">Excluir</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <p v-else-if="pagina">Nenhum produto cadastrado.</p>

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
.cabecalho {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 16px;
}

th,
td {
    text-align: left;
    padding: 8px;
    border-bottom: 1px solid #e5e7eb;
}

.acoes {
    display: flex;
    gap: 8px;
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