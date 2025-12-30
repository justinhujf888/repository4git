<template>
    <ScrollPanel class="w-full">
        <div class="!relative w-full">
            <div class="absolute -top-10 right-1 z-100 row gap-x-2">
                <Button label="设置页面" size="small" severity="warn" rounded @click="updatePage.open(mainPage)"/>
            </div>
            <div class="mt-10 col md:row w-full">
                <Tree v-model:selectionKeys="selectedTreeNodeKey" v-model:expandedKeys="expandedTreeNodeKey" :value="treeDatas" selectionMode="single" class="w-full md:w-1/4 text-sm" @node-select="onNodeSelect"></Tree>
                <div class="flex-1 p-2">
<!--                    <component v-if="componentIndex>-1" :is="pageComponentMap[componentIndex].component"></component>-->
                    <tp :pageJson="pageJson" :read-only="true"></tp>
                </div>
            </div>
        </div>
    </ScrollPanel>

    <Teleport to="#updatePageJson">
        <animationPage ref="updatePage">
            <div class="card">
                <tp v-model:pageJson="pageJson" :read-only="false"></tp>
                <div class="center gap-4 mt-5">
                    <Button label="确定" size="small" @click="saveJson"/>
                    <Button severity="warn" label="取消" size="small" @click="cancelJson"/>
                </div>
            </div>
        </animationPage>
    </Teleport>
</template>

<script setup>
import { defineAsyncComponent, inject, onMounted, ref, useTemplateRef } from 'vue';
import pj from '@/datas/pageJson';
import lodash from 'lodash-es';
import tp from "@/views/cpt/setup/masterCpt/pageSetup/index/tp0.vue";
import animationPage from "@/components/my/animationPage.vue";

const treeDatas = ref([]);
const selectedTreeNodeKey = ref(null);
const expandedTreeNodeKey = ref(null);
const componentIndex = ref(-1);
const pageJson = ref({});
const mainPage = inject("mainPage");
const updatePage = useTemplateRef("updatePage");

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

function saveJson() {
    console.log(pj.beforeSaveJson(pageJson.value));
}

function cancelJson() {
    updatePage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
