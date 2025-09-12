<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-8 z-40">
        <div class="card">
            <div class="flex flex-wrap items-center justify-between">
                <span class="text-base">设置系列赛事作品</span>
                <Button label="系列赛事作品设置" @click="updateSiteZuoPingWorkitemPage.open(mainPage)"/>
            </div>
            <priviewImage v-if="siteZuoPingWorkItemList?.length>0" :files="siteZuoPingWorkItemList"/>
            <!--        <DataView :value="siteZhuTiWorkItemList" layout="grid" :pt="{-->
            <!--            emptyMessage:{-->
            <!--                class:'opacity-0'-->
            <!--            }-->
            <!--        }">-->
            <!--            <template #grid="slotProps">-->
            <!--                <div _class="grid grid-cols-12 gap-4" class="row flex-wrap">-->
            <!--                    <div v-for="(item, index) in slotProps.items" :key="index" _class="col-span-4 sm:col-span-2 md:col-span-2 xl:col-span-2 p-2">-->
            <!--                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">-->
            <!--                            <div class="bg-surface-50 flex justify-center rounded p-1">-->
            <!--                                <div class="w-20 h-20 sm:w-20 sm:h-20 md:w-28 md:h-28 xl:w-36 xl:h-36 mx-auto">-->
            <!--                                    <Image class="rounded w-full h-full object-cover" :src="item.tempMap.imgPath" :alt="item.name" style="max-width: 300px;" preview :pt="{image:{class:'!w-full !h-full object-cover'}}"/>-->
            <!--                                </div>-->
            <!--                            </div>-->
            <!--                        </div>-->
            <!--                    </div>-->
            <!--                </div>-->
            <!--            </template>-->
            <!--        </DataView>-->
        </div>
    </animationPage>

    <animationPage ref="updateSiteZuoPingWorkitemPage" class="w-full absolute top-8 z-40">
        <updateSiteWorkitem v-if="siteZuoPingWorkItemList?.length>0" :files="siteZuoPingWorkItemList" :sourceId="host" :sourceType="0" :type="1" :filePreKey="`cpt/${host}/zuoping`" :maxFileSize="2097152" :fileLimit="20" @callClose="updateSiteZuoPingWorkitemDialogClose"/>
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

const mainPage = useTemplateRef("mainPage");

const updateSiteZuoPingWorkitemPage = useTemplateRef("updateSiteZuoPingWorkitemPage");

const siteZuoPingWorkItemList = ref([]);

const host = ref(inject("domain"));

onMounted(() => {
    oss.genClient();
    workRest.qySiteWorkItemList({sourceId:host.value,sourceType:0,type:1},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                siteZuoPingWorkItemList.value = res.data;
                lodash.forEach(siteZuoPingWorkItemList.value,(v,i)=>{
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

const updateSiteZuoPingWorkitemDialogClose = ()=>{
    updateSiteZuoPingWorkitemPage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
