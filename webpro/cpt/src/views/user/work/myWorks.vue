<template>
    <animationPage ref="mainPage" :show="true" _class="w-full absolute top-0 z-40" class="">
        <div class="mt-5 ml-8"><span class="font-semibold text-base">您已上传{{workList.length}}件作品</span></div>
        <div class="card text-xl !pt-1">
            <div v-if="workList.length>0" class="center grid xs:grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mt-4">
                <Card class="overflow-hidden cursor-pointer" v-for="work of workList" @click="refUploadWork.init(mainPage,updateWorkPage,{data:work.competition,masterCompetition:masterCompetition,uploadRule:uploadRule,userId:userId,work:work,process:'u',returnFunction:returnFunction,refreashUpdateKey:refreashUpdateKey});showDialog=false;updateWorkPage.open(mainPage);" :pt="{root:{class:'!border-none !rounded-none !shadow-none'},body:{class:'!border-none !rounded-none !shadow-none'},content:{class:'!border-none !rounded-none !shadow-none'}}">
                    <template #header>
                        <div class="w-full h-80 row center bg-gray-200">
                            <img v-if="lodash.filter(work.workItemList,(o)=>{return o?.mediaType==0})?.length>0" :src="lodash.filter(work.workItemList,(o)=>{return o?.mediaType==0})[0]?.tempMap?.imgPath" class="h-full w-full object-cover object-center"/>
                            <span v-else class="iconfont text-5xl">&#xe67f;</span>
                        </div>
                    </template>
                    <template #title>
                        <div class="col gap-y-1">
                            <span class="text-base font-semibold">{{work?.name}}</span>
                            <span class="text-sm">{{work?.guiGe?.competition?.name}}</span>
                        </div>
                    </template>
                    <template #subtitle>
                        <div class="between">
<!--                            <span class="text-sm">{{work?.guiGe?.name}}</span>-->
                            <span class="text-sm">{{work?.createDate}}</span>
                            <span class="text-sm font-semibold" :class="{'text-cyan-600':work?.status==1,'text-red-600':work?.status==9}">{{work.tempMap?.status}}</span>
                        </div>
                    </template>
                    <template #content>
                        <p class="m-0">

                        </p>
                    </template>
                </Card>
                <Card class="overflow-hidden cursor-pointer" v-for="wIndex of uploadRule?.maxWorkCount-workList.length" @click="showCompetitionList($event)" :pt="{root:{class:'!border-none !rounded-none !shadow-none'},body:{class:'!border-none !rounded-none !shadow-none'},content:{class:'!border-none !rounded-none !shadow-none'}}">
                    <template #header>
<!--                        <img src="https://primefaces.org/cdn/primevue/images/card-vue.jpg" />-->
                        <div class="w-full h-80 bg-gray-500 content-center">
                            <h1 class="center text-white">+</h1>
                        </div>
                    </template>
                    <template #title>
                        <div class="between items-center">
                            <span class="text-sm">新增一个作品</span>
                            <span class="text-sm"></span>
                        </div>
                    </template>
                    <template #subtitle>
                        <span class="text-sm mx-3 font-semibold"></span>
                        <span class="text-sm"></span>
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
                <span>您还没有创建作品，请添加一个作品最多可提交{{uploadRule?.maxWorkCount}}件作品</span>
                <Button class="!text-4xl" label="+" severity="secondary" @click="showCompetitionList($event)"/>
            </div>
        </div>
        <Dialog v-model:visible="showDialog" header="请选择参赛类别" modal :dismissableMask="true" :pt="{Button:{classd:'!border-none !rounded-none !shadow-none'}}">
            <div class="flex flex-row items-center w-full gap-4 p-4 mb-4 pb-0">
                <div v-for="(competition,index) in lodash.filter(competitionList,(o)=>{return lodash.findIndex(workList,(w)=>{return w.guiGe.competition.id==o.id})<0})" :key="index" class="w-48 h-36">
                    <Button :label="competition.name" severity="secondary" class="w-full h-full !px-8 !border-2 !border-solid !border-gray-200" @click="refUploadWork.init(mainPage,updateWorkPage,{data:competition,masterCompetition:masterCompetition,uploadRule:uploadRule,userId:userId,process:'c',returnFunction:returnFunction,refreashUpdateKey:refreashUpdateKey});showDialog=false;updateWorkPage.open(mainPage);"/>
                </div>
            </div>
        </Dialog>
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
import {Beans} from "@/api/dbs/beans";

const showDialog = ref(false);
const mainPage = useTemplateRef("mainPage");
const refUploadWork = useTemplateRef("refUploadWork");
const updateWorkPage = useTemplateRef("updateWorkPage");
const forceUpdateKey = ref(0);

const userId = useStorage("userId");//注意，userId不是ref对象
const workList = ref([]);
const competitionList = ref([]);
const uploadRule = ref(null);

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
    uploadRule.value = await workRest.gainPageSetup(host,"worksetup");
    masterCompetition = (await workRest.gainCache8MasterCompetitionInfo(host)).masterCompetitionInfo;
    competitionList.value = masterCompetition.competitionList;
    loadWorksByUser();
});

function loadWorksByUser() {
    if (!userId.value) {
        return;
    }
    workRest.qyWorks({appId:host,userId:userId.value,masterCompetitionId:masterCompetition.id,shiWorkItemList:true},async (res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                workList.value = res.data;
                for (let work of workList.value) {
                    for (let workItem of lodash.filter(work.workItemList,(o)=>{return o.mediaType==0})) {
                        workItem.tempMap = {imgPath:await oss.buildPathAsync(workItem.path,true,null)};
                    }
                    work.tempMap = {};
                    work.tempMap.status = lodash.find(Beans.workStatus(),(o)=>{return o.id==work.status}).name;
                }
            }
        }
    });
}

function showCompetitionList(event) {
    showDialog.value = true;
}

async function returnFunction(obj) {
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
    for (let work of workList.value) {
        for (let workItem of lodash.filter(work.workItemList,(o)=>{return o.mediaType==0})) {
            workItem.tempMap = {imgPath:await oss.buildPathAsync(workItem.path,true,false)};
        }
    }
    // loadWorksByUser();
    forceUpdateKey.value++;
}

function refreashUpdateKey() {
    forceUpdateKey.value++;
}
</script>

<style scoped lang="scss">

</style>
