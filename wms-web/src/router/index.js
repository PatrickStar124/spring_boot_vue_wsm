import { createRouter, createWebHistory } from 'vue-router'

// 定义路由表，所有组件从 views/ 目录懒加载
const routes = [
    // 默认根路径重定向到首页
    {
        path: '/',
        redirect: '/home'
    },
    // 首页
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/HomePage.vue'), // 对应你的  HomePage.vue
        meta: { title: '首页' }
    },
    // 图书列表页
    {
        path: '/booklist',
        name: 'BookList',
        component: () => import('@/views/BookList.vue'), // 对应你的 BookList.vue
        meta: { title: '全部图书' }
    },
    // 购物车页面
    {
        path: '/cart',
        name: 'Cart',
        component: () => import('@/views/CartPage.vue'), // 对应你的 CartPage.vue
        meta: { title: '购物车' }
    },
    // 用户中心页面
    {
        path: '/usercenter',
        name: 'UserCenter',
        component: () => import('@/views/UserCenter.vue'), // 对应你的 UserCenter.vue
        meta: { title: '个人中心' }
    },
    // 登录页面 - 作为独立页面，不嵌入布局
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/LoginPage.vue'), // 对应你的 LoginPage.vue
        meta: {
            title: '登录',
            // 你可以在这里添加 noLayout: true 等元信息，虽然App.vue已简化
        }
    },
    // 注册页面
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/RegisterPage.vue'), // 对应你的 RegisterPage.vue
        meta: { title: '注册' }
    },
    // 404 页面处理：重定向到首页
    {
        path: '/:pathMatch(.*)*',
        redirect: '/home'
    }
]

// 创建路由实例
const router = createRouter({
    history: createWebHistory(process.env.BASE_URL), // 使用HTML5历史模式
    routes
})

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - 图书购物车系统`
    }

    // 重要：为了让你能无障碍测试所有页面，这里暂时直接放行所有路由
    // 后续接入后端登录验证后，可以在此处添加鉴权逻辑
    // 例如：检查 localStorage 中的 token 或 user 信息
    // const isAuthenticated = localStorage.getItem('user');
    // if (to.meta.requiresAuth && !isAuthenticated) {
    //   next('/login');
    // } else {
    //   next();
    // }

    next(); // 现在直接放行
})

export default router