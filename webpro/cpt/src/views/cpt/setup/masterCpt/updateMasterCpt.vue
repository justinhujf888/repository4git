<template>
    <div class="card">
        <div class="flex flex-col items-center justify-center w-full">
            <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                <IftaLabel>
                    <label for="cptDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">赛事年份</label>
                    <InputText name="cptDate" v-model="masterCompetition.cptDate" class="hidden"/>
                    <DatePicker v-model="masterCompetition.name" dateFormat="yy" view="year" showIcon placeholder="请输入赛事年份" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="beginDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">报名开始时间</label>
                    <DatePicker name="beginDate" v-model="masterCompetition.beginDate" dateFormat="yy-mm-dd" showIcon placeholder="请输入报名开始时间" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="bmEndDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">报名结束时间</label>
                    <DatePicker name="bmEndDate" v-model="masterCompetition.bmEndDate" dateFormat="yy-mm-dd" showIcon placeholder="请输入报名结束时间" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="pingShenDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">评审时间</label>
                    <DatePicker name="pingShenDate" v-model="masterCompetition.pingShenDate" dateFormat="yy-mm-dd" showIcon placeholder="请输入评审时间" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="endDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">结果公布时间</label>
                    <DatePicker name="endDate" v-model="masterCompetition.endDate" dateFormat="yy-mm-dd" showIcon placeholder="请输入结果公布时间" class="w-full mb-4"/>
                </IftaLabel>

                <div class="row mt-12 center gap-4">
                    <Button type="submit" label="保存设置" class="px-8" _as="router-link" _to="/"></Button>
                    <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
                </div>
            </Form>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, inject } from 'vue';
import { Config } from "@/api/config";
import lodash from "lodash";
import workRest from '@/api/dbs/workRest';
import { Beans } from "@/api/dbs/beans";
import checker from "@/api/check/checker";
// import { useStorage,useUrlSearchParams } from '@vueuse/core';
import primeUtil from '@/api/prime/util';
import util from "@/api/util";
import dayjs from "dayjs";

const masterCompetition = ref(Beans.masterCompetition());
const date = ref(null);
let mainPage = null;
let mePage = null;
let obj = null;

let errors = [];
let host = inject("domain");

onMounted(() => {
    // console.log(util.getUrlParamJson());
    // if (useUrlSearchParams(Config.vueRouterMode)?.param) {
    //     let param = JSON.parse(useUrlSearchParams(Config.vueRouterMode).param);
    //     if (param?.goback) {
    //         Page.redirectTo("cptSetup",null);
    //         return;
    //     }
    // }
});

const resolver = ({ values }) => {
    masterCompetition.value.cptDate = dayjs(masterCompetition.value.name).format("YYYY-MM-DD");

    errors = primeUtil.checkFormRequiredValid([
        { val: masterCompetition.value.cptDate, name: "cptDate" },
        { val: masterCompetition.value.beginDate, name: "beginDate" },
        { val: masterCompetition.value.bmEndDate, name: "bmEndDate" },
        { val: masterCompetition.value.pingShenDate, name: "pingShenDate" },
        { val: masterCompetition.value.endDate, name: "endDate" }
    ]);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {

        if (obj.process=="c") {
            masterCompetition.value.id = Beans.buildPId("");
            // masterCompetition.value.name = dayjs(masterCompetition.value.cptDate).year();
            masterCompetition.value.cptDate = dayjs(masterCompetition.value.name).format("YYYY-MM-DD");
            masterCompetition.value.name = dayjs(masterCompetition.value.cptDate).format("YYYY");
            masterCompetition.value.createDate = new Date().getTime();
            masterCompetition.value.appId = host;
            masterCompetition.value.siteCompetition.id = host;
        } else if (obj.process=="u") {
            // masterCompetition.value.name = dayjs(masterCompetition.value.cptDate).year();
            masterCompetition.value.cptDate = dayjs(masterCompetition.value.name).format("YYYY-MM-DD");
            masterCompetition.value.name = dayjs(masterCompetition.value.cptDate).format("YYYY");
        }

        // masterCompetition.value.workSetup = {
        //     maxWorkCount: 2,
        //     workType:{
        //         image:[
        //             {type:0,mediaType:0,showCount:1,minCount:1,checkExif:true,title:"俯视角度",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",rule:{size:5*1024*1024}},
        //             {type:1,mediaType:0,showCount:1,minCount:1,checkExif:false,title:"侧视角度",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",rule:{size:5*1024*1024}},
        //             {type:2,mediaType:0,showCount:1,minCount:1,checkExif:false,title:"前视角度",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",rule:{size:5*1024*1024}},
        //             {type:3,mediaType:0,showCount:1,minCount:1,checkExif:false,title:"45度角度",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",rule:{size:5*1024*1024}},
        //             {type:4,mediaType:0,showCount:1,minCount:1,checkExif:false,title:"品种特征特写",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",rule:{size:5*1024*1024}},
        //             {type:5,mediaType:0,showCount:1,minCount:1,checkExif:false,title:"最美瞬间",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",rule:{size:5*1024*1024}},
        //
        //         ],
        //         video:[
        //             {type:6,mediaType:1,showCount:1,minCount:1,title:"上传视频",text:"1分钟以内原片",rule:{duration:20,size:15*1024*1024}}
        //         ]
        //     }};

        let mc = lodash.cloneDeep(masterCompetition.value);
        mc.competitionList = null;
        workRest.updateMasterCompetition({masterCompetition:mc}, (res) => {
            if (res.status == "OK") {
                obj.masterCompetition = masterCompetition.value;
                obj.returnFunction(obj);
                mePage.close(mainPage);
            }
        });
    }
};

function cancel() {
    mePage.close(mainPage);
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    if (obj.process=="u") {
        masterCompetition.value = obj.data;
    } else if (obj.process=="c") {
        masterCompetition.value = Beans.masterCompetition();
    }
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
