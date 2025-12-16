<template>
    <div>
        <pageUIEdit :pageJson="pageJson" :read-only="true"/>
    </div>
</template>

<script setup>
import {ref} from "vue";
import pageUIEdit from "@/views/cpt/setup/masterCpt/pageSetup/pageUIEdit.vue";
const pageJson = defineModel("pageJson",{default:{}});

</script>

<style scoped lang="scss">

</style>
