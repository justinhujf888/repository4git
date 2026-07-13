<template>
    <animationPage :show="true">
        <div class="flex-1 p-2 card">
            <div class="row gap-4">
                <Button label="设定全场大奖" @click="saveSubmitWorks" :disabled="shiSubmited"/>
            </div>
            <div v-if="workList?.length>0">
                <DataView :value="workList" class="mt-5" :pt="{
                                            emptyMessage:{
                                                class:'opacity-0'
                                            }
                                        }">
                    <template #list="slotProps">
                        <div class="col gap-2 flex-wrap">
                            <div v-for="(item, index) in slotProps.items" :key="index" class="leading-8">
                                <Panel :header="item.name" toggleable collapsed :pt="{title:{class:'text-2xl font-bold dark:text-yellow-600'}}">
                                    <template #icons>
                                        <RadioButton v-model="selWorkId" name="radioWork" :value="item.id" class="!h-7"/>
                                    </template>
                                    <div class="grid md:grid-cols-2 gap-4 mt-5">
                                        <div class="col">
                                            <span class="dark:text-yellow-400">作品介绍</span>
                                            <span class="break-words">{{item.gousiDescription}}</span>
                                        </div>
                                        <div class="col">
                                            <span class="dark:text-yellow-400">作品理念</span>
                                            <span class="break-words">{{item.myMeanDescription}}</span>
                                        </div>
                                    </div>
                                    <div class="grid md:grid-cols-4 gap-4 mt-5">
                                        <div class="col" v-for="field of item?.hangyeFields?.data">
                                            <span class="dark:text-yellow-400">{{field.name}}</span>
                                            <span class="break-words">{{field.value}}</span>
                                        </div>
                                    </div>
                                    <div class="grid md:grid-cols-4 gap-4 mt-5">
                                        <div class="col" v-for="field of item?.otherFields?.data">
                                            <span class="dark:text-yellow-400">{{field.name}}</span>
                                            <span class="break-words">{{field.value}}</span>
                                        </div>
                                    </div>
                                    <div class="row flex-wrap">
                                        <div v-for="img of item.workItemList">
                                            <Button severity="secondary" class="col center w-36 h-32 md:w-44 md:h-28 border-solid border-gray-500 border-2 rounded-xl relative p-2">
                                                <img v-if="img.mediaType==0" :alt="img.tempMap?.imgPath" :src="img.tempMap?.imgPath" class="absolute top-0 left-0 z-10 w-full h-32 object-cover object-center" @click="viewImg(item.workItemList,img.id)"/>
                                                <videoInfo v-if="img.mediaType==1" :src="img.tempMap?.imgPath" class="aabsolute top-0 left-0 z-10 w-full h-32 object-cover object-center"/>
                                            </Button>
                                            <div class="col center mt-2">
                                                <span class="text-sm font-semibold">{{img.tempMap?.title}}</span>
                                                <!--                                    <span class="text-sm">{{img.tempMap?.text}}</span>-->
                                            </div>
                                        </div>
                                    </div>
                                </Panel>
                            </div>
                        </div>
                    </template>
                </DataView>
                <priviewImage ref="refPriviewImage" :shiShowImgGrid="false" _class="hidden"/>
            </div>
        </div>
    </animationPage>
</template>

<script setup>
import {inject, onMounted, ref, useTemplateRef} from "vue";
import workRest from "@/api/dbs/workRest";
import lodash from "lodash-es";
import animationPage from "@/components/my/animationPage.vue";
import dialog from "@/api/uniapp/dialog";
import oss from "@/api/oss";
import {Beans} from "@/api/dbs/beans";

let host = inject("domain");
let masterCompetition = null;

const workList = ref([]);
const selWorkId = ref('');
const refPriviewImage = useTemplateRef("refPriviewImage");
const shiSubmited = ref(false);

onMounted(async () => {
    masterCompetition = (await workRest.gainCache8MasterCompetitionInfo(host)).masterCompetitionInfo;
    masterCompetition = (await workRest.qyMasterSiteCompetition({id:masterCompetition.id,siteCompetitionId:host},null)).data[0];
    workList.value = (await workRest.getTop1EveryCptCompetitionGuiGe({masterCompetitionId:masterCompetition.id},null)).data;
    for (let work of workList.value) {
        if (!work.workItemList) break;
        for (let workItem of work.workItemList) {
            if (workItem.mediaType==0) {
                workItem.tempMap = {imgPath:await oss.buildPathAsync(workItem.path,(workItem.mediaType==0 ? true : false),null)};
            } else {
                  workItem.tempMap = {imgPath:await oss.buildPathAsync(workItem.path,false,null)};
            }
        }
        work.tempMap = {};
        work.tempMap.status = lodash.find(Beans.workStatus(),(o)=>{return o.id==work.status}).name;
    }
    // console.log(masterCompetition,workList.value); getTop1EveryCptCompetitionGuiGe
    if (masterCompetition.pxWorks) {
        selWorkId.value = masterCompetition.pxWorks.data;
        shiSubmited.value = true;
    }
});

const viewImg = (workItemList,imgId)=>{
    let l = lodash.filter(workItemList,(o)=>{return o.tempMap?.imgPath && o.mediaType==0});
    refPriviewImage.value.imagesShow(l,lodash.findIndex(l,(o)=>{return o.id==imgId}));
};

const saveSubmitWorks = async ()=>{
    // console.log(selWorkId.value);
    if (!selWorkId.value) {
        dialog.toastError("必须勾选一个作品");
        return;
    }
    await workRest.updateTop1ForCpt({masterCompetitionId:masterCompetition.id,workId:selWorkId.value},(data)=>{
        if (data.status=="OK") {
            shiSubmited.value = true;
            dialog.toastSuccess("设置已提交");
        }
    });
}
</script>

<style scoped lang="scss">

</style>