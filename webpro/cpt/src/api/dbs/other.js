import { Http } from '@/api/http.js';
import dialog from "/src/api/uniapp/dialog.js";

export default {
    genAliOssAccessInfo(onfun) {
        Http.httpclient_json(
            '/r/other/genAliOssAccessInfo',
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
    },
    genSignature(onfun) {
        Http.httpclient_json(
            '/r/other/genSignature',
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
