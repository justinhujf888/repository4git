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
                <div class="mt-10 col md:row w-full gap-4">
                    <Fieldset legend="上传图片">
                        <div v-for="(item,index) in workSetupJson?.workType.image" :key="index" class="my-2 text-base">
                            <IftaLabel variant="on">
                                <label :for="'title_'+index" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">标题</label>
                                <InputText :name="'title_'+index" :value="item.title"/>
                            </IftaLabel>
                        </div>
                    </Fieldset>
                    <Fieldset legend="上传视频">
                        <div v-for="(item,index) in workSetupJson?.workType.video" :key="index" class="my-2 text-base">
                            <IftaLabel>
                                <label :for="'title_'+index" class="block text-surface-900 dark:text-surface-0 text-base font-medium">标题</label>
                                <InputText :name="'title_'+index" :value="item.title" class="mb-4 w-full"/>
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

const props = defineProps({
    masterCompetitionId: {type:String, default:""},
    workSetupJson: {type:Object,default:null}
});

const mainPage = inject("mainPage");
const updatePage = useTemplateRef("updatePage");

let host = inject("domain");

onMounted(()=>{
    // console.log(props.masterCompetitionId,props.workSetupJson);
});

function saveJson() {
    updatePage.value.close(mainPage.value);
}

function cancelJson() {
    updatePage.value.close(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
