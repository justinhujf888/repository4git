<template>
    <div>
        <div v-for="(pageEls,i) in judgeDatas" :key="i" :class="{'mt-20':i>0}">
            <div v-for="element of pageEls.setup" class="mt-8" :class="{'md:px-32':element.type!='image'}">
                <build-u-i :element="element" >
                    <template #box="{data}">
                        <DataView :value="data">
                            <template #list="slotProps">
                                <div class="lg:ml-28">
                                    <div class="grid grid-cols-1 md:grid-cols-2 lg:gap-28">
                                        <div v-for="item of slotProps.items" class="row">
                                            <div class="flex flex-col p-3 gap-4">
                                                <div class="relative w-full pb-5 px-5" style="max-width: 35rem;max-height: 35rem;width: 25rem;height: 30rem">
                                                    <Image class="w-full h-full object-cover" :src="item.img.tempMap.imgPath" :pt="{image:{class:'!w-full !h-full object-cover'}}"/>
                                                    <div class="absolute bottom-0 left-0 col gap-2 font-medium w-full p-5 text-white opacity-90" style="background: linear-gradient(45deg, #1d75a4, #2da75c, #f2923f)">
                                                        <span class="text-lg font-semibold">{{item.name}}</span>
                                                        <div class="row">
                                                            <span class="text-lg">{{item.zhiWei}}</span>
                                                            <span class="text-base">{{item.subDescription}}</span>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="gap-2" style="width: 22rem;">
                                                    <span class="text-base textwrap">{{item.description}}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </DataView>
                    </template>
                </build-u-i>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref,useTemplateRef } from 'vue';
import animationPage from "@/components/my/animationPage.vue";
import useGlobal from '@/api/hooks/useGlobal';
import oss from '@/api/oss';
import lodash from 'lodash-es';
import util from '@/api/util';
import BuildUI from '@/components/my/form/buildUI.vue';
import pj from '@/datas/pageJson';

const mainPage = useTemplateRef("mainPage");
const judgeDatas = ref(null);
(async ()=>{
    judgeDatas.value = util.sortBy(await useGlobal.pageSetupDatas("judge"));
})();
</script>

<style scoped lang="scss">

</style>
