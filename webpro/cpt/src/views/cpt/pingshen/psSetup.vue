<template>
    <animation-page :show="true">
        <Panel v-for="f of flow" :header="f.name" class="m-5">
            <div v-if="f.id==0">
                <Button label="分配作品" @click="pingShenWorksInit"/>
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

let masterCompetitionId = "";

onMounted(async ()=>{
    masterCompetitionId = (await workRest.giveCurrentMasterCompetitionSetup({keys:["masterCompetitionId"]},null))?.data?.[0]?.value;
    if (masterCompetitionId) {
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
    workRest.pingShenWorksInit({masterCompetitionId:masterCompetitionId,pingShenStepId:0},(res)=>{
        if (res.status=="OK") {
            dialog.toastSuccess("作品已分配到评委");
        }
    });
};
</script>

<style scoped lang="scss">

</style>
