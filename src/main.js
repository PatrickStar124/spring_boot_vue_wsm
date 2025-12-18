import Vue from 'vue'
import App from '../wms-web/src/App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css';
import axios from 'axios'

Vue.prototype.$axios = axios
axios.defaults.baseURL = 'http://localhost:8090'
axios.defaults.headers.post['Content-Type'] = 'application/json;charset=utf-8'
axios.defaults.withCredentials = true

Vue.config.productionTip = false
Vue.use(ElementUI);

new Vue({
    render: h => h(App),
}).$mount('#app')