<template>
    <animation-page :show="true">
        <div class="card">
            <div class="row items-center gap-4">
                <Button label="重置评审流程" @click="resetFlow"/>
                <span class="text-red-500">重置评审流程会清空目前评委的评审数据，使评审重新开始，请确认谨慎操作。</span>
            </div>
            <Panel v-for="f of flow.flow" :header="f.name" class="mt-5">
                <div v-if="f.id==0" class="col gap-4">
                    <div class="row items-center gap-2">
                        <span>每位评委最少评选通过</span>
                        <InputText name="judgePassWorkMixCount" v-model="f.data.judgePassWorkMixCount" :disabled="masterCompetitionStatus!=-1"/>
                        <span>个作品</span>
                    </div>
                    <div>
                        <Button label="评委开始初筛作品" @click="pingShenWorksInit" :disabled="masterCompetitionStatus!=-1"/>
                    </div>
                </div>
                <div v-if="f.id==1" class="col gap-4">
                    <div>
                        <Button label="对初筛进行汇总，评委开始第一轮评分" @click="pingShenWorksInit" :disabled="masterCompetitionStatus!=0"/>
                    </div>
                </div>
                <div v-if="f.id==2" class="col gap-4">
                    <div class="row items-center gap-2">
                        <span>每组从上一轮评分选出</span>
                        <InputText name="workCount" v-model="f.data.workCount" :disabled="masterCompetitionStatus!=1"/>
                        <span>个作品参加本轮评分</span>
                    </div>
                    <div>
                        <Button label="对第一轮评分进行汇总，评委开始第二轮评分" @click="pingShenWorksInit" :disabled="masterCompetitionStatus!=1"/>
                    </div>
                </div>
                <div v-if="f.id==3" class="col gap-4">
                    <div class="row items-center gap-2">
                        <span>选出排名</span>
                        <InputText name="reportCount" v-model="f.data.reportCount" :disabled="masterCompetitionStatus!=2"/>
                        <span>个作品</span>
                    </div>
                    <div>
                        <Button label="对第二轮评分进行汇总，发布结果" @click="buildFlowWork" :disabled="masterCompetitionStatus!=2"/>
                    </div>
                </div>
            </Panel>
        </div>
    </animation-page>
</template>

<script setup>
import {inject, onMounted, ref} from "vue";
import workRest from "@/api/dbs/workRest";
import AnimationPage from "@/components/my/animationPage.vue";
import util from "@/api/util";
import dialog from "@/api/uniapp/dialog";
import primeUtil from "@/api/prime/util";
import lodash from "lodash-es";

const flow = ref([]);

const masterCompetitionId = ref("");
const masterCompetitionStatus = ref(-1);

let host = inject("domain");

onMounted(async ()=>{
    await init();
});

const init = async ()=>{
    // console.log(host);
    let cms = (await workRest.giveCurrentMasterCompetitionSetup({keys:["masterCompetitionId","masterCompetitionStatus"]},null))?.map;
    masterCompetitionId.value = cms.masterCompetitionId;
    masterCompetitionStatus.value = cms.masterCompetitionStatus;

    if (masterCompetitionId.value) {
        let mcRes = await workRest.qyMasterSiteCompetition({id:masterCompetitionId.value,siteCompetitionId:host},null);
        if (mcRes.status=="OK" && mcRes.data) {
            // console.log(mcRes.data);
            flow.value = mcRes.data?.[0]?.flowSetup;
        }
    } else {
        dialog.alert("还未发布赛事，请先发布赛事后在进行操作");
    }
};

const pingShenWorksInit = async ()=>{
    let errors = [];
    if (masterCompetitionStatus.value==-1) {
        errors = primeUtil.checkFormRequiredValid([
            {val:flow.value.flow?.[0].data.judgePassWorkMixCount,name:"judgePassWorkMixCount",label:"最少通过作品数量"}
        ]);
    } else if (masterCompetitionStatus.value==1) {
        errors = primeUtil.checkFormRequiredValid([
            {val:flow.value.flow?.[2].data.workCount,name:"workCount",label:"参加评分的作品数量"}
        ]);
    }
// console.log(flow,errors);
    let shiEr = false;
    lodash.forEach(errors, (er,key) => {
        if (er && er.length>0) {
            shiEr = true;
            dialog.toastError(er[0].message);
        }
    });
    if (shiEr) {
        return;
    }

    let res = await workRest.pingShenWorksInit({masterCompetitionId:masterCompetitionId.value,pingShenStepId:parseInt(masterCompetitionStatus.value)+1,mapData:{flowSetup:flow.value}},null);
    if (res.status=="OK") {
        dialog.toastSuccess("作品已分配到评委");
        masterCompetitionStatus.value = parseInt(masterCompetitionStatus.value)+1;
    }
};

const buildFlowWork = async () => {
    let errors = primeUtil.checkFormRequiredValid([
        {val:flow.value.flow?.[3].data.reportCount,name:"reportCount",label:"选出排名作品数量"}
    ]);
    let shiEr = false;
    lodash.forEach(errors, (er,key) => {
        if (er && er.length>0) {
            shiEr = true;
            dialog.toastError(er[0].message);
        }
    });
    if (shiEr) {
        return;
    }

    let res = await workRest.buildFlowWork({masterCompetitionId:masterCompetitionId.value,pingShenStepId:parseInt(masterCompetitionStatus.value)+1,mapData:{flowSetup:flow.value}},null);
    if (res.status=="OK") {
        dialog.toastSuccess("作品分数已经汇总完成");
        masterCompetitionStatus.value = parseInt(masterCompetitionStatus.value)+1;
    }
};

const resetFlow = async ()=>{
    dialog.confirm("重置评审流程会清空目前评委的评审数据，使评审重新开始，请确认谨慎操作。",async ()=>{
        let res = await workRest.resetPingShen({appId:host,siteCompetitionId:host,masterCompetitionId:masterCompetitionId.value},null);
        if (res.status=="OK") {
            dialog.toastSuccess("评审流程已重置");
            await init();
        }
    },null);
};
</script>

<style scoped lang="scss">

</style>
