<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <div class="card">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">年份赛事管理</span>
                <Button label="新增年份赛事" @click="refUpdateMasterCpt.init(mainPage,updateMasterCptPage,{process:'c',returnFunction:returnFunction});updateMasterCptPage.open(mainPage)"/>
            </div>
            <DataTable :value="masterCompetitionList" v-model:expandedRows="expandedRows" dataKey="id" @rowExpand="onRowExpand" @rowCollapse="onRowCollapse" header="Flex Scroll" resizableColumns showGridlines stripedRows :pt="
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
<!--                <template #header>-->
<!--                    <div class="flex flex-wrap justify-end gap-2">-->
<!--                        <Button variant="text" icon="pi pi-plus" label="Expand All" @click="expandAll" />-->
<!--                        <Button variant="text" icon="pi pi-minus" label="收起折叠" @click="collapseAll" />-->
<!--                    </div>-->
<!--                </template>-->
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
                <Column class="w-28 !text-end col">
                    <template #body="{ data,index }">
                        <Button label="基本资料" :_model="getSplitItems(data,index)" variant="outlined" class="!text-xs rounded-2xl font-semibold" icon="pi pi-pencil" rounded size="small" @click="refUpdateMasterCpt.init(mainPage,updateMasterCptPage,{process:'u',data:data,index:index,returnFunction:returnFunction});updateMasterCptPage.open(mainPage)" :pt="{Menu:{pcMenu:{class:'!bg-green-400 !text-green-800'}}}"></Button>
<!--                        <Button label="赛事组别" class="!text-xs rounded-2xl font-semibold" variant="outlined" icon="pi pi-pencil" severity="warn" rounded size="small" @click=""></Button>-->
                    </template>
                </Column>
                <template #expansion="slotProps">
                    <Fieldset class="text-wrap text-start" legend="评审标准" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-80 sm:w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[0].command()"/>
                                </div>
                                <div class="mt-10">
                                    <p>{{slotProps.data.pxBiaozun}}</p>
                                </div>
                            </div>
                        </ScrollPanel>
                    </Fieldset>

                    <Fieldset class="text-wrap text-start" legend="主题图片" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-80 sm:w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[1].command()"/>
                                </div>
                                <div class="mt-10">
                                    <priviewImage v-if="slotProps.data.tempMap?.masterZhuTiWorkItemList" :files="slotProps.data.tempMap?.masterZhuTiWorkItemList"/>
                                </div>
                            </div>
                        </ScrollPanel>
                    </Fieldset>
                    <Fieldset class="text-wrap text-start" legend="赛事简介" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-80 sm:w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[2].command()"/>
                                </div>
                                <div class="mt-10">
                                    <DataView :value="slotProps.data.description?.data" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
                                        <template #list="slotProps">
                                            <div class="col">
                                                <div v-for="(item,index) in slotProps.items" :key="index">
                                                    <Panel :header="item.title">
                                                        <p>{{item.description}}</p>
                                                    </Panel>
                                                </div>
                                            </div>
                                        </template>
                                    </DataView>
                                </div>
                            </div>
                        </ScrollPanel>
                    </Fieldset>

                    <Fieldset class="text-wrap text-start" legend="字段配置" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-80 sm:w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[3].command()"/>
                                </div>
                                <div class="mt-10">
                                    <DataView :value="slotProps.data.setupFields?.data" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
                                        <template #list="slotProps">
                                            <div class="row gap-2 flex-wrap">
                                                <Chip v-for="(item,index) in slotProps.items" :key="index" :label="item.name" class="!bg-orange-100 !border-2 !border-orange-200 !border-solid !text-gray-800"/>
                                            </div>
                                        </template>
                                    </DataView>
                                </div>
                            </div>
                        </ScrollPanel>
                    </Fieldset>

                    <Fieldset class="text-wrap text-start" legend="组别设置" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-80 sm:w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[4].command()"/>
                                </div>
                                <div class="mt-10">
                                    <DataView :value="slotProps.data.competitionList" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
                                        <template #list="slotProps">
                                            <DataTable :value="slotProps.items">
                                                <Column field="name" header="分组名称" class="w-32"></Column>
                                                <Column header="规格">
                                                    <template #body="slotProps">
                                                        <div class="row flex-wrap gap-x-2">
                                                            <Chip v-for="(gg,index) in slotProps.data.guiGeList" :key="index" :label="gg.name" class="!bg-sky-100 !border-2 !border-sky-200 !border-solid !text-gray-800"/>
                                                        </div>
                                                    </template>
                                                </Column>
                                            </DataTable>
                                        </template>
                                    </DataView>
                                </div>
                            </div>
                        </ScrollPanel>
                    </Fieldset>
                </template>
            </DataTable>
        </div>
    </animationPage>

    <animationPage ref="updateMasterCptPage">
        <updateMasterCpt ref="refUpdateMasterCpt"/>
    </animationPage>

    <animationPage ref="zuTiPage">
        <myFileUpload ref="refFileUpload" v-if="selMasterCompetition?.tempMap?.masterZhuTiWorkItemList" :files="selMasterCompetition?.tempMap?.masterZhuTiWorkItemList" :filePreKey="`cpt/${host}/master/${selMasterCompetition?.id}/zhiti`" :fileLimit="5" @theFileUploaded="theFileUploaded" @allFilesUploaded="filesUpload" @cancel="cancelUpload" @deleteFile="deleteFile"/>
    </animationPage>

    <animationPage ref="updateDescriptionPage">
        <updateDescription ref="refUpdateDescription"/>
    </animationPage>

    <animationPage ref="updateFieldsPage">
        <updateFields ref="refUpdateFields"/>
    </animationPage>

    <animationPage ref="updateCompetitionPage">
        <updateCompetition ref="refUpdateCompetition"/>
    </animationPage>
</template>

<script setup>
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import dialog from '@/api/uniapp/dialog';
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import updateMasterCpt from "@/views/cpt/setup/masterCpt/updateMasterCpt.vue";
import updateDescription from "@/views/cpt/setup/masterCpt/updateDescription.vue";
import updateFields from "@/views/cpt/setup/masterCpt/updateFields.vue";
import updateCompetition from "@/views/cpt/setup/masterCpt/updateCompetition.vue";
import myFileUpload from "@/components/my/myFileUpload.vue";
import priviewImage from "@/components/my/priviewImage.vue";
import lodash from 'lodash-es';
import dayjs from "dayjs";
import oss from '@/api/oss';
import { Beans } from '@/api/dbs/beans';

const mainPage = useTemplateRef("mainPage");
const updateMasterCptPage = useTemplateRef("updateMasterCptPage");
const refUpdateMasterCpt = useTemplateRef("refUpdateMasterCpt");
const zuTiPage = useTemplateRef("zuTiPage");
const refFileUpload = useTemplateRef("refFileUpload");
const updateDescriptionPage = useTemplateRef("updateDescriptionPage");
const refUpdateDescription = useTemplateRef("refUpdateDescription");
const updateFieldsPage = useTemplateRef("updateFieldsPage");
const refUpdateFields = useTemplateRef("refUpdateFields");
const updateCompetitionPage = useTemplateRef("updateCompetitionPage");
const refUpdateCompetition = useTemplateRef("refUpdateCompetition");

const masterCompetitionList = ref([]);
const expandedRows = ref({});
const selMasterCompetition = ref(null);

let host = inject("domain");
let selIndex = -1;

onMounted(() => {
    oss.genClient();
    workRest.qyMasterSiteCompetition({siteCompetitionId:host},(res)=>{
        if (res.status=="OK") {
            if (res.data) {
                masterCompetitionList.value = res.data;
                // console.log(masterCompetitionList.value);
            }
        }
    });
});

const getSplitItems = (data,index)=>{
    selMasterCompetition.value = data;
    selIndex = index;
    return [
        {label:"修改基本资料",command:()=>{
                refUpdateMasterCpt.value.init(mainPage.value,updateMasterCptPage.value,{process:'u',data:data,index:index,returnFunction:returnFunction});
                updateMasterCptPage.value.open(mainPage.value);
            }},
        {label:"设置主题图",command:()=>{
                // refFileUpload.value.reLoadFiles(selMasterCompetition.value?.tempMap?.masterZhuTiWorkItemList);
                refFileUpload.value.init(selMasterCompetition.value?.tempMap?.masterZhuTiWorkItemList,null,null,null,`cpt/${host}/master/${selMasterCompetition.value?.id}/zhiti`,{data:selMasterCompetition.value});
                zuTiPage.value.open(mainPage.value);
            }},
        {label:"设置简介",command:()=>{
                refUpdateDescription.value.init(mainPage.value,updateDescriptionPage.value,{data:data,index:index,returnFunction:descriptionReturnFunction});
                updateDescriptionPage.value.open(mainPage.value);
            }},
        {label:"设置字段",command:()=>{
                refUpdateFields.value.init(mainPage.value,updateFieldsPage.value,{data:data,index:index,returnFunction:fieldsReturnFunction});
                updateFieldsPage.value.open(mainPage.value);
            }},
        {label:"设置组别",command:()=>{
                refUpdateCompetition.value.init(mainPage.value,updateCompetitionPage.value,{data:data,index:index,returnFunction:competitionReturnFunction});
                updateCompetitionPage.value.open(mainPage.value);
            }}
    ];
}

const onRowExpand = (event) => {
    selMasterCompetition.value = event.data;
    if (!selMasterCompetition.value.tempMap) {
        selMasterCompetition.value.tempMap = {};

        if (!selMasterCompetition.value.tempMap?.masterZhuTiWorkItemList) {
            workRest.qySiteWorkItemList({sourceId:selMasterCompetition.value.id,sourceType:1,type:0},(res)=>{
                if (res.status=="OK") {
                    if (res.data!=null) {
                        let masterZhuTiWorkItemList = res.data;
                        lodash.forEach(masterZhuTiWorkItemList,(v,i)=>{
                            v.tempMap = {};
                            v.tempMap.size = v.fileFields.size;
                            v.tempMap.name = v.fileFields.name;
                            v.tempMap.type = v.fileFields.type;
                            v.tempMap.imgPath = oss.buildImgPath(v.path);
                        });
                        selMasterCompetition.value.tempMap.masterZhuTiWorkItemList = masterZhuTiWorkItemList;
                    } else {
                        selMasterCompetition.value.tempMap.masterZhuTiWorkItemList = [];
                    }
                }
            });
        }

        if (!selMasterCompetition.value.competitionList) {
            workRest.qyCompetitionList({masterCompetitionId:selMasterCompetition.value.id},(res)=>{
                if (res.status=="OK") {
                    if (res.data!=null) {
                        let competitionList = res.data;
                        selMasterCompetition.value.competitionList = competitionList;
                    } else {
                        selMasterCompetition.value.competitionList = [];
                    }
                }
            });
        }
    }

    // console.log(event);
};
const onRowCollapse = (event) => {

};

const theFileUploaded = (file,index,obj)=>{
    let siteWorkItem = Beans.siteWorkItem();
    siteWorkItem.id = Beans.buildPId("zt");
    siteWorkItem.path = file.fileKey;
    siteWorkItem.type = 0;
    siteWorkItem.sourceType = 1;
    siteWorkItem.sourceId = selMasterCompetition.value.id;
    siteWorkItem.mediaType = 0;
    siteWorkItem.createDate = dayjs().valueOf();
    siteWorkItem.appId = host;
    siteWorkItem.fileFields = {name:file.name,size:file.size,type:file.type};
    workRest.updateSiteWorkItem({siteWorkItem:siteWorkItem},(res)=>{
        if (res.status=="OK") {
            siteWorkItem.tempMap = {};
            siteWorkItem.tempMap.size = siteWorkItem.fileFields.size;
            siteWorkItem.tempMap.name = siteWorkItem.fileFields.name;
            siteWorkItem.tempMap.type = siteWorkItem.fileFields.type;
            siteWorkItem.tempMap.imgPath = oss.buildImgPath(siteWorkItem.path);
            selMasterCompetition.value.tempMap?.masterZhuTiWorkItemList.push(siteWorkItem);
            dialog.toastSuccess(`文件${file.name}已上传`);
        }
    });
}

const filesUpload = (files,obj)=>{
    zuTiPage.value.close(mainPage.value);
    dialog.alertBack(`文件上传完成！`,()=>{
        // console.log(selMasterCompetition.value.tempMap?.masterZhuTiWorkItemList);
        refFileUpload.value.reLoadFiles(selMasterCompetition.value.tempMap?.masterZhuTiWorkItemList);
    });
}

const cancelUpload = ()=>{
    zuTiPage.value.close(mainPage.value);
}

const deleteFile = (file,index,obj)=>{
    if (file.id) {
        workRest.deleteSiteWorkItem({id:file.id},(res)=>{
            if (res.status=="OK") {
                oss.deleteFile(file.path);
                selMasterCompetition.value.tempMap.masterZhuTiWorkItemList.splice(index,1);
                dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
            }
        });
    } else {
        selMasterCompetition.value.tempMap.masterZhuTiWorkItemList.splice(index,1);
        dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
    }
}

const returnFunction = (obj)=>{
    obj.masterCompetition.beginDate = dayjs(obj.masterCompetition.beginDate).format("YYYY-MM-DD");
    obj.masterCompetition.endDate = dayjs(obj.masterCompetition.endDate).format("YYYY-MM-DD");
    obj.masterCompetition.pingShenDate = dayjs(obj.masterCompetition.pingShenDate).format("YYYY-MM-DD");
    obj.masterCompetition.cptDate = dayjs(obj.masterCompetition.cptDate).format("YYYY-MM-DD");
    if (obj.process=="c") {
        masterCompetitionList.value.push(obj.masterCompetition);
        dialog.toastSuccess(`${obj.masterCompetition.name}年份赛事基本资料已建立，请在列表中进行其它项目设置。`);
    } else if (obj.process=="u") {
        masterCompetitionList.value[obj.index] = obj.masterCompetition;
        dialog.toastSuccess(`${obj.masterCompetition.name}年份赛事基本资料已更新，请在列表中进行其它项目设置。`);
    }
}

const descriptionReturnFunction = (obj)=>{
    // console.log(obj);
    masterCompetitionList.value[obj.index].description = obj.data.description;
    dialog.toastSuccess(`${obj.data.name}年份赛事简介已更新`);
}

const fieldsReturnFunction = (obj)=>{
    masterCompetitionList.value[obj.index].setupFields = obj.data.setupFields;
    dialog.toastSuccess(`${obj.data.name}年份赛事字段已更新`);
}

const competitionReturnFunction = (obj)=>{
    masterCompetitionList.value[obj.index].competitionList = obj.data.competitionList;
    dialog.toastSuccess(`${obj.data.name}年份赛事组别已更新`);
}

const expandAll = () => {
    // expandedRows.value = products.value.reduce((acc, p) => (acc[p.id] = true) && acc, {});
};
const collapseAll = () => {
    expandedRows.value = null;
};

defineExpose({returnFunction});

</script>

<style scoped lang="scss">

</style>
