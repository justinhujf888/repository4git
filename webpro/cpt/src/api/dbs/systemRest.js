// import { Beans } from '@/api/dbs/beans.js';
import { Http } from '@/api/http.js';
import dialog from "/src/api/uniapp/dialog.js";
import { Config } from '@/api/config.js';

export default {
    pingShenFlow(ds, onfun) {
        Http.callHttpFunction('/r/system/pingShenFlow',ds,onfun);
    }
};
