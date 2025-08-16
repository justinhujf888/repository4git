import App from './App'
import 'uno.css';
/* 全局挂载请求库 */
import lodash from "lodash";
import dayjs from "dayjs";
import cmdjson from "@/api/datas/cmd.json";
import messages from './local/index';


import vconsole from "vconsole";

// import wx from "@/api/uniapp/wx.js";
// wx.getUserOpenId();
// import GraceRequest from '@/Grace6/js/request.js'
// uni.gRequest = GraceRequest;

// new vconsole();

// #ifdef H5
let i18nConfig = {
    locale: uni.getLocale(),
    messages
}
// #endif
// #ifdef MP
let i18nConfig = {
    locale: "zh-Hans",
    messages
}
// #endif


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
import { createSSRApp } from 'vue';
import { createI18n } from 'vue-i18n';
const i18n = createI18n(i18nConfig)

const changeLang = (lang)=>{
    // console.log(lang);
    i18n.locale = lang;
    uni.setLocale(lang);
};

export function createApp() {
  const app = createSSRApp(App);

    // #ifdef H5
    app.use(i18n);
// #endif
// #ifdef MP
    app.use(i18n);
// #endif

  app.config.globalProperties.lodash= lodash;
  app.config.globalProperties.$prePage = prePage;
  app.config.globalProperties.dayjs = dayjs;
  app.config.globalProperties.$i18n = i18n;
  app.config.globalProperties.$changeLang = changeLang;
  // app.config.globalProperties.getDeviceImg = getDeviceImg;
  return {
    app
  }
}
// #endif