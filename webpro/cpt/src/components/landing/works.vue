<template>
    <div id="works">
        <div class="mt-28 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
            <title-text :text="indexDatas?.workStoreArea.setup.title.value" text-class="text-gray-900"/>
        </div>
        <div class="mt-5 w-full py-3 mx-0 my-12">
            <div class="box w-full" style="height: 36rem;">
                <div class="imgList left-0">
                    <div class="bg-blue-500" style="height: 32rem;width: 190rem" :style="'background-image: url(\''+indexDatas?.workStoreArea.setup.mImg.value.tempMap.imgPath+'\')'">
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
            <div class="center">
                <div class="text-base p-1 border-btn">
                    <a class="center px-12 py-2 bg-lime-100 text-gray-800 font-semibold sub-bg">更多获奖作品</a>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { inject, ref, watch } from 'vue';
import lodash from 'lodash-es';
import oss from '@/api/oss';
import TitleText from '@/components/my/form/titleText.vue';
// import Masonry from '@/bit-blocks/Components/Masonry/Masonry.vue';
// import DomeGallery from "@/bit-blocks/Components/DomeGallery/DomeGallery.vue";

const imgList = ref([[],[]]);
const siteDatas = inject("siteDatas");
const indexDatas = inject("indexDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
    // let siteDatas = newValue;
    // lodash.forEach(siteDatas.siteInfo.siteOrgHumanList,(v)=>{
    //     imgList.value[0].push(oss.buildImgPath(v.headImgUrl));
    // });
    // lodash.forEach(siteDatas.siteInfo.siteJudgeList,(v)=>{
    //     imgList.value[1].push(oss.buildImgPath(v.headImgUrl));
    // });
    // imgList.value[0] = lodash.concat(imgList.value[0],imgList.value[1]);
    // imgList.value[1] = lodash.concat(imgList.value[1],imgList.value[0]);
});
watch(indexDatas,async (newValue)=>{
    // console.log(newValue);
    newValue.workStoreArea.setup.mImg.value.tempMap = {imgPath:await oss.buildPathAsync(newValue.workStoreArea.setup.mImg.value.img,true,null)};
});

</script>
<style scoped lang="scss">
.box {
    margin: 0 auto;
    overflow: hidden;
    position: relative;

    .imgList {
        -webkit-animation:linear rolling 80s infinite;
        animation:linear rolling 80s infinite;
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
