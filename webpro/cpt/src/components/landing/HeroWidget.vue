<template>
    <div class="relative">
<!--        <Galleria :value="indexDatas?.bannerArea.setup.banner.value" :numVisible="0" :showThumbnails="false" :showItemNavigators="true" :showIndicators="true" :circular="false" :changeItemOnIndicatorHover="true" :showIndicatorsOnItem="true" indicatorsPosition="bottom" :autoPlay="true" :transitionInterval="3000">-->
<!--            <template #item="slotProps">-->
<!--                <img :src="slotProps.item.img.tempMap?.imgPath" :alt="slotProps.item.alt" class="w-dvw h-dvh object-cover object-top"/>-->
<!--            </template>-->
<!--        </Galleria>-->
        <div>
            <img :src="indexDatas?.bannerArea.setup.banner.value[0].img.tempMap?.imgPath" class="w-full object-cover object-top"/>
        </div>
        <div
            id="hero"
            class="flex flex-col pt-6 px-6 lg:px-20 overflow-hidden hwcenter"
            _style="background: linear-gradient(0deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, rgb(238, 239, 175) 0%, rgb(195, 227, 250) 100%); clip-path: ellipse(150% 87% at 93% 13%)"
        >

            <div class="mt-0 md:mt-6 font-semibold py-60" style="width:100%">
<!--                <Image class="rounded w-full h-full object-cover" :src="indexDatas?.bannerArea.setup.mImg.value.tempMap?.imgPath" preview_ :pt="{image:{class:'!w-full !h-full !object-cover'}}"/>-->
                <!--            <BlurText-->
                <!--                text="每件作品都是一首生命的赞歌"-->
                <!--                :delay="200"-->
                <!--                class-name="text-4xl leading-tight font-semibold wcenter font-light block text-violet-800"-->
                <!--                animate-by="words"-->
                <!--                direction="top"-->
                <!--                :threshold="0.1"-->
                <!--                root-margin="0px"-->
                <!--                :step-duration="0.8"-->
                <!--            />-->

<!--                <h3 class="text-center mix-blend-difference text-white">每件作品都是一首生命的赞歌</h3>-->
<!--                <h1 class="font-bold font-stretch-extra-condensed text-center bg-gradient-to-r from-gray-800 via-purple-700 to-gray-800 bg-clip-text text-transparent text-8xl scale-x-75">{{siteDatas?.siteInfo.siteCompetition.name}} {{siteDatas?.cptInfo.masterCompetitionInfo.name}}</h1>-->
<!--                <h5 class="text-center mix-blend-difference text-white text-xl lg:text-3xl">{{siteDatas?.cptInfo.masterCompetitionInfo.tempMap.beginDate}} — {{siteDatas?.cptInfo.masterCompetitionInfo.tempMap.endDate}}</h5>-->
            </div>
        </div>
    </div>
</template>

<script setup>
// import BlurText from "@/bit-blocks/TextAnimations/BlurText/BlurText.vue";
import { inject, onMounted, watch } from 'vue';
import oss from "@/api/oss";

const siteDatas = inject("siteDatas");
const indexDatas = inject("indexDatas");
watch(siteDatas,async (newValue)=>{
    // console.log(newValue);
    for(let v of newValue.siteInfo.siteZuTiMediaList) {
        v.tempMap = {};
        v.tempMap.imgPath = await oss.buildPathAsync(v.path,true,null);
    }
});
watch(indexDatas,async (newValue)=>{
    // console.log(newValue.bannerArea.setup.mImg.value);
    for(let v of newValue.bannerArea.setup.banner.value) {
        v.img.tempMap = {};
        v.img.tempMap.imgPath = await oss.buildPathAsync(v.img.img,true,null);
    }
    newValue.bannerArea.setup.mImg.value.tempMap = {};
    newValue.bannerArea.setup.mImg.value.tempMap.imgPath = await oss.buildPathAsync(newValue.bannerArea.setup.mImg.value.img,true,null);
});

onMounted(async () => {

});
</script>
