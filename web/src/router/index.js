import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  // {
  //   path: '/',
  //   name: 'home',
  //   component: HomeView
  // },
  // {
  //   path: '/about',
  //   name: 'about',
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  // },
  {
    path: '/login',
    name: 'login-view',
    component: () => import('../views/login.vue')
  },
  {
    path: '/',
    name: 'main-view',
    component: () => import('../views/main.vue'),
    // mata:{
    //   loginRequired:true
    // }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// //路由拦截器
// router.beforeEach((to,from,next)=>{
//   if(to.matched.some(function (item){
//     console.log("是否需要登录校验",item.meta.loginRequired || false);
//     return item.meta.loginRequired;
//   })){
//     const user =store.state.user;
//     console.log("页面登录校验开始：",user);
//     if(!user.token){
//       console.log("用户未登录或登录超时");
//       next("/login")
//     }else {
//       next();
//     }
//   }else {
//     next();
//   }
// });

export default router
