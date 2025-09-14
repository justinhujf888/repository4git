<template>
    <div class="card">
        <div class="flex flex-col items-center justify-center w-full">
            <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                <IftaLabel>
                    <label for="cptDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">赛事年份</label>
                    <InputText name="cptDate" v-model="masterCompetition.cptDate" class="hidden"/>
                    <DatePicker v-model="masterCompetition.cptDate" view="year" dateFormat="yy" showIcon placeholder="请输入赛事年份" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="beginDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">报名时间</label>
                    <DatePicker name="beginDate" v-model="masterCompetition.beginDate" dateFormat="yy-mm-dd" showIcon placeholder="请输入报名时间" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="pingShenDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">评审时间</label>
                    <DatePicker name="pingShenDate" v-model="masterCompetition.pingShenDate" dateFormat="yy-mm-dd" showIcon placeholder="请输入评审时间" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="endDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">结果公布时间</label>
                    <DatePicker name="endDate" v-model="masterCompetition.endDate" dateFormat="yy-mm-dd" showIcon placeholder="请输入结果公布时间" class="w-full mb-4"/>
                </IftaLabel>
                <IftaLabel>
                    <label for="pxBiaozun" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">评选标准</label>
                    <Textarea name="pxBiaozun" v-model="masterCompetition.pxBiaozun" autoResize rows="8" class="w-full" placeholder="请输入评选标准"/>
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
const date = ref();
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
    errors = primeUtil.checkFormRequiredValid([
        { val: masterCompetition.value.cptDate, name: "cptDate" },
        { val: masterCompetition.value.pxBiaozun, name: "pxBiaozun" },
        { val: masterCompetition.value.beginDate, name: "beginDate" },
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
            masterCompetition.value.name = dayjs(masterCompetition.value.cptDate).year();
            masterCompetition.value.createDate = new Date().getTime();
            masterCompetition.value.appId = host;
            masterCompetition.value.siteCompetition.id = host;
        } else if (obj.process=="u") {
            masterCompetition.value.name = dayjs(masterCompetition.value.cptDate).year();
        }
        workRest.updateMasterCompetition({masterCompetition:masterCompetition.value}, (res) => {
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
    }
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
