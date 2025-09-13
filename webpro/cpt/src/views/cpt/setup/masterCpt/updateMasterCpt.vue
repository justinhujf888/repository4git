<template>
    <div class="card">
        <div class="flex flex-col items-center justify-center w-full">
            <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                <IftaLabel>
                    <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">赛事年份</label>
                    <InputText type="text" name="name" placeholder="请输入赛事年份" class="w-full mb-4" v-model="masterCompetition.name" />
                </IftaLabel>
                <IftaLabel>
                    <label for="beginDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">报名时间</label>
                    <InputText type="text" name="name" placeholder="请输入报名时间" class="w-full mb-4" v-model="masterCompetition.beginDate" />
                </IftaLabel>
                <IftaLabel>
                    <label for="pingShenDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">评审时间</label>
                    <InputText type="text" name="name" placeholder="请输入评审时间" class="w-full mb-4" v-model="masterCompetition.pingShenDate" />
                </IftaLabel>
                <IftaLabel>
                    <label for="endDate" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">结果公布时间</label>
                    <InputText type="text" name="endDate" class="w-full mb-4" v-model="masterCompetition.endDate" disabled/>
                </IftaLabel>

                <div class="row mt-12 center gap-4">
                    <Button type="submit" label="保存设置" class="px-8" _as="router-link" _to="/"></Button>
                    <Button severity="warn" label="取消" class="px-8" @click="callClose(false)"></Button>
                </div>
            </Form>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, inject } from 'vue';
import dialog from '@/api/uniapp/dialog';
import { Config } from "@/api/config";
import lodash from "lodash";
import workRest from '@/api/dbs/workRest';
import { Beans } from "@/api/dbs/beans";
import checker from "@/api/check/checker";
// import { useStorage,useUrlSearchParams } from '@vueuse/core';
import Page from '@/api/uniapp/page';
import primeUtil from '@/api/prime/util';
import util from "@/api/util";

const masterCompetition = ref(Beans.masterCompetition());

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
        { val: masterCompetition.value.name, name: "name" },
        { val: masterCompetition.value.cptDate, name: "cptDate" },
        { val: masterCompetition.value.name, name: "name" },
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

        (async () => {
            await new Promise(resolve => {
                siteCompetition.value.domain = host;
                siteCompetition.value.id = host;
                siteCompetition.value.appId = host;
                workRest.setupSiteCompetition(siteCompetition.value, (res) => {
                    if (res.status == "OK") {
                        resolve();
                    }
                });
            });

            await new Promise(resolve => {
                dialog.alertBack("赛事基础信息已设置", () => {

                    resolve();
                });
            });
        })();
    }
};
</script>

<style scoped lang="scss">

</style>
