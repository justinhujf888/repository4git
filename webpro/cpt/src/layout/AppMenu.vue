<script setup>
import {onMounted, ref} from 'vue';

import AppMenuItem from './AppMenuItem.vue';
import lodash from "lodash-es";
import pageJson from "@/datas/pageJson";
import workRest from "@/api/dbs/workRest";
import util from "@/api/util";

const model = ref([]);
let userType = util.giveStorgeCry("userType");

onMounted(() => {
    if (lodash.includes(window.location.href,"/manage/")) {
        model.value = [{label:"",items:[]}];
        let menuJson = pageJson.manageMenu();
        lodash.forEach(menuJson[0].items, (m) => {
            if (m.userType) {
                // console.log(userType,m.userType);
                if (lodash.findIndex(m.userType,(o)=>{return o==userType})>-1) {
                    if (m.items) {
                        let mm = lodash.cloneDeep(m);
                        mm.items = [];
                        lodash.forEach(m.items,(sm)=>{
                            if (lodash.findIndex(sm.userType,(o)=>{return o==userType})>-1) {
                                mm.items.push(sm);
                            }
                        });
                        model.value[0].items.push(mm);
                    } else {
                        model.value[0].items.push(m);
                    }
                    // console.log(JSON.stringify(model.value));
                }
            } else {
                model.value[0].items.push(m);
            }
        });
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
