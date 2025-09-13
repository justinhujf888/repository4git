<template>
    <div class="relative">
        <Tabs value="0">
            <TabList>
                <Tab value="0">赛事基础信息设置</Tab>
                <Tab value="1">设置赛事主题图</Tab>
                <Tab value="2">设置系列赛事作品</Tab>
                <Tab value="3">设置组委会成员信息</Tab>
            </TabList>
            <TabPanels>
                <TabPanel value="0">
                    <siteBase/>
                </TabPanel>
                <TabPanel value="1">
                    <siteZhuTi/>
                </TabPanel>
                <TabPanel value="2">
                    <siteWorkItemPage/>
                </TabPanel>
                <TabPanel value="3">
                    <siteOrgHuman/>
                </TabPanel>
            </TabPanels>
        </Tabs>




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

onMounted(() => {

});
</script>

<style scoped>

</style>
