<template>
    <div class="p-2 card">
        <Button label="back" @click="callClose"/>
    </div>
</template>

<script setup>
const emit = defineEmits(["callClose"]);
const callClose = ()=>{
  emit("callClose");
};
</script>

<style scoped lang="scss">

</style>
