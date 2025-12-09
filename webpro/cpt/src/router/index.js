import AppLayout from '@/layout/AppLayout.vue';
import userLayout from '@/layout/userlayout/AppLayout.vue';
import { createRouter, createWebHistory } from 'vue-router';
import lodash from 'lodash-es';

const router = createRouter({
    // base: import.meta.env.BASE_URL,
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/user',
            component: userLayout,
            children: [
                {
                    path: '/user/myWorks',
                    name: 'myWorks',
                    component: () => import('@/views/user/work/myWorks.vue'),
                    meta:{
                        name: "我的作品"
                    }
                },
                {
                    path: '/user/messages',
                    name: 'myMessages',
                    component: () => import('@/views/user/messages.vue'),
                    meta:{
                        name: "我的消息"
                    }
                },
            ]
        },
        {
            path: '/manage',
            component: AppLayout,
            children: [
                {
                    path: '/manage/index',
                    name: 'dashboard',
                    // component: () => import('@/views/Dashboard.vue')
                    component: () => import('@/views/cpt/setup/siteCpt/cptSetup.vue')
                },
                {
                    path: '/manage/cpt/site/cptSetup',
                    name: 'cptSetup',
                    component: () => import('@/views/cpt/setup/siteCpt/cptSetup.vue'),
                    meta:{
                        isHide: true,
                        keepAlive:true
                    }
                },
                {
                    path: '/manage/cpt/site/siteBase',
                    name: 'siteBase',
                    component: () => import('@/views/cpt/setup/siteCpt/coms/siteBase.vue')
                },
                {
                    path: '/manage/cpt/site/siteZhuTi',
                    name: 'siteZhuTi',
                    component: () => import('@/views/cpt/setup/siteCpt/coms/siteZhuTi.vue')
                },
                {
                    path: '/manage/cpt/site/siteWorkItem',
                    name: 'siteWorkItem',
                    component: () => import('@/views/cpt/setup/siteCpt/coms/siteWorkItem.vue')
                },
                {
                    path: '/manage/cpt/site/siteOrgHuman',
                    name: 'siteOrgHuman',
                    component: () => import('@/views/cpt/setup/siteCpt/coms/siteOrgHuman.vue')
                },
                {
                    path: '/manage/cpt/site/siteFields',
                    name: 'siteFields',
                    component: () => import('@/views/cpt/setup/siteCpt/coms/siteFields.vue')
                },
                {
                    path: '/manage/cpt/site/updateSiteCpt',
                    name: 'updateSiteCpt',
                    component: () => import('@/views/cpt/setup/siteCpt/updateSiteCpt.vue'),
                    meta:{
                        isHide: true,
                        keepAlive:true
                    }
                },
                {
                    path: '/manage/cpt/site/updateSiteWorkitem',
                    name: 'updateSiteWorkitem',
                    component: () => import('@/views/cpt/setup/siteCpt/updateSiteWorkitem.vue')
                },
                {
                    path: '/manage/cpt/master/masterCptSetup',
                    name: 'masterCptSetup',
                    component: () => import('@/views/cpt/setup/masterCpt/masterCptSetup.vue')
                },
                {
                    path: '/manage/cpt/masterCpt/pageSetup/index',
                    name: 'pageSetupIndex',
                    component: () => import('@/views/cpt/setup/masterCpt/pageSetup/index/tp0.vue')
                },
                {
                    path: '/manage/judge/judgeList',
                    name: 'judgeList',
                    component: () => import('@/views/cpt/judge/judgeList.vue')
                },
                {
                    path: '/manage/cpt/system/buildCache',
                    name: 'buildCache',
                    component: () => import('@/views/cpt/setup/system/buildCache.vue')
                },
                {
                    path: '/manage/cpt/system/mediaFiles',
                    name: 'mediaFiles',
                    component: () => import('@/views/cpt/setup/system/mediaFiles.vue')
                },
                {
                    path: '/uikit/formlayout',
                    name: 'formlayout',
                    component: () => import('@/views/uikit/FormLayout.vue')
                },
                {
                    path: '/uikit/input',
                    name: 'input',
                    component: () => import('@/views/uikit/InputDoc.vue'),
                    meta: { transition: 'slide-right' }
                },
                {
                    path: '/uikit/button',
                    name: 'button',
                    component: () => import('@/views/uikit/ButtonDoc.vue')
                },
                {
                    path: '/uikit/table',
                    name: 'table',
                    component: () => import('@/views/uikit/TableDoc.vue')
                },
                {
                    path: '/uikit/list',
                    name: 'list',
                    component: () => import('@/views/uikit/ListDoc.vue')
                },
                {
                    path: '/uikit/tree',
                    name: 'tree',
                    component: () => import('@/views/uikit/TreeDoc.vue')
                },
                {
                    path: '/uikit/panel',
                    name: 'panel',
                    component: () => import('@/views/uikit/PanelsDoc.vue')
                },

                {
                    path: '/uikit/overlay',
                    name: 'overlay',
                    component: () => import('@/views/uikit/OverlayDoc.vue')
                },
                {
                    path: '/uikit/media',
                    name: 'media',
                    component: () => import('@/views/uikit/MediaDoc.vue')
                },
                {
                    path: '/uikit/message',
                    name: 'message',
                    component: () => import('@/views/uikit/MessagesDoc.vue')
                },
                {
                    path: '/uikit/file',
                    name: 'file',
                    component: () => import('@/views/uikit/FileDoc.vue')
                },
                {
                    path: '/uikit/menu',
                    name: 'menu',
                    component: () => import('@/views/uikit/MenuDoc.vue')
                },
                {
                    path: '/uikit/charts',
                    name: 'charts',
                    component: () => import('@/views/uikit/ChartDoc.vue')
                },
                {
                    path: '/uikit/misc',
                    name: 'misc',
                    component: () => import('@/views/uikit/MiscDoc.vue')
                },
                {
                    path: '/uikit/timeline',
                    name: 'timeline',
                    component: () => import('@/views/uikit/TimelineDoc.vue')
                },
                {
                    path: '/pages/empty',
                    name: 'empty',
                    component: () => import('@/views/pages/Empty.vue')
                },
                {
                    path: '/pages/crud',
                    name: 'crud',
                    component: () => import('@/views/pages/Crud.vue')
                },
                {
                    path: '/documentation',
                    name: 'documentation',
                    component: () => import('@/views/pages/Documentation.vue')
                }
            ]
        },
        {
            path: '/',
            name: 'landing',
            component: () => import('@/views/pages/Landing.vue'),
            meta:{
                name: "首页"
            }
        },
        {
            path: '/pages/notfound',
            name: 'notfound',
            component: () => import('@/views/pages/NotFound.vue')
        },

        {
            path: '/auth/login',
            name: 'login',
            component: () => import('@/views/pages/auth/Login.vue')
        },
        {
            path: '/auth/register',
            name: 'register',
            component: () => import('@/views/pages/auth/register.vue')
        },
        {
            path: '/auth/forgotPw',
            name: 'forgotpw',
            component: () => import('@/views/pages/auth/forgotPw.vue')
        },
        {
            path: '/auth/access',
            name: 'accessDenied',
            component: () => import('@/views/pages/auth/Access.vue')
        },
        {
            path: '/auth/error',
            name: 'error',
            component: () => import('@/views/pages/auth/Error.vue')
        }
    ],
    scrollBehavior(to, from, savedPosition) {
        // 始终滚动到顶部
        return { top: 0 }
    },
});

router.beforeEach((to, from) => {
    // ...
    if (lodash.includes(to.href,"/manage/")) {
        document.documentElement.classList.add('app-dark');
    } else {
        document.documentElement.classList.remove('app-dark');
    }
    // 返回 false 以取消导航
    return true
})

export default router;
