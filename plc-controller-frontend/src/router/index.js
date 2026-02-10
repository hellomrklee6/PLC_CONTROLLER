import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory('/plcweb/'),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/monitor',
      name: 'monitor',
      component: () => import('../views/MonitorView.vue')
    },
    {
      path: '/devices',
      name: 'devices',
      component: () => import('../views/DevicesView.vue')
    },
    {
      path: '/addresses',
      name: 'addresses',
      component: () => import('../views/AddressesView.vue')
    },
    {
      path: '/button-schemes',
      name: 'buttonSchemes',
      component: () => import('../views/ButtonSchemesView.vue')
    },
    {
      path: '/data-changes',
      name: 'dataChanges',
      component: () => import('../views/DataChangesView.vue')
    }
  ]
})

export default router
