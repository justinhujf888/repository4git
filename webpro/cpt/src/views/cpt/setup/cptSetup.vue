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

                    <div class="row mt-12">
                        <Button type="submit" label="保存设置" class="w-full" _as="router-link" _to="/" @click="saveSetup"></Button>
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

const siteCompetition = ref(Beans.siteCompetition());

onMounted(() => {
    siteCompetition.value.appId = "test";
    siteCompetition.value.domain = "www.test.com";

});

function saveSetup() {
    workRest.setupSiteCompetition(siteCompetition.value,(res)=>{
        if (res.status=="OK") {
            dialog.toastSuccess("设置已保存");
        }
    });
}

</script>

<style scoped lang="scss">

</style>
