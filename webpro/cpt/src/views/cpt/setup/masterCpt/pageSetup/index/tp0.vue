<template>
    <div>
        <div v-for="pageEls of pageJson">
            <Fieldset :legend="pageEls.name" class="!mt-5" :pt="{legendLabel:{class:'text-orange-300'}}">
                <div v-for="element of pageEls.setup">
                    <div v-if="element.type=='box'" class="p-2">
                        <div class="between">
                            <span class="text-green-600 text-sm">{{element.pre}}</span>
                            <Button v-if="!readOnly" label="新增" size="small" @click="process=-1;openDialog(element)"/>
                        </div>
                        <Divider/>
                        <buildUIElement v-if="readOnly" :element="element" />
                        <pageUIEdit v-else :element="element" @deleteRow="deleteRow" @updateRow="updateRow"/>
                    </div>
                    <div v-else-if="element.type=='image'" class="p-2">
<!--                        <div class="between">-->
<!--                            <span class="text-green-600 text-sm">{{element.pre}}</span>-->
<!--                            <Button v-if="!readOnly" label="选择图片" size="small" @click="openDialog(element)"/>-->
<!--                        </div>-->
                        <buildUIElement v-if="readOnly" :element="element" />
                        <pageUIEdit v-else :element="element" v-model:mediaFiles="allMediaFiles"/>
                    </div>
                    <div v-else>
                        <buildUIElement v-if="readOnly" :element="element" />
                        <pageUIEdit v-else :element="element"/>
                    </div>
                </div>
            </Fieldset>
        </div>
        <Dialog v-model:visible="showDialog" modal>
            <ScrollPanel class="w-full h-96" :dt="{
                bar: {
                    background: '{primary.color}'
                }

            }">
                <div v-if="!element.yeWuType">
                    <div v-for="boxItem of eltTypes" class="col gap-4">
                        <pageUIEdit :element="boxItem" v-model:mediaFiles="allMediaFiles"/>
                    </div>
                </div>
                <div v-else-if="element.yeWuType=='media'">
                    <span>{{mediaFiles[0].name}}</span>
                    <image-grid :mediaFiles="mediaFiles[0].value" :selFiles="element.value" :selCount="1" :funCheckHasIndex="imageGridCheckHas4Media"></image-grid>
                    <Divider/>
                    <span class="mt-10">{{mediaFiles[1].name}}</span>
                    <image-grid :mediaFiles="mediaFiles[1].value" :selFiles="element.value" :selCount="1" :funCheckHasIndex="imageGridCheckHas4Media"></image-grid>
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
            </ScrollPanel>
            <div class="center gap-4 mt-5">
                <Button label="确定" size="small" @click="saveelm"/>
                <Button severity="warn" label="取消" size="small" @click="cancelelm"/>
            </div>
        </Dialog>
        <Popover ref="op">

        </Popover>
<!--        <span class="mt-36" style="background: linear-gradient(9deg, #f0002c,#242ebf);-webkit-background-clip: text;-webkit-text-fill-color: transparent; ">aaa</span>-->
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
const allMediaFiles = ref([]);
const judgeList = ref([]);

let host = inject("domain");
let process = -1;

(async ()=>{
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
                        allMediaFiles.value = lodash.concat(mediaFiles.value[0].value,mediaFiles.value[1].value);
                        resolve();
                    } else {
                        resolve();
                    }
                }
            });
        });
    })();
})();

async function openDialog(_element) {
    element.value = _element;
    eltTypes.value = lodash.cloneDeep(element.value.eltTypes);
    if (element.value.type=="box") {

    }
    if (element.value.yeWuType=="media") {

    }

    if (element.value.yeWuType=="judge") {
        judgeList.value = [];
        await (async ()=>{
            await new Promise(resolve => {
                userRest.queryJudgeList({pageSize:100,currentPage:0},async (res)=>{
                    if (res.status=="OK") {
                        if (res.data!=null) {
                            judgeList.value = [];
                            for(let v of res.data.content) {
                                let judge = {id:v.id,img:{value:v.headImgUrl},name:v.name,subDescription:v.subDescription,zhiWei:v.zhiWei,tempMap:{imgPath:await oss.buildPathAsync(v.headImgUrl,true,null)}};
                                judge.img.tempMap = {imgPath:judge.tempMap.imgPath};
                                judgeList.value.push(judge);
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
    showDialog.value = true;
}
const imageGridCheckHas4Judge = (file,mediaFiles,selFiles,selCount)=>{
    return lodash.findIndex(selFiles,(o)=>{return o.id==file.id});
}
const imageGridCheckHas4Media = (file,mediaFiles,selFiles,selCount)=>{
    // console.log("selFiles",selFiles,"file",file);
    if (selCount>1) {
        return lodash.findIndex(selFiles,(o)=>{return o.id==file.id});
    } else {
        return selFiles.id==file.id ? 0 : -1;
    }
}

function openPop(event,_eltTypes) {
    eltTypes.value = _eltTypes;
    op.value.toggle(event);
}
function saveelm() {
    if (element.value.yeWuType=="judge") {

    } else {
        if (!element.value.value) {
            element.value.value = [];
        }
        let obj = {};
        lodash.forEach(eltTypes.value,(v)=>{
            obj[v.key] = v.value ? v.value : "";
        });
        if (process==-1) {
            element.value.value.push(obj);
        } else if (process>-1) {
            element.value.value[process] = obj;
        }

        eltTypes.value = {};
    }
    showDialog.value = false;
}
function cancelelm() {
    showDialog.value = false;
}

function deleteRow(element,data,index) {
    // console.log(element,data,index);
    element.value.splice(index, 1);
}
function updateRow(element,data,index) {
    // console.log(element,data);
    process = index;
    eltTypes.value = element.eltTypes;
    lodash.forEach(data,(v,k)=>{
        lodash.forEach(eltTypes.value,(ev)=>{
            // console.log(ev.key,k);
            if (ev.key==k) {
                ev.value = v;
            }
        });
    });
    // let obj = {};
    // lodash.forEach(eltTypes.value,(v)=>{
    //     obj[v.key] = v.value ? v.value : "";
    // });
    // element.value.value.push(obj);
    openDialog(element);
}
</script>

<style scoped lang="scss">

</style>
