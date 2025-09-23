<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-8 z-40">
        <div class="card">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">设置组委会成员信息</span>
                <Button label="新增组委会成员" @click="refOrgHumanUpdate.init(null,{process:'c',sourceId:host});orgHumanUpdatePage.open(mainPage)"/>
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
                <Column header="" class="w-16">
                    <template #body="{data,index}">
                        <img class="rounded-full w-16 h-16 object-cover" :src="data?.tempMap?.headImgUrl" :alt="data.name" style="max-width: 300px;" _preview :pt="{image:{class:'!w-full !h-full object-cover'}}" @click="refPriviewImage.imagesShow(siteOrgHumanList,index)"/>
                    </template>
                </Column>
                <Column field="name" header="姓名" class="w-36"></Column>
                <Column header="简介">
                    <template #body="{data}">
                        <p class="truncate w-96">{{data.description}}</p>
                    </template>
                </Column>
                <!--            <Column field="engName" header="Eng Name"></Column>-->
                <Column class="w-16 !text-end">
                    <template #body="{ data,index }">
                        <Button icon="pi pi-pencil" @click="edit(data,index)" severity="secondary" rounded></Button>
                    </template>
                </Column>
            </DataTable>
            <priviewImage ref="refPriviewImage" v-if="siteOrgHumanList?.length>0" :files="siteOrgHumanList" :shiShowImgGrid="false" _class="hidden"/>
        </div>
    </animationPage>

    <animationPage ref="orgHumanUpdatePage" class="w-full absolute top-8 z-40">
        <orgHumanUpdate ref="refOrgHumanUpdate" @callClose="backOrgHumanUpdateClose"/>
    </animationPage>
</template>

<script setup>
import priviewImage from "@/components/my/priviewImage.vue";
import animationPage from "@/components/my/animationPage.vue";
import orgHumanUpdate from "@/views/cpt/setup/siteCpt/updateOrgHuman.vue";
const refOrgHumanUpdate = useTemplateRef("refOrgHumanUpdate");
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import oss from '@/api/oss';
import lodash from "lodash";
import workRest from '@/api/dbs/workRest';
import dialog from '@/api/uniapp/dialog';

const mainPage = useTemplateRef("mainPage");

const orgHumanUpdatePage = useTemplateRef("orgHumanUpdatePage");
const refPriviewImage = useTemplateRef("refPriviewImage");

const siteOrgHumanList = ref([]);

const host = ref(inject("domain"));

onMounted(() => {
    workRest.qyOrgHumanList({appId:host.value,sourceType:0,sourceId:host.value},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteOrgHumanList.value = res.data;
                lodash.forEach(siteOrgHumanList.value,(v)=>{
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
    if (orgHuman) {
        if (obj.process=="u") {
            siteOrgHumanList.value[obj.index] = orgHuman;
            dialog.toastSuccess(`组委会成员${orgHuman.name}资料已修改`);
        } else if (obj.process=="c") {
            siteOrgHumanList.value.push(orgHuman);
            dialog.toastSuccess(`组委会成员${orgHuman.name}资料已新增`);
        }
    }
}

function edit(data,index) {
    refOrgHumanUpdate.value.init(data,{process:'u',sourceId:host,index:index});
    orgHumanUpdatePage.value.open(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
