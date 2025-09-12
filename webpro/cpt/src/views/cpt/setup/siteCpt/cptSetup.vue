<template>
    <div class="relative">
        <siteBase :mainPage="mainPage"/>
        <siteZhuTi :mainPage="mainPage"/>
        <siteWorkItemPage :mainPage="mainPage"/>
        <siteOrgHuman :mainPage="mainPage"/>
<!--        <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-30">-->
<!--            <siteBase :mainPage="mainPage"/>-->
<!--            <siteZhuTi :mainPage="mainPage"/>-->
<!--            <siteWorkItemPage :mainPage="mainPage"/>-->
<!--            <siteOrgHuman :mainPage="mainPage"/>-->
<!--        </animationPage>-->

    </div>
</template>

<script setup>
import { onMounted, ref, useTemplateRef, inject, nextTick } from 'vue';

import animationPage from "@/components/my/animationPage.vue";

import siteBase from "@/views/cpt/setup/siteCpt/coms/siteBase.vue";
import siteZhuTi from "@/views/cpt/setup/siteCpt/coms/siteZhuTi.vue";
import siteWorkItemPage from "@/views/cpt/setup/siteCpt/coms/siteWorkItem.vue";
import siteOrgHuman from "@/views/cpt/setup/siteCpt/coms/siteOrgHuman.vue";

const mainPage = useTemplateRef("mainPage");

onMounted(async () => {

});
</script>

<style scoped>

</style>
