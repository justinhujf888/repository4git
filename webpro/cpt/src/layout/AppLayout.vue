<script setup>
import { useLayout } from '@/layout/composables/layout';
import {computed, ref, watch, onMounted, useTemplateRef} from 'vue';
import AppFooter from './AppFooter.vue';
import AppSidebar from './AppSidebar.vue';
import AppTopbar from './AppTopbar.vue';
import Login from "./login.vue";
import dialog from "@/api/uniapp/dialog";
import util from "@/api/util";
import useGlobal from "@/api/hooks/useGlobal";
import Page from "@/api/uniapp/page";
import AppMenu from "@/layout/AppMenu.vue";

const { layoutConfig, layoutState, isSidebarActive } = useLayout();

const outsideClickListener = ref(null);
const managerId = ref(null);
const managerInfo = ref(null);
const visible = ref(false);
const appSideBar = useTemplateRef("appSideBar");


onMounted(() => {
    if (localStorage.getItem("managerId")) {
        managerId.value = util.giveStorgeCry("managerId");
    }
    if (localStorage.getItem("managerInfo")) {
        managerInfo.value = JSON.parse(util.giveStorgeCry("managerInfo"));
    }
    // console.log("managerId", managerId,"managerInfo",managerInfo);
    // console.log(appSideBar.value);
    if (!managerId.value) {
        visible.value = true;
    }
});

watch(isSidebarActive, (newVal) => {
    if (newVal) {
        bindOutsideClickListener();
    } else {
        unbindOutsideClickListener();
    }
});

const containerClass = computed(() => {
    return {
        'layout-overlay': layoutConfig.menuMode === 'overlay',
        'layout-static': layoutConfig.menuMode === 'static',
        'layout-static-inactive': layoutState.staticMenuDesktopInactive && layoutConfig.menuMode === 'static',
        'layout-overlay-active': layoutState.overlayMenuActive,
        'layout-mobile-active': layoutState.staticMenuMobileActive
    };
});

function bindOutsideClickListener() {
    if (!outsideClickListener.value) {
        outsideClickListener.value = (event) => {
            if (isOutsideClicked(event)) {
                layoutState.overlayMenuActive = false;
                layoutState.staticMenuMobileActive = false;
                layoutState.menuHoverActive = false;
            }
        };
        document.addEventListener('click', outsideClickListener.value);
    }
}

function unbindOutsideClickListener() {
    if (outsideClickListener.value) {
        document.removeEventListener('click', outsideClickListener);
        outsideClickListener.value = null;
    }
}

function isOutsideClicked(event) {
    const sidebarEl = document.querySelector('.layout-sidebar');
    const topbarEl = document.querySelector('.layout-menu-button');

    return !(sidebarEl.isSameNode(event.target) || sidebarEl.contains(event.target) || topbarEl.isSameNode(event.target) || topbarEl.contains(event.target));
}

function afterLogin(manager) {
    visible.value = false;
    // dialog.toastSuccess("您已成功登录系统");
    dialog.alertBack("您已成功登录系统",()=>{
        // console.log(appSideBar.value);
        if (localStorage.getItem("managerId")) {
            managerId.value = util.giveStorgeCry("managerId");
        }
        if (localStorage.getItem("managerInfo")) {
            managerInfo.value = JSON.parse(util.giveStorgeCry("managerInfo"));
        }
        if (!managerId.value) {
            visible.value = true;
        }
        appSideBar.value.buildMenu();
        Page.redirectTo("dashboard",null);
    });
}

function afterLogout() {
    visible.value = true;
}
</script>

<template>
    <div class="layout-wrapper relative" :class="containerClass">
        <div v-if="visible" class="w-dvw h-dvh bg-gray-900 opacity-50 absolute top-0 left-0" style="z-index: 1000"></div>
        <Dialog v-model:visible="visible" pt:root:class="!border-0 !bg-transparent" pt:mask:class="backdrop-blur-sm">
            <template #container="{ closeCallback }">
                <Login @afterLogin="afterLogin"></Login>
            </template>
        </Dialog>
        <app-topbar v-model:manager-info="managerInfo" @afterLogout="afterLogout"></app-topbar>
<!--        <app-sidebar ref="appSideBar"></app-sidebar>-->
        <div class="layout-sidebar">
            <app-menu ref="appSideBar"></app-menu>
        </div>
        <div class="layout-main-container">
            <div class="my-2 mb-5">
                <span class="text-2xl">{{useGlobal.getRouteInfo().meta.name}}</span>
            </div>
            <div id="app_container" styleClass="layout-main" class="layout-main relative" style="overflow-x: auto">
                <router-view/>
            </div>
            <app-footer></app-footer>
        </div>
        <div class="layout-mask animate-fadein"></div>
    </div>
</template>

<style>

</style>
