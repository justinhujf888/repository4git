<!--奖金及奖品-->
<template>
    <div id="features" class="py-6 px-6 lg:px-20 mt-8 mx-0 lg:mx-20">
        <title-text :text="indexDatas?.jiangArea.setup.title.value"/>
        <jiang :jiang-datas="indexDatas?.jiangArea.setup.jiangItems.value" root-class="mt-20" jiang-text-class="text-white"/>
        <div class="center mt-20">
            <div class="text-base p-1 border-btn">
                <a class="center px-16 py-2 bg-gray-950 text-white font-semibold sub-bg" @click="page.redirectTo('saiZhi',null)">在线报名</a>
            </div>
        </div>
    </div>
</template>
<script setup>
import { inject, onMounted, ref, watch } from 'vue';
// import Orb from "@/bit-blocks/Backgrounds/Orb/Orb.vue";
// import GradientText from "@/bit-blocks/TextAnimations/GradientText/GradientText.vue";
import TitleText from '@/components/my/form/titleText.vue';
import Jiang from '@/views/documents/components/jiang.vue';
import page from '@/api/uniapp/page';

const indexDatas = inject("indexDatas");
const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});

onMounted(() => {

});
</script>
