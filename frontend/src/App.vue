<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header class="topbar">
    <h1>Controle de Estoque</h1>
    <nav>
      <RouterLink to="/produtos">Produtos</RouterLink>
      <RouterLink to="/movimentacoes">Movimentações</RouterLink>
      <RouterLink to="/relatorios">Relatórios</RouterLink>
    </nav>
  </header>
  <main class="content">
    <RouterView />
  </main>
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