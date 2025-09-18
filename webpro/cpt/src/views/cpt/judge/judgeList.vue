<template>
    <div class="relative">
        <animationPage ref="mainPage" :show="true">
            <div class="card">
                <div class="flex flex-wrap items-center justify-between">
                    <span class="text-base">评委资料管理</span>
                    <Button label="新增评委资料" @click="refUpdateJudge.init(mainPage,updateJudgePage,{process:'c',returnFunction:returnFunction});updateJudgePage.open(mainPage)"/>
                </div>
                <DataTable :value="judgePageUtil.content" header="Flex Scroll" resizableColumns showGridlines stripedRows paginator :rows="Config.pageSize" :totalRecords="judgePageUtil.totalRecords" :first="judgePageUtil.number" :lazy="true" tableStyle="min-width: 50rem" paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport" currentPageReportTemplate="【{first} - {last}】  记录总数：{totalRecords}" :pageLinkSize="5" @page="updateFirst" :pt="
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
                    <Column field="headImgUrl" header="" class="w-10">
                        <template #body="{ data }">
                            <div class="center w-10">
                                <img :alt="data.name" :src="`https://primefaces.org/cdn/primevue/images/avatar/${data.headImgUrl}`" class="rounded-full w-20 h-20 object-cover" />
                            </div>
                        </template>
                    </Column>
                    <Column field="name" header="姓名" class="w-36"></Column>
                    <Column field="phone" header="手机号码" class="w-36"></Column>
                    <!--            <Column field="engName" header="Eng Name"></Column>-->
                    <Column header="简介" class="">
                        <template #body="{data}">
                            <p class="truncate w-80">{{data.description}}</p>
                        </template>
                    </Column>
                </DataTable>
            </div>
        </animationPage>
        <animationPage ref="updateJudgePage">
            <updateJudge ref="refUpdateJudge"/>
        </animationPage>
    </div>
</template>

<script setup>
import { onMounted, ref, inject, useTemplateRef } from 'vue';
import animationPage from "@/components/my/animationPage.vue";
import updateJudge from "@/views/cpt/judge/updateJudge.vue";
import dialog from '@/api/uniapp/dialog';
import userRest from "@/api/dbs/userRest";
import {Config} from "@/api/config";
import lodash from "lodash";
import util from "@/api/util";

const mainPage = useTemplateRef("mainPage");
const updateJudgePage = useTemplateRef("updateJudgePage");
const refUpdateJudge = useTemplateRef("refUpdateJudge");

const judgePageUtil = ref({});
const currentPage = ref(0);

onMounted(() => {
    loadDatas();
});

function loadDatas() {
    userRest.queryJudgeList({pageSize:Config.pageSize,currentPage:currentPage.value},(res)=>{
        if (res.status=="OK") {
            if (res.status=="OK") {
                if (res.data!=null) {
                    judgePageUtil.value = res.data;
                    console.log(judgePageUtil.value);
                }
            }
        }
    });
}

function updateFirst(e) {
    console.log(e);
    currentPage.value = e.page;
    loadDatas();
}

function returnFunction(obj) {
    dialog.toastSuccess(`${obj.data.name}评委资料已更新`);
}
</script>

<style scoped lang="scss">

</style>
