import { Http } from '@/api/http.js';
import dialog from "/src/api/uniapp/dialog.js";
import { Config } from '@/api/config.js';

export default {
    setupSiteCompetition(siteCompetition, onfun) {
        Http.httpclient_json(
            '/r/work/setupSiteCompetition',
            'post',
            {
                siteCompetition: siteCompetition
            },
            'json',
            (res) => {
                if (res.data.status == 'FA_ER') {
                    dialog.showApiErrorMsg();
                } else {
                    onfun(res.data);
                }
            },
            null,
            true
        );
    },
    qySiteCompetition(id, onfun) {
        Http.httpclient_json(
            '/r/work/qySiteCompetition',
            'post',
            {
                id: id
            },
            'json',
            (res) => {
                if (res.data.status == 'FA_ER') {
                    dialog.showApiErrorMsg();
                } else {
                    onfun(res.data);
                }
            },
            null,
            true
        );
    },
    qySiteWorkItemList(ds, onfun) {
        Http.httpclient_json(
            '/r/work/qySiteWorkItemList',
            'post',
            ds,
            'json',
            (res) => {
                if (res.data.status == 'FA_ER') {
                    dialog.showApiErrorMsg();
                } else {
                    onfun(res.data);
                }
            },
            null,
            true
        );
    },
    updateSiteWorkItem(ds, onfun) {
        Http.httpclient_json(
            '/r/work/updateSiteWorkItem',
            'post',
            ds,
            'json',
            (res) => {
                if (res.data.status == 'FA_ER') {
                    dialog.showApiErrorMsg();
                } else {
                    onfun(res.data);
                }
            },
            null,
            true
        );
    },
    deleteSiteWorkItem(ds, onfun) {
        Http.callHttpFunction('/r/work/deleteSiteWorkItem',ds,onfun);
    },
    qyOrgHumanList(ds,onfun) {
        Http.callHttpFunction('/r/work/qyOrgHumanList',ds,onfun);
    },
    updateOrgHuman(ds,onfun) {
        Http.callHttpFunction('/r/work/updateOrgHuman',ds,onfun);
    },
    qyMasterSiteCompetition(ds,onfun) {
        Http.callHttpFunction('/r/work/qyMasterSiteCompetition',ds,onfun);
    },
    updateMasterCompetition(ds,onfun) {
        Http.callHttpFunction('/r/work/updateMasterCompetition',ds,onfun);
    },
    updateMasterCompetitionDescription(ds,onfun) {
        Http.callHttpFunction('/r/work/updateMasterCompetitionDescription',ds,onfun);
    },
    updateMasterCompetitionSetupFields(ds,onfun) {
        Http.callHttpFunction('/r/work/updateMasterCompetitionSetupFields',ds,onfun);
    },
    qyCompetitionList(ds,onfun) {
        Http.callHttpFunction('/r/work/qyCompetitionList',ds,onfun);
    },
    updateCompetition(ds,onfun) {
        Http.callHttpFunction('/r/work/updateCompetition',ds,onfun);
    },
    updateSiteCompetitionSetupFields(ds,onfun) {
        Http.callHttpFunction('/r/work/updateSiteCompetitionSetupFields',ds,onfun);
    },
    updateCompetitionList(ds,onfun) {
        Http.callHttpFunction('/r/work/updateCompetitionList',ds,onfun);
    },
    deleteCompetition(ds,onfun) {
        Http.callHttpFunction('/r/work/deleteCompetition',ds,onfun);
    },
    deleteGuiGe(ds,onfun) {
        Http.callHttpFunction('/r/work/deleteGuiGe',ds,onfun);
    },
    qyWorks(ds,onfun) {
        Http.callHttpFunction('/r/work/qyWorks',ds,onfun);
    },
    buildCacheCpt(ds,onfun) {
        Http.callHttpFunction('/r/work/buildCacheCpt',ds,onfun);
    },
    async gainCache8MasterCompetitionInfo() {
        return Http.fetchJson(`${Config.siteJson}/masterCompetition.json`);
        // return fetch(`${Config.siteJson}/masterCompetition.json`)
        //     .then(response => response.json())
        //     .then(data => {return data});
    }
}
