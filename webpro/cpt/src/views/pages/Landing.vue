<script setup>
import {ref,provide} from 'vue';
import FeaturesWidget from '@/components/landing/FeaturesWidget.vue';
import FooterWidget from '@/components/landing/FooterWidget.vue';
import HeroWidget from '@/components/landing/HeroWidget.vue';
// import HighlightsWidget from '@/components/landing/HighlightsWidget.vue';
import slogen from "@/components/landing/slogen.vue";
import orgHumans from "@/components/landing/orgHumans.vue";
import Works from '@/components/landing/works.vue';
// import PricingWidget from '@/components/landing/PricingWidget.vue';
import TopbarWidget from '@/components/landing/TopbarWidget.vue';
import {onMounted} from "vue";
import oss from "@/api/oss";
import LightRays from "@/bit-blocks/Backgrounds/LightRays/LightRays.vue";
import useGlobal from "@/api/hooks/useGlobal";

const siteDatas = ref(null);
const shiShowPage = ref(false);

(async ()=>{
    await oss.buildAliOssAccessInfo();
    siteDatas.value = await useGlobal.siteDatas();
})();

let timer = setTimeout(() => {
    shiShowPage.value = true;
    clearTimeout(timer);
},500);

provide("siteDatas",siteDatas);

onMounted(async () => {
    // console.log(await workRest.gainCache8MasterCompetitionInfo(host));
    // console.log(await workRest.gainCache8SiteInfo(host));
});
</script>

<template>
    <div v-show="shiShowPage" class="bg-surface-0 dark:bg-surface-900 animate__animated animate__fadeIn duration-75">
        <div id="home" class="landing-wrapper overflow-hidden">
            <TopbarWidget/>
            <div class="bg-[url('https://iaplc.com/c/wp-content/uploads/sites/3/2024/12/iaplc2025_cover_01.jpg')] bg-center bg-cover">
                <HeroWidget/>
            </div>
            <div class="bg-[url('https://iaplc.com/c/wp-content/uploads/sites/3/2021/02/about.png')]">
                <slogen />
            </div>
            <div class="bg-[url('https://iaplc.com/assets_jp/img/top/background_black.jpg')] bg-fixed py-12 relative">
                <div class="absolute top-0 left-0 w-dvw h-dvh opacity-20 z-0">
                    <LightRays
                        rays-origin="top-center"
                        rays-color="#00ffff"
                        :rays-speed="2"
                        :light-spread="0.8"
                        :ray-length="5"
                        :follow-mouse="true"
                        :mouse-influence="0.1"
                        :noise-amount="0.1"
                        :distortion="0.05"
                        class-name="custom-rays"
                    />
                </div>
                <FeaturesWidget />
                <div class="py-14 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
                    <Divider/>
                </div>
                <orgHumans/>
            </div>
            <Works />
<!--            <HighlightsWidget />-->
            <FooterWidget />
            <ScrollTop />
        </div>
    </div>
</template>
