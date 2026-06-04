<script setup>
import {onMounted, ref} from 'vue';

import AppMenuItem from './AppMenuItem.vue';
import lodash from "lodash-es";
import pageJson from "@/datas/pageJson";
import workRest from "@/api/dbs/workRest";
import util from "@/api/util";
import {ru} from "primelocale/js/ru.js";

const model = ref([]);
let userType = util.giveStorgeCry("userType");

onMounted(() => {
    buildMenu();
});

const buildMenu = ()=>{
    userType = util.giveStorgeCry("userType");
    // console.log(util.giveStorgeCry("managerId"));
    let managerId = util.giveStorgeCry("managerId");
    if (lodash.includes(window.location.href,"/manage/")) {
        model.value = [{label:"",items:[]}];
        let menuJson = pageJson.manageMenu();
        let rulePermissions = [];
        if (lodash.toUpper(util.giveStorgeCry("userType"))=="JUDGE") {
            lodash.forEach(menuJson[0].items, (m) => {
                if (m.userType) {
                    if (lodash.findIndex(m.userType,(o)=>{return o=="judge"})>-1) {
                        rulePermissions.push(m.key);
                    }
                    if (m.items) {
                        lodash.forEach(m.items,(sm)=>{
                            if (lodash.findIndex(sm.userType,(o)=>{return o=="judge"})>-1 ) {
                                rulePermissions.push(sm.key);
                            }
                        });
                    }
                }
            });
        } else if (util.giveStorgeCry("rulePermissions")) {
            rulePermissions = JSON.parse(util.giveStorgeCry("rulePermissions"));
        }
        // console.log(rulePermissions);
        let permissions = [...rulePermissions,"dashboard"];
        lodash.forEach(menuJson[0].items, (m) => {
            if (m.userType) {
                // console.log(userType,m.userType);
                if (lodash.findIndex(m.userType,(o)=>{return o==userType})>-1) {
                    if (m.items) {
                        let mm = lodash.cloneDeep(m);
                        mm.items = [];
                        lodash.forEach(m.items,(sm)=>{
                            if (lodash.findIndex(sm.userType,(o)=>{return o==userType})>-1 && (lodash.findIndex(permissions,(o)=>{return o==sm.key})>-1 || lodash.toUpper(managerId)=="ADMIN")) {
                                mm.items.push(sm);
                            }
                        });
                        if (mm.items.length>0) {
                            model.value[0].items.push(mm);
                        }
                    } else {
                        if (lodash.findIndex(permissions,(o)=>{return o==m.key})>-1 || lodash.toUpper(managerId)=="ADMIN") {
                            model.value[0].items.push(m);
                        }
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
}

defineExpose({buildMenu});
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
