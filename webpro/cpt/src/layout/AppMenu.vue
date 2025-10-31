<script setup>
import {onMounted, ref} from 'vue';

import AppMenuItem from './AppMenuItem.vue';
import lodash from "lodash-es";
import workRest from "@/api/dbs/workRest";

const model = ref([]);

onMounted(() => {
    if (lodash.includes(window.location.href,"/manage/")) {
        model.value = [
            // {
            //     label: '',
            //     items: [{ label: '首页', icon: 'pi pi-fw pi-home', to: '/manage/index' }]
            // },
            {
                label: '',
                items: [
                    {
                        label: "系列赛事设置",icon: 'pi pi-fw pi-bars',
                        items: [
                            // { label: '系列赛事设置', icon: '', to: '/manage/cpt/site/cptSetup' },
                            { label: '基础信息', icon: 'pi pi-fw pi-file', to: '/manage/cpt/site/siteBase' },
                            { label: '主题图', icon: 'pi pi-fw pi-file', to: '/manage/cpt/site/siteZhuTi' },
                            { label: '设置作品', icon: 'pi pi-fw pi-file', to: '/manage/cpt/site/siteWorkItem' },
                            { label: '组委会成员', icon: 'pi pi-fw pi-file', to: '/manage/cpt/site/siteOrgHuman' },
                            { label: '作品字段', icon: 'pi pi-fw pi-file', to: '/manage/cpt/site/siteFields' },
                        ]
                    },
                    { label: '年份赛事', icon: 'pi pi-fw pi-file', to: '/manage/cpt/master/masterCptSetup' },
                    { label: '评委资料库', icon: 'pi pi-fw pi-user', to: '/manage/judge/judgeList' },
                    { label: '发布赛事', icon: 'pi pi-fw pi-file', to: '/manage/cpt/system/buildCache' }
                ]
            },
            // {
            //     label: 'UI Components',
            //     items: [
            //         { label: 'Form Layout', icon: 'pi pi-fw pi-id-card', to: '/uikit/formlayout' },
            //         { label: 'Input', icon: 'pi pi-fw pi-check-square', to: '/uikit/input' },
            //         { label: 'Button', icon: 'pi pi-fw pi-mobile', to: '/uikit/button', class: 'rotated-icon' },
            //         { label: 'Table', icon: 'pi pi-fw pi-table', to: '/uikit/table' },
            //         { label: 'List', icon: 'pi pi-fw pi-list', to: '/uikit/list' },
            //         { label: 'Tree', icon: 'pi pi-fw pi-share-alt', to: '/uikit/tree' },
            //         { label: 'Panel', icon: 'pi pi-fw pi-tablet', to: '/uikit/panel' },
            //         { label: 'Overlay', icon: 'pi pi-fw pi-clone', to: '/uikit/overlay' },
            //         { label: 'Media', icon: 'pi pi-fw pi-image', to: '/uikit/media' },
            //         { label: 'Menu', icon: 'pi pi-fw pi-bars', to: '/uikit/menu' },
            //         { label: 'Message', icon: 'pi pi-fw pi-comment', to: '/uikit/message' },
            //         { label: 'File', icon: 'pi pi-fw pi-file', to: '/uikit/file' },
            //         { label: 'Chart', icon: 'pi pi-fw pi-chart-bar', to: '/uikit/charts' },
            //         { label: 'Timeline', icon: 'pi pi-fw pi-calendar', to: '/uikit/timeline' },
            //         { label: 'Misc', icon: 'pi pi-fw pi-circle', to: '/uikit/misc' }
            //     ]
            // },
            // {
            //     label: 'Pages',
            //     icon: 'pi pi-fw pi-briefcase',
            //     to: '/pages',
            //     items: [
            //         {
            //             label: 'Landing',
            //             icon: 'pi pi-fw pi-globe',
            //             to: '/landing'
            //         },
            //         {
            //             label: 'Auth',
            //             icon: 'pi pi-fw pi-user',
            //             items: [
            //                 {
            //                     label: 'Register',
            //                     icon: 'pi pi-fw pi-sign-in',
            //                     to: '/auth/register'
            //                 },
            //                 {
            //                     label: 'Login',
            //                     icon: 'pi pi-fw pi-sign-in',
            //                     to: '/auth/login'
            //                 },
            //                 {
            //                     label: 'forgotPw',
            //                     icon: 'pi pi-fw pi-sign-in',
            //                     to: '/auth/forgotPw'
            //                 },
            //                 {
            //                     label: 'Error',
            //                     icon: 'pi pi-fw pi-times-circle',
            //                     to: '/auth/error'
            //                 },
            //                 {
            //                     label: 'Access Denied',
            //                     icon: 'pi pi-fw pi-lock',
            //                     to: '/auth/access'
            //                 }
            //             ]
            //         },
            //         {
            //             label: 'Crud',
            //             icon: 'pi pi-fw pi-pencil',
            //             to: '/pages/crud'
            //         },
            //         {
            //             label: 'Not Found',
            //             icon: 'pi pi-fw pi-exclamation-circle',
            //             to: '/pages/notfound'
            //         },
            //         {
            //             label: 'Empty',
            //             icon: 'pi pi-fw pi-circle-off',
            //             to: '/pages/empty'
            //         }
            //     ]
            // },
            // {
            //     label: 'Hierarchy',
            //     items: [
            //         {
            //             label: 'Submenu 1',
            //             icon: 'pi pi-fw pi-bookmark',
            //             items: [
            //                 {
            //                     label: 'Submenu 1.1',
            //                     icon: 'pi pi-fw pi-bookmark',
            //                     items: [
            //                         { label: 'Submenu 1.1.1', icon: 'pi pi-fw pi-bookmark' },
            //                         { label: 'Submenu 1.1.2', icon: 'pi pi-fw pi-bookmark' },
            //                         { label: 'Submenu 1.1.3', icon: 'pi pi-fw pi-bookmark' }
            //                     ]
            //                 },
            //                 {
            //                     label: 'Submenu 1.2',
            //                     icon: 'pi pi-fw pi-bookmark',
            //                     items: [{ label: 'Submenu 1.2.1', icon: 'pi pi-fw pi-bookmark' }]
            //                 }
            //             ]
            //         },
            //         {
            //             label: 'Submenu 2',
            //             icon: 'pi pi-fw pi-bookmark',
            //             items: [
            //                 {
            //                     label: 'Submenu 2.1',
            //                     icon: 'pi pi-fw pi-bookmark',
            //                     items: [
            //                         { label: 'Submenu 2.1.1', icon: 'pi pi-fw pi-bookmark' },
            //                         { label: 'Submenu 2.1.2', icon: 'pi pi-fw pi-bookmark' }
            //                     ]
            //                 },
            //                 {
            //                     label: 'Submenu 2.2',
            //                     icon: 'pi pi-fw pi-bookmark',
            //                     items: [{ label: 'Submenu 2.2.1', icon: 'pi pi-fw pi-bookmark' }]
            //                 }
            //             ]
            //         }
            //     ]
            // },
            // {
            //     label: 'Get Started',
            //     items: [
            //         {
            //             label: 'Documentation',
            //             icon: 'pi pi-fw pi-book',
            //             to: '/documentation'
            //         },
            //         {
            //             label: 'View Source',
            //             icon: 'pi pi-fw pi-github',
            //             url: 'https://github.com/primefaces/sakai-vue',
            //             target: '_blank'
            //         }
            //     ]
            // }
        ];
    } else if (lodash.includes(window.location.href,"/user/")) {
        model.value = [
            {
                label: '',
                items: [{ label: '我的作品', icon: 'pi pi-fw pi-circle', to: '/user/myWorks' }]
            },
            {
                label: '',
                items: [{ label: '我的消息', icon: 'pi pi-fw pi-circle', to: '/user/messages' }]
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
