<template>
    <div class="card">
        <div class="flex flex-wrap items-center justify-between">
            <div>
                <span class="text-base">赛事分组设置</span>
            </div>
            <div class="row end gap-4">
<!--                <span class="text-sm text-red-600">可拖拽排序</span>-->
                <Button label="新增类别" @click="addItem"/>
            </div>
        </div>
        <DataView :value="selMasterCompetition.competitionList" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
            <template #list="slotProps">
                <div class="col">
                    <div v-for="(element,index) in slotProps.items" :key="index" class="m-2 p-2 !relative !border-2 !border-gray-300 !border-solid flex-wrap between">
                        <div class="row flex-wrap gap-2 w-full mt-6">
                            <Fieldset legend="设置类别">
                                <IftaLabel>
                                    <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">类别名称</label>
                                    <InputText v-model="element.name"/>
                                </IftaLabel>
                                <Divider/>
                                <IftaLabel>
                                    <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">类别说明</label>
                                    <Textarea v-model="element.description"/>
                                </IftaLabel>
                            </Fieldset>
                            <Fieldset legend="设置分组" class="col w-full md:w-2/5 lg:w-3/5 border-solid border-2 border-gray-200 p-2 gap-2 relative">
                                <div class="gap-x-2">
                                    <p v-for="(gg,gIndex) in element.guiGeList" :key="gIndex" class="row gap-x-2">
                                        <InputText v-model="gg.name" size="small" placeholder="输入分组名称"/>
                                        <Textarea v-model="gg.description" placeholder="输入分组说明"/>
                                        <button @click="deleteGuiGe(element,gIndex)" class="text-xs border-2 border-solid border-red-300 px-2 py-1 text-red-400 rounded-2xl place-self-center">删除</button>
                                    </p>
                                </div>
                                <div class="absolute -top-8 left-32">
                                    <button @click="addGuiGe(index)" class="bg-indigo-400 text-xs h-6 px-2 text-white rounded-2xl place-self-start">新增分组</button>
                                </div>
                            </Fieldset>
                        </div>
                        <div class="absolute top-1 right-1">
                            <Button class="!border-orange-500 !text-orange-600" label="删除类别" rounded variant="outlined" size="small" @click="deleteItem(index)"/>
                        </div>
                    </div>
<!--                    <draggable :list="slotProps.items" item-key="id">-->
<!--                        <template #item="{ element,index }">-->

<!--                        </template>-->
<!--                    </draggable>-->
                </div>
            </template>
        </DataView>
        <div class="row mt-12 center gap-4">
            <Button label="保存设置" class="px-8" _as="router-link" _to="/"  @click="save()"></Button>
            <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
        </div>
    </div>
</template>

<script setup>
import { inject, onMounted, ref } from 'vue';
import dialog from '@/api/uniapp/dialog';
import workRest from '@/api/dbs/workRest';
// import draggable from 'vuedraggable';
import { Beans } from '@/api/dbs/beans';
import lodash from "lodash-es";
import oss from '@/api/oss';

const selMasterCompetition = ref({});

let mainPage = null;
let mePage = null;
let obj = null;
let host = inject("domain");

onMounted(() => {

});

function addItem() {
    let competition = Beans.competition();
    competition.id = Beans.buildPId("");
    competition.masterCompetition.id = selMasterCompetition.value.id;
    competition.appId = host;
    competition.temp = 0;
    selMasterCompetition.value.competitionList.push(competition);
}

function deleteItem(index) {
    dialog.confirm("类别下的分组将一并删除，是否删除这个类别？",()=>{
        if (selMasterCompetition.value.competitionList[index].temp==0) {
            selMasterCompetition.value.competitionList.splice(index,1);
        } else {
            workRest.deleteCompetition({id:selMasterCompetition.value.competitionList[index].id},(res)=>{
                if (res.status=="OK") {
                    selMasterCompetition.value.competitionList.splice(index,1);
                }
            });
        }
    },null);
}

function cancel() {
    mePage.close(mainPage);
}

function addGuiGe(index) {
    let guiGe = Beans.guiGe();
    guiGe.appId = host;
    guiGe.id = Beans.buildPId("");
    guiGe.competition.id = selMasterCompetition.value.competitionList[index].id;
    guiGe.temp = 0;
    if (!selMasterCompetition.value.competitionList[index].guiGeList) {
        selMasterCompetition.value.competitionList[index].guiGeList = [];
    }
    selMasterCompetition.value.competitionList[index].guiGeList.push(guiGe);
}

function deleteGuiGe(competition,index) {
    dialog.confirm("是否删除这个分组？",()=>{
        if (competition.guiGeList[index].temp==0) {
            competition.guiGeList.splice(index,1);
        } else {
            workRest.deleteGuiGe({id:competition.guiGeList[index].id},(res)=>{
                if (res.status=="OK") {
                    competition.guiGeList.splice(index,1);
                }
            });
        }
    },null);
}

function save() {
    let shiEmpty = true;
    let cList = lodash.cloneDeep(selMasterCompetition.value.competitionList);
    lodash.forEach(cList,(v)=>{
        if (!v.name) {
            shiEmpty = false;
        }
        let mid = selMasterCompetition.value.id;
        v.masterCompetition = Beans.masterCompetition();
        v.masterCompetition.id = mid;
        let gList = lodash.cloneDeep(v.guiGeList);
        v.tempMap = {};
        v.tempMap.guiGeList = gList;
        v.guiGeList = null;
        lodash.forEach(v.tempMap.guiGeList,(g)=>{
            if (!g.name) {
                shiEmpty = false;
            }
            let cid = g.competition.id;
            g.competition = Beans.competition();
            g.competition.id = cid;
        })
    });

    if (!shiEmpty) {
        dialog.toastError("有类别名称以及分组名称没有输入");
        return;
    }

    workRest.updateCompetitionList({competitionList:cList},(res)=>{
        if (res.status=="OK") {
            obj.returnFunction(obj);
            mePage.close(mainPage);
        }
    });
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    selMasterCompetition.value = obj.data;
    // console.log(selMasterCompetition.value.description);
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
