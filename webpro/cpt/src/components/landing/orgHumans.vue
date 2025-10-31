<template>
    <div id="highlights" class="py-6 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
        <div class="text-center">
            <div class="_text-surface-900 _dark:text-surface-0 font-normal mb-2 text-4xl mix-blend-difference text-white">组委会成员</div>
            <!--            <span class="text-muted-color text-2xl">Amet consectetur adipiscing elit...</span>-->
        </div>

        <div class="grid xs:grid-cols-2 sm:grid-cols-3 md:grid-cols-4 xl:grid-cols-5 gap-4 mt-10">
            <Card class="overflow-hidden" v-for="man of orgHumanList" v-animateonscroll="{ enterClass: 'animate-enter fade-in-10 slide-in-from-t-20 animate-duration-1000' }" :pt="{body:{class:'bg-gray-900'}}">
                <template #header>
                    <img :src="man.tempMap.img" class="h-60 w-full object-cover object-center"/>
                </template>
                <template #title>
                    <span class="text-white">{{man.name}}</span>
                </template>
<!--                <template #subtitle>-->
<!--                    <span class="text-md mx-3 font-semibold">{{work.guiGe.competition.name}}</span>-->
<!--                    <span class="text-md">{{work.guiGe.name}}</span>-->
<!--                </template>-->
                <template #content>
                    <p class="m-0 py-2 h-14 overflow-hidden text-ellipsis _truncate text-gray-300">
                        {{man.description}}
                    </p>
                </template>
            </Card>
        </div>
        <div class="center mt-10">
            <div class="text-base p-1 border-btn">
                <a class="center px-5 lg:px-20 py-3 bg-gray-900 text-white sub-bg">评委</a>
            </div>
            <div class="text-base p-1 border-btn ml-10">
                <a class="center px-5 lg:px-20 py-3 bg-gray-900 text-white sub-bg">评审标准</a>
            </div>
        </div>
    </div>
</template>
<script setup>
import {onMounted, ref, getCurrentInstance} from "vue";
import lodash from "lodash-es";
import oss from "@/api/oss";
const orgHumanList = ref(null);
const siteDatas = ref(null);
const {proxy} = getCurrentInstance();
(async ()=>{
    siteDatas.value = await proxy.$getSiteDatas();
    lodash.forEach(siteDatas.value.siteInfo.siteOrgHumanList,(v)=>{
        v.tempMap = {img:oss.buildImgPath(v.headImgUrl)};
    });
    orgHumanList.value = siteDatas.value.siteInfo?.siteOrgHumanList;
})();

onMounted(() => {

});
</script>