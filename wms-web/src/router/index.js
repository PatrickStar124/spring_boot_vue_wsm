import { createRouter, createWebHistory } from 'vue-router'
// 无需再导入MainIndex
import LoginForm from '@/components/LoginForm.vue'
import HomePage from '@/components/HomePage.vue'
import ProductManage from '@/components/ProductManage.vue'
import CartManage from '@/components/CartManage.vue'
import UserManage from '@/components/UserManage.vue'
import OrderManage from '@/components/OrderManage.vue'

const routes = [
    // 根路由直接重定向到首页，不渲染MainIndex
    {
        path: '/',
        redirect: '/home'
    },
    // 所有页面设为顶级路由，渲染到App.vue的MainIndex里的router-view
    {
        path: '/home',
        name: 'Home',
        component: HomePage,
        meta: { title: '首页' }
    },
    {
        path: '/ProductManage',
        name: 'ProductManage',
        component: ProductManage,
        meta: { title: '商品管理' }
    },
    {
        path: '/CartManage',
        name: 'CartManage',
        component: CartManage,
        meta: { title: '购物车' }
    },
    {
        path: '/UserManage',
        name: 'UserManage',
        component: UserManage,
        meta: { title: '用户管理' }
    },
    {
        path: '/OrderManage',
        name: 'OrderManage',
        component: OrderManage,
        meta: { title: '订单管理' }
    },
    // 登录页单独处理（不显示布局）
    {
        path: '/Login',
        name: 'Login',
        component: LoginForm,
        meta: { title: '登录', noLayout: true } // 标记：无需布局
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/home'
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 路由守卫：设置标题 + 保留跳过登录的逻辑
router.beforeEach((to, from, next) => {
    if (to.meta.title) {
        document.title = `${to.meta.title} - 图书购物系统`
    }
    // 直接放行（前端测试用，后续对接后端再恢复登录验证）
    next()
})

export default router