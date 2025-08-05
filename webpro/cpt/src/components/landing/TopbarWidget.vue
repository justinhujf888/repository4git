<template>
    <a class="row items-center" href="#">
        <span class="text-surface-900 dark:text-surface-0 font-medium sm:text-2xl text-xl leading-normal mr-20">{{Config.appName}}</span>
    </a>

    <div id="mis" class="items-center bg-surface-0 dark:bg-surface-900 grow justify-between hidden lg:flex absolute lg:static right-0 top-full px-12 lg:px-0 py-4 z-20 rounded-border">
        <ul class="list-none p-0 m-0 flex lg:items-center select-none flex-col lg:flex-row cursor-pointer gap-8 text-base lg:text-xl">
            <li>
                <a @click="smoothScroll('hero')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                    <span>首页</span>
                </a>
            </li>
            <li>
                <a @click="smoothScroll('features')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                    <span>Features</span>
                </a>
            </li>
            <li>
                <a @click="smoothScroll('highlights')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                    <span>Highlights</span>
                </a>
            </li>
            <li>
                <a @click="smoothScroll('pricing')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                    <span>Pricing</span>
                </a>
            </li>
        </ul>
    </div>

    <div id="usermenu" class="bg-surface-0 dark:bg-surface-900 absolute right-10 top-20 px-12 py-4 z-20 rounded-border hidden">
        <ul class="list-none p-0 m-0 flex select-none flex-col cursor-pointer gap-8 text-base">
            <li>
                <a @click="Page.navigateTo('forgotpw',null)" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                    <span>修改密码</span>
                </a>
            </li>
            <li>
                <a @click="logout" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                    <span>退出登录</span>
                </a>
            </li>
        </ul>
    </div>

    <div class="row items-center gap-4">
        <div v-if="!userId" class="flex lg:py-0 lg:mt-0 gap-x-2">
            <Button label="登录" text as="router-link" to="/auth/login" rounded></Button>
            <Button label="注册" as="router-link" to="/auth/register" rounded></Button>
        </div>
        <div v-else class="row">
            <!--            <button type="button" class="text-xl w-10 h-10">-->
            <!--                <i class="pi pi-calendar"></i>-->
            <!--            </button>-->
            <!--            <button type="button" class="text-xl w-10 h-10">-->
            <!--                <i class="pi pi-inbox"></i>-->
            <!--            </button>-->
            <Button type="button" class="rounded-full text-xl w-10 h-10 border border-gray-800 border-solid border-2" rounded
                    v-styleclass="{ selector: '#usermenu', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true }">
                <i class="pi pi-user font-semibold"></i>
                <!--                        <span>{{userId}}</span>-->
            </Button>
        </div>
        <Button
            class="lg:!hidden"
            text
            severity="secondary"
            rounded
            v-styleclass="{ selector: '#mis', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true }"
        >
            <i class="pi pi-bars !text-2xl"></i>
        </Button>
    </div>
</template>

<script setup>
import {Config} from "@/api/config";
import { useStorage } from '@vueuse/core';
import Page from '@/api/uniapp/page';
import dialog from "@/api/uniapp/dialog";

const userId = useStorage("userId");
function smoothScroll(id) {
    document.body.click();
    const element = document.getElementById(id);
    if (element) {
        element.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

function logout() {
    dialog.confirm("是否退出当前登录？",()=>{
        localStorage.removeItem("userId");
        userId.value = null;
    },null);
}
</script>
