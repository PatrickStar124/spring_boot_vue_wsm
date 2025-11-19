import { createRouter, createWebHistory } from 'vue-router';
import LoginForm from '../components/LoginForm.vue';
import MainIndex from '../components/MainIndex.vue';

// 必须是数组！
const routes = [
    {
        path: '/',
        name: 'login',
        component: LoginForm
    },
    {
        path: '/Index',
        name: 'mainIndex',
        component: MainIndex
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes // 这里必须传入数组
});

export default router;