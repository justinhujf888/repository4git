<template>
    <div class="relative">
        <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
            <div class="card">
                <div v-if="workList.length>0">
                    asdfgasdf
                </div>
                <div v-else class="center grid gap-4">
                    <span>您还没有创建作品，请添加一个作品最多可提交三件作品</span>
                    <Button class="!text-4xl" label="+" severity="secondary" @click="showCompetitionList($event)"/>
                </div>
            </div>
            <ConfirmPopup group="templating" :pt="{footer:{class:'!hidden'}}">
                <template #message="slotProps">
                    <div class="flex flex-col items-center w-full gap-4 p-4 mb-4 pb-0">
<!--                        <i :class="slotProps.message.icon" class="!text-6xl text-primary-500"></i>-->
                        <div class="grid gap-2">
                            <div v-for="(competition,index) in competitionList" :key="index">
                                <Button :label="competition.name" severity="info" class="w-full !px-8" @click="refUploadWork.init(mainPage,updateWorkPage,{data:competition,returnFunction:returnFunction});confirm.close();updateWorkPage.open(mainPage);"/>
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
    </div>
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

const userId = useStorage("userId");
const workList = ref([]);
const competitionList = ref([]);

let host = inject("domain");

onMounted(() => {
    workRest.qyWorks({appId:host,userId:userId.value,masterCompetitionId:"localhost"},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                workList.value = res.data;
            }
        }
    });
    workRest.qyCompetitionList({appId:host,masterCompetitionId:"localhost"},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                competitionList.value = res.data;
            }
        }
    });
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
