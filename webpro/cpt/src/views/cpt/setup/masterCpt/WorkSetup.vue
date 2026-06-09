<template>
    <ScrollPanel class="w-full">
        <div class="!relative w-full">
            <div class="absolute -top-10 right-1 z-100 row gap-x-2">
                <Button label="设置作品参数" size="small" severity="warn" rounded @click="updatePage.open(mainPage)"/>
            </div>
            <div class="mt-10 col md:row w-full gap-4">
                <Fieldset legend="上传图片">
                    <div v-for="item of workSetupJson?.workType.image" class="my-2 text-base">
<!--                        <FloatLabel variant="in">-->
<!--                            <InputText id="mincount" :value="item.title" class="mt-6" readonly="true"/>-->
<!--                            <label for="mincount" class="text-xs">标题</label>-->
<!--                        </FloatLabel>-->
                        <span>{{item.title}}</span>
                    </div>
                </Fieldset>
                <Fieldset legend="上传视频">
                    <div v-for="item of workSetupJson?.workType.video" class="my-2 text-base">
                        <span>{{item.title}}</span>
                    </div>
                </Fieldset>
            </div>
        </div>
    </ScrollPanel>

    <Teleport to="#updatePageJson">
        <animationPage ref="updatePage">
            <div class="card">
                <span>作品设置</span>
                <div class="mt-10 col md:row w-full gap-4">
                    <Fieldset legend="上传图片">
                        <div class="gap-2 my-4"><Button label="新增图片类型" size="small" @click="addImageType()"/></div>
                        <div v-for="(item,index) in workSetup?.workType.image" :key="index" class="my-2 text-base">
                            <IftaLabel variant="on">
                                <label :for="'title_'+index" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">标题</label>
                                <div class="row">
                                    <InputText :name="'title_'+index" v-model="item.title"/>
                                    <Button icon="pi pi-times" severity="danger" variant="text" rounded aria-label="Cancel" size="small" @click="delImageType(index)"/>
                                </div>
                            </IftaLabel>
                        </div>
                    </Fieldset>
                    <Fieldset legend="上传视频">
                        <div class="gap-2 my-4"><Button label="新增视频类型" size="small" @click="addVideoType()"/></div>
                        <div v-for="(item,index) in workSetup?.workType.video" :key="index" class="my-2 text-base">
                            <IftaLabel>
                                <label :for="'title_'+index" class="block text-surface-900 dark:text-surface-0 text-base font-medium">标题</label>
                                <div class="row">
                                    <InputText :name="'title_'+index" v-model="item.title" class="mb-4 w-full"/>
                                    <Button icon="pi pi-times" severity="danger" variant="text" rounded aria-label="Cancel" size="small" @click="delVideoType(index)"/>
                                </div>
                            </IftaLabel>
                        </div>
                    </Fieldset>
                </div>
                <div class="center gap-4 mt-5">
                    <Button label="确定" size="small" @click="saveJson"/>
                    <Button severity="warn" label="取消" size="small" @click="cancelJson"/>
                </div>
            </div>
        </animationPage>
    </Teleport>
</template>

<script setup>
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import animationPage from "@/components/my/animationPage.vue";
import dialog from "@/api/uniapp/dialog";
import lodash from 'lodash-es';
import workRest from '@/api/dbs/workRest';
import draggable from 'vuedraggable';

const props = defineProps({
    masterCompetitionId: {type:String, default:""},
    workSetupJson: {type:Object,default:null}
});

const mainPage = inject("mainPage");
const updatePage = useTemplateRef("updatePage");
const workSetup = ref({workType: {image: [], "video": []}, "maxWorkCount": 2, "competitionGuiGeCount": 1});

let host = inject("domain");

onMounted(()=>{
    // console.log(props.masterCompetitionId,props.workSetupJson);
    workSetup.value = props.workSetupJson;
});

const addImageType = ()=>{
    workSetup.value.workType.image.push({
        rule: {
            size: 5242880
        },
        text: "不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",
        type: workSetup.value.workType.image.length,
        title: "",
        minCount: 1,
        checkExif: false,
        mediaType: 0,
        showCount: 1
    });
};

const delImageType = (index)=>{
    dialog.confirm("是否删除这个图片类型？",()=>{
        workSetup.value.workType.image.splice(index,1);
    },null);
};

const addVideoType = ()=>{
    workSetup.value.workType.video.push({
        rule: {
            size: 15728640,
            duration: 20
        },
        text: "1分钟以内原片",
        type: workSetup.value.workType.video.length,
        title: "",
        minCount: 1,
        mediaType: 1,
        showCount: 1
    });
};

const delVideoType = (index)=>{
    dialog.confirm("是否删除这个视频类型？",()=>{
        workSetup.value.workType.video.splice(index,1);
    },null);
};

const saveJson= async ()=>{
    if (lodash.findIndex(workSetup.value.workType.image,(o)=>{return (o.title=="" || o.title==null)})>-1 ) {
        dialog.toastError("图片类型，标题文字为必须输入项");
        return;
    }
    if (lodash.findIndex(workSetup.value.workType.video,(o)=>{return (o.title=="" || o.title==null)})>-1 ) {
        dialog.toastError("视频类型，标题文字为必须输入项");
        return;
    }
    await workRest.updateMasterCompetitionWorkSetup({id:props.masterCompetitionId,workSetup:workSetup.value},null);
    updatePage.value.close(mainPage.value);
}

function cancelJson() {
    updatePage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
