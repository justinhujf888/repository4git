<template>
    <animationPage ref="mainPage" :show="true">
        <div class="row gap-4">
            <Button severity="warn" label="暂时保存" @click="saveSubmitJudgeWorks(false)" :disabled="selTypeWorkCount==0 || shiSubmited"/>
            <Button label="提交评审" @click="saveSubmitJudgeWorks(true)" :disabled="selTypeWorkCount==0 || shiSubmited"/>
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
                    <span>{{selTypeCount}}个作品选中</span>
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
                                            <ToggleSwitch v-model="item.tempMap.shiPass" @change="switchChange(item,index)" :readonly="shiSubmited"/>
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
const selTypeWorkCount = ref(0);
const shiSubmited = ref(false);

let masterCompetitionId = "";
let host = inject("domain");
let hasChildren = false;
let type = 0;
let key = "";
let selWork = [];
let judgeId = util.giveStorgeCry("managerId");
let competitionId = "";
let guiGeId = "";
let stepStatus = -1;
let shiPassList = [];

onMounted(async () => {
    // console.log("judgeId",judgeId);
    masterCompetitionId = (await workRest.giveCurrentMasterCompetitionSetup({keys:["masterCompetitionId"]},null))?.data?.[0]?.value;
    if (masterCompetitionId) {
        uploadRule.value = await workRest.gainPageSetup(host,"worksetup");
        stepStatus = 0;
        // console.log(uploadRule.value);
        // console.log(judgeId);
        let res = await workRest.qyPingShenJudgeList({masterCompetitionId:masterCompetitionId,judgeId:judgeId,pingShenStepId:stepStatus},null);
        if (res.status=="OK") {
            let psJudgeList = res.data;
            // console.log(psJudgeList);
            let res2 = await workRest.qyCompetitionList({masterCompetitionId:masterCompetitionId,shiQyGuiGeList:true},null);
            if (res2.status=="OK") {
                let list = lodash.filter(res2.data,(o)=>{return lodash.findIndex(psJudgeList,(p)=>{return p.competitionJudgePK.competitionId==o.id})>-1});
                // console.log("list",list);
                lodash.forEach(list,(c)=>{
                    let competition = {key:c.id,label:c.name,data:{bean:c,type:0,masterCompetitionId:masterCompetitionId},children:[]};
                    let glist = lodash.filter(c.guiGeList,(o)=>{return lodash.findIndex(psJudgeList,(p)=>{return p.competitionJudgePK.competitionId==c.id && p.competitionJudgePK.guiGeId==o.id})>-1});
                    lodash.forEach(glist,(g)=>{
                        competition.children.push({key:g.id,label:g.name,data:{bean:g,type:1,pkey:c.id,masterCompetitionId:masterCompetitionId}});
                    });
                    comTree.value.push(competition);
                });
            }
        }

        // workRest.qyPingShenJudgeList({masterCompetitionId:masterCompetitionId,judgeId:null,pingShenStepId:stepStatus},(res)=>{
        //     if (res.status=="OK") {
        //         // console.log(res.data);
        //         let psJudgeList = res.data;
        //         workRest.qyCompetitionList({masterCompetitionId:masterCompetitionId,shiQyGuiGeList:true},(res2)=>{
        //             if (res2.status=="OK") {
        //                 let list = lodash.filter(res2.data,(o)=>{return lodash.findIndex(psJudgeList,(p)=>{return p.competitionJudgePK.competitionId==o.id})>-1});
        //                 // console.log("list",list);
        //                 lodash.forEach(list,(c)=>{
        //                     let competition = {key:c.id,label:c.name,data:{bean:c,type:0,masterCompetitionId:masterCompetitionId},children:[]};
        //                     let glist = lodash.filter(c.guiGeList,(o)=>{return lodash.findIndex(psJudgeList,(p)=>{return p.competitionJudgePK.competitionId==c.id && p.competitionJudgePK.guiGeId==o.id})>-1});
        //                     lodash.forEach(glist,(g)=>{
        //                         competition.children.push({key:g.id,label:g.name,data:{bean:g,type:1,masterCompetitionId:masterCompetitionId}});
        //                     });
        //                     comTree.value.push(competition);
        //                 });
        //             }
        //         });
        //     }
        // });
    } else {
        dialog.alert("还未发布赛事，请先发布赛事后在进行操作");
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
    let res = await workRest.qyPingShenJudgeList({masterCompetitionId:masterCompetitionId,judgeId:judgeId,pingShenStepId:stepStatus,competitionId:competitionId,guiGeId:guiGeId},null);
    if (res.status=="OK" && res.data.length > 0) {
        shiSubmited.value = res.data[0].pingShenStatus==1 ? true : false;
    }
    queryWorks(type,key);
}

const queryWorks = (type,key)=>{
    let query = {};
    if (type==0 && !hasChildren) {
        query = {competitionId:key,appId:host,masterCompetitionId:masterCompetitionId,shiWorkItemList:true,judgeId:judgeId,stepStatus:0,pageSize:Config.pageSize,currentPage:currentPage.value,qyPassCount:true};
    } else {
        query = {guiGeId:key,appId:host,masterCompetitionId:masterCompetitionId,shiWorkItemList:true,judgeId:judgeId,stepStatus:0,pageSize:Config.pageSize,currentPage:currentPage.value,qyPassCount:true};
    }
    workRest.qyJudgeWorks(query,async (res)=>{
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
                    // if (lodash.findIndex(selWork,(o)=>{return o.id==work.id}) > -1) {
                    //     work.temp = true;
                    // }
                    if (!work.tempMap) {
                        work.tempMap = {};
                    }
                    work.tempMap.status = lodash.find(Beans.workStatus(),(o)=>{return o.id==work.status}).name;
                    work.tempMap.type = type;
                    work.tempMap.key = key;
                }
                comShiPassCount();
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
    if (item.tempMap.shiPass==true) {
        if (j < 0) {
            //type:0类别；1组别；key是类别或者组别的ID
            selWork.push({id:item.id,type:item.tempMap.type,key:item.tempMap.key,fg:1});
        } else {
            selWork[j].fg = 1;
            // selWork.slice(j,0);
        }
    } else {
        if (j > -1) {
            selWork[j].fg = 0;
            // selWork.slice(j,0);
        } else {
            selWork.push({id:item.id,type:item.tempMap.type,key:item.tempMap.key,fg:0});
        }
    }
    // console.log(selWork);
    comShiPassCount();
    selTypeWorkCount.value = lodash.filter(selWork,(o)=>{return o.key==item.tempMap.key;}).length;
};

const saveSubmitJudgeWorks = (shiPass)=>{
    if (shiPass)
    {
        dialog.confirm("正式提交审核后将无法再修改，您是否确认此次审核提交？",()=>{
            workRest.saveSubmitJudgeWorks({selWork:selWork,judgeId:judgeId,stepStatus:stepStatus,shiPass:shiPass,masterCompetitionId:masterCompetitionId,competitionId:competitionId,guiGeId:guiGeId},(res)=>{
                if (res.status=="OK") {
                    shiSubmited.value = true;
                    dialog.alert("您已提交此次审核");
                }
            });
        },()=>{

        });
    } else {
        workRest.saveSubmitJudgeWorks({selWork:selWork,judgeId:judgeId,stepStatus:stepStatus,shiPass:shiPass},(res)=>{
            if (res.status=="OK") {
                dialog.alert("您已暂时保存此次审核");
            }
        });
    }
};

const comShiPassCount = ()=>{
    let selWorkPass = lodash.filter(selWork,(o)=>{return o.key==key && o.fg==1});
    let updateDbPassList = [];
    let updateSelPassList = [];
    //将数据库查询到的pass=true的记录，依据前端的记录过滤一下
    lodash.forEach(shiPassList,(o)=>{
        if (lodash.findIndex(selWork,(w)=>{return w.id==o && w.fg==0}) < 0) {
            updateDbPassList.push(o);
        }
    });
    //前端的记录pass=true的过滤出DB list没有的
    lodash.forEach(selWorkPass,(o)=>{
        if (lodash.findIndex(updateDbPassList,(w)=>{return w==o.id}) < 0) {
            updateSelPassList.push(o.id);
        }
    });
    selTypeCount.value = updateDbPassList.length + updateSelPassList.length;
}
</script>

<style scoped lang="scss">

</style>
