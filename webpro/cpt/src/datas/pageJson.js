import lodash from 'lodash-es';
import oss from '@/api/oss';

export default {
    menuTreeDatas() {
        return [
            {key:"index",label:"首页",menuType:0,isUserSetup:true,isInPageMenu:false,route:"landing"},
            {key:"pingJiang",label:"评奖",menuType:1,route:"",isUserSetup:true,isInPageMenu:true,items:[
                    {key:"saiZhi",label:"赛制",isUserSetup:true,route:"saiZhi",isInPageMenu:true},
                    {key:"judge",label:"评委团",isUserSetup:true,route:"judge",isInPageMenu:true},
                    {key:"pingShenBiaoZun",label:"评审标准",isUserSetup:true,route:"",isInPageMenu:true}
                ]},
            {key:"huoJiangWork",label:"优胜作品",menuType:1,route:"",isUserSetup:false,isInPageMenu:true,items:[
                    {key:"storyWork",label:"往届优胜作品",isUserSetup:true,route:"",isInPageMenu:true},
                    {key:"theWork",label:"本界优胜作品",isUserSetup:true,route:"",isInPageMenu:true},
                    {key:"teBie",label:"特别殊荣",isUserSetup:true,route:"",isInPageMenu:true}
                ]},
            {key:"userCenter",label:"参赛",menuType:1,route:"",isUserSetup:false,isInPageMenu:true,items:[
                    {key:"baoMing",label:"报名入口",route:"myWorks",isInPageMenu:true,isLogin:true},
                    {key:"userWork",label:"我的参赛",route:"myWorks",isInPageMenu:true,isLogin:true},
                    {key:"userMsg",label:"我的消息",route:"myMessages",isInPageMenu:true,isLogin:true}
                ]},
            {key:"about",label:"关于",menuType:0,route:"about",isUserSetup:true,isInPageMenu:true},
            // {key:"contact",label:"联系我们",menuType:0,route:"",isUserSetup:true,isInPageMenu:true},
            {key:"news",label:"新闻",menuType:0,route:"",isInPageMenu:false}
        ]
    },
    preProcessPageJson(pageJson,isBeforeSave) {
        lodash.forEach(pageJson,(v)=>{
            lodash.forEach(v.setup,async (elm)=>{
                if (elm.type=="image") {
                    if (isBeforeSave) {
                        elm.value.tempMap = {};
                    } else {
                        // console.log("elm.value",elm.value);
                        elm.value.tempMap = {imgPath:await oss.buildPathAsync(elm.value.img,true,null)};
                    }
                } else if (elm.type=="box") {
                    lodash.forEach(elm.eltTypes,(ev)=>{
                        if (ev.type=="image") {
                            ev.value.tempMap = {};
                            lodash.forEach(elm.value,async (item)=>{
                                if (isBeforeSave) {
                                    item[ev.key].tempMap = {};
                                } else {
                                    // console.log("item",item[ev.key]);
                                    if (item[ev.key].img) {
                                        item[ev.key].tempMap = {imgPath:await oss.buildPathAsync(item[ev.key].img,true,null)};
                                    } else if (item[ev.key].value) {
                                        item[ev.key].tempMap = {imgPath:await oss.buildPathAsync(item[ev.key].value,true,null)};
                                    }
                                }
                            });
                        }
                    });
                    if (elm.yeWuType=="judge" || elm.yeWuType=="orgHuman") {
                        lodash.forEach(elm.value,(jov)=>{
                            jov.tempMap = {};
                            jov.img.tempMap = {};
                        });
                    }
                }
            });
        });
        return pageJson;
    },
    async processPageImageJson(element) {
        if (element.type=="image" && element.value.img) {
            element.value.tempMap = {imgPath:await oss.buildPathAsync(element.value.img,true,null)};
        } else if (element.type=="box") {
            lodash.forEach(element.eltTypes,async (ev)=>{
                if (ev.type=="image") {
                    ev.value.tempMap = {};
                    for (let item of element.value) {
                        if (item[ev.key].img) {
                            item[ev.key].tempMap = {imgPath:await oss.buildPathAsync(item[ev.key].img,true,null)};
                        } else if (item[ev.key].value) {
                            item[ev.key].tempMap = {imgPath:await oss.buildPathAsync(item[ev.key].value,true,null)};
                        }
                    }
                }
            });
        }
    },
    uiIndexJson() {
        return {
            bannerArea:{
                sort:0,
                name:"轮播区域",
                setup:{
                    banner:{sort:0,type:"box",pre:"设置轮播",value:[],eltTypes:[{key:"img",type:"image",yeWuType:"media",pre:"图片",value:{}},{key:"url",type:"text",pre:"URL"},{key:"title",type:"title",pre:"Title"},{key:"content",type:"text",pre:"内容"}]}
                }
            },
            sloganArea:{
                name:"广告语区域",
                sort:1,
                setup:{
                    bgImg:{sort:0,type:"image",yeWuType:"media",value:{},pre:"背景图"},
                    title:{sort:1,type:"text",value:"",pre:"标题"},
                    subtitle:{sort:2,type:"text",value:"",pre:"副标题"}
                }
            },
            jiangArea:{
                name:"奖项",
                sort:2,
                setup:{
                    title:{sort:0,type:"headTitle",value:"奖项",pre:"标题名称"},
                    bgImg:{sort:1,type:"image",yeWuType:"media",value:{},pre:"背景图"},
                    jiangItems:{sort:2,type:"box",pre:"设置各奖项",value:[],eltTypes:[{key:"name",type:"jbText",pre:"名称",value:{text:"",des:0,colors:[]}},{key:"jiangCount",type:"text",pre:"得奖数量文字(如：每组1名)",value:""},{key:"jiangJing",type:"text",pre:"奖金奖杯文字(如：奖金2000元)",value:""},{key:"jiangPing",type:"text",pre:"奖品文字(如：某品牌生态照明设备)",value:""}]}
                }
            },
            judgeArea:{
                name:"评委",
                sort:3,
                setup:{
                    title:{sort:0,type:"headTitle",value:"评委",pre:"标题名称"},
                    judgeItems:{sort:1,type:"box",yeWuType:"judge",pre:"设置评委信息",count:6,value:[],eltTypes:[{key:"img",type:"image",pre:"照片",value:{}},{key:"name",type:"text",pre:"姓名"},{key:"subDescription",type:"text",pre:"一句话介绍(如：xxx公司创始人)"},{key:"zhiWei",type:"text",pre:"职位(如：xxx协会秘书长)"},{key:"description",type:"text",pre:"详细介绍"}]}
                }
            },
            workStoreArea:{
                name:"往届获奖作品",
                sort:4,
                setup:{
                    title:{sort:0,type:"headTitle",value:"往届获奖作品",pre:"标题名称"},
                    mImg:{sort:1,type:"image",yeWuType:"media",value:{},pre:"滚动图片"}
                }
            },
            newsArea:{
                name:"最新动态",
                sort:5,
                setup:{
                    title:{sort:0,type:"headTitle",value:"最新动态",pre:"标题名称"},
                    newsItems:{sort:1,type:"box",pre:"设置最新动态",value:[],eltTypes:[{key:"img",type:"image",yeWuType:"media",pre:"图片",value:{}},{key:"name",type:"text",pre:"名称"},{key:"date",type:"date",pre:"日期",format:"yy-mm-dd"}]}
                }
            },
            orgArea:{
                name:"联合主办机构",
                sort:6,
                setup:{
                    title:{sort:0,type:"headTitle",value:"联合主办机构",pre:"标题名称"},
                    orgItems:{sort:1,type:"box",pre:"设置联合主办机构",value:[],eltTypes:[{key:"img",type:"image",yeWuType:"media",pre:"图片",value:{}},{key:"url",type:"text",pre:"URL"}]}
                }
            },
            boundArea:{
                name:"赞助品牌",
                sort:7,
                setup:{
                    title:{sort:0,type:"headTitle",value:"赞助品牌",pre:"标题名称"},
                    boundItems:{sort:1,type:"box",pre:"设置赞助品牌",value:[],eltTypes:[{key:"img",type:"image",yeWuType:"media",pre:"图片",value:{}},{key:"url",type:"text",pre:"URL"}]}
                }
            }
        }
    },
    uiFootJson() {
        return {
            boundArea:{
                name:"页眉页脚",
                sort:0,
                setup:{
                    mImg:{sort:0,type:"image",yeWuType:"media",value:{},pre:"Logo"},
                    subPageImg:{sort:1,type:"image",yeWuType:"media",value:{},pre:"子页面通用背景图片"},
                    linkItems:{sort:2,type:"box",pre:"设置图标链接",value:[],eltTypes:[{key:"img",type:"image",yeWuType:"media",pre:"图片",value:{}},{key:"text",type:"text",pre:"名称",value:""},{key:"url",type:"text",pre:"URL"}]},
                    footItems:{sort:3,type:"box",pre:"设置页脚文字链接",value:[],eltTypes:[{key:"text",type:"text",pre:"公司/协会名称",value:""},{key:"url",type:"text",pre:"URL"}]}
                }
            }
        }
    },
    uiAboutJson() {
        return {
            titleTitleArea0:{
                sort:0,
                name:"标题文字",
                setup:{
                    titleTextGruop:{sort:0,type:"box",yeWuType:"titleTextGruop",pre:"",value:[],eltTypes:[{key:"title",type:"title",pre:"标题",value:""},{key:"text",type:"textArea",pre:"文字",value:""}]}
                }
            },
            orgHumanArea:{
                sort:1,
                name: "组委会",
                setup: {
                    title:{sort:0,type:"headTitle",value:"组委会",pre:"标题名称"},
                    orgHumanItems:{sort:1,type:"box",yeWuType:"orgHuman",pre:"设置组委会信息",count:6,value:[],eltTypes:[{key:"img",type:"image",pre:"照片",value:{}},{key:"name",type:"text",pre:"姓名"},{key:"subDescription",type:"text",pre:"一句话介绍(如：xxx公司创始人)"},{key:"zhiWei",type:"text",pre:"职位(如：xxx协会秘书长)"},{key:"description",type:"text",pre:"详细介绍"}]}
                }
            },
            titleTitleArea1:{
                sort:2,
                name:"标题文字",
                setup:{
                    titleTextGruop:{sort:0,type:"box",yeWuType:"titleTextGruop",pre:"",value:[],eltTypes:[{key:"title",type:"title",pre:"标题",value:""},{key:"text",type:"textArea",pre:"文字",value:""}]}
                }
            },
            imageArea:{
                sort:3,
                name:"一张图片",
                setup:{
                    mImg:{sort:0,type:"image",yeWuType:"media",value:{},pre:"滚动图片"}
                }
            },
            titleTitleArea2:{
                sort:4,
                name:"标题文字",
                setup:{
                    titleTextGruop:{sort:0,type:"box",yeWuType:"titleTextGruop",pre:"",value:[],eltTypes:[{key:"title",type:"title",pre:"标题",value:""},{key:"text",type:"textArea",pre:"文字",value:""}]},
                    button:{sort:1,type:"link",pre:"链接按钮",value:{url:"",text:""}}
                }
            },
            titleTitleArea3:{
                sort:5,
                name:"标题文字",
                setup:{
                    titleTextGruop:{sort:0,type:"box",yeWuType:"titleTextGruop",pre:"",value:[],eltTypes:[{key:"title",type:"title",pre:"标题",value:""},{key:"text",type:"textArea",pre:"文字",value:""}]}
                }
            }
        }
    },
    uiJudgeJson() {
        return {
            titleTitleArea0:{
                sort:0,
                name:"标题文字",
                setup:{
                    titleTextGruop:{sort:0,type:"box",yeWuType:"titleTextGruop",pre:"",value:[],eltTypes:[{key:"title",type:"title",pre:"标题",value:""},{key:"text",type:"textArea",pre:"文字",value:""}]}
                }
            },
            titleTitleArea1:{
                sort:1,
                name:"标题文字",
                setup:{
                    titleTextGruop:{sort:0,type:"box",yeWuType:"titleTextGruop",pre:"",value:[],eltTypes:[{key:"title",type:"title",pre:"标题",value:""},{key:"text",type:"textArea",pre:"文字",value:""}]}
                }
            },
            judgeArea:{
                sort:2,
                name: "评审团",
                setup: {
                    orgHumanItems:{sort:0,type:"box",yeWuType:"judge",pre:"设置评审团信息",count:6,value:[],eltTypes:[{key:"img",type:"image",pre:"照片",value:{}},{key:"name",type:"text",pre:"姓名"},{key:"subDescription",type:"text",pre:"一句话介绍(如：xxx公司创始人)"},{key:"zhiWei",type:"text",pre:"职位(如：xxx协会秘书长)"},{key:"description",type:"text",pre:"详细介绍"}]}
                }
            }
        }
    },
    uiSaiZhiJson() {
        return {
            titleTitleArea0:{
                sort:0,
                name:"标题文字",
                setup:{
                    title:{sort:0,type:"title",pre:"标题",value:""},
                    html:{sort:1,type:"html",pre:"文字",value:""}
                }
            },
            titleTitleArea1:{
                sort:1,
                name:"标题文字",
                setup:{
                    titleTextGruop:{sort:1,type:"box",yeWuType:"titleTextGruop",pre:"",value:[],eltTypes:[{key:"title",type:"title",pre:"标题",value:""},{key:"text",type:"textArea",pre:"文字",value:""}]}
                }
            },
            saiDateArea:{
                sort:2,
                name:"赛程时间",
                setup:{
                    title:{sort:0,type:"title",pre:"标题",value:""},
                    mImg:{sort:1,type:"image",yeWuType:"media",value:{},pre:"赛程时间图片"}
                }
            },
            jiangArea:{
                sort:3,
                name:"奖项设置",
                setup:{
                    title:{sort:0,type:"title",pre:"标题",value:""},
                    text:{sort:1,type:"textArea",value:"",pre:"说明"},
                    slot:{sort:2,type:"slot",value:[],pre:"各奖项不用设置，引用首页定义"},
                }
            },
            guiZhangArea:{
                sort:4,
                name:"参赛规章",
                setup:{
                    title:{sort:0,type:"title",pre:"标题",value:""},
                    text:{sort:1,type:"html",value:"",pre:"说明"},
                    desc:{sort:2,type:"box",pre:"",value:[],eltTypes:[{key:"img",type:"image",pre:"图片",value:{}},{key:"description",type:"textArea",pre:"说明",value:""}]},
                    text1:{sort:3,type:"html",value:"",pre:"说明"},
                }
            },
            cealArea:{
                sort:5,
                name:"标题文字",
                setup:{
                    title:{sort:0,type:"title",pre:"标题",value:""},
                    text:{sort:1,type:"html",value:"",pre:"说明"}
                }
            }
        }
    }
}
