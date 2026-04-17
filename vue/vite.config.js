import { fileURLToPath, URL } from 'node:url';

import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import vueDevTools from 'vite-plugin-vue-devtools';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
    // 加载当前模式的环境变量
    const env = loadEnv(mode, process.cwd());

    return {
        plugins: [vue(), vueJsx(), vueDevTools()],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('./src', import.meta.url)),
            },
        },
        server: {
            port: 9876, // 设置本地启动端口
            proxy: {
                // 拦截以 /api 开头的请求
                '/api': {
                    target: 'http://localhost:8088', // 目标后端接口地址
                    changeOrigin: true, // 允许跨域
                    rewrite: (path) => path.replace(/^\/api/, '/api'),
                },
            },
        },
    };
});
