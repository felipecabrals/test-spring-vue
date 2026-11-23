<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ApiException } from '@/api/http'
import { produtosApi } from '@/api/produtos'
import { TIPOS_PRODUTO } from '@/constants/tipos'
import type { Produto, ProdutoRequest } from '@/types/api'

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

const props = defineProps<{ produto: Produto | null }>()
const emit = defineEmits<{ salvo: []; cancelar: [] }>()

const vazio = (): ProdutoRequest => ({
    codigo: '',
    descricao: '',
    tipo: 'ELETRONICO',
    valorFornecedor: 0,
    quantidadeInicial: 0,
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
                    quantidadeInicial: 0,
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
    <form class="grid gap-4" @submit.prevent="salvar">
        <div class="grid gap-2">
            <Label for="codigo">Código</Label>
            <Input id="codigo" v-model="form.codigo" required />
        </div>

        <div class="grid gap-2">
            <Label for="descricao">Descrição</Label>
            <Input id="descricao" v-model="form.descricao" required />
        </div>

        <div class="grid gap-2">
            <Label>Tipo</Label>
            <Select v-model="form.tipo">
                <SelectTrigger class="w-full">
                    <SelectValue placeholder="Selecione o tipo" />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem v-for="t in TIPOS_PRODUTO" :key="t.value" :value="t.value">
                        {{ t.label }}
                    </SelectItem>
                </SelectContent>
            </Select>
        </div>

        <div class="grid gap-2">
            <Label for="valor">Valor no fornecedor</Label>
            <Input id="valor" type="number" step="0.01" min="0" :model-value="form.valorFornecedor"
                @update:model-value="(v) => (form.valorFornecedor = Number(v))" />
        </div>

        <div v-if="!produto" class="grid gap-2">
            <Label for="estoque">Estoque inicial</Label>
            <Input id="estoque" type="number" min="0" :model-value="form.quantidadeInicial"
                @update:model-value="(v) => (form.quantidadeInicial = Number(v))" />
        </div>

        <div v-if="erro" class="text-sm text-destructive">
            <p>{{ erro }}</p>
            <ul v-if="detalhes.length" class="list-disc pl-4">
                <li v-for="d in detalhes" :key="d">{{ d }}</li>
            </ul>
        </div>

        <div class="flex gap-2">
            <Button type="submit" :disabled="salvando">Salvar</Button>
            <Button type="button" variant="outline" @click="$emit('cancelar')">
                Cancelar
            </Button>
        </div>
    </form>
</template>