<template>
    <div class="card">
        <div class="flex flex-col items-center justify-center w-full">
            <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full">
                <IftaLabel>
                    <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">角色名称</label>
                    <InputText type="text" name="name" placeholder="请输入角色名称" class="w-full mb-4" v-model="rule.name" />
                </IftaLabel>
                <div>
                    <Tree v-model:selectionKeys="selectedKey" :value="model" selectionMode="checkbox" class="w-full md:w-[30rem]"></Tree>
                </div>
                <div class="row mt-12 center gap-4">
                    <Button type="submit" label="保存设置" class="px-8" _as="router-link" _to="/"></Button>
                    <Button severity="warn" label="取消" class="px-8" @click="cancel()"></Button>
                </div>
            </Form>
        </div>
    </div>
</template>

<script setup>
import { Beans } from '@/api/dbs/beans';
import { inject, onMounted, ref } from 'vue';
import primeUtil from '@/api/prime/util';
import userRest from '@/api/dbs/userRest';
import util from '@/api/util';
import lodash from 'lodash-es';
import pageJson from '@/datas/pageJson';

const rule = ref(Beans.rule());
const model = ref({});
const selectedKey = ref(null);

let errors = [];
let host = inject("domain");
let mainPage = null;
let mePage = null;
let obj = null;

onMounted(()=>{
    // buildMenu();
    // console.log(model.value);
});

const buildMenu = ()=>{
    model.value = [];
    let menuJson = pageJson.manageMenu();
    lodash.forEach(menuJson[0].items, (m) => {
        if (m.userType) {
            // console.log(userType,m.userType);
            if (lodash.findIndex(m.userType,(o)=>{return o=="manager"})>-1) {
                if (m.items) {
                    let mm = lodash.cloneDeep(m);
                    mm.items = null;
                    mm.children = [];
                    lodash.forEach(m.items,(sm)=>{
                        if (lodash.findIndex(sm.userType,(o)=>{return o=="manager"})>-1) {
                            mm.children.push(sm);
                        }
                    });
                    model.value.push(mm);
                } else {
                    model.value.push(m);
                }
            }
        } else {
            model.value.push(m);
        }
    });
}

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:rule.value.name,name:"name"}
    ]);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = async ({ valid }) => {
    if (valid) {
        if (obj.process=="c") {
            rule.value.rulePK.ruleId = Beans.buildPId("");
            rule.value.rulePK.appId = host;
            // console.log(selectedKey.value);
            lodash.forEach(selectedKey.value,(o,k)=>{
                // console.log(o,k);
                let ml = lodash.find(model.value,(m)=>{return k==m.key});
                if (ml && ml.children) {
                    o.pmenu = true;
                }
            });
            return;
        } else if (obj.process=="u") {

        }
        // let res = await userRest.updateRule({rule:rule.value},null);
        // if (res.status=="OK") {
        //     obj.returnFunction(obj);
        //     mePage.close(mainPage);
        // }
    }
};

function cancel() {
    mePage.close(mainPage);
}

const init = (_mainPage,_mePage,_obj)=>{
    mainPage = _mainPage;
    mePage = _mePage;
    obj = _obj;
    buildMenu();
    if (obj.process=="u") {
        rule.value = obj.data;
    } else if (obj.process=="c") {
        rule.value = Beans.rule();
    }
}

defineExpose({init});
</script>

<style scoped lang="scss">

</style>
