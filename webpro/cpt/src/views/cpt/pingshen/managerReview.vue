<template>
    <animationPage ref="mainPage" :show="true">
        <div class="md:row col p-2 card">
            <div class="w-full md:w-1/6 text-sm">
                <Tree :value="comTree" v-model:selectionKeys="selectedTreeNodeKey" v-model:expandedKeys="expandedTreeNodeKey" selectionMode="single" @node-select="onNodeSelect"></Tree>
            </div>
            <div class="flex-1 p-2">
                <div v-if="pageUtil.content?.length>0">
                    <DataView :value="pageUtil.content" class="mt-5" :pt="{
                                            emptyMessage:{
                                                class:'opacity-0'
                                            }
                                        }">
                        <template #list="slotProps">
                            <div class="col gap-2 flex-wrap">
                                <div v-for="(item, index) in slotProps.items" :key="index" class="leading-8">
                                    <Panel :header="item.name" toggleable collapsed :pt="{title:{class:'text-2xl font-bold dark:text-yellow-600'}}">
                                        <template #icons>
                                            <Button icon="pi pi-cog" severity="secondary" rounded text  @click="menuToggle($event,index)"/>
                                            <Menu ref="menu" :model="menuItems" popup />
                                        </template>
                                        <Tabs value="0">
                                            <TabList>
                                                <Tab value="0">作品信息</Tab>
                                                <Tab value="1">作品回执</Tab>
                                            </TabList>
                                            <TabPanels>
                                                <TabPanel value="0">
                                                    <div class="grid md:grid-cols-2 gap-4 mt-5">
                                                        <div class="col">
                                                            <span class="dark:text-yellow-400">作品介绍</span>
                                                            <span class="break-words">{{item.gousiDescription}}</span>
                                                        </div>
                                                        <div class="col">
                                                            <span class="dark:text-yellow-400">作品理念</span>
                                                            <span class="break-words">{{item.myMeanDescription}}</span>
                                                        </div>
                                                    </div>
                                                    <div class="grid md:grid-cols-4 gap-4 mt-5">
                                                        <div class="col" v-for="field of item.hangyeFields.data">
                                                            <span class="dark:text-yellow-400">{{field.name}}</span>
                                                            <span class="break-words">{{field.value}}</span>
                                                        </div>
                                                    </div>
                                                    <div class="grid md:grid-cols-4 gap-4 mt-5">
                                                        <div class="col" v-for="field of item.otherFields.data">
                                                            <span class="dark:text-yellow-400">{{field.name}}</span>
                                                            <span class="break-words">{{field.value}}</span>
                                                        </div>
                                                    </div>
                                                    <div class="row flex-wrap">
                                                        <div v-for="img of item.workItemList">
                                                            <Button severity="secondary" class="col center w-36 h-32 md:w-44 md:h-28 border-solid border-gray-500 border-2 rounded-xl relative p-2">
                                                                <img v-if="img.mediaType==0" :alt="img.tempMap?.imgPath" :src="img.tempMap?.imgPath" class="absolute top-0 left-0 z-10 w-full h-32 object-cover object-center" :class="{'border border-4 border-red-600 border-solid':!img.tempMap?.exifCheck}" @click="viewImg(item.workItemList,img.id)"/>
                                                                <videoInfo v-if="img.mediaType==1" :src="img.tempMap?.imgPath" class="aabsolute top-0 left-0 z-10 w-full h-32 object-cover object-center"/>
                                                            </Button>
                                                            <div class="col center mt-2">
                                                                <span class="text-sm font-semibold">{{img.tempMap?.title}}</span>
                                                                <!--                                    <span class="text-sm">{{img.tempMap?.text}}</span>-->
                                                            </div>
                                                        </div>
                                                    </div>
                                                </TabPanel>
                                                <TabPanel value="1">
                                                    <Panel v-for="(workLog,logIndex) in item.tempMap?.workLogList" :key="workLog.id">
                                                        <template #footer>
                                                            <div class="flex flex-wrap items-center justify-between gap-4">
                                                                <span class="text-surface-500 dark:text-surface-400">{{workLog.log}}</span>
                                                                <div class="flex items-center gap-2">
                                                                    <Button icon="pi pi-pencil" rounded text @click="updateMsg(workLog,logIndex,index)"></Button>
                                                                    <Button icon="pi pi-trash" severity="secondary" rounded text @click="delLog(workLog,logIndex,index)"></Button>
                                                                </div>
                                                            </div>
                                                        </template>
                                                    </Panel>
                                                </TabPanel>
                                            </TabPanels>
                                        </Tabs>
                                    </Panel>
                                </div>
                            </div>
                        </template>
                    </DataView>
                    <Paginator :rows="Config.pageSize" :totalRecords="pageUtil.totalElements" _rowsPerPageOptions="[10, 20, 30]" @page="pageChange"></Paginator>
                </div>
                <div v-else class="center">
                    <span>没有查询到数据</span>
                </div>
            </div>
            <priviewImage ref="refPriviewImage" :shiShowImgGrid="false" _class="hidden"/>
        </div>
    </animationPage>

    <animationPage ref="updateWorkPage">
        <updateWorkInfo ref="refUpdateWorkInfo"/>
    </animationPage>

    <animationPage ref="returnMessagePage">
        <returnMessage ref="refReturnMessage"/>
    </animationPage>
</template>

<script setup>
import {inject, onMounted, ref, useTemplateRef} from 'vue';
import workRest from "@/api/dbs/workRest";
import lodash from "lodash-es";
import oss from "@/api/oss";
import {Beans} from "@/api/dbs/beans";
import {Config} from "@/api/config";
import videoInfo from "@/components/my/videoInfo.vue";
import priviewImage from "@/components/my/priviewImage.vue";
import updateWorkInfo from "@/views/cpt/pingshen/updateWorkInfo.vue";
import returnMessage from "@/views/cpt/pingshen/returnMessage.vue";
import animationPage from "@/components/my/animationPage.vue";
import dialog from "@/api/uniapp/dialog";

const mainPage = useTemplateRef("mainPage");
const updateWorkPage = useTemplateRef("updateWorkPage");
const refUpdateWorkInfo = useTemplateRef("refUpdateWorkInfo");
const returnMessagePage = useTemplateRef("returnMessagePage");
const refReturnMessage = useTemplateRef("refReturnMessage");
const uploadRule = ref(null);
const comTree = ref([]);
const selectedTreeNodeKey = ref(null);
const expandedTreeNodeKey = ref(null);
const pageUtil = ref([]);
const currentPage = ref(0);
const refPriviewImage = useTemplateRef("refPriviewImage");
const menu = useTemplateRef("menu");
const menuItems = ref([
    {
        label: '修改资料',
        icon: 'pi pi-fw pi-file',
        command: ()=>{
            refUpdateWorkInfo.value.init(mainPage.value,updateWorkPage.value,{work:pageUtil.value.content[workIndex],returnFunction:returnFunction,workIndex:workIndex});
            updateWorkPage.value.open(mainPage.value);
        }
    },
    {
        label: '回执信息',
        icon: 'pi pi-fw pi-file',
        command: ()=>{
            refReturnMessage.value.init(mainPage.value,returnMessagePage.value,{process:"c",work:pageUtil.value.content[workIndex],returnFunction:returnMsgFunction,workLogIndex:workLogIndex,workIndex:workIndex});
            returnMessagePage.value.open(mainPage.value);
        }
    }
]);

let masterCompetitionId = "";
let host = inject("domain");
let hasChildren = false;
let type = 0;
let key = "";
let workIndex = -1;
let workLogIndex = -1;

onMounted(async () => {
    masterCompetitionId = (await workRest.giveCurrentMasterCompetitionSetup({keys:["masterCompetitionId"]},null))?.data?.[0]?.value;
    // console.log(masterCompetitionId);

    if (masterCompetitionId) {
        uploadRule.value = await workRest.gainPageSetup(host,"worksetup");
        console.log(uploadRule.value);
        workRest.qyCompetitionList({masterCompetitionId:masterCompetitionId,shiQyGuiGeList:true},(res)=>{
            if (res.status=="OK") {
                lodash.forEach(res.data,(c)=>{
                    let competition = {key:c.id,label:c.name,data:{bean:c,type:0,masterCompetitionId:masterCompetitionId},children:[]};
                    lodash.forEach(c.guiGeList,(g)=>{
                        competition.children.push({key:g.id,label:g.name,data:{bean:g,type:1,masterCompetitionId:masterCompetitionId}});
                    });
                    comTree.value.push(competition);
                });
            }
        });
    } else {
        dialog.alert("还未发布赛事，请先发布赛事后在进行操作");
    }
});

const onNodeSelect = (node) => {
    // console.log(node);
    if (node.data.type==0 && node.children.length > 0) {
        return;
    }
    type = node.data.type;
    key = node.key;
    currentPage.value = 0;
    hasChildren = (node.children?.length > 0);
    queryWorks(type,key,currentPage.value);
}

const queryWorks = (type,key)=>{
    let query = {};
    if (type==0 && !hasChildren) {
        query = {competitionId:key,appId:host,masterCompetitionId:masterCompetitionId,shiWorkItemList:true,statusList:[1,9],pageSize:Config.pageSize,currentPage:currentPage.value};
    } else {
        query = {guiGeId:key,appId:host,masterCompetitionId:masterCompetitionId,shiWorkItemList:true,statusList:[1,9],pageSize:Config.pageSize,currentPage:currentPage.value};
    }
    workRest.qyWorks(query,async (res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                pageUtil.value = res.data;
                if (!pageUtil.value.content) {
                    pageUtil.value.content = [];
                }
                for (let work of pageUtil.value.content) {
                    if (!work.workItemList) break;
                    for (let workItem of work.workItemList) {
                        let ru = lodash.find(uploadRule.value.workType.image,(o)=>{return o.type==workItem.type});
                        let check = true;
                        if (ru.checkExif) {
                            let exif = JSON.parse(workItem.exifInfo);
                            if (exif.Make==null) {
                                check = false;
                            }
                            if (exif.Software) {
                                let software = lodash.toUpper(exif.Software);
                                if (software.includes("ADOBE") || software.includes("PHOTOSHOP") || software.includes("PS") || software.includes("LARK") || software.includes("CAPCUT") || software.includes("JIANYING")) {
                                    check = false;
                                }
                            }
                            if (exif.Producer) {
                                let producer = lodash.toUpper(exif.Producer);
                                if (producer.includes("ADOBE") || producer.includes("PHOTOSHOP") || producer.includes("PS") || producer.includes("LARK") || producer.includes("CAPCUT") || producer.includes("JIANYING")) {
                                    check = false;
                                }
                            }
                        }
                        workItem.tempMap = {title:ru.title,exifCheck:check,imgPath:await oss.buildPathAsync(workItem.path,(workItem.mediaType==0 ? true : false),null)};
                    }
                    work.tempMap = {};
                    work.tempMap.status = lodash.find(Beans.workStatus(),(o)=>{return o.id==work.status}).name;
                    work.tempMap.workLogList = [];
                    workRest.qyWorkLog8Work({workId:work.id},(res)=>{
                        if (res.status=="OK" && res.data!=null) {
                            work.tempMap.workLogList = res.data;
                        }
                    });
                }
                // console.log(pageUtil.value);
            } else {
                pageUtil.value.content = [];
            }
        }
    });
};

const pageChange = (event)=>{
    // console.log(event);
    currentPage.value = event.page;
    queryWorks(type,key,currentPage.value);
};

const viewImg = (workItemList,imgId)=>{
    let l = lodash.filter(workItemList,(o)=>{return o.tempMap?.imgPath && o.mediaType==0});
    refPriviewImage.value.imagesShow(l,lodash.findIndex(l,(o)=>{return o.id==imgId}));
};

const menuToggle = (event,index)=> {
    // console.log(index,menu.value);
    workIndex = index;
    menu.value[index].toggle(event);
};

const returnFunction = (obj)=>{
    pageUtil.value.content[obj.workIndex] = obj.work;
};

const returnMsgFunction = (obj)=>{
    if (obj.process=="c") {
        pageUtil.value.content[obj.workIndex].tempMap.workLogList.push(obj.workLog);
    } else if (obj.process=="u") {
        // console.log(pageUtil.value.content[obj.workIndex].tempMap.workLogList[obj.workLogIndex]);
        pageUtil.value.content[obj.workIndex].tempMap.workLogList[obj.workLogIndex] = obj.workLog;
    }
};

const updateMsg = (workLog,logIndex,index) => {
    workIndex = index;
    refReturnMessage.value.init(mainPage.value,returnMessagePage.value,{process:"u",work:pageUtil.value.content[workIndex],returnFunction:returnMsgFunction,workLogIndex:logIndex,workIndex:workIndex,workLog:workLog});
    returnMessagePage.value.open(mainPage.value);
};

const delLog = (workLog,logIndex,index)=>{
    dialog.confirm("是否删除这个信息？",()=>{
        workRest.delTheWorkLog({id:workLog.id},(res)=>{
            if (res.status=="OK") {
                pageUtil.value.content[index].tempMap.workLogList.splice(logIndex,1);
                dialog.toastSuccess("消息已成功删除");
            }
        });
    },null);
};
</script>

<style scoped lang="scss">

</style>
