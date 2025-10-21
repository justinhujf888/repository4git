<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <div class="card">
            <div v-if="workList.length>0" class="center grid xs:grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <Card class="overflow-hidden cursor-pointer" v-for="work of workList" @click="refUploadWork.init(mainPage,updateWorkPage,{data:work.guiGe.competition,masterCompetition:masterCompetition,uploadRule:uploadRule,userId:userId,work:work,process:'u',returnFunction:returnFunction});confirm.close();updateWorkPage.open(mainPage);">
                    <template #header>
                        <img v-if="work.workItemList?.length>0" :src="work.workItemList[0].tempMap.imgPath" class="h-48 w-full object-cover object-center"/>
                        <img v-else src="https://primefaces.org/cdn/primevue/images/card-vue.jpg" />
                    </template>
                    <template #title>{{work.name}}</template>
                    <template #subtitle>
                        <span class="text-md mx-3 font-semibold">{{work.guiGe.competition.name}}</span>
                        <span class="text-md">{{work.guiGe.name}}</span>
                    </template>
                    <template #content>
                        <p class="m-0">

                        </p>
                    </template>
                </Card>
                <Card class="overflow-hidden cursor-pointer" v-for="wIndex of uploadRule.maxWorkCount-workList.length" @click="showCompetitionList($event)">
                    <template #header>
<!--                        <img src="https://primefaces.org/cdn/primevue/images/card-vue.jpg" />-->
                        <div class="w-full h-48 bg-gray-500 content-center">
                            <h1 class="center text-white">+</h1>
                        </div>
                    </template>
                    <template #title>新增一个作品</template>
                    <template #subtitle>

                    </template>
                    <template #content>
                        <p class="m-0">

                        </p>
                    </template>
                </Card>
            </div>
<!--            <div v-if="workList.length>0" class="center col gap-2">-->
<!--                <Button v-for="work of workList" class="!text-3xl !p-5 w-full md:w-3/5" :class="{'!border-3 !border-solid !border-green-600':work.status==1,'!border-3 !border-solid !border-orange-600':work.status<1}" :label="work.name" severity="secondary" @click="refUploadWork.init(mainPage,updateWorkPage,{data:work.guiGe.competition,masterCompetition:masterCompetition,uploadRule:uploadRule,userId:userId,work:work,process:'u',returnFunction:returnFunction});confirm.close();updateWorkPage.open(mainPage);"/>-->
<!--                <Button v-for="wIndex of uploadRule.maxWorkCount-workList.length" class="!text-4xl !p-5 w-full md:w-3/5" label="+" severity="secondary" @click="showCompetitionList($event)"/>-->
<!--            </div>-->
            <div v-else class="center grid gap-4">
                <span>您还没有创建作品，请添加一个作品最多可提交{{uploadRule.maxWorkCount}}件作品</span>
                <Button class="!text-4xl" label="+" severity="secondary" @click="showCompetitionList($event)"/>
            </div>
        </div>
        <ConfirmPopup group="templating" :pt="{footer:{class:'!hidden'}}">
            <template #message="slotProps">
                <div class="flex flex-col items-center w-full gap-4 p-4 mb-4 pb-0">
                    <!--                        <i :class="slotProps.message.icon" class="!text-6xl text-primary-500"></i>-->
                    <div class="grid gap-2">
                        <div v-for="(competition,index) in lodash.filter(competitionList,(o)=>{return lodash.findIndex(workList,(w)=>{return w.guiGe.competition.id==o.id})<0})" :key="index">
                            <Button :label="competition.name" severity="info" class="w-full !px-8" @click="refUploadWork.init(mainPage,updateWorkPage,{data:competition,masterCompetition:masterCompetition,uploadRule:uploadRule,userId:userId,process:'c',returnFunction:returnFunction});confirm.close();updateWorkPage.open(mainPage);"/>
                        </div>
                    </div>
                    <p>{{ slotProps.message.message }}</p>
                </div>
            </template>
        </ConfirmPopup>
    </animationPage>

    <animationPage ref="updateWorkPage">
        <uploadWork ref="refUploadWork" :key="forceUpdateKey"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import {useStorage} from "@vueuse/core";
import { useConfirm } from "primevue/useconfirm";
import uploadWork from "@/views/user/work/uploadWork.vue";
import lodash from "lodash-es";
import oss from "@/api/oss";

const confirm = useConfirm();
const mainPage = useTemplateRef("mainPage");
const refUploadWork = useTemplateRef("refUploadWork");
const updateWorkPage = useTemplateRef("updateWorkPage");
const forceUpdateKey = ref(0);

const userId = useStorage("userId");//注意，userId不是ref对象
const workList = ref([]);
const competitionList = ref([]);
const uploadRule = ref({
    maxWorkCount: 3,
    workType:{
        image:[
            {type:0,mediaType:0,showCount:1,maxCount:1,checkExif:true,title:"上传相机原图",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",rule:{size:2*1024*1024}},
            {type:1,mediaType:0,showCount:1,maxCount:1,checkExif:false,title:"上传作品全景图主图",text:"",rule:{size:2*1024*1024}},
            {type:2,mediaType:0,showCount:2,maxCount:2,checkExif:false,title:"上传作品全景图其他角度",text:"",rule:{size:2*1024*1024}},
            {type:3,mediaType:0,showCount:4,maxCount:2,checkExif:false,title:"上传作品其他细节至少2张",text:"",rule:{size:2*1024*1024}}
        ],
        video:[
            {type:4,mediaType:1,showCount:1,maxCount:1,title:"上传视频",text:"",rule:{duration:20,size:20*1024*1024}}
        ]
    }
});

let host = inject("domain");
let masterCompetition = null;

onMounted(async () => {
    // workRest.qyCompetitionList({appId:host,masterCompetitionId:"localhost",shiQyGuiGeList:true},(res)=>{
    //     if (res.status=="OK") {
    //         if (res.data!=null) {
    //             competitionList.value = res.data;
    //         }
    //     }
    // });
    masterCompetition = (await workRest.gainCache8MasterCompetitionInfo(host)).masterCompetitionInfo;
    competitionList.value = masterCompetition.competitionList;
    loadWorksByUser();
});

function loadWorksByUser() {
    workRest.qyWorks({appId:host,userId:userId.value,masterCompetitionId:masterCompetition.id,shiWorkItemList:true},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                workList.value = res.data;
                lodash.forEach(workList.value,(work)=>{
                    lodash.forEach(lodash.filter(work.workItemList,(o)=>{return o.mediaType==0}),(workItem)=>{
                        workItem.tempMap = {imgPath:oss.buildImgPath(workItem.path)};
                    })
                });
            }
        }
    });
}

function showCompetitionList(event) {
    confirm.require({
        target: event.currentTarget,
        group: 'templating',
        message: '请选择参赛类别'
    });
}

function returnFunction(obj) {
    let work = null;
    if (obj.process=="c") {
        work = obj.work;
        workList.value.push(work);
    } else {
        let wIndex = lodash.findIndex(workList.value,(o)=>{return o.id==obj.work.id});
        workList.value[wIndex] = obj.work;
        work = workList.value[wIndex];
    }
    if (!work.workItemList) {
        work.workItemList = [];
    }
    if (!work.tempMap?.workItemList) {
        if (!work.tempMap) {
            work.tempMap = {};
        }
        work.tempMap.workItemList = [];
    }
    work.workItemList = lodash.concat(work.workItemList,obj.work.tempMap.workItemList,obj.work.tempMap.upedItemList);
    lodash.forEach(workList.value,(work)=>{
        lodash.forEach(lodash.filter(work.workItemList,(o)=>{return o.mediaType==0}),(workItem)=>{
            workItem.tempMap = {imgPath:oss.buildImgPath(workItem.path)};
        })
    });
    // loadWorksByUser();
    forceUpdateKey.value++;
}
</script>

<style scoped lang="scss">

</style>
