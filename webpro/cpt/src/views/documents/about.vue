<template>
    <div>
        <div v-for="(pageEls,k,i) in aboutDatas" :key="i" :class="{'mt-20':i>0}">
            <div v-for="element of pageEls.setup" class="mt-8" :class="{'md:px-32':element.type!='image'}">
                <build-u-i :element="element" >
                    <template #box="{data}">
                        <DataView :value="data">
                            <template #list="slotProps">
                                <div class="grid grid-cols-1 md:grid-cols-2">
                                    <div v-for="item of slotProps.items" class="row">
                                        <div class="row p-3 gap-8">
                                            <div class="w-40 h-48 md:w-60 md:h-72 relative">
<!--                                                <img class="block xl:block mx-auto rounded w-full" :src="item.img.tempMap.imgPath" />-->
                                                <Image class="w-full h-full object-cover" :src="item.img.tempMap.imgPath" :pt="{image:{class:'!w-full !h-full object-cover'}}"/>
                                            </div>
                                            <div class="col gap-2 font-semibold flex-1">
                                                <span class="text-lg font-semibold">{{item.name}}</span>
                                                <span class="text-base font-medium">{{item.zhiWei}}</span>
                                                <span class="text-base font-medium">{{item.subDescription}}VAC面向全球景观行业从业者和造景爱好者，汇聚全球智慧，造景师们通过艺术的手段，呈现自</span>
<!--                                                <span class="text-base font-medium textwrap">{{item.description}}</span>-->
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
const aboutDatas = ref(null);
(async ()=>{
    aboutDatas.value = util.sortBy(await useGlobal.pageSetupDatas("about"));
})();
</script>

<style scoped lang="scss">

</style>
