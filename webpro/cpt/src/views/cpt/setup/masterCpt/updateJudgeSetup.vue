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
                    <Button size="small" label="新增评委" class="!absolute top-0 right-2" @click="toggle"/>
                    <div v-if="flow.type==0" class="min-h-24">
                        <Chip v-for="ju of flow.setupData" :label="ju.name" :image="ju.tempImg" />
                    </div>
                    <div v-if="flow.type==1" class="min-h-24">
                        <DataTable :value="flow.setupData" class="mt-12">
                            <Column header="姓名" class="w-32">
                                <template #body="slotProps">
                                    <Chip :label="slotProps.data.name" :image="slotProps.data.tempImg" />
                                </template>
                            </Column>
                            <Column header="字段">
                                <template #body="slotProps">
                                    <div class="row flex-wrap gap-x-2">
                                        <Chip v-for="(f,index) in slotProps.data.fields" :key="index" :label="f.name" class="!bg-sky-100 !border-2 !border-sky-200 !border-solid !text-gray-800"/>
                                    </div>
                                </template>
                            </Column>
                        </DataTable>
                    </div>
                </Fieldset>
            </div>
        </Panel>
        <div class="row mt-12 center gap-4">
            <Button label="保存设置" class="px-8" _as="router-link" _to="/" @click="save()"></Button>
            <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
        </div>

        <Popover ref="op">
            <div class="col w-[25rem]">
                <Select v-model="selJudge" :options="judgeList" optionLabel="name" placeholder="选择评审" filter>
                    <template #value="slotProps">
                        <div v-if="slotProps.value" class="flex items-center gap-4">
                            <Avatar :alt="slotProps.value.name" :image="slotProps.value?.tempMap?.headImgUrl" size="large" shape="circle"/>
                            <div>{{ slotProps.value.name }}</div>
                        </div>
                        <span v-else>
                            {{ slotProps.placeholder }}
                        </span>
                    </template>
                    <template #option="slotProps">
                        <div class="flex items-center gap-4">
                            <Avatar :alt="slotProps.option.name" :image="slotProps.option?.tempMap?.headImgUrl" size="large" shape="circle"/>
                            <div>{{ slotProps.option.name }}</div>
                        </div>
                    </template>
                    <template #dropdownicon>
                        <i class="pi pi-map" />
                    </template>
                </Select>

            </div>
        </Popover>
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

const op = useTemplateRef("op")
const selMasterCompetition = ref({});
const judgeList = ref(null);
const selJudge = ref(null);

let mainPage = null;
let mePage = null;
let obj = null;

onMounted(()=>{
    userRest.queryJudgeList({pageSize:50,currentPage:0},(res)=>{
        if (res.status=="OK") {
            if (res.data.content!=null) {
                judgeList.value = res.data.content;
                lodash.forEach(judgeList.value,(j)=>{
                    j.tempMap = {};
                    j.tempMap.headImgUrl = oss.buildImgPath(j.headImgUrl);
                });
            }
        }
    });
});

const toggle = (event) => {
    op.value.toggle(event);
}

function save() {

}

function cancel() {
    mePage.close(mainPage);
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    selMasterCompetition.value = obj.data;

    console.log(selMasterCompetition.value);
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
