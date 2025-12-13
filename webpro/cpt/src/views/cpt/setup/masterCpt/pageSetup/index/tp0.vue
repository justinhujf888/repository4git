<template>
    <div>

    </div>
</template>

<script setup>
import {ref} from "vue";

const pageJson = ref({
    bannerArea:{
        name:"轮播区域",
        setup:{
            banner:{type:"box",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"URL"},{type:"title",pre:"Title"},{type:"text",pre:"内容"}]},
            mImg:{type:"image",src:""}
        }
    },
    sloganArea:{
        name:"广告语区域",
        setup:{
            bgImg:{type:"image",src:""},
            title:{type:"text",value:""},
            subtitle:{type:"text",value:""}
        }
    },
    jiangArea:{
        name:"奖项",
        setup:{
            title:{type:"headTitle",value:"奖项"},
            jiangItems:{type:"box",value:[],eltTypes:[{type:"jbText",pre:"名称"},{type:"text",pre:"得奖数量文字(如：每组1名)"},{type:"text",pre:"奖金奖杯文字(如：奖金2000元)"},{type:"text",pre:"奖品文字(如：某品牌生态照明设备)"}]}
        }
    },
    judgeArea:{
        name:"评委",
        setup:{
            title:{type:"headTitle",value:"评委"},
            judgeItems:{type:"box",value:[],eltTypes:[{type:"text",pre:"姓名"},{type:"text",pre:"一句话介绍(如：xxx公司创始人)"},{type:"text",pre:"职位(如：xxx协会秘书长)"}]}
        }
    },
    workStoreArea:{
        name:"往届获奖作品",
        setup:{
            title:{type:"headTitle",value:"往届获奖作品"},
            mImg:{type:"image",src:""}
        }
    },
    newsArea:{
        name:"最新动态",
        setup:{
            title:{type:"headTitle",value:"最新动态"},
            newsItems:{type:"box",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"名称"},{type:"date",pre:"日期",format:"yyyy-mm-dd"}]}
        }
    },
    orgArea:{
        name:"联合主办机构",
        setup:{
            title:{type:"headTitle",value:"联合主办机构"},
            orgItems:{type:"box",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"URL"}]}
        }
    },
    boundArea:{
        name:"赞助品牌",
        setup:{
            title:{type:"headTitle",value:"赞助品牌"},
            boundItems:{type:"box",value:[],eltTypes:[{type:"image",pre:"图片"},{type:"text",pre:"URL"}]}
        }
    }
});
</script>

<style scoped lang="scss">

</style>
