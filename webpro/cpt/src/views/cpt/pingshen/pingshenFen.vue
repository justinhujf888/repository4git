<template>
    <animationPage ref="mainPage" :show="true">
        <div class="row gap-4">
            <Button severity="warn" label="暂时保存" @click="saveSubmitJudgeWorks(false)" :disabled="shiSubmited"/>
            <Button label="提交评审" @click="saveSubmitJudgeWorks(true)" :disabled="shiSubmited"/>
        </div>
        <div class="md:row col p-2 card mt-8">
            <div class="w-full md:w-1/6 text-sm">
                <Tree :value="comTree" v-model:selectionKeys="selectedTreeNodeKey" v-model:expandedKeys="expandedTreeNodeKey" selectionMode="single" @node-select="onNodeSelect"></Tree>
            </div>
            <div class="flex-1 p-2">
                <OverlayBadge v-if="shiSubmited" value="已提交" severity="danger">
                    <h4>{{selTreeLabel}}</h4>
                </OverlayBadge>
                <div v-if="pageUtil.content?.length>0">
                    <div class="col md:row between">
                        <span>{{workCount}}个参加评选的作品</span>
                    </div>
                    <DataView :value="pageUtil.content" class="mt-5" :pt="{
                                            emptyMessage:{
                                                class:'opacity-0'
                                            }
                                        }">
                        <template #list="slotProps">
                            <div class="col gap-2 flex-wrap">
                                <div v-for="(item, index) in slotProps.items" :key="index" class="leading-8 border border-1 border-solid p-2">
                                    <Panel :header="item.name" toggleable collapsed :pt="{title:{class:'text-2xl font-bold dark:text-yellow-600'}}">
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
                                            <div class="col" v-for="field of item.hangyeFields?.data">
                                                <span class="dark:text-yellow-400">{{field.name}}</span>
                                                <span class="break-words">{{field.value}}</span>
                                            </div>
                                        </div>
                                        <div class="grid md:grid-cols-4 gap-4 mt-5">
                                            <div class="col" v-for="field of item.otherFields?.data">
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
                                    <div class="mt-2">
                                        <span>评分</span>
                                        <div class="grid md:grid-cols-4 gap-4 mt-3">
                                            <div v-for="fenItem of item.tempMap?.fenJson?.fields">
                                                <FloatLabel variant="on">
                                                    <InputNumber :name="fenItem.name" v-model="fenItem.value" :min="0" :max="fenItem.fen" size="small" :readonly="shiSubmited"/>
                                                    <label :for="fenItem.name">{{fenItem.name}} ({{fenItem.fen}}分)</label>
                                                </FloatLabel>
                                            </div>
                                        </div>
                                    </div>
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
const shiSubmited = ref(false);
const masterCompetitionId = ref("");
const masterCompetitionStatus = ref(-1);
const workCount = ref(-1);

let host = inject("domain");
let hasChildren = false;
let type = 0;
let key = "";
let judgeId = util.giveStorgeCry("managerId");
let competitionId = "";
let guiGeId = "";
let stepStatus = -1;
let shiPassList = [];
let flow = {};

onMounted(async () => {
    // console.log("judgeId",judgeId);
    let cms = (await workRest.giveCurrentMasterCompetitionSetup({keys:["masterCompetitionId","masterCompetitionStatus"]},null))?.map;
    masterCompetitionId.value = cms.masterCompetitionId;
    masterCompetitionStatus.value = cms.masterCompetitionStatus;

    if (masterCompetitionId.value && masterCompetitionId.value>0) {
        stepStatus = masterCompetitionStatus.value;
        let mcRes = await workRest.qyMasterSiteCompetition({id:masterCompetitionId.value,siteCompetitionId:host},null);
        if (mcRes.status=="OK" && mcRes.data) {
            flow = lodash.find(mcRes.data?.[0]?.flowSetup.flow,(o)=>{return o.sort == stepStatus});
            // console.log(flow);
        }

        uploadRule.value = await workRest.gainPageSetup(host,"worksetup");

        let res = await workRest.qyPingShenJudgeList({masterCompetitionId:masterCompetitionId.value,judgeId:judgeId,pingShenStepId:stepStatus},null);
        if (res.status=="OK") {
            let psJudgeList = res.data;
            // console.log(psJudgeList);
            let res2 = await workRest.qyCompetitionList({masterCompetitionId:masterCompetitionId.value,shiQyGuiGeList:true},null);
            if (res2.status=="OK") {
                let list = lodash.filter(res2.data,(o)=>{return lodash.findIndex(psJudgeList,(p)=>{return p.competitionJudgePK.competitionId==o.id})>-1});
                // console.log("list",list);
                lodash.forEach(list,(c)=>{
                    let competition = {key:c.id,label:c.name,data:{bean:c,type:0,masterCompetitionId:masterCompetitionId.value},children:[]};
                    let glist = lodash.filter(c.guiGeList,(o)=>{return lodash.findIndex(psJudgeList,(p)=>{return p.competitionJudgePK.competitionId==c.id && p.competitionJudgePK.guiGeId==o.id})>-1});
                    lodash.forEach(glist,(g)=>{
                        competition.children.push({key:g.id,label:g.name,data:{bean:g,type:1,pkey:c.id,masterCompetitionId:masterCompetitionId.value}});
                    });
                    comTree.value.push(competition);
                });
            }
        }
    } else {
        dialog.alert("还未发布赛事，或者评审流程还未进行到评分节点");
    }
});

const onNodeSelect = async (node) => {
    // console.log(node);
    if (node.data.type==0 && node.children.length > 0) {
        return;
    }
    type = node.data.type;
    key = node.key;
    selTreeLabel.value = node.label;
    currentPage.value = 0;
    hasChildren = (node.children?.length > 0);
    if (type==0) {
        competitionId = node.key;
    } else if (type==1) {
        competitionId = node.data.pkey;
        guiGeId = node.key;
    }
    let res = await workRest.qyPingShenJudgeList({masterCompetitionId:masterCompetitionId.value,judgeId:judgeId,pingShenStepId:stepStatus,competitionId:competitionId,guiGeId:guiGeId},null);
    if (res.status=="OK" && res.data.length > 0) {
        shiSubmited.value = res.data[0].pingShenStatus==1 ? true : false;
    }
    queryWorks(type,key);
}

const queryWorks = async (type,key)=>{
    let query = {};
    if (type==0 && !hasChildren) {
        query = {competitionId:key,appId:host,masterCompetitionId:masterCompetitionId.value,shiWorkItemList:true,judgeId:judgeId,stepStatus:stepStatus,pageSize:Config.pageSize,currentPage:currentPage.value,qyPassCount:true};
    } else {
        query = {guiGeId:key,appId:host,masterCompetitionId:masterCompetitionId.value,shiWorkItemList:true,judgeId:judgeId,stepStatus:stepStatus,pageSize:Config.pageSize,currentPage:currentPage.value,qyPassCount:true};
    }
    let res = await workRest.qyJudgeWorks(query,null);
    if (res.status=="OK") {
        if (res.data!=null) {
            pageUtil.value = res.data;
            // console.log(pageUtil.value);
            shiPassList = pageUtil.value.tempMap.shiPassList;
            if (!shiPassList) {
                shiPassList = [];
            }
            if (!pageUtil.value.content) {
                pageUtil.value.content = [];
                return;
            }
            for (let work of pageUtil.value.content) {
                if (!work.workItemList) break;
                for (let workItem of work.workItemList) {
                    if (workItem.mediaType==0) {
                        let ru = lodash.find(uploadRule.value.workType.image,(o)=>{return o.type==workItem.type});
                        workItem.tempMap = {title:ru.title,imgPath:await oss.buildPathAsync(workItem.path,true,null)};
                    } else {
                        let ru = lodash.find(uploadRule.value.workType.video,(o)=>{return o.type==workItem.type});
                        workItem.tempMap = {title:ru.title,exifCheck:true,imgPath:await oss.buildPathAsync(workItem.path,false,null)};
                    }
                }
                if (!work.tempMap) {
                    work.tempMap = {};
                }
                work.tempMap.type = type;
                work.tempMap.key = key;
                if (work.tempMap.fenJson) {
                    work.tempMap.fenJson = JSON.parse(work.tempMap.fenJson);
                    lodash.forEach(work.tempMap.fenJson,(f)=>{
                        if (!f.value) {
                            f.value = 0;
                        }
                    });
                }
            }
            // console.log(pageUtil.value);
            workCount.value = pageUtil.value.totalElements;
        } else {
            pageUtil.value.content = [];
        }
    }
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

const saveSubmitJudgeWorks = (shiPass)=>{
    let selWork = [];
    lodash.forEach(pageUtil.value.content,(o)=>{
        let sum = 0;
        lodash.forEach(o.tempMap.fenJson.fields,(f)=>{
            if (f.value) {
                sum += f.value;
            }
        });
        selWork.push({id:o.id,type:type,key:key,fg:shiPass ? 1 : 0,fenJson:o.tempMap.fenJson,fen:sum});
    });
    if (shiPass)
    {
        dialog.confirm("正式提交审核后将无法再修改，您是否确认此次审核提交？",()=>{
            workRest.saveSubmitJudgeWorks({selWork:selWork,judgeId:judgeId,stepStatus:stepStatus,shiPass:shiPass,masterCompetitionId:masterCompetitionId.value,competitionId:competitionId,guiGeId:guiGeId},(res)=>{
                if (res.status=="OK") {
                    shiSubmited.value = true;
                    dialog.toastSuccess("您已提交此次审核");
                }
            });
        },()=>{

        });
    } else {
        workRest.saveSubmitJudgeWorks({selWork:selWork,judgeId:judgeId,stepStatus:stepStatus,masterCompetitionId:masterCompetitionId.value,shiPass:shiPass},(res)=>{
            if (res.status=="OK") {
                dialog.toastSuccess("您已暂时保存此次审核");
            }
        });
    }
};
</script>

<style scoped lang="scss">

</style>
