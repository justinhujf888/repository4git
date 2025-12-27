<template>
<!--    <span :style="jbStyleStr"><slot></slot></span>-->
    <div>
<!--        {{jbStyleStr}}-->
        <span :style="jbStyleStr"><slot></slot></span>
    </div>
</template>

<script setup>
import {ref, watch} from "vue";

const colors = defineModel("colors",{default:[]});
const fromto = defineModel("fromto",{default:"to right"});

const colorStr = ref("");
const jbStyleStr = ref("");

function buildTextStyle(colors) {
    // console.log(colors);
    colorStr.value = "";
    for(let i = 0; i < colors.length; i++) {
        colorStr.value += colors[i];
        if (i<colors.length - 1) {
            colorStr.value += ",";
        }

    }
    jbStyleStr.value = `background: linear-gradient(${fromto.value}, ${colorStr.value});-webkit-background-clip: text;-webkit-text-fill-color: transparent;`;
}

watch(colors,(newValue)=>{
    buildTextStyle(newValue);
    // console.log("newValue",jbStyleStr.value);
})
</script>

<style scoped lang="scss">

</style>