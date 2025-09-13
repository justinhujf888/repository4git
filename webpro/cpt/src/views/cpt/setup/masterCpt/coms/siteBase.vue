<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <div class="card">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">年份赛事管理</span>
                <Button label="新增年份赛事" @click="updateMasterCptPage.open()"/>
            </div>
            <DataTable :value="masterCompetitionList" dataKey="id" header="Flex Scroll" resizableColumns showGridlines stripedRows :pt="
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
                        <Button icon="pi pi-pencil" severity="secondary" rounded></Button>
                    </template>
                </Column>
            </DataTable>
        </div>
    </animationPage>

    <animationPage ref="updateMasterCptPage">
        <updateMasterCpt/>
    </animationPage>
</template>

<script setup>
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import updateMasterCpt from "@/views/cpt/setup/masterCpt/updateMasterCpt.vue";

const updateMasterCptPage = useTemplateRef("updateMasterCptPage");

const masterCompetitionList = ref([]);

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
</script>

<style scoped lang="scss">

</style>
