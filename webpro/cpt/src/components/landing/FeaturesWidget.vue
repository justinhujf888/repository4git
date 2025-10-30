<!--奖金及奖品-->
<template>
    <div id="features" class="py-6 px-6 lg:px-20 mt-8 mx-0 lg:mx-20">
        <div class="grid grid-cols-12 gap-4 justify-center relative">
            <div class="col-span-12 text-center mt-10 mb-6">
                <GradientText
                    text="奖金及奖品"
                    :colors="['#40ffaa', '#4079ff', '#40ffaa', '#4079ff', '#40ffaa']"
                    :animation-speed="8"
                    :show-border="false"
                    class-name="mix-blend-difference text-white font-normal mb-8 text-4xl"
                />
<!--                <div class="mix-blend-difference text-white font-normal mb-8 text-4xl">奖金及奖品</div>-->
                <span class="text-white mix-blend-difference text-xl">最高奖1、金奖1、银奖2、铜奖3 、优秀奖前30奖牌、奖状、入围奖状、奖牌</span>
            </div>
<!--            <div class="absolute w-full h-[300px] -top-20">-->
<!--                <Orb :hoverIntensity="0.5" :rotateOnHover="true" :hue="0" :forceHoverState="false" />-->
<!--            </div>-->
            <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-4 xl:col-span-4 p-0 lg:pb-8 mt-6 lg:mt-0" v-for="(jiang) of jiangList" v-animateonscroll="{ enterClass: 'animate-enter fade-in-10 animate-duration-1000 zoom-in-50' }">
                <div style="height: 160px; padding: 2px; border-radius: 10px;">
                    <div class="p-4 h-full center col border-4 border-double border-yellow-500/75" style="border-radius: 8px">
<!--                        <div class="flex items-center justify-center mb-4" style="width: 3.5rem; height: 3.5rem; border-radius: 10px" :class="jiang.class">-->
<!--                            <i class="pi pi-fw pi-users !text-2xl text-yellow-700"></i>-->
<!--                        </div>-->
                        <h5 class="mb-2 text-white mix-blend-difference text-2xl">{{jiang.title}}</h5>
                        <span class="text-white mix-blend-difference">{{jiang.desc}}</span>
                    </div>
                </div>
            </div>

<!--            <div class="col-span-12 md:col-span-12 lg:col-span-4 p-0 lg:pr-8 mt-6 lg:mt-0">-->
<!--                <div style="height: 160px; padding: 2px; border-radius: 10px; background: linear-gradient(90deg, rgba(145, 210, 204, 0.2), rgba(160, 210, 250, 0.2)), linear-gradient(180deg, rgba(187, 199, 205, 0.2), rgba(145, 210, 204, 0.2))">-->
<!--                    <div class="p-4 bg-surface-0 dark:bg-surface-900 h-full" style="border-radius: 8px">-->
<!--                        <div class="flex items-center justify-center bg-teal-200 mb-4" style="width: 3.5rem; height: 3.5rem; border-radius: 10px">-->
<!--                            <i class="pi pi-fw pi-shopping-cart !text-2xl text-teal-700"></i>-->
<!--                        </div>-->
<!--                        <div class="mt-6 mb-1 text-surface-900 dark:text-surface-0 text-xl font-semibold">Ready to Use</div>-->
<!--                        <span class="text-surface-600 dark:text-surface-200">Mauris sit amet massa vitae.</span>-->
<!--                    </div>-->
<!--                </div>-->
<!--            </div>-->

<!--            <div class="col-span-12 md:col-span-12 lg:col-span-4 p-0 lg:pr-8 mt-6 lg:mt-0">-->
<!--                <div style="height: 160px; padding: 2px; border-radius: 10px; background: linear-gradient(90deg, rgba(145, 210, 204, 0.2), rgba(212, 162, 221, 0.2)), linear-gradient(180deg, rgba(251, 199, 145, 0.2), rgba(160, 210, 250, 0.2))">-->
<!--                    <div class="p-4 bg-surface-0 dark:bg-surface-900 h-full" style="border-radius: 8px">-->
<!--                        <div class="flex items-center justify-center bg-blue-200 mb-4" style="width: 3.5rem; height: 3.5rem; border-radius: 10px">-->
<!--                            <i class="pi pi-fw pi-globe !text-2xl text-blue-700"></i>-->
<!--                        </div>-->
<!--                        <div class="mt-6 mb-1 text-surface-900 dark:text-surface-0 text-xl font-semibold">Modern Practices</div>-->
<!--                        <span class="text-surface-600 dark:text-surface-200">Elementum nibh tellus molestie nunc non.</span>-->
<!--                    </div>-->
<!--                </div>-->
<!--            </div>-->

<!--            <div class="col-span-12 md:col-span-12 lg:col-span-4 p-0 lg-4 mt-6 lg:mt-0">-->
<!--                <div style="height: 160px; padding: 2px; border-radius: 10px; background: linear-gradient(90deg, rgba(160, 210, 250, 0.2), rgba(212, 162, 221, 0.2)), linear-gradient(180deg, rgba(246, 158, 188, 0.2), rgba(212, 162, 221, 0.2))">-->
<!--                    <div class="p-4 bg-surface-0 dark:bg-surface-900 h-full" style="border-radius: 8px">-->
<!--                        <div class="flex items-center justify-center bg-purple-200 mb-4" style="width: 3.5rem; height: 3.5rem; border-radius: 10px">-->
<!--                            <i class="pi pi-fw pi-eye !text-2xl text-purple-700"></i>-->
<!--                        </div>-->
<!--                        <div class="mt-6 mb-1 text-surface-900 dark:text-surface-0 text-xl font-semibold">Privacy</div>-->
<!--                        <span class="text-surface-600 dark:text-surface-200">Neque egestas congue quisque.</span>-->
<!--                    </div>-->
<!--                </div>-->
<!--            </div>-->


        </div>
        <div class="center">
            <div class="text-base p-1 mt-10 border-btn">
                <a class="center px-20 py-3 bg-gray-900 text-white sub-bg">在线报名</a>
            </div>
        </div>
    </div>
</template>
<script setup>
import {inject, onMounted, watch, ref, getCurrentInstance} from "vue";
import Orb from "@/bit-blocks/Backgrounds/Orb/Orb.vue";
import GradientText from "@/bit-blocks/TextAnimations/GradientText/GradientText.vue";
const jiangList = ref(null);
const siteDatas = ref(null);
const {proxy} = getCurrentInstance();
(async ()=>{
    siteDatas.value = await proxy.$getSiteDatas();
    jiangList.value = [
        {title:"金奖 每组1名",desc:"冠军奖杯,奖牌（排名数字）奖状"},
        {title:"银奖 每组2名",desc:"冠军奖杯,奖牌（排名数字）奖状"},
        {title:"铜奖 每组3名",desc:"冠军奖杯,奖牌（排名数字）奖状"},
        {title:"优异奖 每组4名",desc:"奖牌(排名数字),奖状"},
        {title:"创新奖 每组2名",desc:"奖牌(排名数字),奖状"},
        {title:"入选奖 每组38名",desc:"奖牌(排名数字),奖状"}
    ];
})();

onMounted(() => {

});
</script>