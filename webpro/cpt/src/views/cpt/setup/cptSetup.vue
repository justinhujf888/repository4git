<template>
    <div class="p-2 card">
        <div class="start overflow-hidden">
            <div class="flex flex-col items-center justify-center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                    <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">赛事名称</label>
                    <InputText type="text" name="name" placeholder="请输入赛事名称" class="w-full mb-4" v-model="siteCompetition.name" />

                    <label for="domain" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">域名</label>
                    <InputText type="text" name="domain" class="w-full mb-4" v-model="siteCompetition.domain" disabled/>

                    <label for="description" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">简介</label>
                    <Textarea v-model="siteCompetition.description" autoResize rows="15" class="w-full" />

                    <div class="row mt-12 center">
                        <Button type="submit" label="保存设置" class="px-8" _as="router-link" _to="/"></Button>
                    </div>
                </Form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import dialog from '@/api/uniapp/dialog';
import {Config} from "@/api/config";
import lodash from "lodash";
import workRest from '@/api/dbs/workRest';
import { Beans } from "@/api/dbs/beans";
import checker from "@/api/check/checker";
import { useStorage } from '@vueuse/core';
import Page from '@/api/uniapp/page';
import primeUtil from '@/api/prime/util';
import util from "@/api/util";

const siteCompetition = ref(Beans.siteCompetition());

let errors = [];
let host = window.location.host;

onMounted(() => {
    workRest.qySiteCompetition("001",(res)=>{
        if (res.status=="OK") {
            siteCompetition.value = res.data;
            siteCompetition.value.domain = host;
        }
    });
});

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:siteCompetition.value.name,name:"name"},
        {val:siteCompetition.value.domain,name:"domain"}
    ]);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {

        (async ()=>{
            await new Promise(resolve => {
                siteCompetition.value.domain = host;
                workRest.setupSiteCompetition(siteCompetition.value,(res)=>{
                    if (res.status=="OK") {
                        resolve();
                    }
                });
            });

            await new Promise(resolve => {
                localStorage.setItem("siteCpt",util.encryptStoreInfo(JSON.stringify(siteCompetition.value)));
                // useStorage("siteCpt",util.encryptStoreInfo(JSON.stringify(siteCompetition.value)));
                resolve();
            });

            await new Promise(resolve => {
                dialog.alertBack("设置已保存",()=>{
                    Config.getConfig();
                    window.location = window.location.href;
                });
                resolve();
            });
        })();
    }
};

</script>

<style scoped lang="scss">

</style>
