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
import pageJson from '@/datas/pageJson';
import lodash from 'lodash-es';

const siteDatas = ref(null);
const indexDatas = ref(null);
const shiShowPage = ref(false);

(async ()=>{
    await oss.buildAliOssAccessInfo();
    siteDatas.value = await useGlobal.siteDatas();
    indexDatas.value = await useGlobal.pageSetupDatas("index");
    indexDatas.value.jiangArea.setup.bgImg.value.tempMap = {imgPath:await oss.buildPathAsync(indexDatas.value.jiangArea.setup.bgImg.value.img,true,null)};

    let timer = setTimeout(() => {
        shiShowPage.value = true;
        clearTimeout(timer);
    },500);
})();


provide("siteDatas",siteDatas);
provide("indexDatas",indexDatas);

onMounted(async () => {
    // console.log(await workRest.gainCache8MasterCompetitionInfo(host));
    // console.log(await workRest.gainCache8SiteInfo(host));
});
</script>

<template>
    <div v-show="shiShowPage" class="bg-surface-0 dark:bg-surface-900 animate__animated animate__fadeIn duration-75">
        <div id="home" class="landing-wrapper overflow-hidden">
            <TopbarWidget/>
            <HeroWidget/>
            <slogen />

            <div class="py-12 relative">
                <img :src="indexDatas?.jiangArea.setup.bgImg.value.tempMap.imgPath" class="w-full h-full absolute left-0 top-0" style="z-index: -1"/>
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
