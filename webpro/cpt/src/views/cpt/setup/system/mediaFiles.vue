<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <div class="p-2 card w-full h-auto relative">

            <!--        <Button label="退出" severity="danger" class="!absolute !right-2 !top-2 !rounded-2xl" @click="callClose"/>-->
        </div>
    </animationPage>

    <animationPage ref="updatePage">
        <myFileUpload ref="refFileUpload" @theFileUploaded="theFileUpload" @allFilesUploaded="filesUpload" @cancel="cancelUpload" @deleteFile="deleteFile"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import { ref, useTemplateRef, onMounted, inject } from 'vue';
import lodash from "lodash-es";
import dayjs from "dayjs";
import dialog from '@/api/uniapp/dialog';
import myFileUpload from "@/components/my/myFileUpload.vue";
import { Beans } from '@/api/dbs/beans';
import workRest from '@/api/dbs/workRest';
import oss from "@/api/oss";

const mainPage = useTemplateRef("mainPage");
const refFileUpload = useTemplateRef("refFileUpload");

const files = ref(null);
let sourceType = 8;
let type = 0;//无实际意义
let sourceId = "";//无实际意义
const filePreKey = ref("");
const maxFileSize = ref( 2097152);
const fileLimit = ref(20);

let host = inject("domain");

onMounted(() => {

});

function filesUpload(files, obj) {
    dialog.alertBack(`文件上传完成！`, () => {

    });
}

function theFileUpload(file, index, obj) {
    let siteWorkItem = Beans.siteWorkItem();
    siteWorkItem.id = Beans.buildPId(sourceType == 8 ? "page" : "news");
    siteWorkItem.path = file.fileKey;
    siteWorkItem.type = type;
    siteWorkItem.sourceType = sourceType;
    siteWorkItem.sourceId = sourceId;
    siteWorkItem.mediaType = obj.mediaType;
    siteWorkItem.createDate = dayjs().valueOf();
    siteWorkItem.appId = obj.appId;
    siteWorkItem.fileFields = { name: file.name, size: file.size, type: file.type };
    workRest.updateSiteWorkItem({ siteWorkItem: siteWorkItem }, (res) => {
        if (res.status == "OK") {
            siteWorkItem.tempMap = {};
            siteWorkItem.tempMap.size = siteWorkItem.fileFields.size;
            siteWorkItem.tempMap.name = siteWorkItem.fileFields.name;
            siteWorkItem.tempMap.type = siteWorkItem.fileFields.type;
            siteWorkItem.tempMap.imgPath = oss.buildImgPath(siteWorkItem.path);
            files.value.push(siteWorkItem);
            dialog.toastSuccess(`文件${file.name}已上传`);
        }
    });
}

function deleteFile(file, index, obj) {
    if (file.id) {
        workRest.deleteSiteWorkItem({ id: file.id }, (res) => {
            if (res.status == "OK") {
                oss.deleteFile(file.path);
                files.value.splice(index, 1);
                dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
            }
        });
    } else {
        files.value.splice(index, 1);
        dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
    }
}

function cancelUpload() {

}

function openUpdate() {
    refFileUpload.value.init(files.value, null, null, null, null, {});
}

defineExpose({ init });
</script>

<style scoped lang="scss">

</style>
