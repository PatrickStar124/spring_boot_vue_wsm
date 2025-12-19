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
        component: () => import('@/views/HomePage.vue'),
        meta: { title: '首页' }
    },
    // 图书列表页
    {
        path: '/booklist',
        name: 'BookList',
        component: () => import('@/views/BookList.vue'),
        meta: { title: '全部图书' }
    },
    // 新增书籍页面（仅管理员可访问）
    {
        path: '/bookadd',
        name: 'BookAdd',
        component: () => import('@/views/BookAdd.vue'), // 新增：导入添加书籍页面
        meta: {
            title: '新增书籍',
            requiresAuth: true, // 需要登录
            isAdmin: true // 需要管理员权限
        }
    },
    // 购物车页面
    {
        path: '/cart',
        name: 'Cart',
        component: () => import('@/views/CartPage.vue'),
        meta: { title: '购物车', requiresAuth: true } // 购物车也需要登录
    },
    // 用户中心页面
    {
        path: '/usercenter',
        name: 'UserCenter',
        component: () => import('@/views/UserCenter.vue'),
        meta: { title: '个人中心', requiresAuth: true } // 个人中心需要登录
    },
    // 登录页面
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/LoginPage.vue'),
        meta: { title: '登录' }
    },
    // 注册页面
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/RegisterPage.vue'),
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
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
    // 1. 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - 图书购物车系统`
    }

    // 2. 鉴权逻辑
    // 获取本地存储的用户信息
    const userStr = localStorage.getItem('user')
    let user = null
    if (userStr) {
        try {
            user = JSON.parse(userStr)
        } catch (e) {
            console.error('解析用户信息失败:', e)
            localStorage.removeItem('user') // 解析失败清空无效数据
        }
    }

    // 3. 判断是否需要登录
    if (to.meta.requiresAuth) {
        // 未登录：跳转到登录页
        if (!user) {
            alert('请先登录后再操作！')
            next({ path: '/login', query: { redirect: to.fullPath } }) // 登录后跳回原页面
            return
        }

        // 已登录，但需要管理员权限
        if (to.meta.isAdmin) {
            // 假设用户对象有role字段，'admin'为管理员（根据你后端实际字段调整）
            if (user.role !== 'admin') {
                alert('无管理员权限，无法访问该页面！')
                next({ path: '/booklist' }) // 跳回图书列表
                return
            }
        }
    }

    // 4. 所有验证通过，放行
    next()
})

export default router