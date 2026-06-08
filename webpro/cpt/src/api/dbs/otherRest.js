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
        return await Http.callHttpFunction('/r/other/sendSmsPublic',{phone:phone,accessCode:accessCode},onfun);
    },
    async test(ds,onfun) {
        return await Http.callHttpFunction('/r/other/test',ds,onfun);
    }
}
