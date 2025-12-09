<template>
    <div class="p-2 card">
        <div class="start overflow-hidden">
            <div class="col center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                    <IftaLabel>
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">姓名</label>
                        <InputText name="name" placeholder="请输入姓名" class="w-full md:w-[30rem] mb-4" v-model="orgHuman.name" />
                    </IftaLabel>
                    <IftaLabel>
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">上传照片</label>
                        <div class="col card items-center gap-6">
                            <FileUpload mode="basic" @select="onFileSelect" customUpload auto severity="secondary" class="p-button-outlined" />
                            <Message v-if="!src" severity="error" size="small" variant="simple">{{ $form.headImgUrl?.error?.message}}</Message>
                            <img v-if="src" :src="src" alt="Image" class="shadow-md rounded-xl w-40 sm:w-22" />
                            <inputText name="headImgUrl" :value="src" class="hidden"/>
                        </div>
                    </IftaLabel>
                    <IftaLabel>
                        <label for="description" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">简介</label>
                        <Textarea v-model="orgHuman.description" autoResize rows="15" class="w-full" />
                    </IftaLabel>
                    <div class="row mt-12 center gap-4">
                        <Button type="submit" label="保存" class="px-8" _as="router-link" _to="/"></Button>
                        <Button severity="warn" label="取消" class="px-8" @click="callClose(null,obj)"></Button>
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
import workRest from '@/api/dbs/workRest';
import util from '@/api/util';
import dialog from '@/api/uniapp/dialog';
import checker from '@/api/check/checker';
import oss from "@/api/oss";

const orgHuman = ref(Beans.orgHuman());
const file = ref(null);
const obj = ref(null);
const src = ref(null);

let errors = [];
let host = inject("domain");

onMounted(async () => {
    nextTick();
    if (orgHuman?.value?.headImgUrl) {
        src.value = await oss.buildPathAsync(orgHuman.value.headImgUrl,true,null);
    }
});

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:orgHuman.value.name,name:"name"},
        {val:orgHuman.value.description,name:"description"},
        // {val:src.value,name:"headImg",label:"照片"}
    ]);

    primeUtil.buildFormValidError(errors.headImgUrl,"error","请选择照片",()=>{
        return !src.value;
    },(error)=>{errors.headImgUrl = error});

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        let headImgUrl = null;
        if (obj.value.process=="c") {
            orgHuman.value.appId = host;
            orgHuman.value.sourceType = 0;
            orgHuman.value.sourceId = obj.value.sourceId;
            orgHuman.value.id = Beans.buildPId("");
            orgHuman.value.createDate = new Date().getTime();
            orgHuman.value.headImgUrl = `cpt/${host}/orgHuman/${orgHuman.value.id}_${file.value.name}`;
            headImgUrl = orgHuman.value.headImgUrl;
        } else {
            if (file.value!=null) {
                if (orgHuman.value.headImgUrl) {
                    oss.deleteFile(orgHuman.value.headImgUrl);
                }
                orgHuman.value.headImgUrl = `cpt/${host}/orgHuman/${orgHuman.value.id}_${file.value.name}`;
                headImgUrl = orgHuman.value.headImgUrl;
            }
        }
        console.log(headImgUrl);
        if (headImgUrl) {
            oss.uploadFileWithClient(
                file.value,
                headImgUrl,
                (res) => {
                    workRest.updateOrgHuman({orgHuman:orgHuman.value},async (res)=>{
                        if (res.status=="OK") {
                            orgHuman.value.tempMap = {};
                            orgHuman.value.tempMap.headImgUrl = await oss.buildPathAsync(orgHuman.value.headImgUrl,true,null);
                            orgHuman.value.tempMap.imgPath = orgHuman.value.tempMap.headImgUrl;
                            callClose(orgHuman.value,obj.value);
                            file.value = null;
                            src.value = null;
                        }
                    });
                },
                (er) => {
                    dialog.toastError(er);
                }
            );
        } else {
            workRest.updateOrgHuman({orgHuman:orgHuman.value},async (res)=>{
                if (res.status=="OK") {
                    orgHuman.value.tempMap = {};
                    orgHuman.value.tempMap.headImgUrl = await oss.buildPathAsync(orgHuman.value.headImgUrl,true,null);
                    orgHuman.value.tempMap.imgPath = orgHuman.value.tempMap.headImgUrl;
                    callClose(orgHuman.value,obj.value);
                    file.value = null;
                    src.value = null;
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

const emit = defineEmits(["callClose"]);
const callClose = (orgHuman,obj)=>{
    emit("callClose",orgHuman,obj);
};

const init = async (og,o)=>{
    file.value = null;
    src.value = null;
    if (og) {
        orgHuman.value = og;
        if (orgHuman?.value?.headImgUrl) {
            src.value = await oss.buildPathAsync(orgHuman.value.headImgUrl,true,null);
        }
    } else {
        orgHuman.value = Beans.orgHuman();
    }
    obj.value = o;
    console.log(obj.value);
}
defineExpose({ init });
</script>

<style scoped lang="scss">

</style>
