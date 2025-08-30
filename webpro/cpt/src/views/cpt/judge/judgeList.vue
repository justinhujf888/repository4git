<template>
    <div class="p-2 card">
        <DataTable :value="judgePageUtil.content" header="Flex Scroll" resizableColumns showGridlines stripedRows paginator :rows="Config.pageSize" :totalRecords="300" :first="0" :lazy="true" tableStyle="min-width: 50rem" paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport" currentPageReportTemplate="【{first} - {last}】  记录总数：{totalRecords}" :pageLinkSize="5" @page="updateFirst" :pt="
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
            <Column field="index" header="编号"></Column>
            <Column field="headImgUrl" header="" class="w-10">
                <template #body="{ data }">
                    <div class="center w-10">
                        <img :alt="data.name" :src="`https://primefaces.org/cdn/primevue/images/avatar/${data.headImgUrl}`" class="w-full" />
                    </div>
                </template>
            </Column>
            <Column field="name" header="姓名"></Column>
            <Column field="phone" header="手机号码"></Column>
<!--            <Column field="engName" header="Eng Name"></Column>-->
        </DataTable>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import dialog from '@/api/uniapp/dialog';
import userRest from "@/api/dbs/userRest";
import {Config} from "@/api/config";
import lodash from "lodash";
import { ProductService } from "@/service/ProductService";
import {DataTable} from "primevue";

const judgePageUtil = ref({});

let currentPage = 0;

onMounted(() => {
    console.log(Config)
    loadDatas();
});

function loadDatas() {
    userRest.queryJudgeList({name:"1",phone:"132"},Config.pageSize,currentPage,(res)=>{
        if (res.status=="OK") {
            judgePageUtil.value = res.data;
            if (judgePageUtil.value.content==null) {
                judgePageUtil.value.content = [];

                let images = [
                    { name: 'Amy Elsner', image: 'amyelsner.png' },
                    { name: 'Anna Fali', image: 'annafali.png' },
                    { name: 'Asiya Javayant', image: 'asiyajavayant.png' },
                    { name: 'Bernardo Dominic', image: 'bernardodominic.png' },
                    { name: 'Elwin Sharvill', image: 'elwinsharvill.png' },
                    { name: 'Ioni Bowcher', image: 'ionibowcher.png' },
                    { name: 'Ivan Magalhaes', image: 'ivanmagalhaes.png' },
                    { name: 'Onyama Limba', image: 'onyamalimba.png' },
                    { name: 'Stephen Shaw', image: 'stephenshaw.png' },
                    { name: 'XuXue Feng', image: 'xuxuefeng.png' }
                ];
                let index = 0;
                lodash.forEach(ProductService.getProductsData(),(v,i)=>{
                    judgePageUtil.value.content.push({name:v.name,headImgUrl:images[index].image,phone:v.code,index:currentPage*Config.pageSize+i});
                    index ++;
                    if (i > index) {
                        index = 0;
                    }
                });
            }
        }
    });
}

function updateFirst(e) {
    console.log(e);
    currentPage = e.page;
    loadDatas();
}
</script>

<style scoped lang="scss">

</style>
