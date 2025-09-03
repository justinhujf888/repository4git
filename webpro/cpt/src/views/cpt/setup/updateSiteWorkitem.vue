<template>
    <div class="p-2 card">
        <FileUpload ref="fileupload" mode="basic" :accept="fileAccept" multiple :maxFileSize="maxFileSize" :fileLimit="fileLimit" @uploader="uploader" customUpload @select="onFileSelect" />
<!--        <Button label="back" @click="callClose"/>-->
        <DataView :value="files" layout="grid" :pt="{
            emptyMessage:{
                class:'opacity-0'
            }
        }">
            <template #grid="slotProps">
                <div class="grid grid-cols-12 gap-4">
                    <div v-for="(item, index) in slotProps.items" :key="index" _class="col-span-4 sm:col-span-4 md:col-span-2 xl:col-span-2 p-2" class="col-span-6 sm:col-span-4 md:col-span-3 xl:col-span-2 p-2">
                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">
                            <div class="bg-surface-50 flex justify-center rounded p-1">
                                <div class="relative mx-auto">
                                    <img class="rounded w-full" :src="item.path ? item.path : item.objectURL" :alt="item.name"/>
                                    <div class="absolute bg-black/70 rounded-border -bottom-5 -right-2">
                                        <Tag :value="lodash.floor(lodash.divide(item.size,1024),2) + 'KB'" :severity="item.size"></Tag>
                                    </div>
                                    <div class="absolute -top-6 -right-2">
                                        <Button icon="pi pi-times" severity="danger" rounded aria-label="Cancel" size="small" @click="delTheFile(index)"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </DataView>
    </div>
</template>

<script setup>
import { ref,useTemplateRef,onMounted } from 'vue';
import lodash from "lodash-es";
import dialog from '@/api/uniapp/dialog';

const fileupload = useTemplateRef("fileupload");
const props = defineProps(['sourceType','type','files','maxFileSize','fileLimit','fileAccept'])
const emit = defineEmits(["callClose"]);
const callClose = ()=>{
  emit("callClose");
};

const files = ref([]);
const maxFileSize = ref(props.maxFileSize ? props.maxFileSize : 1000000);
const fileLimit = ref(props.fileLimit ? props.fileLimit : 20);
const fileAccept = ref(props.fileAccept ? props.fileAccept : "image/*");

onMounted(() => {
    if (props.files && props.files.length>0) {
        files.value = lodash.concat(files.value,props.files);
    }
});

function onFileSelect(e) {
    files.value = lodash.concat(files.value,e.files);
    console.log(files.value);
}

function uploader() {

}

function delTheFile(index) {
    dialog.confirm("是否删除这个文件？",()=>{
        if (files.value[index].path) {

        } else {
            files.value.splice(index,1);
        }
    },null);
}
</script>

<style scoped lang="scss">

</style>
