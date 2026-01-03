<template>
    <div id="highlights" class="py-6 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
        <title-text :text="indexDatas?.judgeArea.setup.title.value"/>
        <div class="grid xs:grid-cols-2 sm:grid-cols-3 md:grid-cols-4 xl:grid-cols-5 gap-4 mt-10">
            <div class="overflow-hidden mt-8" v-for="man of indexDatas?.judgeArea.setup.judgeItems.value" v-animateonscroll="{ enterClass: 'animate-enter fade-in-10 slide-in-from-t-20 animate-duration-1000' }" :pt="{body:{class:'bg-transparent'}}">
                <img :src="man.img.tempMap.imgPath" class="h-60 w-full object-cover object-center"/>
                <div class="mt-5 text-white col gap-2">
                    <span>{{man.name}}</span>
                    <span>{{man.subDescription}}</span>
                    <span>{{man.zhiWei}}</span>
                </div>
<!--                <template #header>-->
<!--                    <img :src="man.tempMap.imgPath" class="h-60 w-full object-cover object-center"/>-->
<!--                </template>-->
<!--                <template #title>-->
<!--                    <span class="text-white">{{man.name}}</span>-->
<!--                </template>-->
<!--                <template #subtitle>-->
<!--                    <span class="text-md mx-3 font-semibold">{{work.guiGe.competition.name}}</span>-->
<!--                    <span class="text-md">{{work.guiGe.name}}</span>-->
<!--                </template>-->
<!--                <template #content>-->
<!--                    <p class="m-0 py-2 h-14 overflow-hidden text-ellipsis _truncate text-gray-300">-->
<!--                        {{man.subDescription}}-->
<!--                    </p>-->
<!--                </template>-->
            </div>
        </div>
        <div class="center mt-16 sm:row col gap-8">
            <div class="text-base p-1 border-btn">
                <a class="center w-56 py-3 bg-gray-950 text-white font-semibold sub-bg">评委</a>
            </div>
            <div class="text-base p-1 border-btn">
                <a class="center w-56 py-3 bg-gray-950 text-white font-semibold sub-bg">评审标准</a>
            </div>
        </div>
    </div>
</template>
<script setup>
import { inject, onMounted, ref, watch } from 'vue';
import lodash from "lodash-es";
import oss from "@/api/oss";
import TitleText from '@/components/my/form/titleText.vue';

const orgHumanList = ref(null);
const indexDatas = inject("indexDatas");
const siteDatas = inject("siteDatas");
watch(siteDatas,async (newValue)=>{
    // console.log(newValue);
    // for(let v of newValue.siteInfo.siteJudgeList) {
    //     v.tempMap = {img:await oss.buildPathAsync(v.headImgUrl,true,null)};
    // }
    // orgHumanList.value = newValue.siteInfo?.siteJudgeList;
});
watch(indexDatas,async (newValue)=>{
    // console.log(newValue);
    for(let v of newValue.judgeArea.setup.judgeItems.value) {
        v.img.tempMap = {imgPath:await oss.buildPathAsync(v.img.value,true,null)};
    }
});
onMounted(() => {

});
</script>
