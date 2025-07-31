<script setup>
import { useToast } from 'primevue/usetoast';
import { ref, onMounted } from 'vue';
import { Base64 } from 'js-base64';
import dialog from '@/api/uniapp/dialog';
import { resolve } from 'chart.js/helpers';
import oss from '@/api/oss';
import lodash from 'lodash-es';

const toast = useToast();
const fileupload = ref();
const imgUrls = ref([]);
const responsiveOptions = ref([
    {
        breakpoint: '1300px',
        numVisible: 4
    },
    {
        breakpoint: '575px',
        numVisible: 1
    }
]);

let client = null;
let signatureInfo = {};
let imgArray = [];

onMounted(() => {
    console.log('filedoc onMounted');
    imgArray = [
        'temp/20.jpg',
        'temp/1593265237.fst.2La.jpg',
        'temp/1593265237.fst.KKn.jpg',
        'temp/1616206175.fst.aLj.jpg',
        'temp/1616206260.fst.Ots.jpg',
        'temp/1616206260.fst.RxT.jpg',
        'temp/1616206308.fst.ylZ.jpg',
        'temp/1616340060.fst.pdy.jpg',
        'temp/1616340063.fst.lGp.jpg',
        'temp/1616340064.fst.P6J.jpg',
        'temp/1616340064.fst.W6b.jpg',
        'temp/1664898460.fst.qKP.jpg',
        'temp/1664898468.fst.kT1.jpg',
        'temp/1664898486.fst.Pco.jpg',
        'temp/1664898508.fst.CqX.jpg',
        'temp/1664898508.fst.uZh.jpg',
        'temp/1664898517.fst.Z7i.jpg',
        'temp/1664898520.fst.m1H.jpg',
        'temp/1664898521.fst.ceV.jpg',
        'temp/1664898529.fst.FQl.jpg',
        'temp/1664898531.fst.zq3.jpg',
        'temp/1700433066.fst.VqJ.jpeg',
        'temp/1700433066.fst.wOI.jpeg',
        'temp/1700433067.fst.0Io.jpeg',
        'temp/1700433067.fst.Gaj.jpeg',
        'temp/1700433068.fst.CaM.jpeg',
        'temp/1700433068.fst.ONb.jpeg',
        'temp/1700433069.fst.sXW.jpeg',
        'temp/1700433071.fst.w4h.jpeg',
        'temp/1700611676.fst.ulW.jpeg',
        'temp/1700611676.fst.UY0.jpeg',
        'temp/1700611677.fst.PVa.jpeg',
        'temp/1700611677.fst.T16.jpeg',
        'temp/1700611678.fst.kFY.jpeg',
        'temp/1700611679.fst.5Ry.jpeg',
        'temp/1700611680.fst.7gN.jpeg',
        'temp/1700611681.fst.1PP.jpeg',
        'temp/1700932571.fst.t2c.jpeg',
        'temp/1708582196.fst.DT3.jpg',
        'temp/1708582196.fst.PFD.jpg',
        'temp/1708582196.fst.RY3.jpg',
        'temp/1708582198.fst.N6d.jpg',
        'temp/1708582199.fst.8cj.jpg',
        'temp/1708582199.fst.nJI.jpg',
        'temp/1708582200.fst.PHA.jpg',
        'temp/1708582203.fst.Sfi.jpg',
        'temp/1708582206.fst.1Rl.jpg',
        'temp/1708582210.fst.lWU.jpg',
        'temp/1708582211.fst.M75.jpg',
        'temp/1708582216.fst.2jF.jpg',
        'temp/1708582224.fst.gCT.jpg',
        'temp/1708582226.fst.ar8.jpg',
        'temp/1708582229.fst.2fx.jpg',
        'temp/1680483673241.jpg',
        'temp/BGc4Re.jpeg',
        'temp/BGc6pF.jpeg',
        'temp/BGccnT.jpeg',
        'temp/BGcGZ3.jpeg',
        'temp/BGchuD.jpeg',
        'temp/BGcloc.jpeg',
        'temp/BGcNmv.jpeg',
        'temp/BGcOIH.jpeg',
        'temp/BGcoU9.jpeg',
        'temp/BGcPuC.jpeg',
        'temp/BGcq8A.jpeg',
        'temp/BGcvym.jpeg',
        'temp/BGcwT7.jpeg',
        'temp/BGcyUF.jpeg',
        'temp/BGX3V5.jpeg',
        'temp/BGXdg9.jpeg',
        'temp/BGXIOe.jpeg',
        'temp/BGXkvZ.jpeg',
        'temp/BGXq32.jpeg',
        'temp/BGXQHi.jpeg',
        'temp/BGXrZx.jpeg',
        'temp/BGXTAc.jpeg',
        'temp/BGXWnM.jpeg'
    ];
    imgUrls.value = [];
    lodash.forEach(imgArray, (v, i) => {
        let sPath = oss.buildImgPath(v);
        let bPath = oss.buildPath(v);
        imgUrls.value.push({ itemImageSrc: bPath, thumbnailImageSrc: sPath, alt: v, title: v });
    });
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
    e.formData.append('ossAccessKeyId', signatureInfo.accessId);
    e.formData.append('policy', signatureInfo.policy);
    e.formData.append('signature', signatureInfo.signature);
    e.formData.append('securityToken', signatureInfo.securityToken);
    e.formData.append('x-oss-security-token', signatureInfo.securityToken);
    e.formData.append('success_action_status', 200);
    //阿里云不支持web端批量上传，只能循环一个个传
    for (let f of fileList) {
        e.formData.append('key', f.name);
        e.formData.append('file', f);
    }
}

function uploadFile(step) {
    if (step > fileList.length - 1) {
        imgUrls.value = [];
        lodash.forEach(imgArray, (v, i) => {
            let sPath = oss.buildImgPath(v);
            let bPath = oss.buildPath(v);
            imgUrls.value.push({ itemImageSrc: bPath, thumbnailImageSrc: sPath, alt: v, title: v });
        });
        console.log(JSON.stringify(imgArray));
        dialog.toastNone(`all files uploaded`);
    } else {
        oss.uploadFileWithClient(
            fileList[step],
            'temp/' + fileList[step].name,
            (res) => {
                imgArray.push('temp/' + fileList[step].name);
                dialog.toastNone(`${fileList[step].name} uploaded`);
                uploadFile(step + 1);
            },
            (er) => {
                dialog.toastError(er);
            }
        );
        // oss.uploadFileWithReq(
        //     fileList[step],
        //     'temp/' + fileList[step].name,
        //     (res) => {
        //         dialog.toastNone(`${fileList[step].name} uploaded`);
        //         uploadFile(step + 1);
        //     },
        //     (er) => {
        //         dialog.toastError(er);
        //     }
        // );
    }
}

function uploader() {
    // oss.buildSignatureInfo((signatureInfo) => {
    //     console.log(signatureInfo);
    //     uploadFile(0, signatureInfo);
    // });
    imgArray = [];
    uploadFile(0);
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
    <div class="card center">
        <Galleria :value="imgUrls" :responsiveOptions="responsiveOptions" :numVisible="6" containerStyle="max-width: 640px" :circular="true" :showItemNavigators="true">
            <template #item="slotProps">
                <Image :src="slotProps.item.itemImageSrc" :alt="slotProps.item.alt" style="width: 60%" />
            </template>
            <template #thumbnail="slotProps">
                <div class="center border-2 border-solid border-gray-300 w-24 h-36">
                    <Image :src="slotProps.item.thumbnailImageSrc" :alt="slotProps.item.alt" imageClass="w-24" />
                </div>
            </template>
        </Galleria>
    </div>
    <div class="grid grid-cols-12 gap-8">
        <div class="col-span-full lg:col-span-6">
            <div class="card">
                <div class="font-semibold text-xl mb-4">Advanced</div>
                <FileUpload
                    name="demo[]"
                    url="https://pets-oss.oss-cn-zhangjiakou.aliyuncs.com"
                    @select="onFileSelect"
                    @upload="onAdvancedUpload($event)"
                    @uploader="uploader"
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
                    <FileUpload ref="fileupload" mode="basic" name="demo[]" accept="image/*" :maxFileSize="1000000" @uploader="uploader" customUpload @select="onFileSelect" />
                    <Button label="Upload" @click="upload" severity="secondary" />
                </div>
            </div>
        </div>
    </div>
</template>
