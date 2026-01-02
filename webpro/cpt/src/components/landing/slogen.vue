<template>
    <div class="relative bg-center bg-cover" :style="'background-image: url(\''+indexDatas?.sloganArea.setup.bgImg.value.tempMap.imgPath+'\')'">
        <div class="px-6 lg:px-20 mx-0 lg:mx-20 py-10">
             <div class="grid grid-cols-12 gap-4 justify-center">
                <div
                    class="col-span-12 p-2 md:p-20 bg-opacity-10"
                    _style="border-radius: 20px; background: linear-gradient(0deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, #efe1af 0%, #c3dcfa 100%)"
                >
                    <div class="flex flex-col justify-center items-center text-center px-4 py-4 md:py-0">
                        <div class="text-white mb-2 text-3xl mix-blend-difference font-semibold">{{indexDatas?.sloganArea.setup.title.value}}</div>
                        <p class="text-white mix-blend-difference sm:line-height-2 md:line-height-4 text-xl mt-6" style="max-width: 800px">
                            {{indexDatas?.sloganArea.setup.subtitle.value}}
                        </p>
                        <div class="text-base p-1 border-btn mt-10">
                            <a class="center w-56 py-3 bg-lime-100 font-semibold sub-bg">关于{{siteDatas?.siteInfo.siteCompetition.name}}</a>
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
    </div>
</template>

<script setup>
import { inject, onMounted, ref, watch } from 'vue';
import oss from '@/api/oss';
// import {Config} from "@/api/config";
const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});
const indexDatas = inject("indexDatas");
watch(indexDatas,async (newValue)=>{
    newValue.sloganArea.setup.bgImg.value.tempMap = {imgPath:await oss.buildPathAsync(newValue.sloganArea.setup.bgImg.value.img,true,null)};
    newValue.sloganArea.setup.bgImg.value.tempMap.bgurl = "bg-[url('" + newValue.sloganArea.setup.bgImg.value.tempMap.imgPath + "')]";
});

onMounted(()=>{

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
