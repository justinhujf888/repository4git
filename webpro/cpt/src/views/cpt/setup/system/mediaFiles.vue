<template>
    <animationPage ref="mainPage" :show="true" class="w-full absolute top-0 z-40">
        <Tabs v-model:value="tabIndex">
            <TabList>
                <Tab value="8">页面素材</Tab>
                <Tab value="9">新闻素材</Tab>
            </TabList>
            <TabPanels>
                <Button label="设置" severity="warn" class="!absolute !right-2 !top-2 !rounded-2xl" @click="openUpdate"/>
                <TabPanel value="8">
                    <div class="mt-10">
                        <priviewImage v-model="files[8]"/>
                    </div>
                </TabPanel>
                <TabPanel value="9">
                    <div class="mt-10">
                        <priviewImage v-model="files[9]"/>
                    </div>
                </TabPanel>
            </TabPanels>
        </Tabs>
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
import priviewImage from "@/components/my/priviewImage.vue";
import { Beans } from '@/api/dbs/beans';
import workRest from '@/api/dbs/workRest';
import oss from "@/api/oss";

const mainPage = useTemplateRef("mainPage");
const updatePage = useTemplateRef("updatePage");
const refFileUpload = useTemplateRef("refFileUpload");
const files = ref({8:[],9:[]});

let host = inject("domain");
let sourceType = 8;
const filePreKey = ref("");
const maxFileSize = ref( 2097152);
const fileLimit = ref(20);
const tabIndex = ref("8");

onMounted(() => {
    workRest.qySiteWorkItemList({sourceType:8,sourceId:host,type:9},async (res)=>{
        if (res.status=="OK") {
            if (res.data) {
                for(let v of res.data) {
                    v.tempMap = {};
                    v.tempMap.size = v.fileFields.size;
                    v.tempMap.name = v.fileFields.name;
                    v.tempMap.type = v.fileFields.type;
                    v.tempMap.imgPath = await oss.buildPathAsync(v.path,true,null);
                    files.value[v.sourceType].push(v);
                }
            }
        }
    });
    workRest.qySiteWorkItemList({sourceType:9,sourceId:host,type:9},async (res)=>{
        if (res.status=="OK") {
            if (res.data) {
                for(let v of res.data) {
                    v.tempMap = {};
                    v.tempMap.size = v.fileFields.size;
                    v.tempMap.name = v.fileFields.name;
                    v.tempMap.type = v.fileFields.type;
                    v.tempMap.imgPath = await oss.buildPathAsync(v.path,true,null);
                    files.value[v.sourceType].push(v);
                }
            }
        }
    });
});

function filesUpload(_files, obj) {
    dialog.alertBack(`文件上传完成！`, () => {
        updatePage.value.close(mainPage.value);
    });
}

function theFileUpload(file, index, obj) {
    let siteWorkItem = Beans.siteWorkItem();
    siteWorkItem.id = Beans.buildPId(sourceType == 8 ? "page" : "news");
    siteWorkItem.path = file.fileKey;
    siteWorkItem.type = 9;
    siteWorkItem.sourceType = parseInt(tabIndex.value);
    siteWorkItem.sourceId = host;
    siteWorkItem.mediaType = obj.mediaType;
    siteWorkItem.createDate = dayjs().valueOf();
    siteWorkItem.appId = obj.appId;
    siteWorkItem.fileFields = { name: file.name, size: file.size, type: file.type };
    workRest.updateSiteWorkItem({ siteWorkItem: siteWorkItem }, async (res) => {
        if (res.status == "OK") {
            siteWorkItem.tempMap = {};
            siteWorkItem.tempMap.size = siteWorkItem.fileFields.size;
            siteWorkItem.tempMap.name = siteWorkItem.fileFields.name;
            siteWorkItem.tempMap.type = siteWorkItem.fileFields.type;
            siteWorkItem.tempMap.imgPath = await oss.buildPathAsync(siteWorkItem.path,true,null);
            files.value[parseInt(tabIndex.value)].push(siteWorkItem);
            dialog.toastSuccess(`文件${file.name}已上传`);
        }
    });
}

function deleteFile(file, index, obj) {
    console.log(file.id,file.path);
    if (file.id) {
        workRest.deleteSiteWorkItem({ id: file.id }, (res) => {
            if (res.status == "OK") {
                oss.deleteFile(file.path);
                files.value[parseInt(tabIndex.value)].splice(index, 1);
                dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
            }
        });
    } else {
        files.value[parseInt(tabIndex.value)].splice(index, 1);
        dialog.toastSuccess(`文件${file.tempMap.name}已删除`);
    }
}

function cancelUpload() {
    updatePage.value.close(mainPage.value);
}

function openUpdate() {
    // console.log(`cpt/${host}/mediaFiles/${tabIndex.value}/`,parseInt(tabIndex.value));
    refFileUpload.value.init(files.value[parseInt(tabIndex.value)], null, null, null, `cpt/${host}/mediaFiles/${tabIndex.value}`, {sourceType:parseInt(tabIndex.value),appId:host});
    updatePage.value.open(mainPage.value);
}
</script>

<style scoped lang="scss">

</style>
