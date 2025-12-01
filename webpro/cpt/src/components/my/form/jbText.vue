<template>
    <span style="background: linear-gradient(to right, #8F41E9,#0be4c3);-webkit-background-clip: text;-webkit-text-fill-color: transparent;"><slot></slot></span>
</template>

<script setup>
const props = defineProps({
    fromto: { type: String, default: "to left" },
    colors: { type: Array }
})
</script>

<style scoped lang="scss">

</style>