<template>
    <div class="card">
        <routerPath :home="null" :items="menuItems"/>
    </div>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import routerPath from "@/components/my/routerPath.vue";

const competition = ref(null);
const menuItems = ref([]);

let mainPage = null;
let mePage = null;
let obj = null;

onMounted(() => {
    menuItems.value = [
        {label:'返回我的参赛作品',click:()=>{obj.returnFunction(obj);mePage.close(mainPage);}}
    ];
});

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    competition.value = obj.data;
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
