import App from './App'
import 'uno.css';
/* 全局挂载请求库 */
// import lodash from "lodash";
import GraceRequest from '@/Grace6/js/request.js'
uni.gRequest = GraceRequest;
const prePage = ()=>{
	let pages = getCurrentPages();
	// let currPage = pages[pages.length - 1]; //当前页面
	let prevPage = pages[pages.length - 2]; //上一个页面
	// #ifdef H5
	return prevPage;
	// #endif
	return prevPage.$vm;
};
uni.prePage = prePage;

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
  app.config.globalProperties.prePage = prePage;

  return {
    app
  }
}
// #endif