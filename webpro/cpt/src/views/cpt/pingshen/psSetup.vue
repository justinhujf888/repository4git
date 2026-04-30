<template>
    <animation-page :show="true">
        <Panel v-for="f of flow" :header="f.name" class="m-5">
            <div v-if="f.id==0">
                <Button label="分配作品" @click="pingShenWorksInit" :disabled="masterCompetitionStatus>-1"/>
            </div>
        </Panel>
    </animation-page>
</template>

<script setup>
import {onMounted,ref} from "vue";
import workRest from "@/api/dbs/workRest";
import AnimationPage from "@/components/my/animationPage.vue";
import util from "@/api/util";
import dialog from "@/api/uniapp/dialog";

const flow = ref([]);

const masterCompetitionId = ref("");
const masterCompetitionStatus = ref(-1);

onMounted(async ()=>{
    let cms = (await workRest.giveCurrentMasterCompetitionSetup({keys:["masterCompetitionId","masterCompetitionStatus"]},null))?.map;
    masterCompetitionId.value = cms.masterCompetitionId;
    masterCompetitionStatus.value = cms.masterCompetitionStatus;

    if (masterCompetitionId.value) {
        workRest.qyPingShenFlow({},(res)=>{
            if (res.status=="OK") {
                // console.log(res.data.flow);
                flow.value = res.data.flow;
            }
        });
    } else {
        dialog.alert("还未发布赛事，请先发布赛事后在进行操作");
    }
});

const pingShenWorksInit = async ()=>{
    let res = await workRest.pingShenWorksInit({masterCompetitionId:masterCompetitionId.value,pingShenStepId:0},null);
    if (res.status=="OK") {
        dialog.toastSuccess("作品已分配到评委");
        masterCompetitionStatus.value = 0;
    }
};
</script>

<style scoped lang="scss">

</style>
