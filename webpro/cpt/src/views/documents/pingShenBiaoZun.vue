<template>
    <div>
        <div v-for="(pageEls,k,i) in psbzDatas" :key="i" :class="{'mt-20':i>0}">
            <div v-for="element of pageEls.setup" class="mt-8" :class="{'md:px-32':element.type!='image'}">
                <build-u-i :element="element" html-class="text-xl leading-10">
                    <template #groupFoot>
                        <div class="mt-10"></div>
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
const psbzDatas = ref(null);
(async ()=>{
    psbzDatas.value = util.sortBy(await useGlobal.pageSetupDatas("pingShenBiaoZun"));
})();
</script>

<style scoped lang="scss">

</style>
