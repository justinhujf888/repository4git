import AppLayout from '@/layout/AppLayout.vue';
import userLayout from '@/layout/userlayout/AppLayout.vue'
import withLeftLayOut from '@/layout/userlayout/withLeftLayOut.vue';
import { createRouter, createWebHistory } from 'vue-router';
import lodash from 'lodash-es';
import { useEventBus } from '@vueuse/core';
import pageJson from '@/datas/pageJson';
import page from '@/api/uniapp/page';
import util from "@/api/util";
const bus = useEventBus('login');

const router = createRouter({
    // base: import.meta.env.BASE_URL,
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/user',
            component: withLeftLayOut,
            children: [
                {
                    path: '/user/myWorks',
                    name: 'myWorks',
                    component: () => import('@/views/user/work/myWorks.vue'),
                    meta:{
                        name: "我的参赛作品"
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
            path: '/us',
            component: userLayout,
            children: [
                {
                    path: '/us/about',
                    name: 'about',
                    component: () => import('@/views/documents/about.vue'),
                    meta:{
                        name: "关于我们"
                    }
                },
                {
                    path: '/us/judge',
                    name: 'judge',
                    component: () => import('@/views/documents/judge.vue'),
                    meta:{
                        name: "评委"
                    }
                },
                {
                    path: '/us/saiZhi',
                    name: 'saiZhi',
                    component: () => import('@/views/documents/saiZhi.vue'),
                    meta:{
                        name: "赛制"
                    }
                },
                {
                    path: '/us/pingShenBiaoZun',
                    name: 'pingShenBiaoZun',
                    component: () => import('@/views/documents/pingShenBiaoZun.vue'),
                    meta:{
                        name: "评审标准"
                    }
                },
                {
                    path: '/us/cjWorks',
                    name: 'cjWorks',
                    component: () => import('@/views/documents/works/cjWorks.vue'),
                    meta:{
                        name: "本届优胜作品"
                    }
                }
            ]
        },
        {
            path: '/manage',
            component: AppLayout,
            children: [
                {
                    path: '/manage/index',
                    name: 'dashboard',
                    component: () => import('@/views/Dashboard.vue')
                    // component: () => import('@/views/cpt/setup/siteCpt/coms/siteBase.vue')
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
                    component: () => import('@/views/cpt/setup/masterCpt/masterCptSetup.vue'),
                    meta:{
                        name: "年份赛事"
                    }
                },
                {
                    path: '/manage/cpt/masterCpt/pageSetup/index',
                    name: 'pageSetupIndex',
                    component: () => import('@/views/cpt/setup/masterCpt/pageSetup/index/tp0.vue')
                },
                {
                    path: '/manage/judge/judgeList',
                    name: 'judgeList',
                    component: () => import('@/views/cpt/judge/judgeList.vue'),
                    meta:{
                        name: "评委资料库"
                    }
                },
                {
                    path: '/manage/cpt/system/buildCache',
                    name: 'buildCache',
                    component: () => import('@/views/cpt/setup/system/buildCache.vue'),
                    meta:{
                        name: "发布赛事"
                    }
                },
                {
                    path: '/manage/cpt/system/mediaFiles',
                    name: 'mediaFiles',
                    component: () => import('@/views/cpt/setup/system/mediaFiles.vue'),
                    meta:{
                        name: "素材库"
                    }
                },
                {
                    path: '/manage/cpt/system/rules',
                    name: 'rules',
                    component: () => import('@/views/cpt/setup/rules/ruleList.vue'),
                    meta:{
                        name: "角色权限"
                    }
                },
                {
                    path: '/manage/cpt/system/managers',
                    name: 'managers',
                    component: () => import('@/views/cpt/setup/manager/managerList.vue'),
                    meta:{
                        name: "管理员设置"
                    }
                },
                {
                    path: '/manage/cpt/pingshen/managerReview',
                    name: 'managerReview',
                    component: () => import('@/views/cpt/pingshen/managerReview.vue'),
                    meta:{
                        name: "作品审查"
                    }
                },
                {
                    path: '/manage/cpt/pingshen/psSetup',
                    name: 'psSetup',
                    component: () => import('@/views/cpt/pingshen/psSetup.vue'),
                    meta:{
                        name: "评审流程"
                    }
                },
                {
                    path: '/manage/cpt/pingshen/psFen',
                    name: 'psFen',
                    component: () => import('@/views/cpt/pingshen/pingshenFen.vue'),
                    meta:{
                        name: "作品评分"
                    }
                },
                {
                    path: '/manage/cpt/pingshen/psReport',
                    name: 'psReport',
                    component: () => import('@/views/cpt/pingshen/psReport.vue'),
                    meta:{
                        name: "评选排名"
                    }
                },
                {
                    path: '/manage/cpt/pingshen/psFilte',
                    name: 'psFilte',
                    component: () => import('@/views/cpt/pingshen/psFilte.vue'),
                    meta:{
                        name: "作品初筛"
                    }
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
    // ...localStorage.getItem("userId")
    // console.log(util.giveStorgeCry("userType"));
    document.documentElement.setAttribute('data-theme', "ct0");
    if (lodash.includes(to.href,"/manage/")) {
        document.documentElement.classList.add('app-dark');
        if (lodash.toUpper(util.giveStorgeCry("managerId"))!="ADMIN") {
            let rulePermissions = [];
            if (lodash.toUpper(util.giveStorgeCry("userType"))=="JUDGE") {
                let menuJson = pageJson.manageMenu();
                lodash.forEach(menuJson[0].items, (m) => {
                    if (m.userType) {
                        if (lodash.findIndex(m.userType,(o)=>{return o=="judge"})>-1) {
                            rulePermissions.push(m.key);
                        }
                        if (m.items) {
                            lodash.forEach(m.items,(sm)=>{
                                if (lodash.findIndex(sm.userType,(o)=>{return o=="judge"})>-1 ) {
                                    rulePermissions.push(sm.key);
                                }
                            });
                        }
                    }
                });
            } else if (util.giveStorgeCry("rulePermissions")) {
                rulePermissions = JSON.parse(util.giveStorgeCry("rulePermissions"));
            }
            if (lodash.findIndex([...rulePermissions,"dashboard"],(o)=>{return o==to.name}) < 0) {
                return { name: 'dashboard' };
            }
        }
    } else {
        document.documentElement.classList.remove('app-dark');
        let t = null;
        lodash.forEach(pageJson.menuTreeDatas(),(o)=>{
            if (o.route!=to.name) {
                lodash.forEach(o.items,(c)=>{
                    if (c.route==to.name) {
                        t = c;
                    }
                });
            } else {
                t = o;
            }
        });
        // 返回 false 以取消导航
        if (t) {
            if (t.isLogin) {
                if (util.giveStorgeCry("userId")) {
                    return true;
                } else {
                    bus.emit({route:to.name,isLogin:t.isLogin});
                    return { name: 'landing' };
                }
            } else {
                return true;
            }
        } else {
            return { name: 'landing' };
        }
    }
})

export default router;
