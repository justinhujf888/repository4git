<template>
    <animation-page :show="true">
        <Panel v-for="f of flow.flow" :header="f.name" class="m-5">
            <div v-if="f.id==0" class="col gap-4">
                <div class="row items-center gap-2">
                    <span>每位评委最少评选通过</span>
                    <InputText v-model="f.data.judgePassWorkMixCount" :disabled="masterCompetitionStatus>-1"/>
                    <span>个作品</span>
                </div>
                <div>
                    <Button label="分配作品" @click="pingShenWorksInit" :disabled="masterCompetitionStatus>-1"/>
                </div>
            </div>
        </Panel>
    </animation-page>
</template>

<script setup>
import {inject, onMounted, ref} from "vue";
import workRest from "@/api/dbs/workRest";
import AnimationPage from "@/components/my/animationPage.vue";
import util from "@/api/util";
import dialog from "@/api/uniapp/dialog";

const flow = ref([]);

const masterCompetitionId = ref("");
const masterCompetitionStatus = ref(-1);

let host = inject("domain");

onMounted(async ()=>{
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
});

const pingShenWorksInit = async ()=>{
    let res = await workRest.pingShenWorksInit({masterCompetitionId:masterCompetitionId.value,pingShenStepId:0,mapData:{flowSetup:flow.value}},null);
    if (res.status=="OK") {
        dialog.toastSuccess("作品已分配到评委");
        masterCompetitionStatus.value = 0;
    }
};
</script>

<style scoped lang="scss">

</style>
