<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-8 z-40">
        <div class="card">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">赛事基础信息设置</span>
                <Button label="基础信息设置" _click="Page.navigateTo('updateSiteCpt',null)" @click="updateSiteCptPage.open(mainPage)"/>
            </div>
            <div class="col w-full mt-5 leading-6">
                <Fieldset class="text-wrap text-start" legend="赛事名称" :toggleable="false">
                    <p>{{siteCompetition.name}}</p>
                </Fieldset>
                <Fieldset class="text-wrap text-start" legend="域名" :toggleable="false">
                    <p>{{siteCompetition.domain}}</p>
                </Fieldset>
                <Fieldset class="text-wrap text-start" legend="系列赛事描述" :toggleable="false">
                    <p>{{siteCompetition.description}}</p>
                </Fieldset>
            </div>
        </div>
    </animationPage>

    <animationPage ref="updateSiteCptPage" class="w-full absolute top-8 z-40">
        <updateSiteCpt @callClose="updateSiteCptDialogClose"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import updateSiteCpt from "@/views/cpt/setup/siteCpt/updateSiteCpt.vue";
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import workRest from '@/api/dbs/workRest';
import { Beans } from '@/api/dbs/beans';
import { Config } from '@/api/config';

const mainPage = useTemplateRef("mainPage");

const updateSiteCptPage = useTemplateRef("updateSiteCptPage");

const host = ref(inject("domain"));

const siteCompetition = ref(Beans.siteCompetition());

onMounted(() => {
    workRest.qySiteCompetition(host.value,(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteCompetition.value = res.data;
            }
        }
    });
});

const updateSiteCptDialogClose = (shiSubmit)=>{
    if (shiSubmit) {
        Config.getConfig();
        window.location = window.location.href;
    }
    updateSiteCptPage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
