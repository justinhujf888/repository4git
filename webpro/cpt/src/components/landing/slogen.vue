<template>
    <div class="px-6 lg:px-20 mt-8 mx-0 lg:mx-20 py-20">
        <div class="grid grid-cols-12 gap-4 justify-center">
            <div
                class="col-span-12 p-2 md:p-20 bg-opacity-10"
                _style="border-radius: 20px; background: linear-gradient(0deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, #efe1af 0%, #c3dcfa 100%)"
            >
                <div class="flex flex-col justify-center items-center text-center px-4 py-4 md:py-0">
                    <div class="text-white mb-2 text-3xl mix-blend-difference font-semibold">汇聚全球技艺与智慧的国际生态缸造景大赛</div>
                    <p class="text-white mix-blend-difference sm:line-height-2 md:line-height-4 text-xl mt-6" style="max-width: 800px">
                        通过生态造景这个独特的载体，以艺术的手段,呈现世界各地的生态表现，
                        让每一件作品都成为跨越语言的生命赞歌
                    </p>
                    <div class="text-base p-1 mt-10 border-btn">
                        <a class="center px-20 py-3 bg-white hover:text-white sub-bg">
                            关于{{siteDatas?.siteInfo.siteCompetition.name}}
                        </a>
                    </div>
<!--                    <div class="border-box center">-->
<!--                        <div class="content">-->
<!--                            Lorem ipsum-->
<!--                        </div>-->
<!--                    </div>-->
                    <!--                    <img src="/demo/images/landing/peak-logo.svg" class="mt-6" alt="Company logo" />-->
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { inject, ref, watch } from 'vue';
// import {Config} from "@/api/config";

const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});


</script>
<style scoped>
.border-box {
    border: 4px solid transparent;
    border-radius: 16px;
    background-clip: padding-box, border-box;
    background-origin: padding-box, border-box;
    background-image: linear-gradient(to right, #ffffff, #ffffff), linear-gradient(90deg, #8F41E9, #0be4c3);
}

.border-box {
    width: 300px;
    height: 200px;
    margin: 25px 0;
}

.content {
    display: flex;
    align-items: center;
    padding: 0 8px;
    height: 100%;
}
</style>/
