import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/produtos' },
    {
      path: '/produtos',
      name: 'produtos',
      component: () => import('@/views/ProdutosView.vue'),
    },
    {
      path: '/movimentacoes',
      name: 'movimentacoes',
      component: () => import('@/views/MovimentacoesView.vue'),
    },
    {
      path: '/relatorios',
      name: 'relatorios',
      component: () => import('@/views/RelatoriosView.vue'),
    },
  ],
})

export default router
