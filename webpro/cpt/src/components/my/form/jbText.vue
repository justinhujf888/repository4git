<template>
    <span :style="jbStyleStr"><slot></slot></span>
</template>

<script setup>
import {onMounted, ref, watch} from "vue";

const props = defineProps({
    fromto: { type: String, default: "to right" },
    colors: { type: Array, default: [] }
});

const colorStr = ref("");
const jbStyleStr = ref("");

function buildTextStyle(colors) {
    for(let i = 0; i < colors.length; i++) {
        colorStr.value += colors[i];
        if (i<colors.length - 1) {
            colorStr.value += ",";
        }
    }
    jbStyleStr.value = `background: linear-gradient(${props.fromto}, ${colorStr.value});-webkit-background-clip: text;-webkit-text-fill-color: transparent;`;
}

onMounted(()=>{
    if (props.colors) {
        buildTextStyle(props.colors);
    }
})

watch(props,(newValue)=>{
    buildTextStyle(newValue.colors);
})
</script>

<style scoped lang="scss">

</style>