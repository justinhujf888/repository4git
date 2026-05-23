<template>
    <div class="card">
        <div class="start overflow-hidden">
            <div class="col center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                    <IftaLabel>
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">管理员名称</label>
                        <InputText name="name" placeholder="请输入管理员名称" class="w-full md:w-[30rem] mb-4" v-model="manager.name" />
                    </IftaLabel>
                    <IftaLabel>
                        <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-base mb-2">密码</label>
                        <Password name="password1" v-model="password1" placeholder="请输入密码" :toggleMask="true" fluid :feedback="false" class="w-full md:w-[30rem] mb-4"></Password>
                        <inputText :value="manager.password" class="hidden"/>
                    </IftaLabel>
                    <IftaLabel>
                        <label for="password2" class="block text-surface-900 dark:text-surface-0 font-medium text-base mb-2">再次输入密码</label>
                        <Password name="password2" v-model="password2" placeholder="请再次输入密码" :toggleMask="true" fluid :feedback="false" class="w-full md:w-[30rem] mb-4"></Password>
                        <Message v-if="password1!=password2" severity="error" size="small" variant="simple">两次密码输入不一致</Message>
                    </IftaLabel>
                    <IftaLabel>
                        <label for="rules" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">选择角色</label>
                        <div class="row card items-center gap-6 mt-5">
                            <div v-for="rule of ruleList" :key="rule.rulePK.ruleId" class="row items-center gap-2">
                                <Checkbox v-model="selRules" :inputId="rule.rulePK.ruleId" name="rule" :value="{ruleId:rule.rulePK.ruleId,ruleName:rule.name}" />
                                <label :for="rule.rulePK.ruleId" class="!static">{{ rule.name }}</label>
                            </div>
                            <inputText name="rules" :value="selRules" class="hidden"/>
                        </div>
                    </IftaLabel>
                    <IftaLabel>
                        <label for="description" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">备注</label>
                        <Textarea v-model="manager.description" autoResize rows="15" class="w-full" />
                    </IftaLabel>
                    <div class="row mt-12 center gap-4">
                        <Button type="submit" label="保存" class="px-8" _as="router-link" _to="/"></Button>
                        <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
                    </div>
                </Form>
            </div>
        </div>
    </div>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import { onMounted, ref, useTemplateRef, inject, nextTick } from 'vue';
import Page from '@/api/uniapp/page';
import { Config } from '@/api/config';
import primeUtil from "@/api/prime/util";
import {Beans} from "@/api/dbs/beans";
import userRest from "@/api/dbs/userRest";
import util from '@/api/util';
import dialog from '@/api/uniapp/dialog';
import checker from '@/api/check/checker';
import lodash from 'lodash-es';

const manager = ref(Beans.manager());
const password1 = ref('');
const password2 = ref('');
const ruleList = ref([]);
const selRules = ref([]);

let errors = [];
let host = inject("domain");
let mainPage = null;
let mePage = null;
let obj = null;

onMounted(async () => {

});

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:manager.value.name,name:"name"}
        // {val:src.value,name:"headImg",label:"照片"}
    ]);

    primeUtil.buildFormValidError(errors.password2,"error","两次密码输入不一致",()=>{return password1.value!=password2.value},(error)=>{errors.password2 = error});

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = async ({ valid }) => {
    if (valid) {
        if (obj.process=="c") {
            manager.value.managerPK.appId = host;
            manager.value.managerPK.managerId = Beans.buildPId("");
            manager.value.createDate = new Date().getTime();
        }
        if (password1.value && password2.value && password1.value.length > 0 && password2.value.length > 0) {
            // console.log(password1.value);
            manager.value.temp = true;
            manager.value.password = password1.value;
        }
        if (selRules.value) {
            if (!manager.value.tempMap) {
                manager.value.tempMap = {};
            }
            manager.value.tempMap.selRules = selRules.value;
        }
        // console.log(manager.value.tempMap.selRules);
        let res = await userRest.updateManager({manager:manager.value},null);
        if (res.status=="OK") {
            manager.value.tempMap.selRules = selRules.value;
            obj.data = manager.value;
            obj.returnFunction(obj);
            mePage.close(mainPage);
        }
    }
};

function cancel() {
    mePage.close(mainPage);
}

const init = async (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;

    let res = await userRest.queryRuleList({},null);
    ruleList.value = res.data;
    if (!ruleList.value) {
        ruleList.value = [];
    }
    selRules.value = [];

    if (obj.process=="c") {
        manager.value = Beans.manager();
    } else if (obj.process=="u") {
        manager.value = obj.data;
        // console.log(manager.value?.tempMap?.selRules);
        lodash.forEach(manager.value.tempMap?.selRules,(rp,k)=>{
            selRules.value.push({ruleId:rp.ruleId,ruleName:rp.ruleName})
        });
        // console.log(selRules.value);
    }
}
defineExpose({ init });
</script>

<style scoped lang="scss">

</style>
