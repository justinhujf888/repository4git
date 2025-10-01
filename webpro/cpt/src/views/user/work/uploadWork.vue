<template>
    <div class="card !p-2">
        <routerPath :home="null" :items="menuItems"/>
        <h2>{{competition?.name}}</h2>
        <div class="start overflow-hidden">
            <div class="col center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full grid gap-x-2 gap-y-4">
                    <FloatLabel variant="on">
                        <label for="guige" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">选择分组</label>
                        <Select name="guige" v-model="work.guiGe" :options="competition.guiGeList" optionLabel="name" fluid placeholder="选择分组"/>
                    </FloatLabel>
                    <FloatLabel variant="on">
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品名称</label>
                        <InputText name="name" class="w-full md:w-[30rem]" v-model="work.name" />
                    </FloatLabel>
                    <Fieldset legend="作品照片">
                        <InputText v-model="imageVaild" name="imageVaild" class="hidden"/>
                        <Message v-for="er of $form.imageVaild?.errors" class="my-2" severity="error" size="small" variant="simple">{{er.message}}</Message>
                        <FileUpload v-show="false" ref="imageFileUpload" mode="basic" accept="image/*" @select="onFileSelect" customUpload auto severity="secondary" class="p-button-outlined" />
                        <div class="row flex-wrap gap-2">
                            <Button severity="secondary" class="col center w-36 h-32 md:w-44 md:h-28 border-solid border-gray-500 border-2 rounded-xl relative" v-for="(item,index) in workImageItems" :key="index" @click="uploadButtonClick(item)">
                                <Image :src="item.src" class="absolute top-0 left-0 z-10 object-cover object-center"/>
                                <div class="mix-blend-difference text-white col absolute wcenter z-20 w-full">
                                    <span class="text-xl">{{item.title}}</span>
                                    <span class="text-sm">{{item.text}}</span>
                                </div>
                            </Button>
                        </div>
                    </Fieldset>
                    <Fieldset legend="作品视频">
                        <InputText v-model="videoVaild" name="videoVaild" class="hidden"/>
                        <FileUpload v-show="false" ref="videoFileUpload" mode="basic" accept="video/*" @select="onFileSelect" customUpload auto severity="secondary" class="p-button-outlined" />
                        <Message v-for="er of $form.videoVaild?.errors" class="my-2" severity="error" size="small" variant="simple">{{er.message}}</Message>
                        <div class="row flex-wrap gap-2">
                            <Button severity="secondary" class="col center w-36 h-32 md:w-44 md:h-28 !p-2 border-solid border-gray-500 border-2 rounded-xl relative" v-for="(item,index) in workVideoItems" :key="index" @click="uploadButtonClick(item)">
                                <video :src="item.src" class="absolute top-0 left-0 z-10 object-center"/>
                                <div class="mix-blend-difference text-white col absolute wcenter z-20 w-full">
                                    <span class="text-xl">{{item.title}}</span>
                                    <span class="text-sm">{{item.text}}</span>
                                </div>
                            </Button>
                        </div>
                    </Fieldset>
                    <FloatLabel variant="on">

                    </FloatLabel>
                    <FloatLabel variant="on">
                        <label for="gousiDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品介绍</label>
                        <Textarea name="gousiDescription" v-model="work.gousiDescription" autoResize rows="8" class="w-full" />
                    </FloatLabel>
                    <FloatLabel variant="on">
                        <label for="myMeanDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品理念</label>
                        <Textarea name="myMeanDescription" v-model="work.myMeanDescription" autoResize rows="8" class="w-full" />
                    </FloatLabel>
                    <FloatLabel variant="on" v-for="(item,index) in masterCompetition.tempMap?.setupFields?.data" :key="index">
                        <label :for="item.id" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">{{item.name}}</label>
                        <InputText :name="item.id" v-model="item.value" autoResize rows="8" class="w-full" />
                    </FloatLabel>
                    <FloatLabel variant="on" v-for="(item,index) in masterCompetition.setupFields?.data" :key="index">
                        <label :for="item.id" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">{{item.name}}</label>
                        <InputText :name="item.id" v-model="item.value" autoResize rows="8" class="w-full" />
                    </FloatLabel>
                    <div class="row mt-12 center gap-4">
                        <Button severity="warn" label="暂时保存" class="px-8" @click="tempSave()"></Button>
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

const imageFileUpload = useTemplateRef("imageFileUpload");
const videoFileUpload = useTemplateRef("videoFileUpload");

const masterCompetition = ref(Beans.masterCompetition());
const competition = ref(Beans.competition());
const work = ref(Beans.work());
const menuItems = ref([]);
const workImageItems = ref([]);
const workVideoItems = ref([]);
const imageVaild = ref(false);
const videoVaild = ref(false);

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
    // workImageItems.value = [
    //     {title:"上传相机原图",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",file:null,bean:buildWorkItem(0,0)},
    //     {title:"上传作品全景图主图",text:"",file:null,bean:buildWorkItem(0,1)},
    //     {title:"上传作品全景图其他角度",text:"",file:null,bean:buildWorkItem(0,2)},
    //     {title:"上传作品全景图其他角度",text:"",file:null,bean:buildWorkItem(0,2)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)}
    // ];
});

function uploadButtonClick(_item) {
    item = _item;
    if (item.mediaType==0) {
        imageFileUpload.value.choose();
    } else {
        videoFileUpload.value.choose();
    }
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

    errors.imageVaild = [];
    lodash.forEach(obj.uploadRule.workType.image,(v)=>{
        const count = lodash.size(lodash.filter(workImageItems.value,(o)=>{return o.mediaType==v.mediaType && o.type==v.type && o.file!=null}));
        if (count < v.maxCount) {
            errors.imageVaild.push({type:"error",message:v.title});
        }
    });

    errors.videoVaild = [];
    lodash.forEach(obj.uploadRule.workType.video,(v)=>{
        const count = lodash.size(lodash.filter(workVideoItems.value,(o)=>{return o.mediaType==v.mediaType && o.type==v.type && o.file!=null}));
        if (count < v.maxCount) {
            errors.videoVaild.push({type:"error",message:v.title});
        }
    });
    // primeUtil.buildFormValidError(errors.videoVaild,"error","上传视频",()=>{
    //     workVideoItems.value
    // },(error)=>{errors.videoVaild = error});
    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        console.log(masterCompetition.value.setupFields.data);
    }
};

function tempSave() {

}

async function onFileSelect(event) {
    // console.log(event.files);
    item.file = event.files[0];
    if (item.mediaType==0) {
        item.src = item.file.objectURL;
    } else {
        item.src = URL.createObjectURL(item.file);
    }
    // item.fileUrl = URL.createObjectURL(new Blob([await event.files[0].arrayBuffer()],{type:"video/mp4"}));
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = lodash.cloneDeep(_obj);
    competition.value = obj.data;
    masterCompetition.value = obj.masterCompetition;
    if (obj.process=="c") {
        work.value = Beans.work();
        workImageItems.value = [];
        workVideoItems.value = [];
        errors = [];
    }
    lodash.forEach(obj.uploadRule.workType.image,(v)=>{
        for(let i=0;i<v.showCount;i++) {
            workImageItems.value.push({title:v.title,text:v.text,file:null,mediaType:v.mediaType,type:v.type,bean:buildWorkItem(v.mediaType,v.type)});
        }
    });
    lodash.forEach(obj.uploadRule.workType.video,(v)=>{
        for(let i=0;i<v.showCount;i++) {
            workVideoItems.value.push({title:v.title,text:v.text,file:null,mediaType:v.mediaType,type:v.type,bean:buildWorkItem(v.mediaType,v.type)});
        }
    });
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
