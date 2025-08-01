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

};
