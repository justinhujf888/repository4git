<template>
    <div id="highlights" class="py-6 px-6 lg:px-20 mx-0 my-12 lg:mx-20">
        <div class="text-center">
            <div class="text-surface-900 dark:text-surface-0 font-normal mb-2 text-4xl">组委会成员</div>
            <!--            <span class="text-muted-color text-2xl">Amet consectetur adipiscing elit...</span>-->
        </div>

        <div class="center grid xs:grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <Card class="overflow-hidden cursor-pointer" v-for="man of orgHumanList">
                <template #header>
                    <img :src="man.tempMap.img" class="h-48 w-full object-cover object-center"/>
                </template>
                <template #title>{{man.name}}</template>
<!--                <template #subtitle>-->
<!--                    <span class="text-md mx-3 font-semibold">{{work.guiGe.competition.name}}</span>-->
<!--                    <span class="text-md">{{work.guiGe.name}}</span>-->
<!--                </template>-->
                <template #content>
                    <p class="m-0">
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