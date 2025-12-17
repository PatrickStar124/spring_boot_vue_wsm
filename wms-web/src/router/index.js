import { createRouter, createWebHistory } from 'vue-router'
import MainIndex from '@/components/MainIndex.vue'
import LoginForm from '@/components/LoginForm.vue'
import HomePage from '@/components/HomePage.vue'
import ProductManage from '@/components/ProductManage.vue'
import CartManage from '@/components/CartManage.vue'
import UserManage from '@/components/UserManage.vue'
import OrderManage from '@/components/OrderManage.vue'

const routes = [
    {
        path: '/',
        component: MainIndex,
        redirect: '/Home',
        children: [
            {
                path: 'Home',
                name: 'Home',
                component: HomePage,
                meta: { title: '首页' }
            },
            {
                path: 'ProductManage',
                name: 'ProductManage',
                component: ProductManage,
                meta: { title: '商品管理' }
            },
            {
                path: 'CartManage',
                name: 'CartManage',
                component: CartManage,
                meta: { title: '购物车' }
            },
            {
                path: 'UserManage',
                name: 'UserManage',
                component: UserManage,
                meta: { title: '用户管理' }
            },
            {
                path: 'OrderManage',
                name: 'OrderManage',
                component: OrderManage,
                meta: { title: '订单管理' }
            }
        ]
    },
    {
        path: '/Login',
        name: 'Login',
        component: LoginForm,
        meta: { title: '登录' }
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/Home'
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 路由守卫 - 只设置页面标题，不做登录验证
router.beforeEach((to, from, next) => {
    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - 图书购物系统`
    }

    // 直接放行，不做登录验证
    next()
})

export default router