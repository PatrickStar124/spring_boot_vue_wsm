import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/global.css'
import router from './router'
import store from './store'  // 确保这行存在
import axios from "axios"
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

app.config.globalProperties.$axios = axios
app.config.globalProperties.$httpUrl = 'http://localhost:8090'

app.use(ElementPlus, { size: 'small' })
app.use(router)
app.use(store)  // 确保这行存在

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')