<template>
    <div v-show="shiShow" class="absolute hwcenter col bg-gray-200 center w-48 h-48 rounded-2xl">
        <div class="loader"></div>
        <div class="text-gray-800 text-xl font-semibold mt-4">
            <span>{{msg}}</span>
        </div>
    </div>
</template>

<script setup>
    import { ref } from 'vue';

    const shiShow = ref(false);
    const msg = ref("Loading...");
    const openLoading = (txt)=>{
        if (txt) {
            msg.value = txt;
        }
        shiShow.value = true;
    };
    const closeLoading = ()=>{
        shiShow.value = false;
    };

    defineExpose({openLoading,closeLoading});
</script>

<style scoped lang="scss">
@import '@/static/css/loading.css';
</style>