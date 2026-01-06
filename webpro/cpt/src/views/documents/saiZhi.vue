<template>
    <div>
        <div v-for="(pageEls,k,i) in saiZhiDatas" :key="i" :class="{'mt-20':i>0}" class="text-xl leading-10">
            <div v-for="element of pageEls.setup" class="mt-8 md:px-32" :style="element.type=='image' ? 'max-width:80rem' : ''">
                <jiang v-if="element.type=='slot'" :jiang-datas="indexDatas?.jiangArea.setup.jiangItems.value" root-class="mt-20" jiang-text-class="text-gray-800"/>
                <build-u-i v-else :element="element">
                    <template #box="{data}">
                        <DataView :value="data">
                            <template #list="slotProps">
                                <div class="col">
                                    <div v-for="item of slotProps.items" class="row my-4">
                                        <div class="flex flex-col items-center md:flex-row p-3 gap-4">
                                            <div class="md:w-72 relative">
                                                <img class="block xl:block mx-auto rounded w-full" :src="item.img.tempMap.imgPath" />
                                            </div>
                                            <div class="col gap-2 font-semibold flex-1">
                                                <span class="text-base font-semibold textwrap">{{item.description}}</span>
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
        <div class="center mt-16 sm:row col gap-8">
            <div class="text-base p-1 border-btn">
                <a class="center px-16 py-2 bg-lime-100 text-gray-800 font-semibold sub-bg" @click="bus.emit({route:'myWorks',isLogin:true})">报名参赛</a>
            </div>
            <div class="text-base p-1 border-btn">
                <a class="center px-16 py-2 bg-lime-100 text-gray-800 font-semibold sub-bg">评审规则</a>
            </div>
        </div>
    </div>
</template>

<script setup>
import { inject, ref, useTemplateRef } from 'vue';
import useGlobal from '@/api/hooks/useGlobal';
import util from '@/api/util';
import page from "@/api/uniapp/page";
import BuildUI from '@/components/my/form/buildUI.vue';
import Jiang from '@/views/documents/components/jiang.vue';
import { useEventBus } from '@vueuse/core';
const bus = useEventBus('login');

const saiZhiDatas = ref(null);
const indexDatas = ref(null);

(async () => {
    saiZhiDatas.value = util.sortBy(await useGlobal.pageSetupDatas("saiZhi"));
    indexDatas.value = await useGlobal.pageSetupDatas("index");
})();
</script>

<style scoped lang="scss">

</style>
