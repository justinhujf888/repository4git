<template>
    <div class="p-2 card">
        <div class="flex flex-wrap items-center justify-between">
            <span class="text-base">设置组委会成员信息</span>
            <Button label="组委会成员设置" @click="refOrgHumanUpdate.init(null,{process:'c',sourceId:host});orgHumanUpdatePage.open(mainPage)"/>
        </div>
        <DataTable :value="siteOrgHumanList" header="Flex Scroll" resizableColumns showGridlines stripedRows :pt="
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
            <Column header="" class="!w-20">
                <template #body="{data}">
                    <Image class="rounded w-full h-full object-cover" :src="data?.tempMap?.headImgUrl" :alt="data.name" style="max-width: 300px;" _preview :pt="{image:{class:'!w-full !h-full object-cover'}}"/>
                </template>
            </Column>
            <Column field="name" header="姓名" class="w-36"></Column>
            <Column header="简介" class="">
                <template #body="{data}">
                    <p class="truncate w-80">{{data.description}}</p>
                </template>
            </Column>
            <!--            <Column field="engName" header="Eng Name"></Column>-->
        </DataTable>
        <priviewImage v-if="siteOrgHumanList?.length>0" :files="siteOrgHumanList" class="hidden"/>
    </div>
    <animationPage ref="orgHumanUpdatePage" class="w-full absolute top-0 z-40">
        <orgHumanUpdate ref="refOrgHumanUpdate" @callClose="backOrgHumanUpdateClose"/>
    </animationPage>
</template>

<script setup>
import priviewImage from "@/components/my/priviewImage.vue";
import animationPage from "@/components/my/animationPage.vue";
import orgHumanUpdate from "@/views/cpt/setup/siteCpt/orgHumanUpdate.vue";
const refOrgHumanUpdate = useTemplateRef("refOrgHumanUpdate");
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import oss from '@/api/oss';
import lodash from "lodash";
import workRest from '@/api/dbs/workRest';

const props = defineProps(['mainPage']);
const mainPage = ref(props.mainPage);

const orgHumanUpdatePage = useTemplateRef("orgHumanUpdatePage");

const siteOrgHumanList = ref([]);

const host = ref(inject("domain"));

onMounted(() => {
    oss.genClient();
    workRest.qyOrgHumanList({appId:host.value,sourceType:0,sourceId:host.value},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteOrgHumanList.value = res.data;
                lodash.forEach(siteOrgHumanList.value,(v,i)=>{
                    v.tempMap = {};
                    v.tempMap.headImgUrl = oss.buildImgPath(v.headImgUrl);
                    v.tempMap.imgPath = v.tempMap.headImgUrl;
                });
            }
        }
    });
});

const backOrgHumanUpdateClose = (orgHuman,obj)=>{
    orgHumanUpdatePage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
