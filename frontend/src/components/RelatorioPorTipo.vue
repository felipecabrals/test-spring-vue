<script setup lang="ts">
import { ref, watch } from 'vue'
import { ApiException } from '@/api/http'
import { relatoriosApi } from '@/api/relatorios'
import { TIPOS_PRODUTO } from '@/constants/tipos'
import type { ProdutoPorTipo, TipoProduto } from '@/types/api'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Label } from '@/components/ui/label'
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'

const tipo = ref<TipoProduto>('ELETRONICO')
const itens = ref<ProdutoPorTipo[]>([])
const erro = ref('')

async function carregar() {
    const solicitado = tipo.value
    try {
        const dados = await relatoriosApi.porTipo(solicitado)
        if (solicitado === tipo.value) {
            itens.value = dados
            erro.value = ''
        }
    } catch (e) {
        erro.value = e instanceof ApiException ? e.message : 'Erro ao carregar o relatório'
    }
}

watch(tipo, carregar, { immediate: true })
</script>

<template>
    <Card>
        <CardHeader>
            <CardTitle>Produtos por tipo</CardTitle>
        </CardHeader>
        <CardContent class="grid gap-4">
            <div class="grid max-w-60 gap-2">
                <Label>Tipo</Label>
                <Select v-model="tipo">
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

            <p v-if="erro" class="text-sm text-destructive">{{ erro }}</p>

            <Table v-if="itens.length">
                <TableHeader>
                    <TableRow>
                        <TableHead>Código</TableHead>
                        <TableHead>Descrição</TableHead>
                        <TableHead>Quantidade de saída</TableHead>
                        <TableHead>Quantidade disponível</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    <TableRow v-for="i in itens" :key="i.id">
                        <TableCell>{{ i.codigo }}</TableCell>
                        <TableCell>{{ i.descricao }}</TableCell>
                        <TableCell>{{ i.quantidadeSaida }}</TableCell>
                        <TableCell>{{ i.quantidadeDisponivel }}</TableCell>
                    </TableRow>
                </TableBody>
            </Table>
            <p v-else-if="!erro">Nenhum produto deste tipo.</p>
        </CardContent>
    </Card>
</template>
