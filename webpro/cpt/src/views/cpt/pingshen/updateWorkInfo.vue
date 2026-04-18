<template>
    <div class="card text-xl h-auto">
        <div class="col center w-full p-2">
            <Form v-slot="$form" :resolver @submit="onFormSubmit" class="w-full grid gap-x-2 gap-y-4">
                <FloatLabel variant="on">
                    <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品名称</label>
                    <InputText name="name" class="w-full" v-model="work.name"/>
                </FloatLabel>
                <FloatLabel variant="on">
                    <label for="gousiDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品介绍</label>
                    <Textarea name="gousiDescription" v-model="work.gousiDescription" autoResize rows="8" class="w-full"/>
                </FloatLabel>
                <FloatLabel variant="on">
                    <label for="myMeanDescription" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品理念</label>
                    <Textarea name="myMeanDescription" v-model="work.myMeanDescription" autoResize rows="8" class="w-full"/>
                </FloatLabel>
                <FloatLabel variant="on" v-for="(item,index) in work.hangyeFields?.data" :key="index">
                    <label :for="item.id" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">{{item.name}}</label>
                    <InputText :name="item.id" v-model="item.value" autoResize rows="8" class="w-full"/>
                </FloatLabel>
                <FloatLabel variant="on" v-for="(item,index) in work.otherFields?.data" :key="index">
                    <label :for="item.id" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">{{item.name}}</label>
                    <InputText :name="item.id" v-model="item.value" autoResize rows="8" class="w-full"/>
                </FloatLabel>
                <div class="center mt-16 sm:row col gap-8">
                    <div class="text-base p-1">
                        <Button type="submit" label="保存"></Button>
                    </div>
                    <div class="text-base p-1">
                        <Button severity="warn" label="取消" @click="cancel"></Button>
                    </div>
                </div>
            </Form>
        </div>
    </div>
</template>

<script setup>
import lodash from "lodash-es";
import primeUtil from "@/api/prime/util";
import {ref} from "vue";
import {Beans} from "@/api/dbs/beans";
import dialog from "@/api/uniapp/dialog";
import workRest from "@/api/dbs/workRest";

const work = ref(Beans.work());

let mainPage = null;
let mePage = null;
let obj = null;
let errors = [];

const resolver = ({ values }) => {
    let requireList = [];
    requireList.push({val:work.value.name,name:"name"});
    requireList.push({val:work.value.myMeanDescription,name:"myMeanDescription"});
    requireList.push({val:work.value.gousiDescription,name:"gousiDescription"});
    errors = primeUtil.checkFormRequiredValid(requireList);
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
        workRest.updateBuyerWork({work:work.value},(res)=>{
            if (res.status=="OK") {
                dialog.alertBack("您的修改已经保存",()=>{
                    obj.work = work.value;
                    obj.returnFunction(obj);
                    mePage.close(mainPage);
                });
            }
        });
    }
};

const cancel = ()=>{
    mePage.close(mainPage);
};

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = lodash.cloneDeep(_obj);
    errors = null;
    work.value = obj.work;
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>