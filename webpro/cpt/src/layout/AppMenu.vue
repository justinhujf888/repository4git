<script setup>
import {onMounted, ref} from 'vue';

import AppMenuItem from './AppMenuItem.vue';
import lodash from "lodash-es";
import pageJson from "@/datas/pageJson";
import workRest from "@/api/dbs/workRest";

const model = ref([]);

onMounted(() => {
    if (lodash.includes(window.location.href,"/manage/")) {
        model.value = pageJson.manageMenu();
    } else if (lodash.includes(window.location.href,"/user/")) {
        model.value = [
            {
                label: '',
                items: [{ label: '我的参赛作品', _icon: 'pi pi-fw pi-circle', to: '/user/myWorks' }]
            },
            {
                label: '',
                items: [{ label: '我的消息', _icon: 'pi pi-fw pi-circle', to: '/user/messages' }]
            }
        ];
    }
});
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in model" :key="item">
            <app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator"></li>
        </template>
    </ul>
</template>

<style lang="scss" scoped></style>
