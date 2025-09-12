<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-8 z-40">
        <div class="card mt-12">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">赛事基础信息设置</span>
                <Button label="基础信息设置" _click="Page.navigateTo('updateSiteCpt',null)" @click="updateSiteCptPage.open(mainPage)"/>
            </div>
            <DataTable :value="[siteCompetition]" header="Flex Scroll" resizableColumns showGridlines stripedRows :pt="
    {
        table:{
            class:'min-w-full mt-5'
        },
        column:{
            bodyCell:{class:'!text-center'},
            columnHeaderContent:{class:'justify-self-center'}
        },
        pcPaginator:{

        }
    }">
                <Column field="name" header="系列赛事名称"></Column>
                <Column field="domain" header="域名"></Column>
                <Column header="系列赛事描述" class="!w-80">
                    <template #body="{data}">
                        <p class="truncate w-80">{{data.description}}</p>
                    </template>
                </Column>
                <!--            <Column field="engName" header="Eng Name"></Column>-->
            </DataTable>
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
        // window.location = window.location.href;
    }
    updateSiteCptPage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
