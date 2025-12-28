<template>
    <div class="card">
        <div class="start overflow-hidden">
            <div class="col center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                    <IftaLabel>
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">姓名</label>
                        <InputText name="name" placeholder="请输入姓名" class="w-full md:w-[30rem] mb-4" v-model="judge.name" />
                    </IftaLabel>
                    <IftaLabel>
                        <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">手机号码</label>
                        <InputText name="phone" placeholder="请输入手机号码" class="w-full md:w-[30rem] mb-4" v-model="judge.phone" />
                        <Message v-if="$form.phone?.invalid && $form.phone.error?.type=='error'" severity="error" size="small" variant="simple">{{ $form.phone.error?.message}}</Message>
                    </IftaLabel>
                    <IftaLabel>
                        <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">上传照片</label>
                        <div class="col card items-center gap-6">
                            <FileUpload mode="basic" @select="onFileSelect" customUpload auto severity="secondary" class="p-button-outlined" />
                            <Message v-if="!src" severity="error" size="small" variant="simple">{{ $form.headImgUrl?.error?.message}}</Message>
                            <img v-if="src" :src="src" alt="Image" class="shadow-md rounded-xl w-40 sm:w-22" />
                            <inputText name="headImgUrl" :value="src" class="hidden"/>
                        </div>
                    </IftaLabel>
                    <IftaLabel>
                        <label for="zhiWei" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">职位</label>
                        <InputText name="zhiWei" placeholder="请输入职位" class="w-full md:w-[30rem] mb-4" v-model="judge.zhiWei" />
                    </IftaLabel>
                    <IftaLabel>
                        <label for="subDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">一句话简介</label>
                        <InputText name="subDescription" placeholder="请输入一句话简介" class="w-full md:w-[30rem] mb-4" v-model="judge.subDescription" />
                    </IftaLabel>
                    <IftaLabel>
                        <label for="description" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">具体介绍</label>
                        <Textarea v-model="judge.description" autoResize rows="15" class="w-full" />
                    </IftaLabel>
                    <div class="row mt-12 center gap-4">
                        <Button type="submit" label="保存" class="px-8" _as="router-link" _to="/"></Button>
                        <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
                    </div>
                </Form>
            </div>
        </div>
    </div>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import { onMounted, ref, useTemplateRef, inject, nextTick } from 'vue';
import Page from '@/api/uniapp/page';
import { Config } from '@/api/config';
import primeUtil from "@/api/prime/util";
import {Beans} from "@/api/dbs/beans";
import userRest from "@/api/dbs/userRest";
import util from '@/api/util';
import dialog from '@/api/uniapp/dialog';
import checker from '@/api/check/checker';
import oss from "@/api/oss";

const judge = ref(Beans.judge());
const file = ref(null);
const src = ref(null);

let errors = [];
let host = inject("domain");
let mainPage = null;
let mePage = null;
let obj = null;

onMounted(async () => {
    if (judge?.value?.headImgUrl) {
        src.value = await oss.buildPathAsync(judge.value.headImgUrl,true,null);
    }
});

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:judge.value.name,name:"name"},
        {val:judge.value.phone,name:"phone"},
        {val:judge.value.subDescription,name:"subDescription"},
        // {val:src.value,name:"headImg",label:"照片"}
    ]);

    primeUtil.buildFormValidError(errors.headImgUrl,"error","请选择照片",()=>{
        return !src.value;
    },(error)=>{errors.headImgUrl = error});
    primeUtil.buildFormValidError(errors.phone,"error","手机格式错误",()=>{
        return !checker.check({"phone":judge.value.phone},[{name:"phone",checkType:"phone",checkRule:"",errorMsg:"手机格式错误"}]);
    },(error)=>{errors.phone = error});

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        let headImgUrl = null;
        if (obj.process=="c") {
            judge.value.appId = host;
            judge.value.id = Beans.buildPId("");
            judge.value.createDate = new Date().getTime();
            judge.value.headImgUrl = `cpt/${host}/judge/${judge.value.id}_${file.value.name}`;
            judge.value.temp = true;
            headImgUrl = judge.value.headImgUrl;
        } else {
            if (file.value!=null) {
                if (judge.value.headImgUrl) {
                    oss.deleteFile(judge.value.headImgUrl);
                }
                judge.value.headImgUrl = `cpt/${host}/judge/${judge.value.id}_${file.value.name}`;
                headImgUrl = judge.value.headImgUrl;
            }
        }
        if (headImgUrl) {
            oss.uploadFileWithClient(
                file.value,
                headImgUrl,
                (res) => {
                    userRest.updateJudge({judge:judge.value},async (res)=>{
                        if (res.status=="OK") {
                            judge.value.tempMap = {};
                            judge.value.tempMap.headImgUrl = await oss.buildPathAsync(judge.value.headImgUrl,true,null);
                            judge.value.tempMap.imgPath = judge.value.tempMap.headImgUrl;
                            obj.data = judge.value;
                            obj.returnFunction(obj);
                            file.value = null;
                            src.value = null;
                            mePage.close(mainPage);
                        }
                    });
                },
                (er) => {
                    dialog.toastError(er);
                }
            );
        } else {
            userRest.updateJudge({judge:judge.value},async (res)=>{
                if (res.status=="OK") {
                    judge.value.tempMap = {};
                    judge.value.tempMap.headImgUrl = await oss.buildPathAsync(judge.value.headImgUrl,true,null);
                    judge.value.tempMap.imgPath = judge.value.tempMap.headImgUrl;
                    obj.data = judge.value;
                    obj.returnFunction(obj);
                    file.value = null;
                    src.value = null;
                    mePage.close(mainPage);
                }
            });
        }
    }
};

function onFileSelect(event) {
    file.value = event.files[0];
    src.value = file.value.objectURL;
    // console.log(file);
    // const reader = new FileReader();
    //
    // reader.onload = async (e) => {
    //    src.value = e.target.result;
    //    console.log(src.value);
    // };
    //
    // reader.readAsDataURL(file);
}

function cancel() {
    mePage.close(mainPage);
}

const init = async (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    file.value = null;
    src.value = null;
    if (obj.process=="c") {
        judge.value = Beans.judge();
    } else if (obj.process=="u") {
        judge.value = obj.data;
        console.log(judge.value);
        if (judge?.value?.headImgUrl) {
            src.value = await oss.buildPathAsync(judge.value.headImgUrl,true,null);
        }
    }
}
defineExpose({ init });
</script>

<style scoped lang="scss">

</style>
