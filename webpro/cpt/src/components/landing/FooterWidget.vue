<template>
    <div class="py-6 px-6 mx-0 mt-10 lg:mx-20">
        <div class="center">
            <router-link class="cursor-pointer font-bold text-surface-900 dark:text-surface-0 text-4xl" to="/">{{siteDatas?.siteInfo.siteCompetition.name}}</router-link>
        </div>
        <div id="footmenu" class="center mt-5">
            <ul class="col sm:row gap-4">
                <li>
                    <!--                    underline underline-offset-8 decoration-sky-600 decoration-4-->
                    <a @click="navPage('hero')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">
                        <span>关于</span>
                    </a>
                </li>
                <li>
                    <a @click="navPage('features')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">
                        <span>报名参赛</span>
                    </a>
                </li>
                <li>
                    <a @click="navPage('highlights')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">
                        <span>评委</span>
                    </a>
                </li>
                <li>
                    <a @click="navPage('pricing')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">
                        <span>评审标准</span>
                    </a>
                </li>
                <li>
                    <a @click="navPage('pricing')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">
                        <span>获奖作品</span>
                    </a>
                </li>
                <li v-if="userId">
                    <a @click="navPage('myWorks')" class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium cursor-pointer">
                        <span>用户中心</span>
                    </a>
                </li>
            </ul>

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
    </div>
    <div class="jb-bg-0 col center py-4 text-sm">
        <span class="text-white">@观赏鱼协会</span>
        <span class="text-white">@东莞隽捷信息科技有限公司提供技术支持</span>
    </div>
</template>

<script setup>
import Page from '@/api/uniapp/page';
import { inject,watch } from 'vue';
import { useStorage } from '@vueuse/core';

const userId = useStorage("userId");
const siteDatas = inject("siteDatas");
watch(siteDatas,(newValue)=>{
    // console.log(newValue);
});

function navPage(pageName) {
    Page.navigateTo(pageName,null)
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
