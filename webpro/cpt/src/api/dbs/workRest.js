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
}
