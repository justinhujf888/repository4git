<template>
    <div>
        <div v-for="(pageEls,k,i) in saiZhiDatas" :key="i" :class="{'mt-20':i>0}">
            <div v-for="element of pageEls.setup" class="mt-8" :class="{'md:px-32':element.type!='image'}">
                <build-u-i :element="element" >
                    <template #box="{data}">
                        <DataView :value="data">
                            <template #list="slotProps">
                                <div class="col">
                                    <div v-for="item of slotProps.items" class="row">
                                        <div class="flex flex-col md:flex-row p-3 gap-4">
                                            <div class="md:w-40 relative">
                                                <img class="block xl:block mx-auto rounded w-full" :src="item.img.tempMap.imgPath" />
                                            </div>
                                            <div class="col gap-2 font-semibold flex-1">
                                                <span class="text-lg font-medium">{{item.name}}</span>
                                                <span class="text-lg font-medium">{{item.zhiWei}}</span>
                                                <span class="text-lg font-medium">{{item.subDescription}}</span>
                                                <span class="text-lg font-medium textwrap">{{item.description}}</span>
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
import { ref, useTemplateRef } from 'vue';
import animationPage from "@/components/my/animationPage.vue";
import useGlobal from '@/api/hooks/useGlobal';
import oss from '@/api/oss';
import lodash from 'lodash-es';
import util from '@/api/util';
import BuildUI from '@/components/my/form/buildUI.vue';
import pj from '@/datas/pageJson';

const saiZhiDatas = ref(null);
(async () => {
    saiZhiDatas.value = util.sortBy(await useGlobal.pageSetupDatas("saiZhi"));
})();
</script>

<style scoped lang="scss">

</style>
