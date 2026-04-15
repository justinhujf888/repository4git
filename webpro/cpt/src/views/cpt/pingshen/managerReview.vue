<template>
    <div class="row p-2">
        <div class="w-full md:w-1/6 text-sm">
            <Tree :value="comTree" v-model:selectionKeys="selectedTreeNodeKey" v-model:expandedKeys="expandedTreeNodeKey" selectionMode="single" @node-select="onNodeSelect"></Tree>
        </div>
        <div>

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

const comTree = ref([]);
const selectedTreeNodeKey = ref(null);
const expandedTreeNodeKey = ref(null);
const workList = ref([]);

let masterCompetitionId = "";
let host = inject("domain");


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
    workRest.qyWorks({appId:host,userId:null,masterCompetitionId:masterCompetitionId,shiWorkItemList:true,statusList:[1,9]},async (res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                workList.value = res.data;
                for (let work of workList.value) {
                    for (let workItem of lodash.filter(work.workItemList,(o)=>{return o.mediaType==0})) {
                        workItem.tempMap = {imgPath:await oss.buildPathAsync(workItem.path,true,null)};
                    }
                    work.tempMap = {};
                    work.tempMap.status = lodash.find(Beans.workStatus(),(o)=>{return o.id==work.status}).name;
                }
                console.log(workList.value)
            }
        }
    });
}
</script>

<style scoped lang="scss">

</style>