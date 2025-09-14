<template>
    <div class="">
        <FileUpload ref="fu" mode="advanced" :accept="fileAccept" multiple auto :maxFileSize="maxFileSize" :fileLimit="fileLimit" _uploader="uploader" customUpload @select="onFileSelect" chooseLabel="选择文件" :invalidFileSizeMessage="`{0}: 文件太大, 文件大小必须小于 {1}`" :pt="{
            root: {
                class: '!border-2'
            },
            Message:{
                    pcMessage:{
                        class:''
                    }
                }
            }">
<!--            <template #header>-->
<!--                <Button @click="chooseCallback()" icon="pi pi-images" rounded variant="outlined" severity="secondary"></Button>-->
<!--                <Button vv-if="lodash.findIndex(files,(o)=>{return o.objectURL})>-1" label="上传文件" class="!bg-orange-400 !border-orange-400" @click="uploader"/>-->
<!--            </template>-->
            <template #header="{ chooseCallback, uploadCallback, clearCallback }">
                <div class="flex flex-wrap justify-between items-center flex-1 gap-4">
                    <div class="flex gap-2">
                        <Button @click="chooseCallback()" icon="pi pi-images" rounded label="选择文件"></Button>
                        <Button v-if="lodash.findIndex(files,(o)=>{return o.objectURL})>-1 && files.length<=fileLimit" label="上传文件" class="!bg-orange-400 !border-orange-400" icon="pi pi-cloud-upload" rounded @click="uploader"/>
<!--                        <Button @click="clearCallback()" icon="pi pi-times" rounded variant="outlined" severity="danger" :disabled="!files || files.length === 0"></Button>-->
                        <Button class="!bg-red-400 !border-red-400" @click="cancel" rounded label="退出"></Button>
                    </div>
                </div>
            </template>
            <template #content="{ messages }">
                <Message v-for="message of messages" :key="message" :class="{ 'mb-8': !files.length }" severity="error">
                    {{ message }}
                </Message>
                <Message v-for="message of fileMessages" :key="message" :class="{ 'mb-8': !files.length }" severity="error">
                    {{ message }}
                </Message>
                <DataView :value="files" layout="grid" :pt="{
                    emptyMessage:{
                        class:'opacity-0'
                    }
                }">
                    <template #grid="slotProps">
                        <div class="row flex-wrap">
                            <div v-for="(item, index) in slotProps.items" :key="index" _class="col-span-4 sm:col-span-4 md:col-span-2 xl:col-span-2 p-2" cclass="col-span-2 sm:col-span-2 md:col-span-2 xl:col-span-2 p-2" class="mt-10">
                                <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">
                                    <div class="bg-surface-50 flex justify-center rounded p-1">
                                        <div class="relative mx-auto">
                                            <div class="w-20 h-20 sm:w-20 sm:h-20 md:w-28 md:h-28 xl:w-36 xl:h-36 mx-auto">
                                                <Image class="rounded w-full h-full object-cover" :src="item.tempMap.imgPath" :alt="item.name" style="max-width: 300px;" preview_ :pt="{image:{class:'!w-full !h-full object-cover'}}"/>
                                            </div>
                                            <span v-if="item.path" class="text-xs bg-green-200 text-gray-500 absolute -top-5 left-0 p-1 rounded-md">已上传</span>
                                            <div class="absolute bg-black/70 rounded-border -bottom-5 -right-2">
                                                <Tag :value="formatSize(item.tempMap.size)" :severity="item.size"></Tag>
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
                <div v-if="files?.length>0" class="flex items-center justify-center flex-col">
                    <i class="pi pi-cloud-upload !border-2 !rounded-full !p-8 !text-4xl !text-muted-color" />
                    <p class="mt-6 mb-0 text-gray-600 text-xs">文件拖找也可上传</p>
                </div>
            </template>
            <template #empty>
                <div v-if="files?.length<1">
                    <div class="center text-gray-600 text-xs mb-5">
                        <text>未上传文件</text>
                    </div>
                    <div class="flex items-center justify-center flex-col">
                        <i class="pi pi-cloud-upload !border-2 !rounded-full !p-8 !text-4xl !text-muted-color" />
                        <p class="mt-6 mb-0 text-gray-600 text-xs">文件拖找也可上传</p>
                    </div>
                </div>
            </template>
        </FileUpload>
        <!--        <Button label="back" @click="callClose"/>-->
<!--        <DataView class="mt-10" :value="files" layout="grid" :pt="{-->
<!--            emptyMessage:{-->
<!--                class:'opacity-0'-->
<!--            }-->
<!--        }">-->
<!--            <template #grid="slotProps">-->
<!--                <div class="row flex-wrap">-->
<!--                    <div v-for="(item, index) in slotProps.items" :key="index" _class="col-span-4 sm:col-span-4 md:col-span-2 xl:col-span-2 p-2" cclass="col-span-2 sm:col-span-2 md:col-span-2 xl:col-span-2 p-2">-->
<!--                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">-->
<!--                            <div class="bg-surface-50 flex justify-center rounded p-1">-->
<!--                                <div class="relative mx-auto">-->
<!--                                    <div class="w-24 h-24 sm:w-24 sm:h-24 md:w-28 md:h-28 xl:w-36 xl:h-36 mx-auto">-->
<!--                                        <img class="rounded w-full h-full object-cover" :src="item.tempMap.imgPath" :alt="item.name"/>-->
<!--                                    </div>-->
<!--                                    <span v-if="item.path" class="text-xs bg-green-200 text-gray-500 absolute -top-5 left-0 p-1 rounded-md">已上传</span>-->
<!--                                    <div class="absolute bg-black/70 rounded-border -bottom-5 -right-2">-->
<!--                                        <Tag :value="formatSize(item.tempMap.size)" :severity="item.size"></Tag>-->
<!--                                    </div>-->
<!--                                    <div class="absolute -top-6 -right-2">-->
<!--                                        <Button icon="pi pi-times" severity="danger" rounded aria-label="Cancel" size="small" @click="delTheFile(index)"/>-->
<!--                                    </div>-->
<!--                                </div>-->
<!--                            </div>-->
<!--                        </div>-->
<!--                    </div>-->
<!--                </div>-->
<!--            </template>-->
<!--        </DataView>-->
    </div>
</template>

<script setup>
import { usePrimeVue } from 'primevue/config';
import { ref,useTemplateRef,onMounted } from 'vue';
import lodash from "lodash-es";
import dialog from '@/api/uniapp/dialog';
import oss from '@/api/oss';
import util from "@/api/util";

const fu = useTemplateRef("fu");
const $primevue = usePrimeVue();
// const fileupload = useTemplateRef("fileupload");
const props = defineProps(['files','maxFileSize','fileLimit','fileAccept','filePreKey','obj']);
const emit = defineEmits(["theFileUploaded","allFilesUploaded","deleteFile","cancel"]);
const theFileUploaded = (file,index,obj)=>{
    emit("theFileUploaded",file,index,obj);
};
const allFilesUploaded = (files,obj)=>{
    emit("allFilesUploaded",files,obj);
};
const deleteFile = (file,index,obj)=>{
    emit("deleteFile",file,index,obj);
};
const cancel = ()=>{
    emit("cancel");
}

const files = ref(props.files ? lodash.cloneDeep(props.files) : []);
const maxFileSize = ref(props.maxFileSize ? props.maxFileSize : 2097152);
const fileLimit = ref(props.fileLimit ? props.fileLimit : 20);
const fileAccept = ref(props.fileAccept ? props.fileAccept : "image/*");
const filePreKey = ref(props.filePreKey ? props.filePreKey : "temp");
const fileMessages = ref([]);

let obj = props.obj;

onMounted(() => {
    // console.log(fu);
});

function onFileSelect(e) {
    // console.log(e.files,fu.value.files);

    // lodash.forEach(e.files,(v,i)=>{
    //     if (lodash.findIndex(files.value,(o)=>{
    //         if (o.objectURL) {
    //             return o.objectURL==v.objectURL;
    //         } else {
    //             return false;
    //         }
    //     })<0) {
    //         if (files.value.length > fileLimit.value) {
    //             fileMessages.value.push(`${v.name} 已经超出允许上传的最大数量限制，没有加载。`);
    //         } else {
    //             v.tempMap = {};
    //             v.tempMap.size = v.size;
    //             v.tempMap.name = v.name;
    //             v.tempMap.type = v.type;
    //             v.tempMap.imgPath = v.objectURL;
    //             files.value.push(v);
    //         }
    //     }
    // });

    lodash.forEach(e.files,(v,i)=>{
        if (files.value.length >= fileLimit.value) {
            fileMessages.value.push(`${v.name} 已经超出允许上传的最大数量限制，没有加载。`);
        } else {
            v.tempMap = {};
            v.tempMap.size = v.size;
            v.tempMap.name = v.name;
            v.tempMap.type = v.type;
            v.tempMap.imgPath = v.objectURL;
            files.value.push(v);
        }
    });
    fu.value.files = [];

    // files.value = lodash.concat(files.value,e.files);
    // console.log(files.value);
}

function uploadFile(step) {
    if (step > files.value.length - 1) {
        allFilesUploaded(files.value,obj);
    } else {
        if (files.value[step].path) {
            uploadFile(step + 1);
        } else if (files.value[step].objectURL) {
            let fileKey = `${filePreKey.value}/${util.random_string(5)}_${files.value[step].name}`;//filePreKey.value + '/' + files.value[step].name;
            oss.uploadFileWithClient(
                files.value[step],
                fileKey,
                (res) => {
                    files.value[step].fileKey = fileKey;
                    theFileUploaded(files.value[step],step,obj);
                    uploadFile(step + 1);
                },
                (er) => {
                    dialog.toastError(er);
                }
            );
            // oss.uploadFileWithReq(
            //     fileList[step],
            //     'temp/' + fileList[step].name,
            //     (res) => {
            //         dialog.toastNone(`${fileList[step].name} uploaded`);
            //         uploadFile(step + 1);
            //     },
            //     (er) => {
            //         dialog.toastError(er);
            //     }
            // );
        }
    }
}

function uploader() {
    uploadFile(0);
}

function delTheFile(index) {
    dialog.confirm("是否删除这个文件？",()=>{
        deleteFile(files.value[index],index,obj);
        files.value.splice(index,1);
    },null);
}

const formatSize = (bytes) => {
    const k = 1024;
    const dm = 3;
    const sizes = $primevue.config.locale.fileSizeTypes;

    if (bytes === 0) {
        return `0 ${sizes[0]}`;
    }

    const i = Math.floor(Math.log(bytes) / Math.log(k));
    const formattedSize = parseFloat((bytes / Math.pow(k, i)).toFixed(dm));

    return `${formattedSize} ${sizes[i]}`;
};
</script>

<style scoped lang="scss">

</style>
