<template>
    <div class="card">
        <div class="flex flex-wrap items-center justify-between">
            <div>
                <span class="text-base">赛事字段设置</span>
            </div>
            <div class="row end gap-4">
                <span class="text-sm text-red-600">可拖拽排序</span>
                <Button label="新增字段" @click="addItem"/>
            </div>
        </div>
        <DataView :value="selMasterCompetition.setupFields?.data" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
            <template #list="slotProps">
                <div class="col">
                    <draggable :list="slotProps.items" item-key="id">
                        <template #item="{ element,index }">
                            <Panel class="m-2 !relative !border-2 !border-gray-300 !border-solid">
                                <IftaLabel>
                                    <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">字段名称</label>
                                    <InputText v-model="element.name"/>
                                </IftaLabel>
<!--                                <Divider/>-->
<!--                                <IftaLabel>-->
<!--                                    <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">字段类型</label>-->
<!--                                    <InputText v-model="element.type"/>-->
<!--                                </IftaLabel>-->
                                <div class="absolute top-1 right-1">
                                    <Button class="!border-orange-500 !text-orange-600" label="删除" rounded variant="outlined" @click="deleteItem(index)"/>
                                </div>
                            </Panel>
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

const selMasterCompetition = ref({});

let mainPage = null;
let mePage = null;
let obj = null;

function addItem() {
    selMasterCompetition.value.setupFields.data.push({id:Beans.buildPId(""),name:"",type:"inputText"});
}

function deleteItem(index) {
    dialog.confirm("是否删除这个项目？",()=>{
        selMasterCompetition.value.setupFields.data.splice(index,1);
    },null);
}

function cancel() {
    mePage.close(mainPage);
}

function save() {
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
        selMasterCompetition.value.setupFields = {data:[]};
    }
    // console.log(selMasterCompetition.value.description);
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
