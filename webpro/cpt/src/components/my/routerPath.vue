<template>
    <Breadcrumb :home="home" :model="items">
        <template #item="{ item }">
            <a class="cursor-pointer row" :href="item.url ? item.url : '#'" @click="itemClick(item)">
                <span v-if="!home"><</span>
                <span v-if="item.icon" :class="item.icon"></span>
                <span>{{item.label}}</span>
            </a>
        </template>
        <template #separator> > </template>
    </Breadcrumb>
</template>

<script setup>
import { ref } from "vue";

const props = defineProps({
    home: { type: Object, default:null},
    items: { type: Array, default: [] },
    obj: { type: Object }
});

function itemClick(item) {
    if (item.click) {
        item.click();
    }
}
</script>

<style scoped lang="scss">

</style>
