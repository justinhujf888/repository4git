<template>
    <div id="titleText" class="row gap-x-4 items-center">
        <div v-if="styleType==='rect'" class="col w-2">
            <div class="h-3 w-full" style="background-color: var(--title-text-bg1)"></div>
            <div class="h-3 w-full" style="background-color: var(--title-text-bg2)"></div>
            <div class="h-6 w-full" style="background-color: var(--title-text-bg3)"></div>
        </div>
        <div v-else-if="styleType==='css'" :class="titleCss">
            <slot name="css"></slot>
        </div>
        <span :class="textClass">{{text}}</span>
    </div>
</template>

<script setup>
const props = defineProps({
    text: {type:String,default:""},
    textClass: {type:String,default:"title-text"},
    styleType: {type:String,default:"css"},
    titleCss: {type:String,default:"title-css"},
});
</script>

<style scoped lang="scss">

</style>
