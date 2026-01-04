<template>
    <div v-if="element.type=='title' || element.type=='headTitle'" class="row">
        <title-text :text="element.value" text-class="text-black font-semibold"/>
    </div>
    <div v-else-if="element.type=='box' && element.yeWuType=='titleTextGruop'" class="row">
        <div v-for="group of element.value" class="mt-10 leading-10">
            <title-text :text="group.title" text-class="text-black font-semibold"/>
            <div class="mt-10">
                <span class="textwrap text-xl">{{group.text}}</span>
            </div>
        </div>
    </div>
    <div v-else-if="element.type=='box' && element.yeWuType!='titleTextGruop'" class="row">
<!--        <div v-for="item of element.value" class="mt-10 leading-10">-->
<!--            {{item}}-->
<!--        </div>-->
        <slot name="box" :data="element.value"></slot>
    </div>
    <div v-else-if="element.type=='image'">
        <div v-if="element.value.img" class="w-full">
            <Image class="rounded w-full" :src="element.value?.tempMap?.imgPath" preview_ :pt="{image:{class:'!w-full'}}"/>
        </div>
    </div>
    <div v-else-if="element.type=='link'">
        <div class="flex flex-col justify-center items-center text-center px-4 py-4 md:py-0">
            <div class="text-base p-1 border-btn mt-10">
                <a :href="element.value.url" class="center py-2 px-10 bg-lime-100 font-semibold sub-bg" target="_blank">{{element.value.text}}</a>
            </div>
        </div>
    </div>
</template>

<script setup>
import TitleText from '@/components/my/form/titleText.vue';
import { onMounted } from 'vue';
import pj from '@/datas/pageJson';

const element = defineModel("element");

onMounted(async ()=>{
    await pj.processPageImageJson(element.value);
    // console.log("element.value",element.value);
});
</script>

<style scoped lang="scss">

</style>
