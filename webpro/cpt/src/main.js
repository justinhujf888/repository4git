import {createApp, ref} from 'vue';
import App from './App.vue';
import router from './router';
import {Config} from "@/api/config";
import Aura from '@primeuix/themes/aura';
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import ToastService from 'primevue/toastservice';
import DialogService from 'primevue/dialogservice';
import StyleClass from 'primevue/styleclass';
import util from "@/api/util";
import VueKonva from 'vue-konva';

import '@/assets/styles.scss';
import zhCN from 'primelocale/zh-CN.json';
import workRest from "@/api/dbs/workRest";
import dayjs from "dayjs";

const app = createApp(App);
app.directive('styleclass', StyleClass);
app.provide("domain",util.getDomainFromUrl(window.location));

const routerParams = ()=>{
    return JSON.parse(decodeURIComponent(router.currentRoute.value.params.param));
};
async function loadSkin(skinName) {
    console.log("skin",skinName);
    let loadCss = Config.skinStyleList[skinName];
    await loadCss();
    // 注意：Vite 会把导入的 CSS 自动注入到 <head> 中
}
await loadSkin("ct0");

app.config.globalProperties.$router = router;
app.config.globalProperties.$routerParams = routerParams;
app.config.globalProperties.$config = Config;

app.use(router);
app.use(PrimeVue, {
    locale: zhCN['zh-CN'],
    pt:{

    },
    theme: {
        preset: Aura,
        options: {
            darkModeSelector: '.app-dark'//false || 'none'
        }
    }
});
app.use(ToastService);
app.use(ConfirmationService);
app.use(DialogService);
app.use(VueKonva);

app.mount('#app');
