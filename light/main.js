import App from './App'
import 'uno.css';
/* 全局挂载请求库 */
// import lodash from "lodash";
import GraceRequest from '@/Grace6/js/request.js'
uni.gRequest = GraceRequest;

// #ifndef VUE3
import Vue from 'vue'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App);
  // app.config.globalProperties.lodash= lodash;

  return {
    app
  }
}
// #endif