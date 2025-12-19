<template>
    <div class="row start flex-wrap gap-4 p-2">
        <div v-for="file of mediaFiles" @click="choiceFile(file)">
            <div class="w-20 h-20 sm:w-20 sm:h-20 md:w-28 md:h-28 xl:w-36 xl:h-36 mx-auto relative col" :class="checkHas(file) ? 'border-4 border-solid border-green-500' :''">
                <Image class="rounded w-full h-full object-cover" :src="file.tempMap.imgPath" :alt="file.name" style="max-width: 300px;" preview_ :pt="{image:{class:'!w-full !h-full !object-cover'}}"/>
            </div>
            <slot name="content" :file="file"></slot>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";
import oss from "@/api/oss";
const mediaFiles = defineModel("mediaFiles", {default:[]});
const selFiles = defineModel("selFiles", {default:[]});
const props = defineProps({
    funCheckHasIndex: Function
});
const checkHas = (file)=>{
    return props.funCheckHasIndex(file,mediaFiles.value,selFiles.value)>=0;
}
const choiceFile = (file)=>{
    let index = props.funCheckHasIndex(file,mediaFiles.value,selFiles.value);
    if (index>=0) {
        selFiles.value.splice(index,1);
    } else {
        selFiles.value.push(file);
    }
    // console.log(selFiles.value.length);
}
</script>

<style scoped lang="scss">

</style>