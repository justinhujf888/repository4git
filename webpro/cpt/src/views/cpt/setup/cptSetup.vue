<template>
    <div class="p-2 card">
        <div class="flex flex-wrap items-center justify-between">
            <span class="text-base">赛事基础信息设置</span>
            <Button label="基础信息设置" _click="Page.navigateTo('updateSiteCpt',null)" @click="visible4updateSiteCpt=true"/>
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
            <Button label="主题图设置" _click="Page.navigateTo('updateSiteWorkitem',null)" @click="visible4updateSiteZhuTiWorkitem=true"/>
        </div>
        <DataView :value="siteZhuTiWorkItemList" layout="grid" :pt="{
            emptyMessage:{
                class:'opacity-0'
            }
        }">
            <template #grid="slotProps">
                <div _class="grid grid-cols-12 gap-4" class="row flex-wrap">
                    <div v-for="(item, index) in slotProps.items" :key="index" class="col-span-4 sm:col-span-2 md:col-span-2 xl:col-span-2 p-2">
                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">
                            <div class="bg-surface-50 flex justify-center rounded p-1">
                                <div class="relative mx-auto">
                                    <img class="rounded w-32" :src="oss.buildImgPath(item.path)" :alt="item.name" style="max-width: 300px"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </DataView>
    </div>

    <div class="p-2 card">
        <div class="flex flex-wrap items-center justify-between">
            <span class="text-base">设置系列赛事作品</span>
            <Button label="系列赛事作品设置" @click="visible4updateSiteZuoPingWorkitem=true"/>
        </div>
        <DataView :value="siteZuoPingWorkItemList" layout="grid" :pt="{
            emptyMessage:{
                class:'opacity-0'
            }
        }">
            <template #grid="slotProps">
                <div class="grid grid-cols-12 gap-4">
                    <div v-for="(item, index) in slotProps.items" :key="index" class="col-span-4 sm:col-span-2 md:col-span-2 xl:col-span-2 p-2">
                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">
                            <div class="bg-surface-50 flex justify-center rounded p-1">
                                <div class="relative mx-auto">
                                    <img class="rounded w-full" :src="`https://primefaces.org/cdn/primevue/images/product/${item.image}`" :alt="item.name" style="max-width: 300px"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </DataView>
    </div>

    <div class="p-2 card">
        <div class="flex flex-wrap items-center justify-between">
            <span class="text-base">设置组委会成员信息</span>
            <Button label="组委会成员设置" @click="visible4updateSiteOrgHuman=true"/>
        </div>
        <DataView :value="siteOrgHumanList" layout="grid" :pt="{
            emptyMessage:{
                class:'opacity-0'
            }
        }">
            <template #grid="slotProps">
                <div class="grid grid-cols-12 gap-4">
                    <div v-for="(item, index) in slotProps.items" :key="index" class="col-span-4 sm:col-span-2 md:col-span-2 xl:col-span-2 p-2">
                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">
                            <div class="bg-surface-50 flex justify-center rounded p-1">
                                <div class="relative mx-auto">
                                    <img class="rounded w-full" :src="`https://primefaces.org/cdn/primevue/images/product/${item.image}`" :alt="item.name" style="max-width: 300px"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </DataView>
    </div>

    <div>
        <Dialog v-model:visible="visible4updateSiteCpt" modal header="设置赛事基础信息" class="w-11/12 h-full">
            <updateSiteCpt @callClose="updateSiteCptDialogClose"/>
        </Dialog>
    </div>
    <div>
        <Dialog v-model:visible="visible4updateSiteZhuTiWorkitem" modal header="上传主题图" class="w-11/12 h-full">
            <updateSiteWorkitem :files="siteZhuTiWorkItemList" :sourceId="host" :sourceType="0" :type="0" :filePreKey="`cpt/${host}/zhiti`" @callClose="updateSiteZhiTiWorkitemDialogClose"/>
        </Dialog>
    </div>
    <div>
        <Dialog v-model:visible="visible4updateSiteZuoPingWorkitem" modal header="设置系列作品" class="w-11/12 h-full">
            <updateSiteWorkitem :sourceId="host" :sourceType="0" :type="1" :filePreKey="`cpt/${host}/zuoping`" @callClose="updateSiteZuoPingWorkitemDialogClose"/>
        </Dialog>
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
import Page from '@/api/uniapp/page';
import primeUtil from '@/api/prime/util';
import util from "@/api/util";
import oss from "@/api/oss";
import updateSiteWorkitem from "@/views/cpt/setup/updateSiteWorkitem.vue";
import updateSiteCpt from "@/views/cpt/setup/updateSiteCpt.vue";

const siteCompetition = ref(Beans.siteCompetition());
const siteZhuTiWorkItemList = ref([]);
const siteZuoPingWorkItemList = ref([]);
const siteOrgHumanList = ref([]);

const visible4updateSiteCpt = ref(false);
const visible4updateSiteZhuTiWorkitem = ref(false);
const visible4updateSiteZuoPingWorkitem = ref(false);
const visible4updateSiteOrgHuman = ref(false);

let errors = [];
const host = ref(util.getDomainFromUrl(window.location));

onMounted(() => {
    oss.genClient(null);
    workRest.qySiteCompetition(host.value,(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteCompetition.value = res.data;
            }
        }
    });

    workRest.qySiteWorkItemList({sourceId:host.value,sourceType:0,type:0},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteZhuTiWorkItemList.value = res.data;
                // lodash.forEach(siteZhuTiWorkItemList.value,(v,i)=>{
                //     console.log(oss.buildImgPath(v.path));
                // });
            }
        }
      });

    workRest.qySiteWorkItemList({sourceId:host.value,sourceType:0,type:1},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteZuoPingWorkItemList.value = res.data;
            }
        }
    });
});

const updateSiteZhiTiWorkitemDialogClose = ()=>{
    visible4updateSiteZhuTiWorkitem.value = false;
}

const updateSiteZuoPingWorkitemDialogClose = ()=>{
    visible4updateSiteZuoPingWorkitem.value = false;
}

const updateSiteCptDialogClose = ()=>{
    Config.getConfig();
    visible4updateSiteCpt.value = false;
}
</script>

<style scoped lang="scss">

</style>
