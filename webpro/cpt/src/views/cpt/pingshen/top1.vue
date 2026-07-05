<template>
    <div class="card">

    </div>
</template>

<script setup>
import {onMounted} from "vue";
import workRest from "@/api/dbs/workRest";

onMounted(async () => {
    await workRest.getTop1EveryCptCompetitionGuiGe({masterCompetitionId:"1764062888925hifPtTTM"},null);
});
</script>

<style scoped lang="scss">

</style>