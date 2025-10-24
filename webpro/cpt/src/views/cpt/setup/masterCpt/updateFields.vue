<template>
    <div class="card">
        <div class="flex flex-wrap items-center justify-between">
            <div>
                <span class="text-base">赛事字段设置</span>
            </div>
            <div class="row end gap-4">
                <span class="text-sm text-red-600">可拖拽排序</span>
            </div>
        </div>
        <Panel header="作品字段" class="!relative">
            <Button size="small" label="新增字段" @click="addWorkItem" class="!absolute top-2 right-2"/>
            <DataView :value="selMasterCompetition.setupFields?.data" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
                <template #list="slotProps">
                    <div class="col">
                        <draggable :list="slotProps.items" item-key="id">
                            <template #item="{ element,index }">
                                <div class="m-2 p-2 !relative !border-2 !border-gray-300 !border-solid between">
                                    <IftaLabel>
                                        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">字段名称</label>
                                        <InputText v-model="element.name"/>
                                    </IftaLabel>
                                    <!--                                <Divider/>-->
                                    <!--                                <IftaLabel>-->
                                    <!--                                    <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">字段类型</label>-->
                                    <!--                                    <InputText v-model="element.type"/>-->
                                    <!--                                </IftaLabel>-->
                                    <div _class="absolute top-1 right-1">
                                        <Button class="!border-orange-500 !text-orange-600" label="删除" rounded variant="outlined" size="small" @click="deleteItem(0,index)"/>
                                    </div>
                                </div>
                            </template>
                        </draggable>
                        <!--                    <div v-for="(item,index) in slotProps.items" :key="index">-->
                        <!--                        <Panel class="m-2 !relative">-->
                        <!--                            <IftaLabel>-->
                        <!--                                <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">标题</label>-->
                        <!--                                <InputText v-model="item.title"/>-->
                        <!--                            </IftaLabel>-->
                        <!--                            <Divider/>-->
                        <!--                            <IftaLabel>-->
                        <!--                                <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">内容</label>-->
                        <!--                                <Textarea v-model="item.description" autoResize rows="4" class="w-full"/>-->
                        <!--                            </IftaLabel>-->
                        <!--                            <div class="absolute top-1 right-1">-->
                        <!--                                <Button class="!border-orange-500 !text-orange-600" label="删除" rounded variant="outlined" @click="deleteItem(index)"/>-->
                        <!--                            </div>-->
                        <!--                        </Panel>-->
                        <!--                    </div>-->
                    </div>
                </template>
            </DataView>
        </Panel>
        <Divider/>
        <Panel header="评审字段" class="!relative">
            <Button size="small" label="新增字段" @click="addPingShenItem" class="!absolute top-2 right-2"/>
            <DataView :value="selMasterCompetition.setupFields?.pingshen" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
                <template #list="slotProps">
                    <div class="col">
                        <draggable :list="slotProps.items" item-key="id">
                            <template #item="{ element,index }">
                                <div class="m-2 p-2 !relative !border-2 !border-gray-300 !border-solid between">
                                    <div class="row">
                                        <IftaLabel>
                                            <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">字段名称</label>
                                            <InputText v-model="element.name"/>
                                        </IftaLabel>
                                        <IftaLabel>
                                            <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">分数</label>
                                            <InputText v-model="element.fen"/>
                                        </IftaLabel>
                                    </div>
                                    <div _class="absolute top-1 right-1">
                                        <Button class="!border-orange-500 !text-orange-600" label="删除" rounded variant="outlined" size="small" @click="deleteItem(1,index)"/>
                                    </div>
                                </div>
                            </template>
                        </draggable>
                    </div>
                </template>
            </DataView>
        </Panel>
        <div class="row mt-12 center gap-4">
            <Button label="保存设置" class="px-8" _as="router-link" _to="/"  @click="save()"></Button>
            <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import dialog from '@/api/uniapp/dialog';
import workRest from '@/api/dbs/workRest';
import draggable from 'vuedraggable';
import { Beans } from '@/api/dbs/beans';
import lodash from 'lodash-es';

const selMasterCompetition = ref({});

let mainPage = null;
let mePage = null;
let obj = null;

function addWorkItem() {
    selMasterCompetition.value.setupFields.data.push({id:Beans.buildPId(""),name:"",type:"inputText"});
}

function addPingShenItem() {
    selMasterCompetition.value.setupFields.pingshen.push({id:Beans.buildPId(""),name:"",type:"inputText",fen:"10"});
}

function deleteItem(type,index) {
    dialog.confirm("是否删除这个项目？",()=>{
        if (type==0) {
            selMasterCompetition.value.setupFields.data.splice(index,1);
        } else if (type==1) {
            selMasterCompetition.value.setupFields.pingshen.splice(index,1);
        }
    },null);
}

function cancel() {
    mePage.close(mainPage);
}

function save() {
    let shiEmpty = true;
    lodash.forEach(selMasterCompetition.value.setupFields.data,(v)=>{
        if (!v.name) {
            shiEmpty = false;
        }
    });
    if (!shiEmpty) {
        dialog.toastError("有字段名称没有输入");
        return;
    }
    workRest.updateMasterCompetitionSetupFields({id:selMasterCompetition.value.id,setupFields:selMasterCompetition.value.setupFields},(res)=>{
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
    if (!selMasterCompetition.value.setupFields) {
        selMasterCompetition.value.setupFields = {data:[],pingshen:[]};
    }
    // console.log(selMasterCompetition.value.description);
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
