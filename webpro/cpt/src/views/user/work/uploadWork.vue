<template>
    <routerPath :home="null" :items="menuItems"/>
    <div class="card md:px-32 text-xl">
        <title-text :text="competition?.name" text-class="text-black font-semibold"/>
        <div class="start overflow-hidden mt-10">
            <div class="col center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full grid gap-x-2 gap-y-4">
                    <FloatLabel variant="on" v-if="process=='c'">
                        <label for="guige" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">选择分组</label>
                        <Select name="guige" v-model="work.guiGe" :options="competition.guiGeList" optionLabel="name" fluid placeholder="选择分组"/>
                    </FloatLabel>
                    <IftaLabel variant="on" v-if="process=='u'">
                        <label for="guige" class="block text-surface-900 dark:text-surface-0 text-base font-medium">选择分组</label>
                        <InputText name="guige" class="w-full md:w-[30rem]" :value="work.guiGe?.name" disabled/>
                    </IftaLabel>
                    <FloatLabel variant="on">
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品名称</label>
                        <InputText name="name" class="w-full md:w-[30rem]" v-model="work.name" :readonly="work.status==1"/>
                    </FloatLabel>
                    <Fieldset legend="作品照片">
                        <InputText v-model="imageVaild" name="imageVaild" class="hidden"/>
                        <Message v-for="er of $form.imageVaild?.errors" class="my-2" severity="error" size="small" variant="simple">{{er.message}}</Message>
                        <FileUpload v-show="false" ref="imageFileUpload" mode="basic" accept="image/*" @select="onFileSelect" customUpload auto severity="secondary" class="p-button-outlined" />
                        <div class="row flex-wrap gap-2">
                            <Button severity="secondary" class="col center w-36 h-32 md:w-44 md:h-28 border-solid border-gray-500 border-2 rounded-xl relative" v-for="(item,index) in workImageItems" :key="index" @click="uploadButtonClick(item,index)">
                                <img v-show="item.src" :alt="item.src" :src="item.src" class="absolute top-0 left-0 z-10 w-full h-32 object-cover object-center"/>
                                <div class="mix-blend-difference text-white col absolute wcenter z-20 w-full">
                                    <span class="text-xl">{{item.title}}</span>
                                    <span class="text-sm">{{item.text}}</span>
                                </div>
                                <div class="row absolute bottom-2 right-2 z-20">
                                    <Tag v-if="item.src && work.status<1" severity="danger" value="删除" class="" @click.stop="deleteFile(item)"></Tag>
                                </div>
                            </Button>
                        </div>
                    </Fieldset>
                    <Fieldset legend="作品视频">
                        <InputText v-model="videoVaild" name="videoVaild" class="hidden"/>
                        <FileUpload v-show="false" ref="videoFileUpload" mode="basic" accept="video/*" @select="onFileSelect" customUpload auto severity="secondary" class="p-button-outlined" />
                        <Message v-for="er of $form.videoVaild?.errors" class="my-2" severity="error" size="small" variant="simple">{{er.message}}</Message>
                        <div class="row flex-wrap gap-2">
                            <Button severity="secondary" class="col center w-36 h-32 md:w-44 md:h-28 !p-2 border-solid border-gray-500 border-2 rounded-xl relative" v-for="(item,index) in workVideoItems" :key="index" @click="uploadButtonClick(item,index)">
                                <videoInfo ref="refVideoInfo" v-if="item.src" :src="item.src" class="absolute top-0 left-0 z-10 object-center"/>
                                <div class="mix-blend-difference text-white col absolute wcenter z-10 w-full">
                                    <span class="text-xl">{{item.title}}</span>
                                    <span class="text-sm">{{item.text}}</span>
                                </div>
                                <div class="row absolute bottom-2 right-2 z-20">
                                    <Tag v-if="item.src && work.status<1" severity="danger" value="删除" class="" @click.stop="deleteFile(item)"></Tag>
                                </div>
                            </Button>
                        </div>
                    </Fieldset>
                    <FloatLabel variant="on">

                    </FloatLabel>
                    <FloatLabel variant="on">
                        <label for="gousiDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品介绍</label>
                        <Textarea name="gousiDescription" v-model="work.gousiDescription" autoResize rows="8" class="w-full" :readonly="work.status==1"/>
                    </FloatLabel>
                    <FloatLabel variant="on">
                        <label for="myMeanDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品理念</label>
                        <Textarea name="myMeanDescription" v-model="work.myMeanDescription" autoResize rows="8" class="w-full" :readonly="work.status==1"/>
                    </FloatLabel>
                    <FloatLabel variant="on" v-for="(item,index) in work.hangyeFields?.data" :key="index">
                        <label :for="item.id" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">{{item.name}}</label>
                        <InputText :name="item.id" v-model="item.value" autoResize rows="8" class="w-full" :readonly="work.status==1"/>
                    </FloatLabel>
                    <FloatLabel variant="on" v-for="(item,index) in work.otherFields?.data" :key="index">
                        <label :for="item.id" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">{{item.name}}</label>
                        <InputText :name="item.id" v-model="item.value" autoResize rows="8" class="w-full" :readonly="work.status==1"/>
                    </FloatLabel>
                    <div class="center mt-16 sm:row col gap-8" v-if="work.status<1">
                        <div class="text-base p-1 border-btn">
                            <Button size="small" type="submit" label="暂时保存" variant="text" class="!center !px-5 !py-2 !w-40 !bg-lime-100 !text-gray-800 !font-semibold sub-bg" @click="preSave(0)"></Button>
                        </div>
                        <div class="text-base p-1 border-btn">
                            <Button size="small" type="submit" label="提交" variant="text" class="!center !px-5 !py-2 !w-40 !text-white !bg-gray-800 !font-semibold sub-bg" _as="router-link" _to="/" @click="preSave(1)"></Button>
                        </div>
                    </div>
                </Form>
                <priviewImage ref="refPriviewImage" v-if="work" :files="work" :shiShowImgGrid="false" _class="hidden"/>
            </div>
        </div>
    </div>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import routerPath from "@/components/my/routerPath.vue";
import primeUtil from "@/api/prime/util";
import checker from "@/api/check/checker";
import {Beans} from "@/api/dbs/beans";
import oss from "@/api/oss";
import userRest from "@/api/dbs/userRest";
import dialog from "@/api/uniapp/dialog";
import lodash from "lodash-es";
import exifr from 'exifr';
import priviewImage from "@/components/my/priviewImage.vue";
import videoInfo from "@/components/my/videoInfo.vue";
import TitleText from '@/components/my/form/titleText.vue';
import page from '@/api/uniapp/page';

const imageFileUpload = useTemplateRef("imageFileUpload");
const videoFileUpload = useTemplateRef("videoFileUpload");
const refVideoInfo = useTemplateRef("refVideoInfo");

const masterCompetition = ref(Beans.masterCompetition());
const competition = ref(Beans.competition());
const work = ref(Beans.work());
const menuItems = ref([]);
const workImageItems = ref([]);
const workVideoItems = ref([]);
const imageVaild = ref(false);
const videoVaild = ref(false);
const process = ref(null);

const refPriviewImage = useTemplateRef("refPriviewImage");

let item = null;
let shiTempSave = true;
let preUploadFiles = [];
let errors = [];
let host = inject("domain");
let mainPage = null;
let mePage = null;
let obj = null;

onMounted(() => {
    menuItems.value = [
        {label:'返回我的参赛作品',click:()=>{obj.refreashUpdateKey();mePage.close(mainPage);}}
    ];
    // workImageItems.value = [
    //     {title:"上传相机原图",text:"不可在原片基础上做任何修改调整，包括裁切、调整颜色、修改内容",file:null,bean:buildWorkItem(0,0)},
    //     {title:"上传作品全景图主图",text:"",file:null,bean:buildWorkItem(0,1)},
    //     {title:"上传作品全景图其他角度",text:"",file:null,bean:buildWorkItem(0,2)},
    //     {title:"上传作品全景图其他角度",text:"",file:null,bean:buildWorkItem(0,2)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)},
    //     {title:"上传作品其他细节至少2张",text:"",file:null,bean:buildWorkItem(0,3)}
    // ];
});

function uploadButtonClick(_item,index) {
    item = _item;
    if (work.value.status<1 && !item.file) {
        if (item.mediaType==0) {
            imageFileUpload.value.choose();
        } else {
            videoFileUpload.value.choose();
        }
    } else if (item.file) {
        if (item.mediaType==0) {
            let l = lodash.filter(workImageItems.value,(o)=>{return o.tempMap?.imgPath});
            refPriviewImage.value.imagesShow(l,lodash.findIndex(l,(o)=>{return o.bean.id==item.bean.id}));
        } else {
            console.log(refVideoInfo.value[index].getVideoInfo());
        }
    }

}

function buildWorkItem(mediaType,type) {
    let workItem = Beans.workItem();
    workItem.id = Beans.buildPId("");
    workItem.mediaType = mediaType;
    workItem.type = type;
    workItem.work.id = work.value.id;
    return workItem;
}

const resolver = ({ values }) => {
    if (shiTempSave) {
        errors = primeUtil.checkFormRequiredValid([
            {val:work.value.name,name:"name"},
            {val:work.value.guiGe.id,name:"guige"},
            // {val:src.value,name:"headImg",label:"照片"}
        ]);
        errors.imageVaild = [];
        errors.videoVaild = [];
    } else {
        errors = primeUtil.checkFormRequiredValid([
            {val:work.value.name,name:"name"},
            {val:work.value.guiGe.id,name:"guige"},
            {val:work.value.myMeanDescription,name:"myMeanDescription"},
            {val:work.value.gousiDescription,name:"gousiDescription"},
            // {val:src.value,name:"headImg",label:"照片"}
        ]);
        errors.imageVaild = [];
        errors.videoVaild = [];
        lodash.forEach(obj.uploadRule.workType.image,(v)=>{
            const count = lodash.size(lodash.filter(workImageItems.value,(o)=>{return o.mediaType==v.mediaType && o.type==v.type && o.file!=null}));
            if (count < v.maxCount) {
                errors.imageVaild.push({type:"error",message:v.title});
            }
            if (v.checkExif) {
                lodash.forEach(lodash.filter(workImageItems.value,(o)=>{return o.mediaType==v.mediaType && o.type==v.type && o.file!=null}),(cv)=>{
                    if (!cv.exifInfo.Make || lodash.includes(["ps","photoshop"],lodash.toLower(cv.exifInfo.Software))) {
                        // errors.imageVaild.push({type:"error",message:`${v.title}${cv.file.name}不符合规定`});
                    }
                });
            }
        });

        lodash.forEach(obj.uploadRule.workType.video,(v)=>{
            const count = lodash.size(lodash.filter(workVideoItems.value,(o)=>{return o.mediaType==v.mediaType && o.type==v.type && o.file!=null}));
            if (count < v.maxCount) {
                errors.videoVaild.push({type:"error",message:v.title});
            }
        });
    }

    //不区分暂存与提交的公共部分判断
    lodash.forEach(obj.uploadRule.workType.image,(v)=>{
        if (v.rule) {
            lodash.forEach(lodash.filter(workImageItems.value,(o)=>{return o.mediaType==v.mediaType && o.type==v.type && o.file!=null}),(cv,index)=>{
                if (v.rule.size) {
                    if (cv.file.size>v.rule.size) {
                        errors.imageVaild.push({type:"error",message:`${v.title}${cv.file.name}文件太大，需小于${v.rule.size/1024/1024}MB`});
                    }
                }
            });
        }
    });

    lodash.forEach(obj.uploadRule.workType.video,(v)=>{
        if (v.rule) {
            lodash.forEach(lodash.filter(workVideoItems.value,(o)=>{return o.mediaType==v.mediaType && o.type==v.type && o.file!=null}),(cv,index)=>{
                if (v.rule.size) {
                    if (cv.file.size>v.rule.size) {
                        errors.videoVaild.push({type:"error",message:`${v.title}${cv.file.name}文件太大，需小于${v.rule.size/1024/1024}MB`});
                    }
                }
                if (v.rule.duration) {
                    if (refVideoInfo.value[index].getVideoInfo().duration>v.rule.duration) {
                        errors.videoVaild.push({type:"error",message:`${v.title}${cv.file.name}视频时间长度太长，需小于${v.rule.duration}秒`});
                    }
                }
            });
        }
    });

    // primeUtil.buildFormValidError(errors.videoVaild,"error","上传视频",()=>{
    //     workVideoItems.value
    // },(error)=>{errors.videoVaild = error});
    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        // console.log(workImageItems.value,workVideoItems.value);
        dialog.openLoading("");
        work.value.guiGeId = work.value.guiGe.id;
        work.value.status = shiTempSave ? 0 : 1;
        work.value.tempMap = {workItemList:[],upedItemList:work.value.workItemList};
        work.value.workItemList = null;

        preUploadFiles = lodash.filter(lodash.concat(workImageItems.value,workVideoItems.value),(o)=>{return o.src});
        uploadFile(0);

        // lodash.forEach(lodash.concat(workImageItems.value,workVideoItems.value),(v)=>{
        //     if (v.src) {
        //         let workItem = v.bean;
        //         workItem.createDate = work.value.createDate;
        //         workItem.path = `cpt/${host}/work/${obj.masterCompetition.name}/${obj.userId}/${work.value.id}/${v.file.name}`;
        //         if (workItem.mediaType==0) {
        //             workItem.exifInfo = JSON.stringify(v.exifInfo);
        //         }
        //         workItem.mediaFields = {name:v.file.name,size:v.file.size,type:v.file.type};
        //         work.value.tempMap.workItemList.push(workItem);
        //     }
        // });
        // console.log(work.value);
    }
};

function preSave(f) {
    shiTempSave = (f==0) ? true : false;
}

function uploadFile(step) {
    if (step > preUploadFiles.length-1) {
        // console.log(work.value);
        dialog.closeLoading();
        workRest.updateBuyerWork({work:work.value},(res)=>{
            if (res.status=="OK") {
                dialog.alertBack(shiTempSave ? "您的作品已经保存" : "您的作品已成功提交",()=>{
                    obj.work = work.value;
                    obj.returnFunction(obj);
                    mePage.close(mainPage);
                });
            }
        });
    } else {
        if (!preUploadFiles[step].uploaded) {
            let workItem = preUploadFiles[step].bean;
            if (!workItem.work) {
                workItem.work = Beans.work();
            }
            workItem.work.id = work.value.id;
            workItem.createDate = work.value.createDate;
            workItem.path = `cpt/${host}/work/${obj.masterCompetition.name}/${obj.userId}/${work.value.id}/${workItem.id}_${preUploadFiles[step].file.name}`;
            if (workItem.mediaType==0) {
                workItem.exifInfo = JSON.stringify(preUploadFiles[step].exifInfo);
            }
            workItem.mediaFields = {name:preUploadFiles[step].file.name,size:preUploadFiles[step].file.size,type:preUploadFiles[step].file.type};
            if (workItem.mediaType==1) {
                // let l = lodash.filter(workVideoItems.value,(o)=>{return o.file});
                workItem.mediaFields.duration = refVideoInfo.value[lodash.findIndex(workVideoItems.value,(o)=>{return o.bean.id==workItem.id})].getVideoInfo().duration;
            }
            oss.uploadFileWithClient(
                preUploadFiles[step].file,
                workItem.path,
                (res) => {
                    work.value.tempMap.workItemList.push(workItem);
                    dialog.toastSuccess(`文件${workItem.mediaFields.name}成功上传`);
                    uploadFile(step+1);
                },
                (er) => {
                    dialog.toastError(er);
                }
            );
        } else {
            uploadFile(step+1);
        }
    }
}

async function onFileSelect(event) {
    // console.log(event.files);
    item.file = event.files[0];
    if (item.mediaType==0) {
        item.src = item.file.objectURL;
        exifr.parse(item.file).then(output => {
            if (output) {
                item.exifInfo = {Make:output.Make,Software:output.Software};
            } else {
                item.exifInfo = {Make:null,Software:null};
            }
            // console.log(output);
        })
    } else {
        item.src = URL.createObjectURL(item.file);
    }
    item.tempMap = {};
    item.tempMap.imgPath = item.src;
    item.uploaded = false;
    // item.fileUrl = URL.createObjectURL(new Blob([await event.files[0].arrayBuffer()],{type:"video/mp4"}));
}

function deleteFile(ele) {
    if (ele.uploaded) {
        dialog.confirm("是否确认删除这个文件？",()=>{
            workRest.deleteWorkItem({id:ele.bean.id},(res)=>{
                if (res.status=="OK") {
                    oss.deleteFile(ele.bean.path);
                    ele.file = null;
                    ele.src = null;
                    work.value.workItemList.splice(lodash.findIndex(work.value.workItemList,(o)=>{return o.id==ele.bean.id}),1);
                }
            });
        },null);
    } else {
        ele.file = null;
        ele.src = null;
    }
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = lodash.cloneDeep(_obj);
    process.value = obj.process;
    competition.value = obj.data;
    masterCompetition.value = obj.masterCompetition;

    workImageItems.value = [];
    workVideoItems.value = [];
    errors = null;
    lodash.forEach(obj.uploadRule.workType.image,(v)=>{
        for(let i=0;i<v.showCount;i++) {
            workImageItems.value.push({title:v.title,text:v.text,file:null,mediaType:v.mediaType,type:v.type,checkExif:v.checkExif,bean:buildWorkItem(v.mediaType,v.type)});
        }
    });
    lodash.forEach(obj.uploadRule.workType.video,(v)=>{
        for(let i=0;i<v.showCount;i++) {
            workVideoItems.value.push({title:v.title,text:v.text,file:null,mediaType:v.mediaType,type:v.type,bean:buildWorkItem(v.mediaType,v.type)});
        }
    });

    if (obj.process=="c") {
        work.value = Beans.work();
        work.value.id = Beans.buildPId(obj.userId.substring(7));
        work.value.appId = host;
        work.value.buyer = Beans.buyer();
        work.value.buyer.phone = obj.userId;
        work.value.hangyeFields = masterCompetition.value.tempMap.setupFields;
        work.value.otherFields = masterCompetition.value.setupFields;
        work.value.createDate = new Date().getTime();
        work.value.workItemList = [];
    } else if (obj.process=="u") {
        // console.log(work.value);
        workRest.qyWorks({appId:host,userId:obj.userId,workId:obj.work.id,masterCompetitionId:obj.masterCompetition.id,shiWorkItemList:true},async (res)=>{
            if (res.status=="OK") {
                if (res.data!=null) {
                    work.value = res.data[0];
                    for (let workItem of lodash.filter(work.value.workItemList,(o)=>{return o.mediaType==0})) {
                        let item = lodash.find(workImageItems.value,(o)=>{return !o.file && o.mediaType==workItem.mediaType && o.type==workItem.type});
                        item.file = workItem.mediaFields;
                        item.path = workItem.path;
                        item.exifInfo = workItem.exifInfo;
                        item.src = await oss.buildPathAsync(workItem.path,true,null);
                        item.uploaded = true;
                        item.tempMap = {};
                        item.tempMap.imgPath = item.src;
                        item.bean = workItem;
                    }
                    for (let workItem of lodash.filter(work.value.workItemList,(o)=>{return o.mediaType==1})) {
                        let item = lodash.find(workVideoItems.value,(o)=>{return !o.file && o.mediaType==workItem.mediaType && o.type==workItem.type});
                        item.file = workItem.mediaFields;
                        item.path = workItem.path;
                        item.src = await oss.buildPathAsync(workItem.path,false,null);
                        item.bean = workItem;
                        item.uploaded = true;
                    }
                }
            }
        });
    }
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
