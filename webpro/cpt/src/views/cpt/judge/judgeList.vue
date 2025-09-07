<template>
    <div class="p-2 card">
        <DataTable :value="judgePageUtil.content" header="Flex Scroll" resizableColumns showGridlines stripedRows paginator :rows="Config.pageSize" :totalRecords="judgePageUtil.totalRecords" :first="judgePageUtil.number" :lazy="true" tableStyle="min-width: 50rem" paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport" currentPageReportTemplate="【{first} - {last}】  记录总数：{totalRecords}" :pageLinkSize="5" @page="updateFirst" :pt="
        {
            table:{
                class:'min-w-full'
            },
            column:{
                bodyCell:{class:'!text-center'},
                columnHeaderContent:{class:'justify-self-center'}
            },
            pcPaginator:{

            }
        }">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl">评委列表</span>
                    <Button label="新增评委" @click="loadDatas"/>
                </div>
            </template>
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
</template>

<script setup>
import { onMounted, ref } from 'vue';
import dialog from '@/api/uniapp/dialog';
import userRest from "@/api/dbs/userRest";
import {Config} from "@/api/config";
import lodash from "lodash";
import util from "@/api/util";

const judgePageUtil = ref({});

const currentPage = ref(0);

onMounted(() => {
    loadDatas();
});

function loadDatas() {
    userRest.queryJudgeList({name:"1",phone:"132",appId:util.getDomainFromUrl(window.location),pageSize:Config.pageSize,currentPage:currentPage.value},(res)=>{
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
</script>

<style scoped lang="scss">

</style>
