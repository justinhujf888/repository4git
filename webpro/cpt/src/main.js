import { createApp } from 'vue';
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

import '@/assets/styles.scss';
import zhCN from 'primelocale/zh-CN.json';

const app = createApp(App);
app.directive('styleclass', StyleClass);
app.provide("domain",util.getDomainFromUrl(window.location));

const routerParams = ()=>{
    return JSON.parse(decodeURIComponent(router.currentRoute.value.params.param));
};
app.config.globalProperties.$router = router;
app.config.globalProperties.$routerParams = routerParams;
app.config.globalProperties.$config = Config;

app.use(router);
app.use(PrimeVue, {
    locale: zhCN['zh-CN'],
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

app.mount('#app');
