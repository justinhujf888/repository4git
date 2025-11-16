<template>
    <div class="card">
        <div class="flex flex-wrap items-center justify-between">
            <div>
                <span class="text-base">{{selMasterCompetition.tempMap?.judgeData?.type==0 ? '类别' : '组别'}}{{selMasterCompetition.tempMap?.judgeData?.name}}评委设置</span>
            </div>
        </div>
        <Panel class="mt-10">
            <div v-for="flow of selMasterCompetition.tempMap?.judgeData?.data">
                <span>{{flow.name}}</span>
                <div v-if="flow.type==0">
                    <Chip v-for="ju of flow.setupData" label="Amy Elsner" image="/images/avatar/amyelsner.png" />
                </div>
            </div>
        </Panel>
        <div class="row mt-12 center gap-4">
            <Button label="保存设置" class="px-8" _as="router-link" _to="/" @click="save()"></Button>
            <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
        </div>
    </div>
</template>

<script setup>

import {ref} from "vue";
import systemRest from "@/api/dbs/systemRest";

const selMasterCompetition = ref({});

let mainPage = null;
let mePage = null;
let obj = null;

function save() {

}

function cancel() {
    mePage.close(mainPage);
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    selMasterCompetition.value = obj.data;

    console.log(selMasterCompetition.value);
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
