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
        path:"base/",
        children:[
          {
            path:'station',
            component: () => import('../views/main/base/station.vue')
          },
          {
            path:'train',
            component: () => import('../views/main/base/train.vue')
          },
          {
            path:'train-station',
            component: () => import('../views/main/base/train-station.vue')
          },
          {
            path:'train-carriage',
            component: () => import('../views/main/base/train-carriage.vue')
          },
          {
            path:'train-seat',
            component: () => import('../views/main/base/train-seat.vue')
          },
        ]
      },
      {
        path:"business/",
        children:[
          {
            path:'daily-train',
            component: () => import('../views/main/business/daily-train.vue')
          },
          {
            path:'daily-train-station',
            component: () => import('../views/main/business/daily-train-station.vue')
          },
        ]
      },
      {
        path:'batch/',
        children: [
          {
            path:'job',
            component: () => import('../views/main/batch/job.vue')
          },
        ]
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