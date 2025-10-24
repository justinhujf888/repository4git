<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <div class="card">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">设置作品字段信息</span>
                <Button label="设置" @click="refUpdateFields.init(mainPage,updateFieldsPage,{data:siteCompetition,returnFunction:returnFunction});updateFieldsPage.open(mainPage)"/>
            </div>
            <Panel class="mt-5 w-80 sm:w-full">
                <div class="row gap-2 flex-wrap">
                    <Chip v-for="(item,index) in siteCompetition.setupFields?.data" :key="index" :label="item.name" class="!bg-orange-100 !border-2 !border-orange-200 !border-solid !text-gray-800"/>
                </div>
            </Panel>
        </div>
    </animationPage>

    <animationPage ref="updateFieldsPage" class="w-full absolute top-0 z-40">
        <updateFields ref="refUpdateFields"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import updateFields from "@/views/cpt/setup/siteCpt/updateFields.vue";
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import workRest from '@/api/dbs/workRest';
import { Beans } from '@/api/dbs/beans';
import { Config } from '@/api/config';

const mainPage = useTemplateRef("mainPage");
const updateFieldsPage = useTemplateRef("updateFieldsPage");
const refUpdateFields = useTemplateRef("refUpdateFields");

const host = ref(inject("domain"));

const siteCompetition = ref(Beans.siteCompetition());

onMounted(() => {
    workRest.qySiteCompetition(host.value,(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteCompetition.value = res.data;
                console.log(siteCompetition.value);
            }
        }
    });
});

function returnFunction(obj) {

}
</script>

<style scoped lang="scss">

</style>
