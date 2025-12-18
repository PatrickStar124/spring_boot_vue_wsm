import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/HomePage.vue')
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/LoginPage.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/RegisterPage.vue')
    },
    {
        path: '/cart',
        name: 'Cart',
        component: () => import('@/views/CartPage.vue')
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 【核心修改】添加路由前置守卫，强制放行所有页面（跳过登录校验）
// 无论是否登录，访问任意页面都不会跳转到登录页
router.beforeEach((to, from, next) => {
    // 直接放行所有路由跳转
    next()
})

export default router