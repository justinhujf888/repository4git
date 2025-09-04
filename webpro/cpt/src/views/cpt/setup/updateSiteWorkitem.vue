<template>
    <div class="p-2 card">
        <myFileUpload :files="files" :filePreKey="filePreKey" :maxFileSize="1000000" :fileLimit="20" fileAccept="image/*" :obj="{mediaType:0,appId:Config.appId}" @allFilesUploaded="filesUpload" @theFileUploaded="theFileUpload"/>
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
    dialog.alert(`文件已上传！数量：${files.length}`);
}

function theFileUpload(file,index,obj) {
    let siteWorkItem = Beans.siteWorkItem();
    siteWorkItem.id = Beans.buildPId("zt");
    siteWorkItem.path = file.filekey;
    siteWorkItem.type = type;
    siteWorkItem.sourceType = sourceType;
    siteWorkItem.sourceId = sourceId;
    siteWorkItem.mediaType = obj.mediaType;
    siteWorkItem.createDate = dayjs().valueOf();
    siteWorkItem.appId = obj.appId;
    workRest.updateSiteWorkItem({siteWorkItem:siteWorkItem},(res)=>{
        if (res.status=="OK") {
            dialog.toastSuccess(`文件${file.name}已上传`);
        }
    });
}

</script>

<style scoped lang="scss">

</style>
