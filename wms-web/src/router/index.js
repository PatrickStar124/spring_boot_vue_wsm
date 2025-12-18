// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginForm from '../components/LoginForm.vue'
// import MainIndex from '../components/MainIndex.vue'
import HomePage from '../components/HomePage.vue'
import AppMain from '../components/AppMain.vue'

const routes = [
    {
        path: '/',
        name: 'Login',
        component: LoginForm
    },
    // {
    //     path: '/Index',
    //     name: 'MainIndex',
    //     component: MainIndex,
    //     redirect: '/Home',
    //     children: [
    //         {
    //             path: '/Home',
    //             name: 'HomePage',
    //             component: HomePage
    //         },
    //         {
    //             path: '/AppMain',
    //             name: 'AppMain',
    //             component: AppMain
    //         }
    //     ]
    // }
    {
        path: '/Home',
        name: 'HomePage',
        component: HomePage
    },
    {
        path: '/AppMain',
        name: 'AppMain',
        component: AppMain
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router