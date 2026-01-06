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
import UnitOrg from '@/components/landing/unitOrg.vue';
import ZanZhu from '@/components/landing/zanZhu.vue';

const siteDatas = ref(null);
const indexDatas = ref(null);
const footDatas = ref(null);
const shiShowPage = ref(false);

(async ()=>{
    await oss.buildAliOssAccessInfo();
    siteDatas.value = await useGlobal.siteDatas();
    indexDatas.value = await useGlobal.pageSetupDatas("index");
    footDatas.value = await useGlobal.pageSetupDatas("foot");
    indexDatas.value.jiangArea.setup.bgImg.value.tempMap = {imgPath:await oss.buildPathAsync(indexDatas.value.jiangArea.setup.bgImg.value.img,true,null)};

    let timer = setTimeout(() => {
        shiShowPage.value = true;
        clearTimeout(timer);
    },500);
})();


provide("siteDatas",siteDatas);
provide("indexDatas",indexDatas);
provide("footDatas",footDatas);

onMounted(async () => {
    // console.log(await workRest.gainCache8MasterCompetitionInfo(host));
    // console.log(await workRest.gainCache8SiteInfo(host));
});
</script>

<template>
    <div v-show="shiShowPage" class="bg-surface-0 dark:bg-surface-900 animate__animated animate__fadeIn">
        <div id="home" class="landing-wrapper overflow-hidden">
            <TopbarWidget/>
            <HeroWidget/>
            <slogen />

            <div class="py-12 relative bg-center bg-cover" :style="'background-image: url(\''+indexDatas?.jiangArea.setup.bgImg.value.tempMap.imgPath+'\');'">
                <FeaturesWidget />
                <div class="py-14 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
                    <Divider/>
                </div>
                <orgHumans/>
            </div>
            <Works />
            <unit-org/>
            <zan-zhu/>
<!--            <HighlightsWidget />-->
            <Divider/>
            <FooterWidget />
            <ScrollTop />
        </div>
    </div>
</template>
