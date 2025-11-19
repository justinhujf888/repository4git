// import { Beans } from '@/api/dbs/beans.js';
import { Http } from '@/api/http.js';
import dialog from "/src/api/uniapp/dialog.js";
import { Config } from '@/api/config.js';

export default {
    registBuyer(buyer, onfun) {
        Http.httpclient_json(
            '/r/user/registBuyer',
            'post',
            {
                buyer: buyer
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
    buyerLogin(userId,password, onfun) {
        Http.httpclient_json(
            '/r/user/buyerLogin',
            'post',
            {
                userId: userId,password:password
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
    resetBuyerPassword(userId,password, onfun) {
        Http.httpclient_json(
            '/r/user/resetBuyerPassword',
            'post',
            {
                userId: userId,password:password
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
    queryJudgeList(ds, onfun) {
        Http.callHttpFunction('/r/user/queryJudgeList',ds,onfun);
    },
    updateJudge(ds, onfun) {
        Http.callHttpFunction('/r/user/updateJudge',ds,onfun);
    },
    managerLogin(ds,onfun) {
        Http.callHttpFunction('/r/user/managerLogin',ds,onfun);
    }
};
