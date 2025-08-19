<template>
    <view class="absolute right-2 top-5 text-xs grid grid-cols-2 gap-2">
        <text v-for="(lang,index) in [{id:'en',name:'EN'},{id:'zh-Hans',name:'中文'}]" class="border-4 border-solid silderborder center p-1 rounded-xl" :class="lang.id==locale ? 'border-gray-700 sliderbg text-white' : ''" @tap="changeLocale(lang.id)">{{lang.name}}</text>
    </view>
</template>

<script setup>
import { ref,watch,onMounted,getCurrentInstance, toRefs } from 'vue';
const { proxy } = getCurrentInstance();
const locale = ref("");
onMounted(()=>{
    locale.value = uni.getLocale();
});
watch(locale,(newValue,oldValue)=>{
    locale.value = newValue;
});
const changeLocale = (l)=>{
    proxy.$i18n.locale = l;
    uni.setLocale(l);
    locale.value = l;
};
</script>

<style scoped lang="scss">

</style>