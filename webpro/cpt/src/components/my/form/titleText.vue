<template>
    <div class="row gap-x-4 items-center">
        <div class="col w-2">
            <div class="h-3 w-full" :style="`background-color: #${colors[0]}`"></div>
            <div class="h-3 w-full" :style="`background-color: #${colors[1]}`"></div>
            <div class="h-7 w-full" :style="`background-color: #${colors[2]}`"></div>
        </div>
        <span class="text-2xl" :class="textClass">{{text}}</span>
    </div>
</template>

<script setup>
const props = defineProps({
    text: {type:String,default:""},
    colors: {type:Array,default:["e28e44","62a718","1d7b41"]},
    textClass: {type:String,default:"text-white"}
});
</script>

<style scoped lang="scss">

</style>
