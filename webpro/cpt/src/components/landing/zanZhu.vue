<template>
    <div id="works" class="py-6 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
        <div>
            <title-text :text="indexDatas?.boundArea.setup.title.value" text-class="text-gray-900"/>
        </div>
        <div class="mt-10">
            <div class="row items-center gap-8 flex-wrap">
                <a v-for="bound of indexDatas?.boundArea.setup.boundItems.value">
                    <img :src="bound.img.tempMap.imgPath" class="max-h-20"/>
                </a>
            </div>
        </div>
    </div>
</template>

<script setup>
import { inject, ref, watch } from 'vue';
import oss from '@/api/oss';
import TitleText from '@/components/my/form/titleText.vue';

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
    for(let bound of newValue.boundArea.setup.boundItems.value) {
        bound.img.tempMap = {imgPath:await oss.buildPathAsync(bound.img.img,true,null)};
    }
});

</script>
<style scoped lang="scss">

</style>
