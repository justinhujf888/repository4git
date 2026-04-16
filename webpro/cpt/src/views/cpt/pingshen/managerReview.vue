<template>
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
                                <div>
                                    <span class="text-2xl font-semibold">{{item.name}}</span>
                                </div>
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
                                <div>

                                </div>
                                <Divider/>
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
    </div>
</template>

<script setup>
import {inject, onMounted, ref} from 'vue';
import util from "@/api/util";
import workRest from "@/api/dbs/workRest";
import lodash from "lodash-es";
import oss from "@/api/oss";
import {Beans} from "@/api/dbs/beans";
import {Config} from "@/api/config";

const comTree = ref([]);
const selectedTreeNodeKey = ref(null);
const expandedTreeNodeKey = ref(null);
const pageUtil = ref([]);
const currentPage = ref(0);

let masterCompetitionId = "";
let host = inject("domain");
let hasChildren = false;
let type = 0;
let key = "";

onMounted(() => {
    if (util.giveStorgeMessage("masterCompetitionId")) {
        masterCompetitionId = util.giveStorgeCry("masterCompetitionId");
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
                    for (let workItem of lodash.filter(work.workItemList,(o)=>{return o.mediaType==0})) {
                        workItem.tempMap = {imgPath:await oss.buildPathAsync(workItem.path,true,null)};
                    }
                    work.tempMap = {};
                    work.tempMap.status = lodash.find(Beans.workStatus(),(o)=>{return o.id==work.status}).name;
                }
                // console.log(pageUtil.value);
            } else {
                pageUtil.value.content = [];
            }
        }
    });
}

const pageChange = (event)=>{
    // console.log(event);
    currentPage.value = event.page;
    queryWorks(type,key,currentPage.value);
}
</script>

<style scoped lang="scss">

</style>