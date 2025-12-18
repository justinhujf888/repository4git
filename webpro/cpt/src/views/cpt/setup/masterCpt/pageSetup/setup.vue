<template>
    <ScrollPanel class="w-full">
        <div class="!relative w-full">
            <div class="absolute -top-10 right-1 z-100 row gap-x-2">
                <Button label="设置页面" size="small" severity="warn" rounded />
            </div>
            <div class="mt-10 col md:row w-full">
                <Tree v-model:selectionKeys="selectedTreeNodeKey" v-model:expandedKeys="expandedTreeNodeKey" :value="treeDatas" selectionMode="single" class="w-full md:w-1/4 text-sm" @node-select="onNodeSelect"></Tree>
                <div class="flex-1 p-2">
<!--                    <component v-if="componentIndex>-1" :is="pageComponentMap[componentIndex].component"></component>-->
                    <tp :pageJson="pageJson" :read-only="false"></tp>
                </div>
            </div>
        </div>
    </ScrollPanel>
</template>

<script setup>
import { defineAsyncComponent, onMounted, ref } from 'vue';
import pj from '@/datas/pageJson';
import lodash from 'lodash-es';
import tp from "@/views/cpt/setup/masterCpt/pageSetup/index/tp0.vue";

const treeDatas = ref([]);
const selectedTreeNodeKey = ref(null);
const expandedTreeNodeKey = ref(null);
const componentIndex = ref(-1);
const pageJson = ref({});
let a = "tp0";
let pageComponentMap = [
    {key:"index",component:defineAsyncComponent(()=>{return import(`@/views/cpt/setup/masterCpt/pageSetup/index/${a}.vue`)})},
    {key:"pingWei",component:defineAsyncComponent(()=>{return import(`@/views/cpt/setup/masterCpt/pageSetup/index/${a}.vue`)})},
];

onMounted(()=>{
    treeDatas.value = [];
    lodash.forEach(pj.menuTreeDatas(),(v)=>{
        if (v.isUserSetup) {
            let chs = [];
            lodash.forEach(v.items,(c)=>{
                if (c.isUserSetup) {
                    chs.push(c);
                }
            });
            v.children = chs;
            v.items = null;
            treeDatas.value.push(v);
        }
    });
});

const onNodeSelect = (node) => {
    // console.log(node);

    // if (!node.children || node.children.length<1) {
    //     componentIndex.value = lodash.findIndex(pageComponentMap,(o)=>{return o.key==node.key});
    // } else {
    //     componentIndex.value = -1;
    // }
    pageJson.value = {};
    switch (node.key) {
        case "index":
            pageJson.value = pj.uiIndexJson();
    }
};

</script>

<style scoped lang="scss">

</style>
