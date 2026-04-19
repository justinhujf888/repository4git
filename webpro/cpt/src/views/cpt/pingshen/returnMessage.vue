<template>
    <div class="card text-xl h-auto">
        <div class="col center w-full p-2">
            <Form v-slot="$form" :resolver @submit="onFormSubmit" class="w-full grid gap-x-2 gap-y-4">
                <FloatLabel variant="on">
                    <label for="log" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">作品{{work?.name}}回执信息</label>
                    <Textarea name="log" v-model="workLog.log" autoResize rows="8" class="w-full"/>
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

import {ref} from "vue";
import {Beans} from "@/api/dbs/beans";
import primeUtil from "@/api/prime/util";
import workRest from "@/api/dbs/workRest";
import dialog from "@/api/uniapp/dialog";
import lodash from "lodash-es";

const workLog = ref(Beans.workLog());
const work = ref(Beans.work());

let mainPage = null;
let mePage = null;
let obj = null;
let errors = [];

const resolver = ({ values }) => {
    let requireList = [];
    requireList.push({val:workLog.value.log,name:"log"});
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
        workRest.saveWorkLog({workLog:workLog.value},(res)=>{
            if (res.status=="OK") {
                dialog.alertBack("信息已经保存",()=>{
                    obj.workLog = workLog.value;
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
    if (obj.process=="c") {
        workLog.value = Beans.workLog();
        workLog.value.id = Beans.buildPId("log");
        workLog.value.workId = work.value.id;
        workLog.value.appId = work.value.appId;
    } else if (obj.process=="u") {
        workLog.value = obj.workLog;
    }
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>