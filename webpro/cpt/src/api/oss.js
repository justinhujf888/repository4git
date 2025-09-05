import OSS from 'ali-oss';
import otherRest from '@/api/dbs/otherRest.js';
import { Http } from '@/api/http';
import dayjs from "dayjs";

let client = null;
let signatureInfo = {};
let aliOssAccessInfo = {};

export default {
    access() {
        return new Promise(resolve => {
            this.buildAliOssAccessInfo((data) => {
                resolve(data);
            });
        });
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
    genClient(fun) {
        if (client) {
            if (fun) {
                fun(client);
            }
        } else {
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
                        return {
                            accessKeyId: info.accessId,
                            accessKeySecret: info.accessKey,
                            stsToken: info.securityToken
                        }
                    },
                    refreshSTSTokenInterval: 400000
                });
                if (fun) {
                    fun(client);
                }
            });
        }
    },
    buildImgPath(imgPath) {
        if (client) {
            return client.signatureUrl(imgPath,{expires: Date.parse(new Date()) / 1000 + 3600,'process': 'style/mobile'});
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
        this.genClient(async ()=>{
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
        });
    },
    deleteFile(path) {
        this.genClient(async (c)=>{
            try {
                return await c.delete(path);
            } catch(er) {
                return null;
            }
        });
        // client.delete(path);
    },
};
