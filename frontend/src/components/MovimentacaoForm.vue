<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ApiException } from '@/api/http'
import { movimentosApi } from '@/api/movimentos'
import { TIPOS_MOVIMENTACAO } from '@/constants/tipos'
import type { Produto, TipoMovimentacao } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select'

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
    <form class="grid gap-4" @submit.prevent="registrar">
        <div class="grid gap-2">
            <Label>Produto</Label>
            <Select v-model="form.produtoId">
                <SelectTrigger class="w-full">
                    <SelectValue placeholder="Selecione..." />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem v-for="p in produtos" :key="p.id" :value="p.id">
                        {{ p.codigo }} - {{ p.descricao }} (saldo: {{ p.quantidadeEstoque }})
                    </SelectItem>
                </SelectContent>
            </Select>
        </div>

        <div class="grid gap-2">
            <Label>Tipo</Label>
            <Select v-model="form.tipo">
                <SelectTrigger class="w-full">
                    <SelectValue />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem v-for="t in TIPOS_MOVIMENTACAO" :key="t.value" :value="t.value">
                        {{ t.label }}
                    </SelectItem>
                </SelectContent>
            </Select>
        </div>

        <div class="grid gap-2">
            <Label for="quantidade">Quantidade</Label>
            <Input id="quantidade" v-model.number="form.quantidade" type="number" min="1" step="1" required />
        </div>

        <div v-if="ehSaida" class="grid gap-2">
            <Label for="valorVenda">Valor de venda (unitário)</Label>
            <Input id="valorVenda" v-model.number="form.valorVenda" type="number" min="0.01" step="0.01" required />
        </div>

        <p v-if="sucesso" class="text-sm text-green-600">{{ sucesso }}</p>
        <div v-if="erro" class="text-sm text-destructive">
            <p>{{ erro }}</p>
            <ul v-if="detalhes.length" class="list-disc pl-4">
                <li v-for="d in detalhes" :key="d">{{ d }}</li>
            </ul>
        </div>

        <Button type="submit" :disabled="enviando">
            {{ enviando ? 'Registrando...' : 'Registrar' }}
        </Button>
    </form>
</template>
