<template>
    <div class="py-6 px-6 mx-0 md:mx-12 lg:mx-20 lg:px-20 flex items-center justify-between lg:static relative">
        <div class="row gap-4">
            <Button v-if="shiShowButton" id="tagmismenu"
                    class="lg:!hidden"
                    text
                    severity="secondary"
                    rounded
                    v-styleclass="{ selector: '#mis', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true } " click="barButtonClick('mis')"
            >
                <i class="pi pi-bars !text-2xl"></i>
            </Button>
            <div v-else class="w-12"></div>
<!--            <ProgressSpinner v-else strokeWidth="4" fill="transparent" animationDuration=".5s" class="!w-12 !h-12 lg:!hidden"/>-->
            <a class="row items-center">
                <router-link :to="{name:'landing'}" class="text-3xl md:text-4xl font-bold leading-normal mr-20 text-surface-900 dark:text-surface-0">{{siteDatas?.siteInfo.siteCompetition.name}}</router-link>
            </a>
        </div>
        <div id="mis" class="items-center grow justify-between hidden lg:flex absolute lg:static left-10 top-full px-12 lg:px-0 py-4 z-50 bg-surface-0 dark:bg-surface-900 _bg-transparent border-solid border-2 border-gray-200 lg:border-0 rounded-border shadow-2xl lg:shadow-none">
            <ul class="list-none p-0 m-0 flex lg:items-center select-none flex-col lg:flex-row cursor-pointer gap-8 text-base lg:text-lg text-center">
                <li>
<!--                    underline underline-offset-8 decoration-sky-600 decoration-4-->
                    <a @click="smoothScroll('hero')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>关于</span>
                    </a>
                </li>
                <li>
                    <a @click="smoothScroll('features')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>报名参赛</span>
                    </a>
                </li>
                <li>
                    <a @click="smoothScroll('highlights')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>评委</span>
                    </a>
                </li>
                <li>
                    <a @click="smoothScroll('pricing')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>评审标准</span>
                    </a>
                </li>
                <li>
                    <a @click="smoothScroll('pricing')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>获奖作品</span>
                    </a>
                </li>
                <li v-if="userId">
                    <a @click="page.navigateTo('myWorks',null)" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>用户中心</span>
                    </a>
                </li>
            </ul>
        </div>

        <div id="usermenu" class="bg-surface-0 dark:bg-surface-900 border-solid border-2 border-gray-200 rounded-border shadow-2xl absolute right-10 top-20 px-12 py-4 z-50 hidden">
            <ul class="list-none p-0 m-0 flex select-none flex-col cursor-pointer gap-8 text-base">
                <li>
                    <a @click="userBarClick('myWorks')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>我的作品</span>
                    </a>
                </li>
                <li>
                    <a @click="userBarClick('myMessages')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>我的消息</span>
                    </a>
                </li>
                <li>
                    <a @click="userBarClick('forgotpw')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
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
                <Button v-if="shiShowButton" id="tagusermenu" severity="success" variant="outlined" class="rounded-full text-xl w-10 h-10 !border-green-500 border-solid !border-2 animate__animated animate__fadeIn duration-300" rounded
                        v-styleclass="{ selector: '#usermenu', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true }" click="barButtonClick('usermenu')">
                    <i class="pi pi-user font-semibold"></i>
                    <!--                        <span>{{userId}}</span>-->
                </Button>
                <ProgressSpinner v-else strokeWidth="4" fill="transparent" animationDuration=".5s" class="!w-12 !h-12"/>
            </div>
<!--            <Button label="test" @click="shiShowButton=true"/>-->
        </div>
    </div>
</template>

<script setup>
import { useStorage } from '@vueuse/core';
import page from '@/api/uniapp/page';
import dialog from "@/api/uniapp/dialog";
import { inject, onMounted, ref, watch } from 'vue';

const shiShowButton = ref(false);
const userId = useStorage("userId");

const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});

onMounted(() => {
    let timer = setTimeout(() => {
        shiShowButton.value = true;
        clearTimeout(timer);
    },1500);
});

// document.addEventListener('click', (event)=>{
//     // console.log(event.target);
//     let cDom = document.querySelector("#tagusermenu");
//     let tDom = event.target;
//     if (cDom == tDom || cDom.contains(tDom)) {
//
//     } else {
//         if (lodash.findIndex(document.getElementById("usermenu").classList,(o)=>{return o=="hidden"})<0) {
//             document.getElementById("usermenu").classList.toggle("hidden");
//         }
//     }
// });
//
// document.addEventListener('click', (event)=>{
//     // console.log(event.target);
//     let cDom = document.querySelector("#tagmismenu");
//     let tDom = event.target;
//     if (cDom == tDom || cDom.contains(tDom)) {
//
//     } else {
//         if (lodash.findIndex(document.getElementById("mis").classList,(o)=>{return o=="hidden"})<0) {
//             document.getElementById("mis").classList.toggle("hidden");
//         }
//     }
// });

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

function userBarClick(pathName) {
    document.body.click();
    page.navigateTo(pathName,null)
}

function barButtonClick(id) {
    const element = document.getElementById(id);
    if (element) {
        element.classList.toggle("hidden");
    }
}

function logout() {
    dialog.confirm("是否退出当前登录？",()=>{
        localStorage.removeItem("userId");
        userId.value = null;
    },null);
}
</script>
