<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <div class="card">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">年份赛事管理</span>
                <Button label="新增年份赛事" @click="refUpdateMasterCpt.init(mainPage,updateMasterCptPage,{process:'c',returnFunction:returnFunction});updateMasterCptPage.open(mainPage)"/>
            </div>
            <DataTable :value="masterCompetitionList" v-model:expandedRows="expandedRows" dataKey="id" header="Flex Scroll" resizableColumns showGridlines stripedRows :pt="
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
                <Column expander class="w-5" />
                <Column field="name" header="年份" class="w-36"></Column>
<!--                <Column header="简介">-->
<!--                    <template #body="{data}">-->
<!--                        <p class="truncate w-96">{{data.description}}</p>-->
<!--                    </template>-->
<!--                </Column>-->
                <Column field="beginDate" header="报名时间"></Column>
                <Column field="pingShenDate" header="评审时间"></Column>
                <Column field="endDate" header="公布时间"></Column>
                <Column class="w-24 !text-end">
                    <template #body="{ data,index }">
                        <Button icon="pi pi-pencil" severity="secondary" rounded @click="refUpdateMasterCpt.init(mainPage,updateMasterCptPage,{process:'u',data:data,index:index,returnFunction:returnFunction});updateMasterCptPage.open(mainPage)"></Button>
                    </template>
                </Column>
                <template #expansion="slotProps">
                    <Fieldset class="text-wrap text-start" legend="评审标准" :toggleable="true">
                        <p>{{slotProps.data.pxBiaozun}}</p>
                    </Fieldset>
                </template>
            </DataTable>
        </div>
    </animationPage>

    <animationPage ref="updateMasterCptPage">
        <updateMasterCpt ref="refUpdateMasterCpt"/>
    </animationPage>
</template>

<script setup>
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import dialog from '@/api/uniapp/dialog';
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import updateMasterCpt from "@/views/cpt/setup/masterCpt/updateMasterCpt.vue";
import lodash from 'lodash-es';
import dayjs from "dayjs";

const mainPage = useTemplateRef("mainPage");
const updateMasterCptPage = useTemplateRef("updateMasterCptPage");
const refUpdateMasterCpt = useTemplateRef("refUpdateMasterCpt");

const masterCompetitionList = ref([]);
const expandedRows = ref({});

let host = inject("domain");

onMounted(() => {
    workRest.qyMasterSiteCompetition({siteCompetitionId:host},(res)=>{
        if (res.status=="OK") {
            if (res.data) {
                masterCompetitionList.value = res.data;
            }
        }
    });
});

const returnFunction = (obj)=>{
    if (obj.process=="c") {
        masterCompetitionList.value.push(obj.masterCompetition);
        dialog.toastSuccess(`${obj.masterCompetition.name}年份赛事基本资料已建立，请在列表中进行其它项目设置。`);
    } else if (obj.process=="u") {
        obj.masterCompetition.beginDate = dayjs(obj.masterCompetition.beginDate).format("YYYY-MM-DD");
        obj.masterCompetition.endDate = dayjs(obj.masterCompetition.endDate).format("YYYY-MM-DD");
        obj.masterCompetition.pingShenDate = dayjs(obj.masterCompetition.pingShenDate).format("YYYY-MM-DD");
        obj.masterCompetition.cptDate = dayjs(obj.masterCompetition.cptDate).format("YYYY-MM-DD");
        masterCompetitionList.value[obj.index] = obj.masterCompetition;
        dialog.toastSuccess(`${obj.masterCompetition.name}年份赛事基本资料已更新，请在列表中进行其它项目设置。`);
    }
}
defineExpose({returnFunction});

</script>

<style scoped lang="scss">

</style>
