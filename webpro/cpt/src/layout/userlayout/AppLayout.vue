<script setup>
import { useLayout } from '@/layout/composables/layout';
import { computed, ref, watch, onMounted, provide, inject } from 'vue';
import AppFooter from '../AppFooter.vue';
import AppMenu from '../AppMenu.vue';
import TopbarWidget from "@/components/landing/TopbarWidget.vue";
import useGlobal from "@/api/hooks/useGlobal";
import oss from '@/api/oss';
import FooterWidget from '@/components/landing/FooterWidget.vue';

const { layoutConfig, layoutState, isSidebarActive } = useLayout();

const outsideClickListener = ref(null);
const footDatas = ref(null);
const shiShowPage = ref(false);
const siteDatas = ref(null);
(async ()=>{
    siteDatas.value = await useGlobal.siteDatas();
    footDatas.value = await useGlobal.pageSetupDatas("foot");
    footDatas.value.boundArea.setup.subPageImg.value.tempMap = {imgPath:await oss.buildPathAsync(footDatas.value.boundArea.setup.subPageImg.value.img,true,null)};
    shiShowPage.value = true;
})();

provide("siteDatas",siteDatas);
provide("footDatas",footDatas);

onMounted(async () => {

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
    <div class="layout-wrapper bg-surface-0 dark:bg-surface-900 animate__animated animate__fadeIn" :class="containerClass">
<!--        <app-topbar></app-topbar>-->
        <div class="w-full h-80 landing-wrapper">
            <TopbarWidget/>
            <div class="w-full h-56 bg-center bg-cover relative" :style="'background-image: url(\''+footDatas?.boundArea.setup.subPageImg.value.tempMap.imgPath+'\')'">
                <h2 class="-text-surface-0 _mix-blend-difference text-white absolute bottom-10 left-20">{{useGlobal.getRouteInfo().meta.name}}</h2>
            </div>
        </div>
         <div class="layout-main-container lg:!ml-0 !pt-3">
            <div class="row">
<!--                <div class="_layout-sidebar !top-80 w-64 h-dvh mr-5 bg-surface-0 hidden md:block">-->
<!--                    <app-menu></app-menu>-->
<!--                </div>-->
                <div id="app_container" styleClass="layout-main" class="layout-main relative">
                    <router-view/>
                </div>
            </div>
        </div>
        <div class="layout-mask animate-fadein"></div>
        <FooterWidget />
        <ScrollTop />
    </div>
</template>

<style>

</style>
