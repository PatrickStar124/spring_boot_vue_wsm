// main.js（修复后）
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 直接导入 router/index.js 中创建好的路由实例（无需重复创建）
import router from './router'

// axios 配置
import axios from "axios";

// 创建 app 实例1
const app = createApp(App)

// 配置全局变量
app.config.globalProperties.$axios = axios
app.config.globalProperties.$httpUrl = 'http://localhost:8090'

// 安装插件（直接使用导入的路由实例）
app.use(ElementPlus, { size: 'small' })
app.use(router) // 这里的 router 是从 ./router 导入的现成实例

// 挂载应用
app.mount('#app')