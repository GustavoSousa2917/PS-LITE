import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/HomeView'
    },
    {
      path: '/processos',
      name: 'Processos',
      component: () => import('../views/processos/ProcessoListView.vue')
    },
    {
      path: '/processos/novo',
      name: 'NovoProcesso',
      component: () => import('../views/processos/ProcessoFormView.vue')
    },
    {
      path: '/processos/:id',
      name: 'DetalhesProcesso',
      component: () => import('../views/processos/ProcessoDetailView.vue')
    },
    {
      path: '/processos/:id/editar',
      name: 'EditarProcesso',
      component: () => import('../views/processos/ProcessoFormView.vue')
    }
  ]
})

export default router