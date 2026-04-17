import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import '@/assets/css/style.css';

import zhCn from 'element-plus/es/locale/lang/zh-cn';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import '@/assets/icon/iconfont.js'; // 图标
import '@/assets/icon/iconfont.css';

import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

const app = createApp(App);
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
app.use(pinia);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.use(router).use(ElementPlus, { locale: zhCn, size: 'default' }).mount('#app');
