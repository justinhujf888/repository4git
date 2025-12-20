<template>
    <div>
        <div v-for="pageEls of pageJson">
            <Fieldset :legend="pageEls.name" class="!mt-5" :pt="{legendLabel:{class:'text-orange-300'}}">
                <div v-for="element of pageEls.setup">
                    <div v-if="element.type=='box'" class="p-2">
                        <div class="between">
                            <span class="text-green-600 text-sm">{{element.pre}}</span>
                            <Button v-if="!readOnly" label="新增" size="small" @click="openDialog(element)"/>
                        </div>
                        <Divider/>
                        <buildUIElement v-if="readOnly" :element="element" />
                        <pageUIEdit v-else :element="element"/>
                    </div>
                    <div v-else-if="element.type=='image'" class="p-2">
                        <div class="between">
                            <span class="text-green-600 text-sm">{{element.pre}}</span>
                            <Button v-if="!readOnly" label="选择图片" size="small" @click="openDialog(element)"/>
                        </div>
                        <buildUIElement v-if="readOnly" :element="element" />
                        <pageUIEdit v-else :element="element"/>
                        <Divider/>
                    </div>
                    <div v-else>
                        <buildUIElement v-if="readOnly" :element="element" />
                        <pageUIEdit v-else :element="element"/>
                    </div>
                </div>
            </Fieldset>
        </div>
        <Dialog v-model:visible="showDialog" modal>
            <div v-if="!element.yeWuType">
                <pageUIEdit v-for="boxItem of eltTypes" :element="boxItem"/>
            </div>
            <div v-else-if="element.yeWuType=='media'">
                <image-grid :mediaFiles="mediaFiles[0].value" :selFiles="element.value" :funCheckHasIndex="imageGridCheckHas4Media"></image-grid>
            </div>
            <div v-else-if="element.yeWuType=='judge'">
                <image-grid :mediaFiles="judgeList" :selFiles="element.value" :selCount="element.count" :funCheckHasIndex="imageGridCheckHas4Judge">
                    <template #content="slotProps">
                        <div class="w-full h-12 center">
                            <span>{{slotProps.file.name}}</span>
                        </div>
                    </template>
                </image-grid>
            </div>
            <div class="center gap-4 mt-5">
                <Button label="确定" size="small" @click="saveelm"/>
                <Button severity="warn" label="取消" size="small" @click="cancelelm"/>
            </div>
        </Dialog>
        <Popover ref="op">

        </Popover>
    </div>
</template>

<script setup>
import pageUIEdit from "@/views/cpt/setup/masterCpt/pageSetup/pageUIEdit.vue";
import buildUIElement from "@/views/cpt/setup/masterCpt/pageSetup/buildUIElement.vue";
import imageGrid from "@/components/my/imageGrid.vue";
import {inject, ref, useTemplateRef} from "vue";
import lodash from "lodash-es";
import workRest from '@/api/dbs/workRest';
import oss from "@/api/oss";
import dialog from "@/api/uniapp/dialog";
import userRest from "@/api/dbs/userRest";
import {Config} from "@/api/config";

const pageJson = defineModel("pageJson",{default:{}});
const readOnly = defineModel("readOnly",{default:true});
const eltTypes = ref([]);
const element = ref({});
const showDialog = ref(false);
const op = useTemplateRef("op");
const mediaFiles = ref([{sourceType:8,name:"页面素材",value:[]},{sourceType:9,name:"新闻素材",value:[]}]);
const judgeList = ref([]);

let host = inject("domain");

async function openDialog(_element) {
    element.value = _element;
    eltTypes.value = lodash.cloneDeep(element.value.eltTypes);
    if (element.value.yeWuType=="media") {
        mediaFiles.value = [{sourceType:8,name:"页面素材",value:[]},{sourceType:9,name:"新闻素材",value:[]}];
        await (async ()=>{
            await new Promise(resolve => {
                workRest.qySiteWorkItemList({sourceType:8,sourceId:host,type:9},async (res)=>{
                    if (res.status=="OK") {
                        if (res.data) {
                            for(let v of res.data) {
                                v.tempMap = {};
                                v.tempMap.size = v.fileFields.size;
                                v.tempMap.name = v.fileFields.name;
                                v.tempMap.type = v.fileFields.type;
                                v.tempMap.imgPath = await oss.buildPathAsync(v.path,true,null);
                                v.img = v.path;
                                let mfs = lodash.find(mediaFiles.value,(o)=>{return o.sourceType==8});
                                mfs.value.push(v);
                            }
                            resolve();
                        } else {
                            resolve();
                        }
                    }
                });
            });
            await new Promise(resolve => {
                workRest.qySiteWorkItemList({sourceType:9,sourceId:host,type:9},async (res)=>{
                    if (res.status=="OK") {
                        if (res.data) {
                            for(let v of res.data) {
                                v.tempMap = {};
                                v.tempMap.size = v.fileFields.size;
                                v.tempMap.name = v.fileFields.name;
                                v.tempMap.type = v.fileFields.type;
                                v.tempMap.imgPath = await oss.buildPathAsync(v.path,true,null);
                                v.img = v.path;
                                let mfs = lodash.find(mediaFiles.value,(o)=>{return o.sourceType==9});
                                mfs.value.push(v);
                            }
                            resolve();
                        } else {
                            resolve();
                        }
                    }
                });
            });
        })();
    }

    if (element.value.yeWuType=="judge") {
        judgeList.value = [];
        await (async ()=>{
            await new Promise(resolve => {
                userRest.queryJudgeList({pageSize:100,currentPage:0},async (res)=>{
                    if (res.status=="OK") {
                        if (res.data!=null) {
                            judgeList.value = res.data.content;
                            for(let v of judgeList.value) {
                                v.tempMap = {};
                                v.tempMap.imgPath = await oss.buildPathAsync(v.headImgUrl,true,null);
                                v.img = v.headImgUrl;
                            }
                            // console.log(judgePageUtil.value);
                            resolve();
                        } else {
                            resolve();
                        }
                    }
                });
            });
        })();
    }
    showDialog.value = true;
}
const imageGridCheckHas4Judge = (file,mediaFiles,selFiles)=>{
    return lodash.findIndex(selFiles,(o)=>{return o.id==file.id});
}
const imageGridCheckHas4Media = (file,mediaFiles,selFiles)=>{
    console.log("selFiles",selFiles,"file",file);
    return lodash.findIndex(selFiles,(o)=>{return o.id==file.id});
}

function openPop(event,_eltTypes) {
    eltTypes.value = _eltTypes;
    op.value.toggle(event);
}
function saveelm() {
    if (element.value.yeWuType!="media") {
        if (!element.value.value) {
            element.value.value = [];
        }
        let obj = {};
        lodash.forEach(eltTypes.value,(v)=>{
            obj[v.key] = v.value ? v.value : "";
        });
        element.value.value.push(obj);
        eltTypes.value = {};
    } else {

    }
    showDialog.value = false;
}
function cancelelm() {
    showDialog.value = false;
}
</script>

<style scoped lang="scss">

</style>
