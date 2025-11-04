<template>
    <div id="works" class="py-6 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
        <div class="text-center">
            <div class="_text-surface-900 _dark:text-surface-0 font-normal mb-2 text-4xl mix-blend-difference text-white">获奖作品</div>
        </div>
        <div class="mt-20" style="height: 50rem">
            <Masonry v-if="imgList"
                :items="imgList"
                :duration="0.6"
                :stagger="0.05"
                animate-from="bottom"
                :scale-on-hover="true"
                :hover-scale="0.95"
                :blur-to-focus="true"
                :color-shift-on-hover="false"
            />
        </div>
    </div>
</template>

<script setup>
import useGlobal from '@/api/hooks/useGlobal';
import { ref } from 'vue';
import lodash from 'lodash-es';
import oss from '@/api/oss';
import Masonry from '@/bit-blocks/Components/Masonry/Masonry.vue';

const imgList = ref(null);
(async ()=>{
    let siteDatas = await useGlobal.siteDatas();
    imgList.value = [];
    lodash.forEach(siteDatas.siteInfo.siteOrgHumanList,(v)=>{
        imgList.value.push({id:v.id,img:oss.buildImgPath(v.headImgUrl),height:600});
    });
    lodash.forEach(siteDatas.siteInfo.siteJudgeList,(v)=>{
        imgList.value.push({id:v.id,img:oss.buildImgPath(v.headImgUrl),height:400});
        console.log(v);
    });
})();
</script>

<style scoped lang="scss">

</style>
