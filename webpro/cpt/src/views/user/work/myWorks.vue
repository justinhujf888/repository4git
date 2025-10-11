<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <div class="card">
            <div v-if="workList.length>0">
                asdfgasdf
            </div>
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
                        <div v-for="(competition,index) in competitionList" :key="index">
                            <Button :label="competition.name" severity="info" class="w-full !px-8" @click="refUploadWork.init(mainPage,updateWorkPage,{data:competition,masterCompetition:masterCompetition,uploadRule:uploadRule,userId:userId,process:'c',returnFunction:returnFunction});confirm.close();updateWorkPage.open(mainPage);"/>
                        </div>
                    </div>
                    <p>{{ slotProps.message.message }}</p>
                </div>
            </template>
        </ConfirmPopup>
    </animationPage>

    <animationPage ref="updateWorkPage">
        <uploadWork ref="refUploadWork"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import {useStorage} from "@vueuse/core";
import { useConfirm } from "primevue/useconfirm";
import uploadWork from "@/views/user/work/uploadWork.vue";

const confirm = useConfirm();
const mainPage = useTemplateRef("mainPage");
const refUploadWork = useTemplateRef("refUploadWork");
const updateWorkPage = useTemplateRef("updateWorkPage");

const userId = useStorage("userId");//注意，userId不是ref对象
const workList = ref([]);
const competitionList = ref([]);
const uploadRule = ref({
    maxWorkCount: 3,
    workType:{
        image:[
            {type:0,mediaType:0,showCount:1,maxCount:1,title:"上传相机原图",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容"},
            {type:1,mediaType:0,showCount:1,maxCount:1,title:"上传作品全景图主图",text:""},
            {type:2,mediaType:0,showCount:2,maxCount:2,title:"上传作品全景图其他角度",text:""},
            {type:3,mediaType:0,showCount:4,maxCount:2,title:"上传作品其他细节至少2张",text:""}
        ],
        video:[
            {type:4,mediaType:1,showCount:1,maxCount:1,title:"上传视频",text:""}
        ]
    }
});

let host = inject("domain");
let masterCompetition = null;

onMounted(async () => {
    workRest.qyWorks({appId:host,userId:userId.value,masterCompetitionId:"localhost"},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                workList.value = res.data;
            }
        }
    });
    // workRest.qyCompetitionList({appId:host,masterCompetitionId:"localhost",shiQyGuiGeList:true},(res)=>{
    //     if (res.status=="OK") {
    //         if (res.data!=null) {
    //             competitionList.value = res.data;
    //         }
    //     }
    // });
    masterCompetition = (await workRest.gainCache8MasterCompetitionInfo(host)).masterCompetitionInfo;
    competitionList.value = masterCompetition.competitionList;
});

function showCompetitionList(event) {
    confirm.require({
        target: event.currentTarget,
        group: 'templating',
        message: '请选择参赛类别'
    });
}

function returnFunction(obj) {

}
</script>

<style scoped lang="scss">

</style>
