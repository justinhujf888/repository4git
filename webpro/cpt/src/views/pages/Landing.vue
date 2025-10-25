<script setup>
import FeaturesWidget from '@/components/landing/FeaturesWidget.vue';
import FooterWidget from '@/components/landing/FooterWidget.vue';
import HeroWidget from '@/components/landing/HeroWidget.vue';
import HighlightsWidget from '@/components/landing/HighlightsWidget.vue';
import orgHumans from "@/components/landing/orgHumans.vue";
import PricingWidget from '@/components/landing/PricingWidget.vue';
import TopbarWidget from '@/components/landing/TopbarWidget.vue';
import {inject, onMounted, provide, ref} from "vue";
import workRest from '@/api/dbs/workRest';
import dayjs from "dayjs";

let host = inject("domain");

const siteDatas = ref(null);

(async ()=>{
    siteDatas.value = {cptInfo:await workRest.gainCache8MasterCompetitionInfo(host),siteInfo:await workRest.gainCache8SiteInfo(host)};
    siteDatas.value.cptInfo.masterCompetitionInfo.tempMap.beginDate = dayjs(siteDatas.value.cptInfo.masterCompetitionInfo.beginDate).format("YYYY-MM-DD");
    siteDatas.value.cptInfo.masterCompetitionInfo.tempMap.endDate = dayjs(siteDatas.value.cptInfo.masterCompetitionInfo.endDate).format("YYYY-MM-DD");
})();
provide("siteDatas",siteDatas);

onMounted(async () => {
    // console.log(await workRest.gainCache8MasterCompetitionInfo(host));
    // console.log(await workRest.gainCache8SiteInfo(host));
});
</script>

<template>
    <div class="bg-surface-0 dark:bg-surface-900">
        <div id="home" class="landing-wrapper overflow-hidden">
            <div class="py-6 px-6 mx-0 md:mx-12 lg:mx-20 lg:px-20 flex items-center justify-between relative lg:static">
                <TopbarWidget />
            </div>
            <HeroWidget />
            <FeaturesWidget />
            <orgHumans/>
<!--            <HighlightsWidget />-->
            <PricingWidget />
            <FooterWidget />
        </div>
    </div>
</template>
