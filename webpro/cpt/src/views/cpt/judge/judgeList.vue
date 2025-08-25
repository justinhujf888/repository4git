<template>
    <div class="p-2 card">
        <DataTable :value="judgePageUtil.content" header="Flex Scroll" resizableColumns showGridlines stripedRows paginator :rows="Config.pageSize" :totalRecords="100" :first="0" :lazy="true" tableStyle="min-width: 50rem" paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink" currentPageReportTemplate="{first} to {last} of {totalRecords}" :pt="
        {column:
            {
                bodyCell:{class:'!text-center'},
                columnHeaderContent:{class:'justify-self-center'}
            }
        }">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl">评委列表</span>
                    <Button label="新增评委" raised @click="loadDatas"/>
                </div>
            </template>
            <Column field="headImgUrl" header="" class="w-12">
                <template #body="{ data }">
                    <div class="center w-12">
                        <img :alt="data.name" :src="`https://primefaces.org/cdn/primevue/images/avatar/${data.headImgUrl}`" class="w-full" />
                    </div>
                </template>
            </Column>
            <Column field="name" header="姓名" class="self-center"></Column>
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

const judgePageUtil = ref({});

let currentPage = 0;

onMounted(() => {
    loadDatas();
});

function loadDatas() {
    userRest.queryJudgeList({},Config.pageSize,currentPage,(data)=>{
        if (data.status=="OK") {
            judgePageUtil.value = data.judgePageUtil;
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
                    judgePageUtil.value.content.push({name:v.name,headImgUrl:images[index].image,phone:v.code});
                    index ++;
                    if (i > index) {
                        index = 0;
                    }
                });
            }
        }
    });
}
</script>

<style scoped lang="scss">

</style>
