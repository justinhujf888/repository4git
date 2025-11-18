<template>
    <div class="card">
        <div class="flex flex-wrap items-center justify-between">
            <div>
                <span class="text-base">{{selMasterCompetition.tempMap?.judgeData?.type==0 ? '类别' : '组别'}}{{selMasterCompetition.tempMap?.judgeData?.name}}评委设置</span>
            </div>
        </div>
        <Panel class="mt-10">
            <div v-for="flow of selMasterCompetition.tempMap?.judgeData?.data">
                <Fieldset :legend="flow.name" class="!relative">
                    <Button size="small" label="新增评委" class="!absolute top-0 right-2" @click="toggle($event,flow)"/>
                    <div class="mt-5">
                        <div v-if="flow.type==0" class="min-h-24">
                            <Chip v-for="ju of flow.setupData" :label="ju.name" :image="ju.tempImg" removable/>
                        </div>
                        <div v-if="flow.type==1" class="min-h-24">
                            <DataTable :value="flow.setupData" class="mt-12">
                                <Column header="姓名" class="w-48">
                                    <template #body="slotProps">
                                        <Chip :label="slotProps.data.name" :image="slotProps.data.tempImg" />
                                    </template>
                                </Column>
                                <Column header="字段">
                                    <template #body="slotProps">
                                        <div class="row flex-wrap gap-x-2">
                                            <Chip v-for="(f,index) in slotProps.data.fields" :key="index" :label="f.name" removable @remove="removeChip($event,slotProps.index,flow,f,index)"/>
                                        </div>
                                    </template>
                                </Column>
                                <Column class="w-16 !text-end">
                                    <template #body="{ data,index }">
                                        <Button icon="pi pi-trash" @click="deleteJudge(data,index)" severity="secondary" rounded></Button>
                                    </template>
                                </Column>
                            </DataTable>
                        </div>
                    </div>
                </Fieldset>
            </div>
        </Panel>
        <div class="row mt-12 center gap-4">
            <Button label="保存设置" class="px-8" _as="router-link" _to="/" @click="save()"></Button>
            <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
        </div>

        <Dialog v-model:visible="dialogVisible" ref="op" modal position="right" pt:root:class="!border-0 !bg-transparent" pt:mask:class="backdrop-blur-sm">
            <div class="col w-[38rem] gap-4 dark:bg-gray-900 p-2">
                <Select v-if="!shiMulti" v-model="selJudge" :options="judgeList" optionLabel="name" placeholder="选择评审" filter>
                    <template #value="slotProps">
                        <div v-if="slotProps.value" class="flex items-center gap-4">
                            <Avatar :alt="slotProps.value.name" :image="slotProps.value?.tempImg" size="large" shape="circle"/>
                            <div>{{ slotProps.value.name }}</div>
                        </div>
                        <span v-else>
                            {{ slotProps.placeholder }}
                        </span>
                    </template>
                    <template #option="slotProps">
                        <div class="flex items-center gap-4">
                            <Avatar :alt="slotProps.option.name" :image="slotProps.option?.tempImg" size="large" shape="circle"/>
                            <div>{{ slotProps.option.name }}</div>
                        </div>
                    </template>
                    <template #dropdownicon>
                        <i class="pi pi-map" />
                    </template>
                </Select>
                <MultiSelect v-else v-model="selJudgeList" :options="judgeList" optionLabel="name" placeholder="选择评审" filter display="chip">
                    <template #option="slotProps">
                        <div class="flex items-center gap-4">
                            <Avatar :alt="slotProps.option.name" :image="slotProps.option?.tempImg" size="large" shape="circle"/>
                            <div>{{ slotProps.option.name }}</div>
                        </div>
                    </template>
                    <template #dropdownicon>
                        <i class="pi pi-map" />
                    </template>
                    <template #filtericon>
                        <i class="pi pi-map-marker" />
                    </template>
                </MultiSelect>
                <MultiSelect v-if="!shiMulti" v-model="selFields" :options="selMasterCompetition.setupFields?.pingshen" optionLabel="name" placeholder="请选择评审字段" _maxSelectedLabels="3" />
                <div class="row mt-5 center gap-4">
                    <Button label="保存设置" size="small" class="px-8" _as="router-link" _to="/" @click="savePop()"></Button>
                    <Button severity="warn" size="small" label="取消" class="px-8" @click="cancelPop()"></Button>
                </div>
            </div>
        </Dialog>
    </div>
</template>

<script setup>

import {inject, onMounted, ref, useTemplateRef} from "vue";
import systemRest from "@/api/dbs/systemRest";
import workRest from "@/api/dbs/workRest";
import userRest from "@/api/dbs/userRest";
import {Config} from "@/api/config";
import lodash from "lodash-es";
import oss from "@/api/oss";
import dialog from '@/api/uniapp/dialog';

const op = useTemplateRef("op")
const selMasterCompetition = ref({});
const judgeList = ref([]);
const selJudge = ref(null);
const selJudgeList = ref([]);
const selFields = ref([]);
const shiMulti = ref(false);
const dialogVisible = ref(false);

let flow = null;
let mainPage = null;
let mePage = null;
let obj = null;

onMounted(()=>{
    userRest.queryJudgeList({pageSize:50,currentPage:0},(res)=>{
        if (res.status=="OK") {
            if (res.data.content!=null) {
                lodash.forEach(res.data.content,(j)=>{
                    let jj = {};
                    jj.id = j.id;
                    jj.name = j.name;
                    jj.phone = j.phone;
                    jj.engName = j.engName;
                    jj.headImgUrl = j.headImgUrl;
                    jj.tempImg = oss.buildImgPath(j.headImgUrl);
                    judgeList.value.push(jj);
                });
            }
        }
    });
});

const toggle = (event,_flow) => {
    flow = _flow;
    shiMulti.value = flow.type==0;
    selJudge.value = null;
    selFields.value = [];
    selJudgeList.value = [];
    dialogVisible.value = true;
    op.value.maximize();
    // console.log(_flow,shiMulti.value);
}

function savePop() {
    // console.log(selJudgeList.value,selJudge.value,selFields.value);
    if (!flow.setupData) {
        flow.setupData = [];
    }
    if (flow.type==0) {
        flow.setupData = selJudgeList.value;
        for(let j of selJudgeList.value) {
            if (lodash.findIndex(flow.setupData,(o)=>{return o.id==j.id})<0) {
                flow.setupData.push(j);
            }
        }
    } else if (flow.type==1) {
        flow.setupData.push({...selJudge.value,fields:selFields.value});
    }
    op.value.close();
}

function cancelPop() {
    op.value.close();
}

function removeChip(event,judgeIndex,_flow,field,fieldIndex) {
    console.log(field.name);
    let ay = lodash.remove(_flow.setupData[judgeIndex].fields,(o)=>{return o.id==field.id});
    console.log(ay,_flow.setupData[judgeIndex].fields);
    // _flow.setupData[judgeIndex].fields.splice(fieldIndex,1);
}

function deleteJudge(data,index) {
    dialog.confirm("是否删除？",()=>{
        flow.setupData.splice(index,1);
    },null);
}

function save() {
    // console.log(selMasterCompetition.value.tempMap.judgeData);
    obj.returnFunction(obj);
    mePage.close(mainPage);
}

function cancel() {
    mePage.close(mainPage);
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = lodash.cloneDeep(_obj);
    selMasterCompetition.value = obj.data;

    // console.log(selMasterCompetition.value);
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
