import { Http } from '@/api/http.js';
import dialog from "/src/api/uniapp/dialog.js";
import util from '@/api/util';

export default {
    async genAliOssAccessInfo(onfun) {
        let ossServiceId = util.getDomainSetupAtt(1).ossServiceId;
        return await Http.callHttpFunction('/r/other/genAliOssAccessInfo',{ossServiceId:ossServiceId},onfun);
    },
    async genSignature(onfun) {
        let ossServiceId = util.getDomainSetupAtt(1).ossServiceId;
        return await Http.callHttpFunction('/r/other/genSignature',{ossServiceId:ossServiceId},onfun);
    },
    async sendSmsPublic(phone,accessCode,templateParam,onfun) {
        let smsServiceId = util.getDomainSetupAtt(1).ossServiceId;//因为sms与OSS在后台是同一个服务提供
        return await Http.callHttpFunction('/r/other/sendSmsPublic',{phone:phone,accessCode:accessCode,smsServiceId:smsServiceId},onfun);
    },
    async test(ds,onfun) {
        return await Http.callHttpFunction('/r/other/test',ds,onfun);
    }
}
