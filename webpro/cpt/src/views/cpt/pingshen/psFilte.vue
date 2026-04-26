<template>
    <animationPage ref="mainPage" :show="true">
        <div class="md:row col p-2 card">
            <div class="w-full md:w-1/6 text-sm">
                <Tree :value="comTree" v-model:selectionKeys="selectedTreeNodeKey" v-model:expandedKeys="expandedTreeNodeKey" selectionMode="single" @node-select="onNodeSelect"></Tree>
            </div>
            <div class="flex-1 p-2">
                <h4>{{selTreeLabel}}</h4>
                <div v-if="pageUtil.content?.length>0">
                    {{selTypeCount}}个作品选中
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
                                            <ToggleSwitch v-model="item.temp" @change="switchChange(item,index)"/>
                                        </template>
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
</template>

<script setup>
import {Config} from "@/api/config";
import {inject, onMounted, ref, useTemplateRef} from 'vue';
import util from "@/api/util";
import workRest from "@/api/dbs/workRest";
import lodash from "lodash-es";
import oss from "@/api/oss";
import {Beans} from "@/api/dbs/beans";
import videoInfo from "@/components/my/videoInfo.vue";
import priviewImage from "@/components/my/priviewImage.vue";
import animationPage from "@/components/my/animationPage.vue";
import dialog from "@/api/uniapp/dialog";

const mainPage = useTemplateRef("mainPage");
const uploadRule = ref(null);
const comTree = ref([]);
const selectedTreeNodeKey = ref(null);
const expandedTreeNodeKey = ref(null);
const pageUtil = ref([]);
const currentPage = ref(0);
const refPriviewImage = useTemplateRef("refPriviewImage");
const selTypeCount = ref(0)
const selTreeLabel = ref(null);

let masterCompetitionId = "";
let host = inject("domain");
let hasChildren = false;
let type = 0;
let key = "";
let selWork = [];

onMounted(async () => {
    if (util.giveStorgeMessage("masterCompetitionId")) {
        masterCompetitionId = util.giveStorgeCry("masterCompetitionId");
        uploadRule.value = await workRest.gainPageSetup(host,"worksetup");
        // console.log(uploadRule.value);
        workRest.pingShenWorksInit({masterCompetitionId:masterCompetitionId,pingShenStepId:2},null);
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
    }
});

const onNodeSelect = (node) => {
    // console.log(node);
    if (node.data.type==0 && node.children.length > 0) {
        return;
    }
    type = node.data.type;
    key = node.key;
    selTreeLabel.value = node.label;
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
                                // check = false;
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
                    if (lodash.findIndex(selWork,(o)=>{return o.id==work.id}) > -1) {
                        work.temp = true;
                    }
                    work.tempMap = {};
                    work.tempMap.status = lodash.find(Beans.workStatus(),(o)=>{return o.id==work.status}).name;
                    work.tempMap.type = type;
                    work.tempMap.key = key;
                }
                selTypeCount.value = lodash.filter(selWork,(o)=>{return o.key==key}).length;
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

const switchChange = (item,index)=> {
    // console.log(item);
    let j = lodash.findIndex(selWork,(o)=>{return o.id==item.id});
    if (item.temp==true) {
        if (j < 0) {
            selWork.push({id:item.id,type:item.tempMap.type,key:item.tempMap.key});
        }
    } else {
        if (j > -1) {
            selWork.splice(j,1);
        }
    }
    selTypeCount.value = lodash.filter(selWork,(o)=>{return o.key==item.tempMap.key;}).length;
};
</script>

<style scoped lang="scss">

</style>