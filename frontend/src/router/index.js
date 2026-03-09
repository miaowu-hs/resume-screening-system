import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'resumes',
        name: 'ResumeList',
        component: () => import('../views/ResumeList.vue'),
        meta: { title: '简历管理' }
      },
      {
        path: 'positions',
        name: 'PositionList',
        component: () => import('../views/PositionList.vue'),
        meta: { title: '岗位管理' }
      },
      {
        path: 'match',
        name: 'MatchResult',
        component: () => import('../views/MatchResult.vue'),
        meta: { title: '匹配结果' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
