<template>
    <div :class="[styleClass,shiAnimationPageShow ? `block ${animationInName}` : `${animationOutName} hidden`]" :style="{
		'animation-duration'         : duration,
		'animation-timing-function'  : timingFunction,
		'animation-fill-mode'        : fillMode,
		'animation-delay'            : delay,
		'animation-iteration-count'  : iterationCcount
	}">
        <slot></slot>
    </div>
</template>

<script setup>
import { ref } from "vue";
const props = defineProps(['animationInName','animationOutName','duration','timingFunction','fillMode','delay','iterationCcount','styleClass','show']);
const animationInName = ref(props.animationInName ? props.animationInName : "slideInRight");
const animationOutName = ref(props.animationOutName ? props.animationOutName : "slideOutRight");
const duration = ref(props.duration ? props.duration : "500ms");
const fillMode = ref(props.fillMode ? props.fillMode : "forwards");
const delay = ref(props.delay ? props.delay : "0ms");
const timingFunction = ref(props.timingFunction ? props.timingFunction : "linear");
const iterationCcount = ref(props.iterationCcount ? props.iterationCcount : 1);

const styleClass = ref(props.styleClass ? props.styleClass : "");
const shiAnimationPageShow = ref(props.show ? props.show : false);

const open = ()=>{
    shiAnimationPageShow.value = true;
}
const close = ()=>{
    shiAnimationPageShow.value = false;
}
defineExpose({ open, close });
</script>

<style scoped lang="scss">

</style>
