<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
const links = [
  { to: '/produtos', label: 'Produtos' },
  { to: '/movimentacoes', label: 'Movimentações' },
  { to: '/relatorios', label: 'Relatórios' },
]
</script>

<template>
  <div class="min-h-screen bg-muted/40">
    <header class="border-b bg-background">
      <div class="mx-auto flex h-14 max-w-5xl items-center gap-6 px-4">
        <span class="font-semibold">Controle de Estoque</span>
        <nav class="flex gap-1">
          <RouterLink v-for="l in links" :key="l.to" :to="l.to"
            class="rounded-md px-3 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground"
            active-class="!bg-accent !text-accent-foreground font-medium">
            {{ l.label }}
          </RouterLink>
        </nav>
      </div>
    </header>

    <main class="mx-auto max-w-5xl px-4 py-6">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  background: #1f2937;
  color: #fff;
}

.topbar h1 {
  font-size: 1.1rem;
  margin: 0;
}

nav {
  display: flex;
  gap: 16px;
}

nav a {
  color: #d1d5db;
  text-decoration: none;
}

nav a.router-link-active {
  color: #fff;
  font-weight: 600;
}

.content {
  max-width: 960px;
  margin: 24px auto;
  padding: 0 24px;
}
</style>