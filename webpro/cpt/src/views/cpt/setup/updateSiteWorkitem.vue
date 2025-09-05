<template>
    <div class="p-2 card">
        <myFileUpload :files="files" :filePreKey="filePreKey" :maxFileSize="1000000" :fileLimit="20" fileAccept="image/*" :obj="{mediaType:0,appId:Config.appId}" @allFilesUploaded="filesUpload" @theFileUploaded="theFileUpload" @deleteFile="deleteFile"/>
    </div>
</template>

<script setup>
import { ref,useTemplateRef,onMounted } from 'vue';
import lodash from "lodash-es";
import dayjs from "dayjs";
import dialog from '@/api/uniapp/dialog';
import myFileUpload from "@/components/my/myFileUpload.vue";
import { Beans } from '@/api/dbs/beans';
import workRest from '@/api/dbs/workRest';
import {Config} from '@/api/config';
import oss from "@/api/oss"

const props = defineProps(['files','sourceType','type','sourceId','filePreKey'])
const emit = defineEmits(["callClose"]);
const callClose = ()=>{
  emit("callClose");
};

const files = ref(props.files ? props.files : []);
let sourceType = props.sourceType ? props.sourceType : 0;
let type = props.type ? props.type : 0;
let sourceId = props.sourceId ? props.sourceId : "";
const filePreKey = ref(props.filePreKey ? props.filePreKey : "temp");

onMounted(() => {

});

function filesUpload(files,obj) {
    dialog.alertBack(`文件上传完成！`,()=>{
        callClose();
    });
}

function theFileUpload(file,index,obj) {
    let siteWorkItem = Beans.siteWorkItem();
    siteWorkItem.id = Beans.buildPId("zt");
    siteWorkItem.path = file.fileKey;
    siteWorkItem.type = type;
    siteWorkItem.sourceType = sourceType;
    siteWorkItem.sourceId = sourceId;
    siteWorkItem.mediaType = obj.mediaType;
    siteWorkItem.createDate = dayjs().valueOf();
    siteWorkItem.appId = obj.appId;
    siteWorkItem.fileFields = {name:file.name,size:file.size,type:file.type};
    workRest.updateSiteWorkItem({siteWorkItem:siteWorkItem},(res)=>{
        if (res.status=="OK") {
            files.value.push(siteWorkItem);
            dialog.toastSuccess(`文件${file.name}已上传`);
        }
    });
}

function deleteFile(file,index,obj) {
    if (file.id) {
        workRest.deleteSiteWorkItem({id:file.id},(res)=>{
            if (res.status=="OK") {
                oss.deleteFile(file.path);
                dialog.toastSuccess(`文件${file.name}已删除`);
            }
        });
    } else {
        dialog.toastSuccess(`文件${file.name}已删除`);
    }
}

</script>

<style scoped lang="scss">

</style>
