<template>
    <div
        id="hero"
        class="flex flex-col pt-6 px-6 lg:px-20 overflow-hidden"
        _style="background: linear-gradient(0deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, rgb(238, 239, 175) 0%, rgb(195, 227, 250) 100%); clip-path: ellipse(150% 87% at 93% 13%)"
    >
        <div class="mx-6 md:mx-20 mt-0 md:mt-6 font-semibold">
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
            <h2 class="text-center mix-blend-difference text-white">每件作品都是一首生命的赞歌</h2>
            <h1 class="font-normal leading-normal text-center mix-blend-difference text-white">{{Config.appName}} {{siteDatas?.cptInfo.masterCompetitionInfo.name}}</h1>
            <h5 class="text-center mix-blend-difference text-white">{{siteDatas?.cptInfo.masterCompetitionInfo.tempMap.beginDate}} — {{siteDatas?.cptInfo.masterCompetitionInfo.tempMap.endDate}}</h5>
            <Button label="Get Started" as="router-link" to="/manage/index" rounded clas s="!text-xl mt-8 !px-4"></Button>
        </div>
        <div class="flex justify-center md:justify-end hidden">
            <img src="https://iaplc.com/c/wp-content/uploads/sites/3/2021/03/2011_grand_.jpg" alt="Hero Image" class="w-9/12 md:w-auto" />
        </div>
    </div>
    <div class="py-6 px-6 lg:px-20 mt-8 mx-0 lg:mx-20">
        <div class="grid grid-cols-12 gap-4 justify-center">
            <div
                class="col-span-12 p-2 md:p-20 bg-opacity-10"
                style="border-radius: 20px; background: linear-gradient(0deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, #efe1af 0%, #c3dcfa 100%)"
            >
                <div class="flex flex-col justify-center items-center text-center px-4 py-4 md:py-0 font-semibold">
                    <div class="text-white mb-2 text-3xl mix-blend-difference">汇聚全球技艺与智慧的国际生态缸造景大赛</div>
                    <p class="text-white mix-blend-difference sm:line-height-2 md:line-height-4 text-xl mt-6" style="max-width: 800px">
                        通过生态造景这个独特的载体，以艺术的手段,呈现世界各地的生态表现，
                        让每一件作品都成为跨越语言的生命赞歌
                    </p>
                    <a class="text-gray-600 text-2xl text-white mix-blend-difference">关于{{Config.appName}}</a>
                    <!--                    <img src="/demo/images/landing/peak-logo.svg" class="mt-6" alt="Company logo" />-->
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { Config } from "@/api/config";
// import BlurText from "@/bit-blocks/TextAnimations/BlurText/BlurText.vue";
import {inject, onMounted, watch} from "vue";

const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    console.log(newValue);
});

onMounted(async () => {

});
</script>
