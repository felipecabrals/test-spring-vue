<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ApiException } from '@/api/http'
import { produtosApi } from '@/api/produtos'
import { TIPOS_PRODUTO } from '@/constants/tipos'
import type { Produto, ProdutoRequest } from '@/types/api'

const props = defineProps<{ produto: Produto | null }>()
const emit = defineEmits<{ salvo: []; cancelar: [] }>()

const vazio = (): ProdutoRequest => ({
    codigo: '',
    descricao: '',
    tipo: 'ELETRONICO',
    valorFornecedor: 0,
})

const form = reactive<ProdutoRequest>(vazio())
const erro = ref('')
const detalhes = ref<string[]>([])
const salvando = ref(false)

watch(
    () => props.produto,
    (produto) => {
        Object.assign(
            form,
            produto
                ? {
                    codigo: produto.codigo,
                    descricao: produto.descricao,
                    tipo: produto.tipo,
                    valorFornecedor: produto.valorFornecedor,
                }
                : vazio(),
        )
        erro.value = ''
        detalhes.value = []
    },
    { immediate: true },
)

async function salvar() {
    salvando.value = true
    erro.value = ''
    detalhes.value = []
    try {
        if (props.produto) {
            await produtosApi.atualizar(props.produto.id, form)
        } else {
            await produtosApi.criar(form)
        }
        emit('salvo')
    } catch (e) {
        if (e instanceof ApiException) {
            erro.value = e.message
            detalhes.value = e.detalhes
        } else {
            throw e
        }
    } finally {
        salvando.value = false
    }
}
</script>

<template>
    <form class="form" @submit.prevent="salvar">
        <h3>{{ produto ? 'Editar produto' : 'Novo produto' }}</h3>

        <label>Código <input v-model="form.codigo" required /></label>
        <label>Descrição <input v-model="form.descricao" required /></label>
        <label>Tipo
            <select v-model="form.tipo">
                <option v-for="t in TIPOS_PRODUTO" :key="t.value" :value="t.value">
                    {{ t.label }}
                </option>
            </select>
        </label>
        <label>Valor no fornecedor
            <input v-model.number="form.valorFornecedor" type="number" min="0.01" step="0.01" required />
        </label>

        <p v-if="erro" class="erro">{{ erro }}</p>
        <ul v-if="detalhes.length" class="erro">
            <li v-for="d in detalhes" :key="d">{{ d }}</li>
        </ul>

        <div class="acoes">
            <button type="submit" :disabled="salvando">
                {{ salvando ? 'Salvando...' : 'Salvar' }}
            </button>
            <button type="button" @click="emit('cancelar')">Cancelar</button>
        </div>
    </form>
</template>

<style scoped>
.form {
    display: grid;
    gap: 12px;
    max-width: 420px;
    margin: 16px 0;
    padding: 16px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
}

label {
    display: grid;
    gap: 4px;
}

.acoes {
    display: flex;
    gap: 8px;
}

.erro {
    color: #b91c1c;
}
</style>