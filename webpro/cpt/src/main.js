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

import '@/assets/styles.scss';
import zhCN from 'primelocale/zh-CN.json';
import workRest from "@/api/dbs/workRest";
import dayjs from "dayjs";

const app = createApp(App);
app.directive('styleclass', StyleClass);
app.provide("domain",util.getDomainFromUrl(window.location));

let siteDatas = null;

const routerParams = ()=>{
    return JSON.parse(decodeURIComponent(router.currentRoute.value.params.param));
};

const getSiteDatas = async ()=>{
    if (siteDatas) {
        return siteDatas;
    } else {
        siteDatas = {cptInfo:await workRest.gainCache8MasterCompetitionInfo(util.getDomainFromUrl(window.location)),siteInfo:await workRest.gainCache8SiteInfo(util.getDomainFromUrl(window.location))};
        siteDatas.cptInfo.masterCompetitionInfo.tempMap.beginDate = dayjs(siteDatas.cptInfo.masterCompetitionInfo.beginDate).format("YYYY-MM-DD");
        siteDatas.cptInfo.masterCompetitionInfo.tempMap.endDate = dayjs(siteDatas.cptInfo.masterCompetitionInfo.endDate).format("YYYY-MM-DD");
        return siteDatas;
    }
};

app.config.globalProperties.$getSiteDatas = getSiteDatas;
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

app.mount('#app');
