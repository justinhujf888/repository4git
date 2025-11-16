<template>
    <div class="card">
        <div v-for="flow of pingShenflow?.flow">
            <span>{{flow.name}}</span>
        </div>
    </div>
</template>

<script setup>

import {ref, watch} from "vue";
import systemRest from "@/api/dbs/systemRest";

const pingShenflow = ref(null);
const selMasterCompetition = ref({});

let mainPage = null;
let mePage = null;
let obj = null;

watch(pingShenflow,(newValue)=>{
    // console.log(newValue);
});

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    selMasterCompetition.value = obj.data;

    // console.log(selMasterCompetition.value);

    systemRest.pingShenFlow({},(res)=>{
        if (res.status=="OK") {
            pingShenflow.value = res.data;
        }
    });
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>