import {getCurrentInstance}  from "vue";

export default {
    async siteDatas() {
        const instance = getCurrentInstance();
        if (instance) {
            return await instance.proxy.$getSiteDatas();
        }
        return {};
    }
}