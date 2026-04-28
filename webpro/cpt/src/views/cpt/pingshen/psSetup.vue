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

onMounted(()=>{
    workRest.qyPingShenFlow({},(res)=>{
        if (res.status=="OK") {
            // console.log(res.data.flow);
            flow.value = res.data.flow;
        }
    });
});

const pingShenWorksInit = ()=>{
    workRest.pingShenWorksInit({masterCompetitionId:util.giveStorgeCry("masterCompetitionId"),pingShenStepId:0},(res)=>{
        if (res.status=="OK") {
            dialog.toastSuccess("作品已分配到评委");
        }
    });
};
</script>

<style scoped lang="scss">

</style>