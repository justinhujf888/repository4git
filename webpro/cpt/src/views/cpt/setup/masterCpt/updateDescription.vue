<template>
    <div class="card">
        <div class="flex flex-wrap items-center justify-between">
            <div>
                <span class="text-base">{{obj?.title}}</span>
            </div>
            <div class="row end gap-4">
                <span class="text-sm text-red-500 dark:text-red-300">可拖拽排序</span>
                <Button label="新增标题内容" @click="addItem"/>
            </div>
        </div>
        <DataView :value="datas?.data" :pt="{
                                        emptyMessage:{
                                            class:'opacity-0'
                                        }
                                    }">
            <template #list="slotProps">
                <div class="col">
                    <draggable :list="slotProps.items" item-key="id">
                        <template #item="{ element,index  }">
                            <div class="m-2 p-2 !border-2 !border-gray-600 !border-solid rounded-2xl between !relative">
                                <div class="w-full">
                                    <IftaLabel>
                                        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">标题</label>
                                        <InputText v-model="element.title"/>
                                    </IftaLabel>
                                    <Divider/>
                                    <IftaLabel>
                                        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">内容</label>
                                        <Textarea v-model="element.description" autoResize rows="4" class="w-full"/>
                                    </IftaLabel>
                                </div>
                                <div class="absolute top-3 right-1">
                                    <Button class="!border-orange-500 !text-orange-600" label="删除" rounded variant="outlined" @click="deleteItem(index)" size="small"/>
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
const datas = ref({});

let mainPage = null;
let mePage = null;
let obj = null;

function addItem() {
    datas.value.data.push({id:Beans.buildPId(""),title:"",description:""});
}

function deleteItem(index) {
    if (datas.value.data[index].title!="" && datas.value.data[index].description!="") {
        dialog.confirm("是否删除这个项目？",()=>{
            datas.value.data.splice(index,1);
        },null);
    } else {
        datas.value.data.splice(index,1);
    }
}

function cancel() {
    mePage.close(mainPage);
}

function save() {
    let typeId = null;
    if (obj.id=="masterCompetition.description") {
        typeId = "description";
    } else if (obj.id=="masterCompetition.pxBiaozun") {
        typeId = "pxBiaozun";
    }
    workRest.updateMasterCompetitionDescription({id:selMasterCompetition.value.id,typeId:typeId,data:datas.value},(res)=>{
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
    if (obj.id=="masterCompetition.description") {
        if (!selMasterCompetition.value.description) {
            selMasterCompetition.value.description = {template:"001",data:[]};
        }
        datas.value = selMasterCompetition.value.description;
    } else if (obj.id=="masterCompetition.pxBiaozun") {
        if (!selMasterCompetition.value.pxBiaozun) {
            selMasterCompetition.value.pxBiaozun = {template:"001",data:[]};
        }
        datas.value = selMasterCompetition.value.pxBiaozun;
    }

    // console.log(selMasterCompetition.value.description);
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
