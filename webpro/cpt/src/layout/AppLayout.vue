<script setup>
import { useLayout } from '@/layout/composables/layout';
import {computed, ref, watch, onMounted} from 'vue';
import AppFooter from './AppFooter.vue';
import AppSidebar from './AppSidebar.vue';
import AppTopbar from './AppTopbar.vue';
import Login from "./login.vue";
import {useStorage} from "@vueuse/core";

const { layoutConfig, layoutState, isSidebarActive } = useLayout();

const outsideClickListener = ref(null);
const managerId = useStorage("managerId");
const visible = ref(false);


onMounted(() => {
    // console.log("managerId", managerId.value);
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
</script>

<template>
    <div class="layout-wrapper" :class="containerClass">
        <Dialog v-model:visible="visible" pt:root:class="!border-0 !bg-transparent" pt:mask:class="backdrop-blur-sm">
            <template #container="{ closeCallback }">
                <Login></Login>
            </template>
        </Dialog>
        <app-topbar></app-topbar>
        <app-sidebar></app-sidebar>
        <div class="layout-main-container">
            <div id="app_container" styleClass="layout-main" class="layout-main relative">
                <router-view/>
            </div>
            <app-footer></app-footer>
        </div>
        <div class="layout-mask animate-fadein"></div>
    </div>
</template>

<style>

</style>
