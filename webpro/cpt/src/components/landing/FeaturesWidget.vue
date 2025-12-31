<!--奖金及奖品-->
<template>
    <div id="features" class="py-6 px-6 lg:px-20 mt-8 mx-0 lg:mx-20">
        <div class="grid grid-cols-12 gap-4 justify-center relative">
<!--            <div class="col-span-12 text-center mt-10 mb-6">-->
<!--                <GradientText-->
<!--                    text="奖金及奖品"-->
<!--                    :colors="['#40ffaa', '#4079ff', '#40ffaa', '#4079ff', '#40ffaa']"-->
<!--                    :animation-speed="8"-->
<!--                    :show-border="false"-->
<!--                    class-name="mix-blend-difference text-white font-normal mb-8 text-4xl"-->
<!--                />-->
<!--                <div class="mix-blend-difference text-white font-normal mb-8 text-4xl">奖金及奖品</div>-->
<!--                <span class="text-white mix-blend-difference text-xl">最高奖1、金奖1、银奖2、铜奖3 、优秀奖前30奖牌、奖状、入围奖状、奖牌</span>-->
<!--            </div>-->
<!--            <div class="absolute w-full h-[300px] -top-20">-->
<!--                <Orb :hoverIntensity="0.5" :rotateOnHover="true" :hue="0" :forceHoverState="false" />-->
<!--            </div>-->
            <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-4 xl:col-span-4 p-0 lg:pb-8 mt-6 lg:mt-0" v-for="(jiang) of indexDatas?.jiangArea.setup.jiangItems.value" v-animateonscroll="{ enterClass: 'animate-enter fade-in-10 animate-duration-1000 zoom-in-50' }">
                <div style="height: 160px; padding: 2px; border-radius: 10px;">
                    <div class="p-4 h-full center col gap-4" style="border-radius: 8px">
<!--                        <div class="flex items-center justify-center mb-4" style="width: 3.5rem; height: 3.5rem; border-radius: 10px" :class="jiang.class">-->
<!--                            <i class="pi pi-fw pi-users !text-2xl text-yellow-700"></i>-->
<!--                        </div>-->
                        <jb-text class="text-4xl font-semibold siyuansongBold" :colors="buildColors(jiang.name.colors)" :fromto="jiang.name.des+'deg'">{{jiang.name.text}}</jb-text>
                        <span class="font-semibold text-xl mix-blend-difference text-white">{{jiang.jiangCount}}</span>
                        <span class="text-white mix-blend-difference">{{jiang.jiangJing}}</span>
                        <span class="text-white mix-blend-difference">{{jiang.jiangPing}}</span>
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
        <div class="center mt-20">
            <div class="text-base p-1 border-btn">
                <a class="center px-20 py-3 bg-gray-950 text-white font-semibold sub-bg">在线报名</a>
            </div>
        </div>
    </div>
</template>
<script setup>
import { inject, onMounted, ref, watch } from 'vue';
// import Orb from "@/bit-blocks/Backgrounds/Orb/Orb.vue";
import GradientText from "@/bit-blocks/TextAnimations/GradientText/GradientText.vue";
import JbText from "@/components/my/form/jbText.vue";
import lodash from 'lodash-es';

const indexDatas = inject("indexDatas");
const jiangList = ref(null);
const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});

onMounted(() => {

});
function buildColors(cry) {
    let ay = [];
    lodash.forEach(cry,(value,index) => {
        ay.push("#"+value);
    });
    return ay;
}
</script>
