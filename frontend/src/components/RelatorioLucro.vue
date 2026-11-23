<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ApiException } from '@/api/http'
import { relatoriosApi } from '@/api/relatorios'
import type { LucroProduto } from '@/types/api'
import { formatarMoeda } from '@/utils/formato'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
    Table,
    TableBody,
    TableCell,
    TableFooter,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'
import { cn } from '@/lib/utils'

const itens = ref<LucroProduto[]>([])
const erro = ref('')

const totalSaida = computed(() => itens.value.reduce((soma, i) => soma + i.quantidadeTotalSaida, 0))
const totalLucro = computed(
    () => itens.value.reduce((soma, i) => soma + Math.round(i.lucroTotal * 100), 0) / 100,
)

onMounted(async () => {
    try {
        itens.value = await relatoriosApi.lucro()
    } catch (e) {
        erro.value = e instanceof ApiException ? e.message : 'Erro ao carregar o relatório'
    }
})
</script>

<template>
    <Card>
        <CardHeader>
            <CardTitle>Lucro por produto</CardTitle>
        </CardHeader>
        <CardContent>
            <p v-if="erro" class="text-sm text-destructive">{{ erro }}</p>

            <Table v-if="itens.length">
                <TableHeader>
                    <TableRow>
                        <TableHead>Código</TableHead>
                        <TableHead>Descrição</TableHead>
                        <TableHead>Total de saída</TableHead>
                        <TableHead>Lucro total</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    <TableRow v-for="i in itens" :key="i.id">
                        <TableCell>{{ i.codigo }}</TableCell>
                        <TableCell>{{ i.descricao }}</TableCell>
                        <TableCell>{{ i.quantidadeTotalSaida }}</TableCell>
                        <TableCell :class="cn(i.lucroTotal < 0 && 'text-destructive')">
                            {{ formatarMoeda(i.lucroTotal) }}
                        </TableCell>
                    </TableRow>
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TableHead colspan="2">Total</TableHead>
                        <TableHead>{{ totalSaida }}</TableHead>
                        <TableHead :class="cn(totalLucro < 0 && 'text-destructive')">
                            {{ formatarMoeda(totalLucro) }}
                        </TableHead>
                    </TableRow>
                </TableFooter>
            </Table>
            <p v-else-if="!erro">Nenhum produto cadastrado.</p>
        </CardContent>
    </Card>
</template>
