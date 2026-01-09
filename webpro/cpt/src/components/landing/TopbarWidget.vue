<template>
    <div class="py-6 px-6 mx-0 flex items-center justify-between lg:static relative">
<!--        md:mx-12 lg:mx-20 lg:px-20-->
        <div class="row items-center gap-4">
            <div class="w-12">
                <Button v-if="shiShowButton" id="tagmismenu"
                        class="lg:!hidden"
                        text
                        severity="secondary"
                        rounded
                        _v-styleclass="{ selector: '#mis', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true } " click="barButtonClick('mis')" @click="toggle(null,$event)">
                    <i class="pi pi-bars !text-2xl"></i>
                </Button>
            </div>
<!--            <ProgressSpinner v-else strokeWidth="4" fill="transparent" animationDuration=".5s" class="!w-12 !h-12 lg:!hidden"/>-->
            <div>
                <router-link class="cursor-pointer font-bold text-surface-900 dark:text-surface-0" to="/">
                    <img :src="footDatas?.boundArea.setup.mImg.value.tempMap.imgPath" class="!max-h-8"/>
                </router-link>
<!--                <router-link :to="{name:'landing'}" class="text-xl lg:text-3xl font-bold leading-normal mr-20 text-surface-900 dark:text-surface-0">{{siteDatas?.siteInfo.siteCompetition.name}}</router-link>-->
            </div>
        </div>
        <div class="">
            <ul class="list-none p-0 m-0 flex lg:items-center select-none hidden lg:flex lg:flex-row cursor-pointer gap-8 text-base lg:text-lg text-center">
                <li v-for="menu of menuBar" v-styleclass="{ selector: '#mis', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true } " click="barButtonClick('mis')">
                    <!--                    underline underline-offset-8 decoration-sky-600 decoration-4-->
                    <a _click="smoothScroll('hero')" class="px-5 py-4 text-surface-900 dark:text-surface-0 font-medium hover:underline hover:underline-offset-8 hover:decoration-orange-500 hover:decoration-solid hover:decoration-4" @mouseenter="toggle(menu,$event)" @click="toggle(menu,$event)">
                        <span>{{menu.name}}</span>
                    </a>
                </li>
            </ul>
        </div>

        <div id="usermenu" class="bg-surface-0 dark:bg-surface-900 border-solid border-2 border-gray-200 rounded-border shadow-2xl absolute right-10 top-20 px-12 py-4 z-50 hidden">
            <ul class="list-none p-0 m-0 flex select-none flex-col cursor-pointer gap-8 text-base">
                <li>
                    <a @click="userBarClick({route:'myWorks',isLogin:true})" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>我的作品</span>
                    </a>
                </li>
                <li>
                    <a @click="userBarClick({route:'myMessages',isLogin:true})" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
                        <span>我的消息</span>
                    </a>
                </li>
                <li>
                    <a @click="showForgotMode=true" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium">
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

        <div class="row items-center gap-4" v-if="shiShowButton">
            <div v-if="!userId" class="flex lg:py-0 lg:mt-0 gap-x-2">
                <Button label="登录" text rounded @click="wantTo='';showLoginMode=true"></Button>
                <Button label="注册" rounded @click="wantTo='';showRegistMode=true"></Button>
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
        <Teleport to="body">
            <Dialog modal header="登录" class="w-[40rem]" v-model:visible="showLoginMode" pt:mask:class="backdrop-blur-sm">
                <div>
                    <login @afterLogin="afterLogin" @cancel="cancelLogin" @forgot="loginForgot" @regist="loginRegist"/>
                </div>
            </Dialog>

            <Dialog modal header="注册" class="w-[40rem]" v-model:visible="showRegistMode" pt:mask:class="backdrop-blur-sm">
                <div>
                    <register @afterLogin="afterRegist" @cancel="cancelRegist"/>
                </div>
            </Dialog>

            <Dialog modal header="重置密码" class="w-[40rem]" v-model:visible="showForgotMode" pt:mask:class="backdrop-blur-sm">
                <div>
                    <forgot-pw @afterLogin="afterForgot4Login" @afterSave="afterForgot4Save" @cancel="cancelForgot"/>
                </div>
            </Dialog>
        </Teleport>

        <Popover ref="op">
            <div class="px-2 md:px-10 py-5">
                <div class="grid md:grid-cols-4 grid-cols-3 gap-2 md:gap-4 top-10">
                    <div class="col start" v-for="menu in treeDatas">
                        <div class="col">
                            <span class="text-base md:text-xl px-5 font-semibold cursor-pointer" :class="menu.isHover ? 'underline underline-offset-8 decoration-orange-500 decoration-solid decoration-4': ''" @click="userBarClick({isLogin:false,route:menu.route})" @mouseenter="toggle(menu,$event)">{{menu.label}}</span>
                            <ul v-if="menu.menuType==1" class="mt-5">
                                <li v-for="item of menu.children" class="col my-4 cursor-pointer" @click="userBarClick(item);">
                                    <span class="text-base px-5">{{item.label}}</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="mt-10 md:between">
                    <div class="row">
                        <a v-for="link of footDatas?.boundArea.setup.linkItems.value">
                            <span class="px-5 text-sm">{{link.text}}</span>
                        </a>
                    </div>
                    <div class="hidden md:row">
                        <a v-for="item of footDatas?.boundArea.setup.footItems.value">
                            <span class="px-5 text-sm">{{item.text}}</span>
                        </a>
                    </div>
                </div>
            </div>
        </Popover>
    </div>
</template>

<script setup>
import { useStorage } from '@vueuse/core';
import page from '@/api/uniapp/page';
import dialog from "@/api/uniapp/dialog";
import { inject, onMounted, ref, watch, useTemplateRef } from 'vue';
import oss from '@/api/oss';
import lodash from 'lodash-es';
import pj from '@/datas/pageJson';
import util from '@/api/util';
import Page from '@/api/uniapp/page';
import Login from '@//views/pages/auth/Login.vue';
import Register from '@/views/pages/auth/register.vue';
import ForgotPw from '@/views/pages/auth/forgotPw.vue';

import { useEventBus } from '@vueuse/core';
import pageJson from '@/datas/pageJson';
const bus = useEventBus('login');
const unsubscribe = bus.on(busListener);

const shiShowButton = ref(false);
const userId = useStorage("userId");
const treeDatas = ref([]);
const menuBar = ref([{key:"pingJiang",name:"评奖"},{key:"huoJiangWork",name:"获奖作品"},{key:"userCenter",name:"参赛"}]);

const siteDatas = inject("siteDatas");
const footDatas = inject("footDatas");
const op = useTemplateRef("op");
const showLoginMode = ref(false);
const showRegistMode = ref(false);
const showForgotMode = ref(false);
const wantTo = ref("");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});
watch(footDatas,async (newValue)=>{
    // console.log(newValue);
    newValue.boundArea.setup.mImg.value.tempMap = {imgPath:await oss.buildPathAsync(newValue.boundArea.setup.mImg.value.img)}
    // for(let link of newValue.boundArea.setup.linkItems.value) {
    //     link.img.tempMap = {imgPath:await oss.buildPathAsync(link.img.img,true,null)};
    // }
});

onMounted(() => {
    treeDatas.value = [];
    lodash.forEach(pj.menuTreeDatas(),(v)=>{
        if (v.isInPageMenu) {
            let chs = [];
            lodash.forEach(v.items,(c)=>{
                if (c.isInPageMenu) {
                    chs.push(c);
                }
            });
            v.children = chs;
            v.items = null;
            treeDatas.value.push(v);
        }
    });
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

function busListener(treeNode) {
    userBarClick(treeNode);
}

function toggle(menu,event) {
    op.value.show(event);
    if (menu) {
        lodash.forEach(treeDatas.value,(v,k)=>{
            v.isHover = false;
        });
        let m = lodash.find(treeDatas.value,(o)=>{return o.key==menu.key});
        m.isHover = true;
    }
}

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

function userBarClick(treeNode) {
    if (!util.checkLoginGoPage(treeNode)) {
        wantTo.value = treeNode.route;
        showLoginMode.value = true;
    }
    if (treeNode.route) {
        op.value.hide();
    }
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

function afterLogin(_userId,_loginToken) {
    userId.value = _userId;
    showLoginMode.value = false;
    if (wantTo.value) {
        page.redirectTo(wantTo.value,null);
    }
}
function cancelLogin() {
    showLoginMode.value = false;
}
function loginForgot() {
    showLoginMode.value = false;
    showForgotMode.value = true;
}
function loginRegist() {
    showLoginMode.value = false;
    showRegistMode.value = true;
}
function afterRegist(_userId,_loginToken) {
    userId.value = _userId;
    showRegistMode.value = false;
    if (wantTo.value) {
        page.redirectTo(wantTo.value,null);
    }
}
function cancelRegist() {
    showRegistMode.value = false;
}
function afterForgot4Login(_userId) {
    showForgotMode.value = true;
}
function afterForgot4Save() {
    showForgotMode.value = false;
    showLoginMode.value = true;
}
function cancelForgot() {
    showForgotMode.value = false;
}
</script>
