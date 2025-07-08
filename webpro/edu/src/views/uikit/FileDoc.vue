<script setup>
import { useToast } from 'primevue/usetoast';
import { ref, onMounted } from 'vue';
import axios from 'axios';
// import OSS from 'ali-oss';
import { Base64 } from 'js-base64';
import { Http } from '@/api/http';
import dialog from '@/api/uniapp/dialog';
import deviceRest from '@/api/dbs/device.js';
import { resolve } from 'chart.js/helpers';

const toast = useToast();
const fileupload = ref();

let client = null;
let datas = {};

onMounted(() => {
    deviceRest.genSignature((data) => {
        if (data.status == 'OK') {
            datas = data;
        }
    });
    //     deviceRest.genAliOssAccessInfo((data) => {
    //         console.log(data.signatureInfo);
    //         let info = data.signatureInfo;
    //         client = new OSS({
    //             // yourRegion填写Bucket所在地域。以华东1（杭州）为例，Region填写为oss-cn-hangzhou。
    //             region: info.region,
    //             // 开启V4版本签名。
    //             authorizationV4: true,
    //             // 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
    //             accessKeyId: info.accessId,
    //             accessKeySecret: info.accessKey,
    //             // 从STS服务获取的安全令牌（SecurityToken）。
    //             stsToken: info.securityToken,
    //             refreshSTSToken: async () => {
    //                 // 向您搭建的STS服务获取临时访问凭证。
    //                 deviceRest.genAliOssAccessInfo((res) => {
    //                     if (data.status == 'OK') {
    //                         return {
    //                             accessKeyId: info.accessId,
    //                             accessKeySecret: info.accessKey,
    //                             stsToken: info.securityToken
    //                         };
    //                     }
    //                 });
    //             },
    //             // 刷新临时访问凭证的时间间隔，单位为毫秒。
    //             refreshSTSTokenInterval: 300000,
    //             // 填写Bucket名称。
    //             bucket: info.bucketName
    //         });
    //     });
});

// 随机字符串
function randomString(num) {
    const chars = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];
    let res = '';
    for (let i = 0; i < num; i++) {
        let id = Math.ceil(Math.random() * 35);
        res += chars[id];
    }
    return res;
}

// 计算签名。
function computeSignature(accessKeySecret, canonicalString) {
    return crypto.enc.Base64.stringify(crypto.HmacSHA1(canonicalString, accessKeySecret));
}

const date = new Date();
date.setHours(date.getHours() + 1);
const policyText = {
    expiration: date.toISOString(), // 设置policy过期时间。
    conditions: [
        // 限制上传大小。
        ['content-length-range', 0, 1024 * 1024 * 1024]
    ]
};
let fileList = [];

function upload() {
    fileupload.value.upload();
}

const onAdvancedUpload = () => {
    dialog.toastNone('file uploaded');
};

function onBeforeUpload(e) {
    console.log('onBeforeUpload', e);
    e.formData.append('ossAccessKeyId', datas.signatureInfo.accessId);
    e.formData.append('policy', datas.signatureInfo.policy);
    e.formData.append('signature', datas.signatureInfo.signature);
    e.formData.append('securityToken', datas.signatureInfo.securityToken);
    e.formData.append('x-oss-security-token', datas.signatureInfo.securityToken);
    e.formData.append('success_action_status', 200);
    //阿里云不支持web端批量上传，只能循环一个个传
    for (let f of fileList) {
        e.formData.append('key', f.name);
        e.formData.append('file', f);
    }
}

function uploadFile(step,data) {
    if (step > fileList.length-1) {
        dialog.toastNone(`all files uploaded`);
    } else {
        Http.uploadFileOss(
            fileList[step],
            data.signatureInfo.bucketUrl,
            "temp/"+fileList[step].name,
            data.signatureInfo.accessId,
            data.signatureInfo.policy,
            data.signatureInfo.signature,
            data.signatureInfo.securityToken,
            (res) => {
                console.log("res",res);
                dialog.toastNone(`${fileList[step].name} uploaded`);
                uploadFile(step+1,data);
            },
            (er) => {
                console.log(er);
            }
        );
    }
}

function onUpload(e) {
    deviceRest.genSignature((data) => {
        if (data.status == 'OK') {
            console.log(data);
            uploadFile(0,data);
        }
    });
}

function onProgress(e) {
    console.log(e);
}

function onFileSelect(e) {
    console.log(e.files);
    fileList = e.files;
}
</script>

<template>
    <div class="grid grid-cols-12 gap-8">
        <div class="col-span-full lg:col-span-6">
            <div class="card">
                <div class="font-semibold text-xl mb-4">Advanced</div>
                <FileUpload
                    name="demo[]"
                    url="https://pets-oss.oss-cn-zhangjiakou.aliyuncs.com"
                    @select="onFileSelect"
                    @upload="onAdvancedUpload($event)"
                    @uploader="onUpload"
                    @before-upload="onBeforeUpload"
                    @progress="onProgress"
                    :multiple="true"
                    _accept="image/*"
                    :_maxFileSize="1000000"
                    customUpload
                    :show-cancel-button="false"
                />
            </div>
        </div>
        <div class="col-span-full lg:col-span-6">
            <div class="card">
                <div class="font-semibold text-xl mb-4">Basic</div>
                <div class="card flex flex-col gap-6 items-center justify-center">
                    <Toast />
                    <FileUpload ref="fileupload" mode="basic" name="demo[]" accept="image/*" :maxFileSize="1000000" @uploader="onUpload" customUpload @select="onFileSelect" />
                    <Button label="Upload" @click="upload" severity="secondary" />
                </div>
            </div>
        </div>
    </div>
</template>
