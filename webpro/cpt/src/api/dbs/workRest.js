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
    }
}
