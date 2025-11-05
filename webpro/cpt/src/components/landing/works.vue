<template>
    <div id="works" class="py-6 mx-0 my-12">
        <div class="text-center">
            <div class="_text-surface-900 _dark:text-surface-0 font-normal mb-2 text-4xl mix-blend-difference text-white">获奖作品</div>
        </div>
        <div class="mt-20 w-full">
            <div class="box w-full" style="height: 45rem;">
                <div class="imgList left-0">
                    <div class="col">
                        <div class="row h-80 w-auto"><img v-for="img of imgList[0]" :src="img" class="flex-1 h-full object-cover object-center"/></div>
                        <div class="row h-80 w-auto"><img v-for="img of imgList[1]" :src="img" class="flex-1 h-full object-cover object-center"/></div>
                    </div>
                </div>
            </div>
<!--            <DomeGallery-->
<!--                :images="imgList"-->
<!--                :fit="1"-->
<!--                fit-basis="max"-->
<!--                overlay-blur-color="#ffffff"-->
<!--                :min-radius="0"-->
<!--                :segments="34"-->
<!--                :drag-sensitivity="20"-->
<!--                :enlarge-transition-ms="300"-->
<!--                :grayscale="false"-->
<!--                image-border-radius="0px"-->
<!--            />-->
            <!--                overlay-blur-color="#060010" fit-basis="auto"-->
        </div>
    </div>
</template>

<script setup>
import useGlobal from '@/api/hooks/useGlobal';
import { ref } from 'vue';
import lodash from 'lodash-es';
import oss from '@/api/oss';
// import Masonry from '@/bit-blocks/Components/Masonry/Masonry.vue';
// import DomeGallery from "@/bit-blocks/Components/DomeGallery/DomeGallery.vue";

const imgList = ref([[],[]]);
(async ()=>{
    let siteDatas = await useGlobal.siteDatas();
    lodash.forEach(siteDatas.siteInfo.siteOrgHumanList,(v)=>{
        imgList.value[0].push(oss.buildImgPath(v.headImgUrl));
    });
    lodash.forEach(siteDatas.siteInfo.siteJudgeList,(v)=>{
        imgList.value[1].push(oss.buildImgPath(v.headImgUrl));
    });
    imgList.value[0] = lodash.concat(imgList.value[0],imgList.value[1]);
    imgList.value[1] = lodash.concat(imgList.value[1],imgList.value[0]);
})();
</script>

<style scoped lang="scss">
.box {
    margin: 0 auto;
    overflow: hidden;
    position: relative;

    .imgList {
        animation:rolling 30s linear infinite;
        position: absolute;
    }
}

@keyframes rolling {
    from {
        transform: translateX(0);
    }

    to {
        transform: translateX(-50%);
    }
}

</style>
