<script setup lang="ts">
import RelatorioLucro from '@/components/RelatorioLucro.vue'
import RelatorioPorTipo from '@/components/RelatorioPorTipo.vue'
import RelatorioMovimentacao from '@/components/RelatorioMovimentacao.vue'
</script>

<template>
    <section class="grid gap-8">
        <h2 class="text-2xl font-semibold tracking-tight">Relatórios</h2>
        <RelatorioMovimentacao />
        <RelatorioPorTipo />
        <RelatorioLucro />
    </section>
</template>
