<template>
    <div class="p-2 card">
        <div class="flex flex-wrap items-center justify-between">
            <span class="text-base">设置赛事主题图</span>
            <Button label="主题图设置" _click="Page.navigateTo('updateSiteWorkitem',null)" @click="updateSiteZhuTiWorkitemPage.open(mainPage)"/>
        </div>
        <priviewImage v-if="siteZhuTiWorkItemList?.length>0" :files="siteZhuTiWorkItemList"/>
    </div>
    <animationPage ref="updateSiteZhuTiWorkitemPage" class="w-full absolute top-0 z-40">
        <updateSiteWorkitem v-if="siteZhuTiWorkItemList?.length > 0" :files="siteZhuTiWorkItemList" :sourceId="host" :sourceType="0" :type="0" :filePreKey="`cpt/${host}/zhuti`" :maxFileSize="2097152" :fileLimit="5" @callClose="updateSiteZhiTiWorkitemDialogClose"/>
    </animationPage>
</template>

<script setup>
import priviewImage from "@/components/my/priviewImage.vue";
import animationPage from "@/components/my/animationPage.vue";
import updateSiteWorkitem from "@/views/cpt/setup/siteCpt/updateSiteWorkitem.vue";
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import oss from '@/api/oss';
import lodash from "lodash";
import workRest from '@/api/dbs/workRest';

const props = defineProps(['mainPage']);
const mainPage = ref(props.mainPage);

const updateSiteZhuTiWorkitemPage = useTemplateRef("updateSiteZhuTiWorkitemPage");

const siteZhuTiWorkItemList = ref([]);

const host = ref(inject("domain"));

onMounted(() => {
    oss.genClient();
    workRest.qySiteWorkItemList({sourceId:host.value,sourceType:0,type:0},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteZhuTiWorkItemList.value = res.data;
                lodash.forEach(siteZhuTiWorkItemList.value,(v,i)=>{
                    v.tempMap = {};
                    v.tempMap.size = v.fileFields.size;
                    v.tempMap.name = v.fileFields.name;
                    v.tempMap.type = v.fileFields.type;
                    v.tempMap.imgPath = oss.buildImgPath(v.path);
                });
            }
        }
    });
});

const updateSiteZhiTiWorkitemDialogClose = ()=>{
    updateSiteZhuTiWorkitemPage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
