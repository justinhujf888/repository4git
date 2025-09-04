<template>
    <div class="p-2 card">
        <myFileUpload :files="files" :filePreKey="filePreKey" :maxFileSize="20" :fileLimit="1000000" fileAccept="image/*" @allFilesUploaded="filesUpload" @theFileUploaded="theFileUpload"/>
    </div>
</template>

<script setup>
import { ref,useTemplateRef,onMounted } from 'vue';
import lodash from "lodash-es";
import dialog from '@/api/uniapp/dialog';
import myFileUpload from "@/components/my/myFileUpload.vue";
import { Beans } from '@/api/dbs/beans';

const props = defineProps(['files','sourceType','type'])
const emit = defineEmits(["callClose"]);
const callClose = ()=>{
  emit("callClose");
};

const files = ref([]);
const sourceType = ref(props.sourceType ? props.sourceType : 0);
const type = ref(props.type ? props.type : 0);
const filePreKey = ref(props.filePreKey ? props.filePreKey : "temp");

onMounted(() => {
    if (props.files && props.files.length>0) {
        files.value = lodash.concat(files.value,props.files);
    }
});

function filesUpload(files) {

}

function theFileUpload(file,index) {
    let siteWorkItem = Beans.siteWorkItem();
    siteWorkItem.id = Beans.buildPId("zt");
    siteWorkItem.path = file.filekey;
    siteWorkItem.type = type.value;
}

</script>

<style scoped lang="scss">

</style>
