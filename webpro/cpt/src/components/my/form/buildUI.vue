<template>
    <div v-if="element.type=='title' || element.type=='headTitle'" class="row">
        <title-text :text="element.value" text-class="text-black font-semibold"/>
    </div>
    <div v-else-if="element.type=='box' && element.yeWuType=='titleTextGruop'" class="col">
        <div v-for="group of element.value" class="mt-10 leading-10" :class="groupClass">
            <title-text :text="group.title" text-class="text-black font-semibold"/>
            <div class="mt-10">
                <span v-if="eltTypes.text=='text' || eltTypes.text=='textArea'" class="textwrap text-xl">{{group.text}}</span>
                <div v-else-if="eltTypes.text=='html'" v-html="group.text" :class="htmlClass"></div>
            </div>
            <slot name="groupFoot"></slot>
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
    <div v-else-if="element.type=='html'">
        <div v-html="element.value" :class="htmlClass"></div>
    </div>
    <div v-else-if="element.type=='text'">
        <span>{{element.value}}</span>
    </div>
    <div v-else-if="element.type=='textArea'">
        <span class="textwrap">{{element.value}}</span>
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
import { onMounted, ref } from 'vue';
import pj from '@/datas/pageJson';
import lodash from 'lodash-es';

const element = defineModel("element");
const eltTypes = ref({});
const props = defineProps({
    htmlClass: {type:String,default:""},
    groupClass: {type:String,default:""}
});

onMounted(async ()=>{
    await pj.processPageImageJson(element.value);
    // console.log("element.value",element.value);
    lodash.forEach(element.value.eltTypes,(v)=>{
        eltTypes.value[v.key] = v.type;
    });
    // console.log(eltTypes.value);
});
</script>

<style scoped lang="scss">

</style>
