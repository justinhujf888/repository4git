<template>
    <div class="p-2 card">
        <DataTable :value="[siteCompetition]" header="Flex Scroll" resizableColumns showGridlines stripedRows :pt="
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
                    <span class="text-base">赛事基础信息设置</span>
                    <Button label="设置赛事" @click="Page.navigateTo('updatesitecpt',null)"/>
                </div>
            </template>
            <Column field="name" header="赛事名称"></Column>
            <Column field="domain" header="域名"></Column>
            <Column header="简介" class="!w-80">
                <template #body="{data}">
                    <p class="truncate w-80">{{data.description}}</p>
                </template>
            </Column>
            <!--            <Column field="engName" header="Eng Name"></Column>-->
        </DataTable>
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

const siteCompetition = ref(Beans.siteCompetition());

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
});



</script>

<style scoped lang="scss">

</style>
