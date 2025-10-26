<template>
    <div id="highlights" class="py-6 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
        <div class="text-center">
            <div class="_text-surface-900 _dark:text-surface-0 font-normal mb-2 text-4xl mix-blend-difference text-white">组委会成员</div>
            <!--            <span class="text-muted-color text-2xl">Amet consectetur adipiscing elit...</span>-->
        </div>

        <div class="grid xs:grid-cols-2 sm:grid-cols-3 md:grid-cols-4 xl:grid-cols-5 gap-4 mt-10">
            <Card class="overflow-hidden" v-for="man of orgHumanList">
                <template #header>
                    <img :src="man.tempMap.img" class="h-48 w-full object-cover object-center"/>
                </template>
                <template #title>{{man.name}}</template>
<!--                <template #subtitle>-->
<!--                    <span class="text-md mx-3 font-semibold">{{work.guiGe.competition.name}}</span>-->
<!--                    <span class="text-md">{{work.guiGe.name}}</span>-->
<!--                </template>-->
                <template #content>
                    <p class="m-0 py-2 h-14 overflow-hidden text-ellipsis _truncate">
                        {{man.description}}
                    </p>
                </template>
            </Card>
        </div>
    </div>
</template>
<script setup>
import {inject, onMounted, watch, ref} from "vue";
import lodash from "lodash-es";
import oss from "@/api/oss";
const orgHumanList = ref(null);
const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue.siteInfo.siteOrgHumanList);
    lodash.forEach(newValue.siteInfo.siteOrgHumanList,(v)=>{
        v.tempMap = {img:oss.buildImgPath(v.headImgUrl)};
    });
    orgHumanList.value = newValue.siteInfo?.siteOrgHumanList;
});

onMounted(() => {

});
</script>