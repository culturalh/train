import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd, {notification} from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css'
import * as Icons from '@ant-design/icons-vue'
import axios from "axios";

// createApp(App).use(store).use(Antd).use(router).mount('#app');
const app = createApp(App);
app.use(store).use(Antd).use(router).mount('#app');
const icons = Icons;
for (const i in icons) {
    app.component(i, icons[i])
}

/**
 * axios拦截器
 */
axios.interceptors.request.use(config => {
    const _token = store.state.member.token;
    if (_token) {
        config.headers.token = _token;
        console.log("请求头headers增加token：",_token);
    }
    //成功之后的处理
    // 获取token
    console.log("请求参数：",config);
    return config;
}, error => {
    //失败之后的处理
    return Promise.reject(error);
});
axios.interceptors.response.use(res => {
    console.log("响应参数：",res);
    return res;
},error => {
    const  response = error.response;
    if(response.status === 401){
        console.log("未登录或者登录超时")
        store.commit("setMember",{});
        notification.error({description:"未登录或者登录超时"})
        router.push("/login");
    }
    return Promise.reject(error);
})
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log("环境",process.env.NODE_ENV);
console.log("服务端地址",process.env.VUE_APP_SERVER);