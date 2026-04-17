import { createRouter, createWebHistory } from 'vue-router';
import Layout from '@/layout/Layout.vue';

const routes = [
    {
        path: '/',
        name: 'Layout',
        redirect: 'user',
        component: Layout,
        children: [
            {
                path: 'user',
                name: 'user',
                component: () => import('@/views/User.vue'),
            },
            {
                path: 'book',
                name: 'book',
                component: () => import('@/views/Book.vue'),
            },
            {
                path: 'person',
                name: 'Person',
                component: () => import('@/views/Person.vue'),
            },
            {
                path: 'password',
                name: 'Password',
                component: () => import('@/views/Password.vue'),
            },
            {
                path: 'lendrecord',
                name: 'LendRecord',
                component: () => import('@/views/LendRecord.vue'),
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue'),
            },
            {
                path: 'bookwithuser',
                name: 'BookWithUser',
                component: () => import('@/views/BookWithUser.vue'),
            },
            {
                path: 'category',
                name: 'Category',
                component: () => import('@/views/Category.vue'),
            },
            {
                path: 'syslog',
                name: 'SysLog',
                component: () => import('@/views/SysLog.vue'),
            },
        ],
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
    },
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
});

export default router;
