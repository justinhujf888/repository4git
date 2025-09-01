<template>
    <div class="p-2 card">
        <div class="flex flex-wrap items-center justify-between">
            <span class="text-base">赛事基础信息设置</span>
            <Button label="基础信息设置" @click="Page.navigateTo('updateSiteCpt',null)"/>
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

    <div class="p-2 card">
        <div class="flex flex-wrap items-center justify-between">
            <span class="text-base">设置赛事主题图</span>
            <Button label="主题图设置" @click="Page.navigateTo('updateSiteWorkitem',null)"/>
        </div>
        <DataView :value="siteZhuTiWorkItemList" layout="grid">
            <template #grid="slotProps">
                <div class="grid grid-cols-12 gap-4">
                    <div v-for="(item, index) in slotProps.items" :key="index" class="col-span-6 sm:col-span-4 md:col-span-4 xl:col-span-4 p-2">
                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">
                            <div class="bg-surface-50 flex justify-center rounded p-1">
                                <div class="relative mx-auto">
                                    <img class="rounded w-full" :src="`https://primefaces.org/cdn/primevue/images/product/${item.image}`" :alt="item.name" style="max-width: 300px"/>
                                </div>
                            </div>
                            <div class="pt-2">
                                <div class="flex flex-row justify-between items-start gap-2">
                                    <div>
                                        <div class="text-base font-medium mt-1">{{ item.name }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </DataView>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import dialog from '@/api/uniapp/dialog';
import {Config} from "@/api/config";
import lodash from "lodash";
import workRest from '@/api/dbs/workRest';
import { Beans } from "@/api/dbs/beans";
import checker from "@/api/check/checker";
import { useUrlSearchParams } from '@vueuse/core';
import Page from '@/api/uniapp/page';
import primeUtil from '@/api/prime/util';
import util from "@/api/util";
import { ProductService } from '@/service/ProductService';

const siteCompetition = ref(Beans.siteCompetition());
const siteZhuTiWorkItemList = ref([]);
const siteZuoPingWorkItemList = ref([]);

let errors = [];
const host = ref(window.location.host);

onMounted(() => {
    workRest.qySiteCompetition(host.value,(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteCompetition.value = res.data;
            }
        }
    });

    workRest.qySiteWorkItemList({sourceType:0,type:0},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteZhuTiWorkItemList.value = res.data;
            }
        }
        ProductService.getProducts().then((data) => (siteZhuTiWorkItemList.value = data.slice(0, 12)));
    });

    workRest.qySiteWorkItemList({sourceType:0,type:1},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteZuoPingWorkItemList.value = res.data;
            }
        }
    });
});

const getSeverity = (product) => {
    switch (product.inventoryStatus) {
        case 'INSTOCK':
            return 'success';

        case 'LOWSTOCK':
            return 'warn';

        case 'OUTOFSTOCK':
            return 'danger';

        default:
            return null;
    }
}

</script>

<style scoped lang="scss">

</style>
