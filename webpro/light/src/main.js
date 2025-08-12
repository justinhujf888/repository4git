import App from './App'
import 'uno.css';
/* 全局挂载请求库 */
import lodash from "lodash";
import dayjs from "dayjs";
import cmdjson from "@/api/datas/cmd.json";

import wx from "@/api/uniapp/wx.js";
// wx.getUserOpenId();
// import GraceRequest from '@/Grace6/js/request.js'
// uni.gRequest = GraceRequest;
uni.dayjs = dayjs;
const prePage = ()=>{
	let pages = getCurrentPages();
	// let currPage = pages[pages.length - 1]; //当前页面
	let prevPage = pages[pages.length - 2]; //上一个页面
	// #ifdef H5
	return prevPage;
	// #endif
	return prevPage.$vm;
};
const getDeviceImg = (deviceName)=>{
    let imgElt = lodash.find(cmdjson.deviceImage,(o)=>{return o.name==deviceName});
    if (imgElt) {
        return imgElt.path;
    } else {
        return null;
    }
};
uni.prePage = prePage;
// uni.getDeviceImg = getDeviceImg;

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
  app.config.globalProperties.lodash= lodash;
  app.config.globalProperties.$prePage = prePage;
  app.config.globalProperties.dayjs = dayjs;
  // app.config.globalProperties.getDeviceImg = getDeviceImg;
  return {
    app
  }
}
// #endif