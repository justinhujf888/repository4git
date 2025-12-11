<template>
    <ScrollPanel class="w-full">
        <div class="!relative w-full">
            <div class="absolute -top-10 right-1 z-100 row gap-x-2">
                <Button label="设置页面" size="small" severity="warn" rounded />
            </div>
            <div class="mt-10 col md:row w-full">
                <Tree v-model:selectionKeys="selectedTreeNodeKey" v-model:expandedKeys="expandedTreeNodeKey" :value="treeDatas" selectionMode="single" class="w-full md:w-1/4 text-sm" @node-select="onNodeSelect"></Tree>
                <div class="flex-1 p-2">
                    <component :is="pageSetupList[pageSetupComponent]"></component>
                </div>
            </div>
        </div>
    </ScrollPanel>
</template>

<script setup>


import { onMounted, ref } from 'vue';
import pageSetupIndex from '@/views/cpt/setup/masterCpt/pageSetup/index/tp0.vue';
import {Beans} from '@/api/dbs/beans';
import lodash from 'lodash-es';


const pageSetupComponent = ref("pageSetupIndex");
const pageSetupList = {pageSetupIndex};
const treeDatas = ref([]);
const selectedTreeNodeKey = ref(null);
const expandedTreeNodeKey = ref(null);

onMounted(()=>{
    treeDatas.value = Beans.menuTreeDatas();
    lodash.forEach(treeDatas.value,(v)=>{
        v.children = v.items;
        v.items = null;
    });
});

const onNodeSelect = (node) => {

};

</script>

<style scoped lang="scss">

</style>
