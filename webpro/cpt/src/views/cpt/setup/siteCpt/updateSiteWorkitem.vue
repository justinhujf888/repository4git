<template>
    <div class="p-2 card w-full h-auto relative">
        <myFileUpload :files="files" :filePreKey="filePreKey" :maxFileSize="maxFileSize" :fileLimit="fileLimit" fileAccept="image/*" :obj="{mediaType:0,appId:Config.appId}" @allFilesUploaded="filesUpload" @theFileUploaded="theFileUpload" @deleteFile="deleteFile" @cancel="cancelUpload"/>
<!--        <Button label="退出" severity="danger" class="!absolute !right-2 !top-2 !rounded-2xl" @click="callClose"/>-->
    </div>
</template>

<script setup>
import { ref,useTemplateRef,onMounted,watch } from 'vue';
import lodash from "lodash-es";
import dayjs from "dayjs";
import dialog from '@/api/uniapp/dialog';
import myFileUpload from "@/components/my/myFileUpload.vue";
import { Beans } from '@/api/dbs/beans';
import workRest from '@/api/dbs/workRest';
import {Config} from '@/api/config';
import oss from "@/api/oss"

const props = defineProps(['files','sourceType','type','sourceId','filePreKey','maxFileSize','fileLimit'])
const emit = defineEmits(["callClose"]);
const callClose = ()=>{
  emit("callClose");
};

const files = ref(props.files ? props.files : []);
let sourceType = props.sourceType ? props.sourceType : 0;
let type = props.type ? props.type : 0;
let sourceId = props.sourceId ? props.sourceId : "";
const filePreKey = ref(props.filePreKey ? props.filePreKey : "temp");
const maxFileSize = ref(props.maxFileSize ? props.maxFileSize : 2097152);
const fileLimit = ref(props.fileLimit ? props.fileLimit : 20);

onMounted(() => {

});

function filesUpload(files,obj) {
    dialog.alertBack(`文件上传完成！`,()=>{
        callClose();
    });
}

function theFileUpload(file,index,obj) {
    let siteWorkItem = Beans.siteWorkItem();
    siteWorkItem.id = Beans.buildPId( type==0 ? "zt" : "zp");
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

function deleteFile(file,index,obj) {
    if (file.id) {
        workRest.deleteSiteWorkItem({id:file.id},(res)=>{
            if (res.status=="OK") {
                oss.deleteFile(file.path);
                files.value.splice(index,1);
                dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
            }
        });
    } else {
        files.value.splice(index,1);
        dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
    }
}

function cancelUpload() {
    callClose();
}
</script>

<style scoped lang="scss">

</style>
