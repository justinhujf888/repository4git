<template>
    <div
        id="hero"
        class="flex flex-col pt-6 px-6 lg:px-20 overflow-hidden"
        _style="background: linear-gradient(0deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, rgb(238, 239, 175) 0%, rgb(195, 227, 250) 100%); clip-path: ellipse(150% 87% at 93% 13%)"
    >
        <div class="mx-6 md:mx-20 mt-0 md:mt-6 font-semibold py-60">
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
            <h3 class="text-center mix-blend-difference text-white">每件作品都是一首生命的赞歌</h3>
            <h1 class="font-bold font-stretch-extra-condensed text-center bg-gradient-to-r from-gray-800 via-purple-700 to-gray-800 bg-clip-text text-transparent text-8xl scale-x-75">{{siteDatas?.siteInfo.siteCompetition.name}} {{siteDatas?.cptInfo.masterCompetitionInfo.name}}</h1>
            <h5 class="text-center mix-blend-difference text-white text-xl lg:text-3xl">{{siteDatas?.cptInfo.masterCompetitionInfo.tempMap.beginDate}} — {{siteDatas?.cptInfo.masterCompetitionInfo.tempMap.endDate}}</h5>
        </div>
        <div class="flex justify-center md:justify-end hidden">
            <img src="https://iaplc.com/c/wp-content/uploads/sites/3/2021/03/2011_grand_.jpg" alt="Hero Image" class="w-9/12 md:w-auto" />
        </div>
    </div>
</template>

<script setup>
// import BlurText from "@/bit-blocks/TextAnimations/BlurText/BlurText.vue";
import { inject, onMounted, watch } from 'vue';

const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});

onMounted(async () => {

});
</script>
