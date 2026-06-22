<template>
    <div id="titleText" class="row gap-x-4 items-center">
        <div class="col w-2">
            <div class="h-3 w-full" style="background-color: var(--title-text-bg1)"></div>
            <div class="h-3 w-full" style="background-color: var(--title-text-bg2)"></div>
            <div class="h-6 w-full" style="background-color: var(--title-text-bg3)"></div>
        </div>
        <span :class="textClass">{{text}}</span>
    </div>
</template>

<script setup>
const props = defineProps({
    text: {type:String,default:""},
    textClass: {type:String,default:"title-text"}
});
</script>

<style scoped lang="scss">

</style>
