<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ApiException } from '@/api/http'
import { relatoriosApi } from '@/api/relatorios'
import type { ResumoMovimentacao } from '@/types/api'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'

const inicio = ref('')
const fim = ref('')
const itens = ref<ResumoMovimentacao[]>([])
const erro = ref('')

const totalEntrada = computed(() => itens.value.reduce((s, i) => s + i.totalEntrada, 0))
const totalSaida = computed(() => itens.value.reduce((s, i) => s + i.totalSaida, 0))
const saldoTotal = computed(() => itens.value.reduce((s, i) => s + i.saldoAtual, 0))
const maior = computed(() =>
    Math.max(1, ...itens.value.flatMap((i) => [i.totalEntrada, i.totalSaida])),
)
const largura = (valor: number) => `${(valor / maior.value) * 100}%`

async function carregar() {
    try {
        itens.value = await relatoriosApi.movimentacao(inicio.value || undefined, fim.value || undefined)
        erro.value = ''
    } catch (e) {
        erro.value = e instanceof ApiException ? e.message : 'Erro ao carregar o relatório'
    }
}

function limpar() {
    inicio.value = ''
    fim.value = ''
}

watch([inicio, fim], carregar, { immediate: true })
</script>

<template>
    <Card>
        <CardHeader>
            <CardTitle>Entradas e saídas</CardTitle>
        </CardHeader>
        <CardContent class="grid gap-4">
            <div class="flex flex-wrap items-end gap-3">
                <div class="grid gap-2">
                    <Label for="inicio">De</Label>
                    <Input id="inicio" v-model="inicio" type="date" class="w-auto" />
                </div>
                <div class="grid gap-2">
                    <Label for="fim">Até</Label>
                    <Input id="fim" v-model="fim" type="date" class="w-auto" />
                </div>
                <Button type="button" variant="outline" @click="limpar">Limpar período</Button>
            </div>

            <p v-if="erro" class="text-sm text-destructive">{{ erro }}</p>

            <div class="grid gap-3 sm:grid-cols-3">
                <Card>
                    <CardContent class="grid gap-1 py-4">
                        <span class="text-sm text-muted-foreground">Entrou</span>
                        <strong class="text-2xl text-green-600">{{ totalEntrada }}</strong>
                    </CardContent>
                </Card>
                <Card>
                    <CardContent class="grid gap-1 py-4">
                        <span class="text-sm text-muted-foreground">Saiu</span>
                        <strong class="text-2xl text-destructive">{{ totalSaida }}</strong>
                    </CardContent>
                </Card>
                <Card>
                    <CardContent class="grid gap-1 py-4">
                        <span class="text-sm text-muted-foreground">Saldo em estoque hoje</span>
                        <strong class="text-2xl">{{ saldoTotal }}</strong>
                    </CardContent>
                </Card>
            </div>

            <Table v-if="itens.length">
                <TableHeader>
                    <TableRow>
                        <TableHead>Produto</TableHead>
                        <TableHead>Entrou</TableHead>
                        <TableHead>Saiu</TableHead>
                        <TableHead>Saldo atual</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    <TableRow v-for="i in itens" :key="i.id">
                        <TableCell>{{ i.codigo }} - {{ i.descricao }}</TableCell>
                        <TableCell>
                            <div class="flex items-center gap-2">
                                <div class="h-2.5 flex-1 overflow-hidden rounded-full bg-muted">
                                    <div class="h-full bg-green-600" :style="{ width: largura(i.totalEntrada) }"></div>
                                </div>
                                <span>{{ i.totalEntrada }}</span>
                            </div>
                        </TableCell>
                        <TableCell>
                            <div class="flex items-center gap-2">
                                <div class="h-2.5 flex-1 overflow-hidden rounded-full bg-muted">
                                    <div class="h-full bg-destructive" :style="{ width: largura(i.totalSaida) }"></div>
                                </div>
                                <span>{{ i.totalSaida }}</span>
                            </div>
                        </TableCell>
                        <TableCell>{{ i.saldoAtual }}</TableCell>
                    </TableRow>
                </TableBody>
            </Table>
            <p v-else-if="!erro">Nenhum produto cadastrado.</p>

            <p class="text-sm text-muted-foreground">
                O período filtra só as entradas e saídas; o saldo atual é sempre o estoque de hoje.
            </p>
        </CardContent>
    </Card>
</template>
