<template>
    <animationPage ref="mainPage" :show="true">
        <div class="card">
            <Button label="新增角色" @click="refUpdateRule.init(mainPage,updateRulePage,{process:'c',returnFunction:returnFunction});updateRulePage.open(mainPage)"/>
            <DataTable :value="ruleList" header="Flex Scroll" resizableColumns showGridlines stripedRows  tableStyle="min-width: 50rem" :pt="
            {
                table:{
                    class:'min-w-full mt-5 !w-full'
                },
                column:{
                    bodyCell:{class:'!text-center'},
                    columnHeaderContent:{class:'justify-self-center'}
                },
                pcPaginator:{

                }
            }">
                <Column field="name" header="角色名称" class="w-36"></Column>
                <Column header="权限">
                    <template #body="{data}">
                        <div class="row gap-2 flex-wrap">
                            <Chip v-for="qx of data?.ruleJson" :label="qx.label"/>
                        </div>
                    </template>
                </Column>
                <Column class="w-16 !text-end">
                    <template #body="{ data,index }">
                        <Button icon="pi pi-pencil" @click="edit(data,index)" severity="secondary" rounded></Button>
                    </template>
                </Column>
            </DataTable>
        </div>
    </animationPage>

    <animationPage ref="updateRulePage" _class="w-full absolute top-0 z-40">
        <update-rule ref="refUpdateRule"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import { onMounted, ref, useTemplateRef } from 'vue';
import userRest from '@/api/dbs/userRest';
import UpdateRule from '@/views/cpt/setup/rules/updateRule.vue';
import dialog from '@/api/uniapp/dialog';
import { Config } from '@/api/config';

const mainPage = useTemplateRef("mainPage");
const updateRulePage = useTemplateRef("updateRulePage");
const refUpdateRule = useTemplateRef("refUpdateRule");

const ruleList = ref([]);

onMounted(async () => {
    let res = await userRest.queryRuleList({},null);
    ruleList.value = res.data;
    if (!ruleList.value) {
        ruleList.value = [];
    }
});

const edit = (data,index)=>{
    refUpdateRule.value.init(mainPage.value,updateRulePage.value,{data:data,process:'u',index:index,returnFunction:returnFunction});
    updateRulePage.value.open(mainPage.value);
}

const returnFunction = (obj)=>{
    if (obj.process=="c") {
        ruleList.value.push(obj.data);
        dialog.toastSuccess(`${obj.data.name}的权限已更新`);
    } else if (obj.process=="u") {
        ruleList.value[obj.index] = obj.data;
        dialog.toastSuccess(`${obj.data.name}的权限已更新`);
    }
}
</script>

<style scoped lang="scss">

</style>
