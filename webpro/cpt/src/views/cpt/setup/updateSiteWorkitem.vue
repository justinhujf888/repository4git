<template>
    <div class="p-2 card">
        <Button label="back" @click="callClose"/>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';

const props = defineProps(['sourceType','type'])
const emit = defineEmits(["callClose"]);
const callClose = ()=>{
  emit("callClose");
};

onMounted(() => {
    console.log(props);
});
</script>

<style scoped lang="scss">

</style>
