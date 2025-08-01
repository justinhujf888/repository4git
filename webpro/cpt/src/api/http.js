import { Config } from '@/api/config.js';
import util from '@/api/util.js';
import axios from 'axios';

export const Http = {
    httpclient_json(url, method, ds, ptype, returnfun, errorfun, loading) {
        // console.log(ds);
        if (loading) {
            // dialog.openLoading("处理中");
        }
        let str = new Date(+new Date() + 8 * 3600 * 1000)
            .toISOString()
            .replace(/T/g, ' ')
            .replace(/\.[\d]{3}Z/, '');
        ds.loginState = null; //login.getLoginState();
        ds.appId = Config.appId;
        ds.vstr = str;
        axios({
            baseURL: Config.apiBaseURL,
            url: url,
            method: method, // header: {timestamp:jsEncrypt.encrypt(str),createTime:str,nonceStr:util.encryptStoreInfo(str)},
            headers: {
                'Content-Type': 'application/json'
            },
            data: util.encryptStoreInfo(encodeURIComponent(JSON.stringify(ds)))
        })
            .then((res) => {
                returnfun(res);
                // dialog.closeLoading();
            })
            .catch((error) => {
                // dialog.closeLoading();
                if (errorfun) {
                    errorfun(error);
                }
                // dialog.alert("系统错误，请通知系统管理员或者客服；谢谢！");
                console.log(error);
            });
    },

    uploadFileOss(file,host,key,ossAccessKeyId,policy,signature,securityToken,okfun,erfun) {
        // axios({
        //     method: 'post',
        //     url: host,
        //     data: formData,
        //     params: {
        //         signature: data.signatureInfo.signature // 传递签名至服务器
        //     }
        // });
        const formData = new FormData();
        formData.append('key', key);
        formData.append('ossAccessKeyId', ossAccessKeyId);
        formData.append('policy', policy);
        formData.append('signature', signature);
        formData.append('securityToken', securityToken);
        formData.append('x-oss-security-token', securityToken);
        formData.append('success_action_status', 200);
        formData.append('file', file);

        axios
            .post(host, formData, {})
            .then((response) => {
                console.log('上传成功', response);
                okfun(response);
            })
            .catch((error) => {
                console.error('上传失败', error);
                erfun(error);
            });
    }
};
