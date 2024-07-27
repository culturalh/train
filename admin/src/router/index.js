import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';
import {notification} from "ant-design-vue";


const routes = [

  {
    path: '/',
    name: 'main-view',
    component: () => import('../views/main.vue'),

    children:[
      {
        path:'welcome',
        name:'welcome-view',
        component: () => import('../views/main/welcome.vue')
      },
      {
        path:'about',
        component: () => import('../views/main/about.vue')
      },
      {
        path:'station',
        component: () => import('../views/main/station.vue')
      },
      // {
      //   path: 'ticket',
      //   component: () => import('../views/main/ticket.vue')
      // },
      // {
      //   path: 'order',
      //   component: () => import('../views/main/order.vue')
      // },
      // {
      //   path: 'my-ticket',
      //   component: () => import('../views/main/my-ticket.vue')
      // },
      // {
      //   path: 'seat',
      //   component: () => import('../views/main/seat.vue')
      // },
      // {
      //   path: 'admin',
      //   component: () => import('../views/main/admin.vue')
      // }
    ]
  },
  {
    path: '',
    redirect: '/welcome'
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})


export default router