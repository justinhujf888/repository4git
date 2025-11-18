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
<!--                        <Button label="赛事类别" class="!text-xs rounded-2xl font-semibold" variant="outlined" icon="pi pi-pencil" severity="warn" rounded size="small" @click=""></Button>-->
                    </template>
                </Column>
                <template #expansion="slotProps">
                    <Fieldset class="text-wrap text-start" legend="评审标准" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-80 sm:w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[5].command()"/>
                                </div>
                                <div class="mt-10">
                                    <DataView :value="slotProps.data.pxBiaozun?.data" :pt="{
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

                    <Fieldset class="text-wrap text-start" legend="主题图片" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-80 sm:w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[1].command()"/>
                                </div>
                                <div class="mt-10">
                                    <priviewImage v-model="slotProps.data.tempMap.masterZhuTiWorkItemList"/>
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
                                    <Panel header="作品字段">
                                        <DataView :value="slotProps.data.setupFields?.data" class="mt-5" :pt="{
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
                                    </Panel>
                                    <Panel header="评审字段">
                                        <DataView :value="slotProps.data.setupFields?.pingshen" class="mt-5" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
                                            <template #list="slotProps">
                                                <div class="row gap-2 flex-wrap">
                                                    <Chip v-for="(item,index) in slotProps.items" :key="index" :label="item.name+'（' + item.fen +'分)'" class="!bg-orange-100 !border-2 !border-orange-200 !border-solid !text-gray-800"/>
                                                </div>
                                            </template>
                                        </DataView>
                                    </Panel>
                                </div>
                            </div>
                        </ScrollPanel>
                    </Fieldset>

                    <Fieldset class="text-wrap text-start" legend="分组及评委设置" :toggleable="true" :collapsed="true">
                        <ScrollPanel class="w-full">
                            <div class="!relative w-full">
                                <div class="absolute -top-10 right-1 z-100">
                                    <Button label="设置组别" size="small" severity="warn" rounded @click="getSplitItems(slotProps.data,slotProps.index)[4].command()"/>
                                </div>
                                <div class="mt-10 col md:row w-full">
                                    <Tree v-model:selectionKeys="selectedTreeNodeKey" :value="slotProps.data.tempMap.comTree" selectionMode="single" class="w-full md:w-1/4 text-sm" @node-select="onNodeSelect"></Tree>
                                    <div class="flex-1 p-2">
                                        <div v-if="slotProps.data.tempMap.judgeData?.competitionId">
                                            <div class="center">
                                                <span class="text-base font-semibold">{{slotProps.data.tempMap?.judgeData?.type==0 ? '类别' : '组别'}}{{slotProps.data.tempMap?.judgeData?.name}}评委</span>
                                            </div>
                                            <div v-for="flow of slotProps.data.tempMap?.judgeData?.data">
                                                <Fieldset :legend="flow.name" class="!relative">
                                                    <div class="mt-5">
                                                        <div v-if="flow.type==0" class="min-h-24">
                                                            <Chip v-for="ju of flow.setupData" :label="ju.name" :image="ju.tempImg"/>
                                                        </div>
                                                        <div v-if="flow.type==1" class="min-h-24">
                                                            <DataTable :value="flow.setupData" class="mt-12">
                                                                <Column header="姓名" class="w-48">
                                                                    <template #body="slotProps">
                                                                        <Chip :label="slotProps.data.name" :image="slotProps.data.tempImg" />
                                                                    </template>
                                                                </Column>
                                                                <Column header="字段">
                                                                    <template #body="slotProps">
                                                                        <div class="row flex-wrap gap-x-2">
                                                                            <Chip v-for="(f,index) in slotProps.data.fields" :key="index" :label="f.name"/>
                                                                        </div>
                                                                    </template>
                                                                </Column>
                                                            </DataTable>
                                                        </div>
                                                    </div>
                                                </Fieldset>
                                            </div>
                                            <div class="mt-5 center">
                                                <Button class="!bg-blue-800 !text-white !rounded-2xl !text-base place-self-start" @click="getSplitItems(slotProps.data,slotProps.index)[6].command()">评委设置</Button>
                                            </div>
                                        </div>
                                    </div>
<!--                                    <DataView :value="slotProps.data.competitionList" :pt="{-->
<!--                                        emptyMessage:{-->
<!--                                            class:'opacity-0'-->
<!--                                        }-->
<!--                                    }">-->
<!--                                        <template #list="slotProps">-->
<!--                                            <DataTable :value="slotProps.items">-->
<!--                                                <Column field="name" header="类别名称" class="w-32"></Column>-->
<!--                                                <Column header="分组">-->
<!--                                                    <template #body="slotProps">-->
<!--                                                        <div class="row flex-wrap gap-x-2">-->
<!--                                                            <Chip v-for="(gg,index) in slotProps.data.guiGeList" :key="index" :label="gg.name" class="!bg-sky-100 !border-2 !border-sky-200 !border-solid !text-gray-800"/>-->
<!--                                                        </div>-->
<!--                                                    </template>-->
<!--                                                </Column>-->
<!--                                            </DataTable>-->
<!--                                        </template>-->
<!--                                    </DataView>-->
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
        <myFileUpload ref="refFileUpload" v-if="selMasterCompetition?.tempMap?.masterZhuTiWorkItemList?.length>=0" :files="selMasterCompetition?.tempMap?.masterZhuTiWorkItemList" :filePreKey="`cpt/${host}/master/${selMasterCompetition?.id}/zhiti`" :fileLimit="5" @theFileUploaded="theFileUploaded" @allFilesUploaded="filesUpload" @cancel="cancelUpload" @deleteFile="deleteFile"/>
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

    <animationPage ref="updateJudgeSetupPage">
        <updateJudgeSetup ref="refUpdateJudgeSetup"/>
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
import updateJudgeSetup from "@/views/cpt/setup/masterCpt/updateJudgeSetup.vue";
import myFileUpload from "@/components/my/myFileUpload.vue";
import priviewImage from "@/components/my/priviewImage.vue";
import lodash from 'lodash-es';
import dayjs from "dayjs";
import oss from '@/api/oss';
import { Beans } from '@/api/dbs/beans';
import systemRest from "@/api/dbs/systemRest";

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
const updateJudgeSetupPage = useTemplateRef("updateJudgeSetupPage");
const refUpdateJudgeSetup = useTemplateRef("refUpdateJudgeSetup");

const masterCompetitionList = ref([]);
const expandedRows = ref({});
const selMasterCompetition = ref(null);
const selectedTreeNodeKey = ref(null);
const pingShenflow = ref(null);

let host = inject("domain");
let selIndex = -1;

onMounted(() => {
    workRest.qyMasterSiteCompetition({siteCompetitionId:host},(res)=>{
        if (res.status=="OK") {
            if (res.data) {
                masterCompetitionList.value = res.data;
                // console.log("masterCompetitionList",masterCompetitionList.value);
            }
        }
    });
    systemRest.pingShenFlow({},(res)=>{
        if (res.status=="OK") {
            pingShenflow.value = res.data;
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
                refUpdateDescription.value.init(mainPage.value,updateDescriptionPage.value,{data:data,index:index,returnFunction:descriptionReturnFunction,id:"masterCompetition.description",title:"赛事描述标题内容设置"});
                updateDescriptionPage.value.open(mainPage.value);
            }},
        {label:"设置字段",command:()=>{
                refUpdateFields.value.init(mainPage.value,updateFieldsPage.value,{data:data,index:index,returnFunction:fieldsReturnFunction});
                updateFieldsPage.value.open(mainPage.value);
            }},
        {label:"设置类别",command:()=>{
                refUpdateCompetition.value.init(mainPage.value,updateCompetitionPage.value,{data:data,index:index,returnFunction:competitionReturnFunction});
                updateCompetitionPage.value.open(mainPage.value);
            }},
        {label:"设置评审标准",command:()=>{
                refUpdateDescription.value.init(mainPage.value,updateDescriptionPage.value,{data:data,index:index,returnFunction:descriptionReturnFunction,id:"masterCompetition.pxBiaozun",title:"评审标准标题内容设置"});
                updateDescriptionPage.value.open(mainPage.value);
            }},
        {label:"设置评委",command:()=>{
                refUpdateJudgeSetup.value.init(mainPage.value,updateJudgeSetupPage.value,{data:data,index:index,returnFunction:judgeSetupReturnFunction});
                updateJudgeSetupPage.value.open(mainPage.value);
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
                        lodash.forEach(masterZhuTiWorkItemList,(v)=>{
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
            workRest.qyCompetitionList({masterCompetitionId:selMasterCompetition.value.id,shiQyGuiGeList:true},(res)=>{
                if (res.status=="OK") {
                    if (res.data!=null) {
                        selMasterCompetition.value.competitionList = res.data;
                        // console.log("onRowExpand",selMasterCompetition.value.competitionList);
                        if (!selMasterCompetition.value.tempMap) {
                            selMasterCompetition.value.tempMap = {};
                        }
                        selMasterCompetition.value.tempMap.comTree = [];
                        lodash.forEach(selMasterCompetition.value.competitionList,(c)=>{
                            let competition = {key:c.id,label:c.name,data:{bean:c,type:0,masterCompetitionId:selMasterCompetition.value.id},children:[]};
                            lodash.forEach(c.guiGeList,(g)=>{
                                competition.children.push({key:g.id,label:g.name,data:{bean:g,type:1,masterCompetitionId:selMasterCompetition.value.id}});
                            });
                            selMasterCompetition.value.tempMap.comTree.push(competition);
                        });
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

const onNodeSelect = (node) => {
    // console.log(node);
    let masterCompetition = lodash.find(masterCompetitionList.value,(mc)=>{return mc.id==node.data.masterCompetitionId});
    // console.log(judgeSetup);
    if (!masterCompetition.judgeSetup) {
        masterCompetition.tempMap.judgeData = {id:node.key,type:node.data.type,name:node.label};
        if (node.data.type==0) {
            masterCompetition.tempMap.judgeData.competitionId = node.data.bean.id;
            masterCompetition.tempMap.judgeData.guiGeList = [];
        } else if (node.data.type==1) {
            masterCompetition.tempMap.judgeData.competitionId = node.data.bean.competition.id;
        }
        masterCompetition.tempMap.judgeData.data = pingShenflow.value.flow;
    } else {
        if (node.data.type==0) {
            masterCompetition.tempMap.judgeData = lodash.find(masterCompetition.judgeSetup.datas,(c)=>{return c.id==node.key});
        } else if (node.data.type==1) {
            let ct = lodash.find(masterCompetition.judgeSetup.datas,(c)=>{return c.competitionId==node.data.bean.competition.id});
            if (ct) {
                masterCompetition.tempMap.judgeData = lodash.find(ct.guiGeList,(g)=>{return g.id==node.key});
            }
        }
        if (!masterCompetition.tempMap.judgeData) {
            masterCompetition.tempMap.judgeData = {id:node.key,type:node.data.type,name:node.label};
            if (node.data.type==0) {
                masterCompetition.tempMap.judgeData.competitionId = node.data.bean.id;
                masterCompetition.tempMap.judgeData.guiGeList = [];
            } else if (node.data.type==1) {
                masterCompetition.tempMap.judgeData.competitionId = node.data.bean.competition.id;
            }
            masterCompetition.tempMap.judgeData.data = pingShenflow.value.flow;
        }
    }
    // console.log(masterCompetition.judgeSetup);
};

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
    dialog.toastSuccess(`${obj.data.name}${obj.title}已更新`);
}

const fieldsReturnFunction = (obj)=>{
    masterCompetitionList.value[obj.index].setupFields = obj.data.setupFields;
    dialog.toastSuccess(`${obj.data.name}年份赛事字段已更新`);
}

const competitionReturnFunction = (obj)=>{
    masterCompetitionList.value[obj.index].competitionList = obj.data.competitionList;
    dialog.toastSuccess(`${obj.data.name}年份赛事类别已更新`);
}

const judgeSetupReturnFunction = (obj)=>{
    // console.log(obj);
    // masterCompetitions是子组件深拷贝的，realMasterCompetition则是引用的
    let masterCompetition = obj.data;
    let realMasterCompetition = lodash.find(masterCompetitionList.value,(mc)=>{return mc.id==masterCompetition.id});
    lodash.forEach(masterCompetition.tempMap.judgeData?.data,(v)=>{
        lodash.forEach(v.setupData,(j)=>{
            j.tempImg = null;
        });
    });

    if (!realMasterCompetition.judgeSetup) {realMasterCompetition.judgeSetup={datas:[]};}
    let ct = lodash.find(realMasterCompetition.judgeSetup.datas,(c)=>{return c.competitionId==masterCompetition.tempMap.judgeData.competitionId});
    if (masterCompetition.tempMap.judgeData.type==0) {
        if (!ct) {
            ct = masterCompetition.tempMap.judgeData;
            realMasterCompetition.judgeSetup.datas.push(ct);
        } else {
            ct.data = masterCompetition.tempMap.judgeData.data;
        }
    } else if (masterCompetition.tempMap.judgeData.type==1) {
        if (!ct) {
            ct = {id:masterCompetition.tempMap.judgeData.competitionId,competitionId:masterCompetition.tempMap.judgeData.competitionId,name:masterCompetition.tempMap.judgeData.name,type:0,data:null,guiGeList:[{...masterCompetition.tempMap.judgeData}]};
            realMasterCompetition.judgeSetup.datas.push(ct);
        } else {
            if (!ct.guiGeList) {
                ct.guiGeList=[];
                ct.guiGeList.push(masterCompetition.tempMap.judgeData);
            } else {
                let ggData = lodash.find(ct.guiGeList,(o)=>{return o.id==masterCompetition.tempMap.judgeData.id});
                if (ggData) {
                    ggData.data = masterCompetition.tempMap.judgeData.data;
                } else {
                    ct.guiGeList.push(masterCompetition.tempMap.judgeData);
                }
            }
        }
    }
    // console.log(masterCompetitionList.value);
    workRest.updateJudgeSetup({id:realMasterCompetition.id,judgeSetup:realMasterCompetition.judgeSetup},(res)=>{
        if (res.status=="OK") {
            dialog.toastSuccess("评委设置已更新");
        }
    });
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
