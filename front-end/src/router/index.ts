import { createRouter, createWebHistory } from 'vue-router'
import type { DefineComponent } from 'vue'

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
      component: () => import('../views/processos/ProcessoListView.vue') as Promise<{ default: DefineComponent }>
    },
    {
      path: '/processos/novo',
      name: 'NovoProcesso',
      component: () => import('../views/processos/ProcessoFormView.vue') as Promise<{ default: DefineComponent }>
    },
    {
      path: '/processos/:id',
      name: 'DetalhesProcesso',
      component: () => import('../views/processos/ProcessoDetailView.vue') as Promise<{ default: DefineComponent }>
    },
    {
      path: '/processos/:id/editar',
      name: 'EditarProcesso',
      component: () => import('../views/processos/ProcessoFormView.vue') as Promise<{ default: DefineComponent }>
    }
  ]
})

export default router