<template>
    <div class="py-6 px-6 mx-0 mt-10 lg:mx-20">
        <div class="center">
            <router-link class="cursor-pointer font-bold text-surface-900 dark:text-surface-0 text-4xl" to="/">
                <img :src="footDatas?.boundArea.setup.mImg.value.tempMap.imgPath" class="!max-h-12"/>
            </router-link>
        </div>
        <span class="center text-xl mt-10">快速链接</span>
        <div id="footmenu2" class="row sm:row justify-center flex-wrap gap-20 mt-10">
            <div class="col start" v-for="menu in treeDatas">
                <div class="col">
                    <span class="text-base md:text-xl font-semibold cursor-pointer" @click="userBarClick({isLogin:false,route:menu.route})">{{menu.label}}</span>
                    <ul v-if="menu.menuType==1">
                        <li v-for="item of menu.children" class="col my-4 cursor-pointer" @click="userBarClick(item)">
                            <span>{{item.label}}</span>
                        </li>
                    </ul>
                </div>
            </div>
<!--            <ul class="col sm:row gap-4">-->
<!--                <li>-->
<!--                    &lt;!&ndash;                    underline underline-offset-8 decoration-sky-600 decoration-4&ndash;&gt;-->
<!--                    <a @click="navPage('hero')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">-->
<!--                        <span>关于</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--                <li>-->
<!--                    <a @click="navPage('features')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">-->
<!--                        <span>报名参赛</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--                <li>-->
<!--                    <a @click="navPage('highlights')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">-->
<!--                        <span>评委</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--                <li>-->
<!--                    <a @click="navPage('pricing')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">-->
<!--                        <span>评审标准</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--                <li>-->
<!--                    <a @click="navPage('pricing')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">-->
<!--                        <span>获奖作品</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--                <li v-if="userId">-->
<!--                    <a @click="navPage('myWorks')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">-->
<!--                        <span>用户中心</span>-->
<!--                    </a>-->
<!--                </li>-->
<!--            </ul>-->

<!--            <div class="col-span-12 md:col-span-10">-->
<!--                <div class="grid grid-cols-12 gap-8 text-center md:text-left">-->
<!--                    <div class="col-span-12 md:col-span-3">-->
<!--                        <h4 class="font-medium text-2xl leading-normal mb-4 text-surface-900 dark:text-surface-0">Company</h4>-->
<!--                        <router-link to="/manage/index" class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Manager Control</router-link>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">About Us</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">News</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Investor Relations</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Careers</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer text-surface-700 dark:text-surface-100">Media Kit</a>-->
<!--                    </div>-->

<!--                    <div class="col-span-12 md:col-span-3">-->
<!--                        <h4 class="font-medium text-2xl leading-normal mb-4 text-surface-900 dark:text-surface-0">Resources</h4>-->
<!--                        <a @click="Page.redirectTo('dashboard',null)" class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Get Started</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Learn</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer text-surface-700 dark:text-surface-100">Case Studies</a>-->
<!--                    </div>-->

<!--                    <div class="col-span-12 md:col-span-3">-->
<!--                        <h4 class="font-medium text-2xl leading-normal mb-4 text-surface-900 dark:text-surface-0">Community</h4>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Discord</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Events<img src="/demo/images/landing/new-badge.svg" class="ml-2" /></a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">FAQ</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer text-surface-700 dark:text-surface-100">Blog</a>-->
<!--                    </div>-->

<!--                    <div class="col-span-12 md:col-span-3">-->
<!--                        <h4 class="font-medium text-2xl leading-normal mb-4 text-surface-900 dark:text-surface-0">Legal</h4>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Brand Policy</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer mb-2 text-surface-700 dark:text-surface-100">Privacy Policy</a>-->
<!--                        <a class="leading-normal text-xl block cursor-pointer text-surface-700 dark:text-surface-100">Terms of Service</a>-->
<!--                    </div>-->
<!--                </div>-->
<!--            </div>-->
        </div>
        <div class="mt-10">
            <div class="row items-center gap-8 flex-wrap center">
                <a v-for="link of footDatas?.boundArea.setup.linkItems.value">
                    <img :src="link.img.tempMap.imgPath" class="max-h-6"/>
                </a>
            </div>
        </div>
    </div>
    <div class="bg-gray-900 col center py-4 text-sm">
        <span v-for="item of footDatas?.boundArea.setup.footItems.value" class="text-white">@{{item.text}}</span>
<!--        <span class="text-white">@东莞隽捷信息科技有限公司提供技术支持</span>-->
    </div>
</template>

<script setup>
import Page from '@/api/uniapp/page';
import { inject, onMounted, ref, watch } from 'vue';
import { useStorage } from '@vueuse/core';
import oss from '@/api/oss';
import lodash from 'lodash-es';
import pj from '@/datas/pageJson';
import page from '@/api/uniapp/page';
import util from '@/api/util';

const userId = useStorage("userId");
const siteDatas = inject("siteDatas");
const footDatas = inject("footDatas");
const treeDatas = ref([]);
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});
watch(footDatas,async (newValue)=>{
    // console.log(newValue);
    newValue.boundArea.setup.mImg.value.tempMap = {imgPath:await oss.buildPathAsync(newValue.boundArea.setup.mImg.value.img)}
    for(let link of newValue.boundArea.setup.linkItems.value) {
        link.img.tempMap = {imgPath:await oss.buildPathAsync(link.img.img,true,null)};
    }
});

onMounted(()=>{
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
});

function navPage(pageName) {
    Page.navigateTo(pageName,null)
}

function userBarClick(treeNode) {
    util.checkLoginGoPage(treeNode);
}
</script>

<style scoped lang="scss">
#footmenu {
    ul {
        li {
            text-align: center;
            a {
                span {
                    color: #5f6b67;
                    font-size: 0.9rem;
                }
            }
        }
        li::after {
            content: "|";
            padding-left: 1.5rem;
        }
        @media (max-width: 576px) {
            li::after {
                content: "";
                padding-left: 0rem;
            }
        }
        li:last-child::after {
            content: "";
        }
    }
}
</style>
