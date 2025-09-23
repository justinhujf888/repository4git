import OSS from 'ali-oss';
import otherRest from '@/api/dbs/otherRest.js';
import { Http } from '@/api/http';
import dayjs from "dayjs";
import userRest from '@/api/dbs/userRest';
import dialog from '@/api/uniapp/dialog';

let client = null;
let signatureInfo = {};
let aliOssAccessInfo = {};
let  _tokenExpiredTime = 0;

export default {
    access() {
        return new Promise(resolve => {
            this.buildAliOssAccessInfo((info) => {
                resolve(info);
            });
        });
    },
    async checkToken() {
        if (!client) {
            await this.genClient();
        }
        let times = dayjs(_tokenExpiredTime).diff(new Date());
        console.log("init times",times,_tokenExpiredTime);
        setTimeout(async ()=>{
            try {
                if (Date.now() >= _tokenExpiredTime && client) {
                    const info = await this.access();
                    client.options.stsToken = info.securityToken;
                    client.options.accessKeyId = info.accessId;
                    client.options.accessKeySecret =  info.accessKey;
                    _tokenExpiredTime = new Date(info.expiration).getTime();
                    times = dayjs(_tokenExpiredTime).diff(new Date());
                    console.log("checkToken update token",times,_tokenExpiredTime);
                }
            } catch (e) {
                console.log(e);
            } finally {
                await this.checkToken();
            }
        },times);
    },
    buildAliOssAccessInfo(fun) {
        otherRest.genAliOssAccessInfo((data) => {
            if (data.status == 'OK') {
                aliOssAccessInfo = data.signatureInfo;
                console.log("signatureInfo",aliOssAccessInfo);
                if (fun) {
                    fun(aliOssAccessInfo);
                }
            }
        });
    },
    buildSignatureInfo(fun) {
        otherRest.genSignature((data) => {
            if (data.status == 'OK') {
                signatureInfo = data.signatureInfo;
                // console.log(signatureInfo);
                if (fun) {
                    fun(signatureInfo);
                }
            }
        });
    },
    async genClient() {
        if (client) {
            return new Promise(resolve => {
                resolve(client);
            });
        } else {
            return new Promise(resolve => {
                this.buildAliOssAccessInfo(() => {
                    client = new OSS({
                        authorizationV4: true,
                        region: aliOssAccessInfo.region, //换成你自己的
                        accessKeyId: aliOssAccessInfo.accessId,
                        accessKeySecret: aliOssAccessInfo.accessKey,
                        bucket: aliOssAccessInfo.bucketName,
                        stsToken: aliOssAccessInfo.securityToken,
                        refreshSTSToken: async () => {
                            console.log("refreshSTSToken");
                            const info = await this.access();
                            console.log("info",info);
                            _tokenExpiredTime = new Date(info.expiration).getTime();
                            return {
                                accessKeyId: info.accessId,
                                accessKeySecret: info.accessKey,
                                stsToken: info.securityToken
                            }
                        },
                        refreshSTSTokenInterval: 400000
                    });
                    _tokenExpiredTime = new Date(aliOssAccessInfo.expiration).getTime();
                    resolve(client);
                });
            });
        }
    },
    buildImgPath(imgPath) {
        if (client) {
            return client.signatureUrl(imgPath,{'process': 'style/mobile'});
            // return client.signatureUrl(imgPath,{expires: Date.parse(new Date()) / 1000 + 3600,'process': 'style/mobile'});
        } else {
            return null;
        }
    },
    buildPath(imgPath) {
        if (client) {
            return client.signatureUrl(imgPath,{expires: Date.parse(new Date()) / 1000 + 3600});
        } else {
            return null;
        }
    },
    uploadFileWithReq(file,key,okfun,erfun) {
        Http.uploadFileOss(
            file,
            signatureInfo.bucketUrl,
            key,
            signatureInfo.accessId,
            signatureInfo.policy,
            signatureInfo.signature,
            signatureInfo.securityToken,
            (res) => {
                console.log("res",res);
                if (okfun) {
                    okfun(res);
                }
            },
            (er) => {
                console.log(er);
                if (erfun) {
                    erfun(er);
                }
            }
        );
    },
    uploadFileWithClient(file,key,okfun,erfun) {
        (async ()=>{
            try {
                let res = await client.put(key,file);
                console.log("res",res);
                if (okfun) {
                    okfun(res);
                }
            } catch(er) {
                console.log(er);
                if (erfun) {
                    erfun(er);
                }
            }
        })();
    },
    deleteFile(path) {
        (async (c)=>{
            try {
                return await c.delete(path);
            } catch(er) {
                return null;
            }
        })();
        // client.delete(path);
    },
};
