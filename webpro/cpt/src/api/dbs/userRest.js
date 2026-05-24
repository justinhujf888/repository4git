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
    async managerLogin(ds,onfun) {
        return await Http.callHttpFunction('/r/user/managerLogin',ds,onfun);
    },
    judgeLogin(ds,onfun) {
        Http.callHttpFunction('/r/user/judgeLogin',ds,onfun);
    },
    async queryRuleList(ds,onfun) {
        return await Http.callHttpFunction('/r/user/queryRuleList',ds,onfun);
    },
    async updateRule(ds,onfun) {
        return await Http.callHttpFunction('/r/user/updateRule',ds,onfun);
    },
    async deleteRule(ds,onfun) {
        return await Http.callHttpFunction('/r/user/deleteRule',ds,onfun);
    },
    async queryManagerList(ds,onfun) {
        return await Http.callHttpFunction('/r/user/queryManagerList',ds,onfun);
    },
    async updateManager(ds,onfun) {
        return await Http.callHttpFunction('/r/user/updateManager',ds,onfun);
    },
    async deleteManager(ds,onfun) {
        return await Http.callHttpFunction('/r/user/deleteManager',ds,onfun);
    }
};
