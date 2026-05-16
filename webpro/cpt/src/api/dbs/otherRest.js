import { Http } from '@/api/http.js';
import dialog from "/src/api/uniapp/dialog.js";

export default {
    async genAliOssAccessInfo(onfun) {
        return await Http.callHttpFunction('/r/other/genAliOssAccessInfo',{},onfun);
    },
    async genSignature(onfun) {
        return await Http.callHttpFunction('/r/other/genSignature',{},onfun);
    },
    async sendSmsPublic(phone,accessCode,templateParam,onfun) {
        return await Http.callHttpFunction('/r/other/sendSmsPublic',{},onfun);
    },
    test(onfun) {
        Http.httpclient_json(
            '/r/other/test',
            'post',
            {},
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
