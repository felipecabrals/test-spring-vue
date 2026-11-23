<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ApiException } from '@/api/http'
import { movimentosApi } from '@/api/movimentos'
import { TIPOS_MOVIMENTACAO } from '@/constants/tipos'
import type { Produto, TipoMovimentacao } from '@/types/api'

defineProps<{ produtos: Produto[] }>()
const emit = defineEmits<{ registrada: [] }>()

interface FormState {
    produtoId: number | null
    tipo: TipoMovimentacao
    quantidade: number
    valorVenda: number | null
}

const form = reactive<FormState>({
    produtoId: null,
    tipo: 'ENTRADA',
    quantidade: 1,
    valorVenda: null,
})
const erro = ref('')
const detalhes = ref<string[]>([])
const sucesso = ref('')
const enviando = ref(false)

const ehSaida = computed(() => form.tipo === 'SAIDA')

async function registrar() {
    if (form.produtoId === null) return
    enviando.value = true
    erro.value = ''
    detalhes.value = []
    sucesso.value = ''
    try {
        await movimentosApi.registrar({
            produtoId: form.produtoId,
            tipo: form.tipo,
            quantidade: form.quantidade,
            valorVenda: ehSaida.value ? form.valorVenda : null,
        })
        sucesso.value = 'Movimentação registrada com sucesso.'
        form.quantidade = 1
        form.valorVenda = null
        emit('registrada')
    } catch (e) {
        if (e instanceof ApiException) {
            erro.value = e.message
            detalhes.value = e.detalhes
        } else {
            throw e
        }
    } finally {
        enviando.value = false
    }
}
</script>

<template>
    <form class="form" @submit.prevent="registrar">
        <h3>Registrar movimentação</h3>

        <label>Produto
            <select v-model="form.produtoId" required>
                <option :value="null" disabled>Selecione...</option>
                <option v-for="p in produtos" :key="p.id" :value="p.id">
                    {{ p.codigo }} - {{ p.descricao }} (saldo: {{ p.quantidadeEstoque }})
                </option>
            </select>
        </label>

        <label>Tipo
            <select v-model="form.tipo">
                <option v-for="t in TIPOS_MOVIMENTACAO" :key="t.value" :value="t.value">
                    {{ t.label }}
                </option>
            </select>
        </label>

        <label>Quantidade
            <input v-model.number="form.quantidade" type="number" min="1" step="1" required />
        </label>

        <label v-if="ehSaida">Valor de venda (unitário)
            <input v-model.number="form.valorVenda" type="number" min="0.01" step="0.01" required />
        </label>

        <p v-if="sucesso" class="sucesso">{{ sucesso }}</p>
        <p v-if="erro" class="erro">{{ erro }}</p>
        <ul v-if="detalhes.length" class="erro">
            <li v-for="d in detalhes" :key="d">{{ d }}</li>
        </ul>

        <button type="submit" :disabled="enviando">
            {{ enviando ? 'Registrando...' : 'Registrar' }}
        </button>
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

.erro {
    color: #b91c1c;
}

.sucesso {
    color: #15803d;
}
</style>