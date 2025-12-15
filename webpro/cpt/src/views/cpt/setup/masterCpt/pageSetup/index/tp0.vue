<template>
    <div>
        <pageUIEdit :pageJson="pageJson" :read-only="true"/>
    </div>
</template>

<script setup>
import {ref} from "vue";
import pageUIEdit from "@/views/cpt/setup/masterCpt/pageSetup/pageUIEdit.vue";

const pageJson = ref({
    bannerArea:{
        name:"轮播区域",
        setup:{
            banner:{type:"box",pre:"设置轮播",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"URL"},{type:"title",pre:"Title"},{type:"text",pre:"内容"}]},
            mImg:{type:"image",src:"",pre:"轮播图片"}
        }
    },
    sloganArea:{
        name:"广告语区域",
        setup:{
            bgImg:{type:"image",src:"",pre:"背景图"},
            title:{type:"text",value:"",pre:"标题"},
            subtitle:{type:"text",value:"",pre:"副标题"}
        }
    },
    jiangArea:{
        name:"奖项",
        setup:{
            title:{type:"headTitle",value:"奖项",pre:"标题名称"},
            jiangItems:{type:"box",pre:"设置各奖项",value:[],eltTypes:[{type:"jbText",pre:"名称",props:{}},{type:"text",pre:"得奖数量文字(如：每组1名)"},{type:"text",pre:"奖金奖杯文字(如：奖金2000元)"},{type:"text",pre:"奖品文字(如：某品牌生态照明设备)"}]}
        }
    },
    judgeArea:{
        name:"评委",
        setup:{
            title:{type:"headTitle",value:"评委",pre:"标题名称"},
            judgeItems:{type:"box",pre:"设置评委信息",value:[],eltTypes:[{type:"text",pre:"姓名"},{type:"text",pre:"一句话介绍(如：xxx公司创始人)"},{type:"text",pre:"职位(如：xxx协会秘书长)"}]}
        }
    },
    workStoreArea:{
        name:"往届获奖作品",
        setup:{
            title:{type:"headTitle",value:"往届获奖作品",pre:"标题名称"},
            mImg:{type:"image",src:"",pre:"滚动图片"}
        }
    },
    newsArea:{
        name:"最新动态",
        setup:{
            title:{type:"headTitle",value:"最新动态",pre:"标题名称"},
            newsItems:{type:"box",pre:"设置最新动态",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"名称"},{type:"date",pre:"日期",format:"yyyy-mm-dd"}]}
        }
    },
    orgArea:{
        name:"联合主办机构",
        setup:{
            title:{type:"headTitle",value:"联合主办机构",pre:"标题名称"},
            orgItems:{type:"box",pre:"设置联合主办机构",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"URL"}]}
        }
    },
    boundArea:{
        name:"赞助品牌",
        setup:{
            title:{type:"headTitle",value:"赞助品牌",pre:"标题名称"},
            boundItems:{type:"box",pre:"设置赞助品牌",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"URL"}]}
        }
    }
});
</script>

<style scoped lang="scss">

</style>
