<template>
    <div class="card !p-2">
        <routerPath :home="null" :items="menuItems"/>
        <h2>{{competition?.name}}</h2>
        <div class="start overflow-hidden">
            <div class="col center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full grid gap-2">
                    <IftaLabel>
                        <label for="guige" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">分组</label>
                        <Select name="guige" v-model="work.guiGe" :options="competition.guiGeList" optionLabel="name" placeholder="选择分组" fluid />
                    </IftaLabel>
                    <IftaLabel>
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品名称</label>
                        <InputText name="name" placeholder="请输入作品名称" class="w-full md:w-[30rem] mb-4" v-model="work.name" />
                    </IftaLabel>
                    <IftaLabel>
                        <FileUpload v-show="false" ref="fileUpload" mode="basic" @select="onFileSelect" customUpload auto severity="secondary" class="p-button-outlined" />
                        <div class="row flex-wrap gap-2">
                            <Button severity="secondary" class="col center w-36 h-32 md:w-44 md:h-28 !p-2 border-solid border-gray-500 border-2 rounded-xl relative" v-for="(item,index) in workItems" :key="index" @click="uploadButtonClick(item)">
                                <Image :src="item.file?.objectURL" class="absolute top-0 left-0 z-10"/>
                                <div class="absolute top-5 left-0 mix-blend-difference z-20 text-white">
                                    <span class="text-xl">{{item.title}}</span>
                                    <span class="text-sm">{{item.text}}</span>
                                </div>
                            </Button>
                        </div>
                    </IftaLabel>
                    <IftaLabel>

                    </IftaLabel>
                    <IftaLabel>
                        <label for="gousiDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品介绍</label>
                        <Textarea name="gousiDescription" v-model="work.gousiDescription" autoResize rows="15" class="w-full" />
                    </IftaLabel>
                    <IftaLabel>
                        <label for="myMeanDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品理念</label>
                        <Textarea name="myMeanDescription" v-model="work.myMeanDescription" autoResize rows="15" class="w-full" />
                    </IftaLabel>
                    <div class="row mt-12 center gap-4">
                        <Button severity="warn" label="暂时保存" class="px-8" @click="cancel()"></Button>
                        <Button type="submit" label="提交" class="px-8" _as="router-link" _to="/"></Button>
                    </div>
                </Form>
            </div>
        </div>
    </div>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import routerPath from "@/components/my/routerPath.vue";
import primeUtil from "@/api/prime/util";
import checker from "@/api/check/checker";
import {Beans} from "@/api/dbs/beans";
import oss from "@/api/oss";
import userRest from "@/api/dbs/userRest";
import dialog from "@/api/uniapp/dialog";
import lodash from "lodash-es";

const fileUpload = useTemplateRef("fileUpload");

const competition = ref(Beans.competition());
const work = ref(Beans.work());
const menuItems = ref([]);
const workItems = ref([]);

let item = null;
let errors = [];
let host = inject("domain");
let mainPage = null;
let mePage = null;
let obj = null;

onMounted(() => {
    menuItems.value = [
        {label:'返回我的参赛作品',click:()=>{obj.returnFunction(obj);mePage.close(mainPage);}}
    ];
    workItems.value = [
        {title:"上传相机原图",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",file:null,bean:buildWorkItem(0,0)},
        {title:"上传作品全景图主图",text:"",file:null,bean:buildWorkItem(0,1)},
        {title:"上传作品全景图其他角度",text:"",file:null,bean:buildWorkItem(0,2)},
        {title:"上传作品全景图其他角度",text:"",file:null,bean:buildWorkItem(0,2)},
        {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
        {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
        {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
        {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)}
    ];
});

function uploadButtonClick(_item) {
    item = _item;
    fileUpload.value.choose();
}

function buildWorkItem(mediaType,type) {
    let workItem = Beans.workItem();
    workItem.id = Beans.buildPId("");
    workItem.mediaType = mediaType;
    workItem.type = type;
    workItem.work.id = work.value.id;
    return workItem;
}

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:work.value.name,name:"name"},
        {val:work.value.guiGe.id,name:"guige"},
        {val:work.value.myMeanDescription,name:"myMeanDescription"},
        {val:work.value.gousiDescription,name:"gousiDescription"},
        // {val:src.value,name:"headImg",label:"照片"}
    ]);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {

    }
};

function onFileSelect(event) {
    item.file = event.files[0];
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    competition.value = obj.data;
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
