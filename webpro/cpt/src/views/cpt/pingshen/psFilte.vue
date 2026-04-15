<template>

</template>

<script setup>
import { onMounted, ref } from 'vue';
import util from "@/api/util";
let masterCompetitionId = "";


onMounted(() => {
    if (util.giveStorgeMessage("masterCompetitionId")) {
        masterCompetitionId = util.giveStorgeCry("masterCompetitionId");
    }
    console.log(masterCompetitionId);
})
</script>

<style scoped lang="scss">

</style>