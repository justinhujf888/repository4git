import {getCurrentInstance}  from "vue";
import workRest from '@/api/dbs/workRest';
import util from '@/api/util';
import dayjs from 'dayjs';

export default {
    async siteDatas() {
        let siteDatas = null;
        siteDatas = {cptInfo:await workRest.gainCache8MasterCompetitionInfo(util.getDomainFromUrl(window.location)),siteInfo:await workRest.gainCache8SiteInfo(util.getDomainFromUrl(window.location))};
        siteDatas.cptInfo.masterCompetitionInfo.tempMap.beginDate = dayjs(siteDatas.cptInfo.masterCompetitionInfo.beginDate).format("YYYY-MM-DD");
        siteDatas.cptInfo.masterCompetitionInfo.tempMap.endDate = dayjs(siteDatas.cptInfo.masterCompetitionInfo.endDate).format("YYYY-MM-DD");
        return siteDatas;
    },
    async pageSetupDatas(key) {
        return workRest.gainPageSetup(util.getDomainFromUrl(window.location),key);
    },
    getRouteInfo() {
        const instance = getCurrentInstance();
        if (instance) {
            return instance.proxy.$route;
        }
        return {};
    }
}
