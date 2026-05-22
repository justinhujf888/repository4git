<template>
    <animationPage ref="mainPage" :show="true">
        <div class="card">
            <Button label="新增管理员" @click="refUpdateManager.init(mainPage,updateManagerPage,{process:'c',returnFunction:returnFunction});updateManagerPage.open(mainPage)"/>
            <DataTable :value="managerList" header="Flex Scroll" resizableColumns showGridlines stripedRows  tableStyle="min-width: 50rem" :pt="
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
                <Column field="name" header="管理员名称" class="w-36"></Column>
                <Column header="权限">
                    <template #body="{data}">
                        <div class="row gap-2 flex-wrap">
                            <div v-for="qx of data?.ruleJson">
                                <Chip v-if="!qx.pmenu" :label="qx.label"/>
                            </div>
                        </div>
                    </template>
                </Column>
                <Column class="w-16 !text-end">
                    <template #body="{ data,index }">
                        <div class="row gap-4">
                            <Button icon="pi pi-pencil" @click="edit(data,index)" severity="secondary" rounded></Button>
                            <Button icon="pi pi-trash" @click="del(data,index)" severity="secondary" rounded></Button>
                        </div>
                    </template>
                </Column>
            </DataTable>
        </div>
    </animationPage>

    <animationPage ref="updateManagerPage" _class="w-full absolute top-0 z-40">
        <update-manager ref="refUpdateManager"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import { onMounted, ref, useTemplateRef } from 'vue';
import userRest from '@/api/dbs/userRest';
import dialog from '@/api/uniapp/dialog';
import { Config } from '@/api/config';
import UpdateManager from '@/views/cpt/setup/manager/updateManager.vue';

const mainPage = useTemplateRef("mainPage");
const updateManagerPage = useTemplateRef("updateManagerPage");
const refUpdateManager = useTemplateRef("refUpdateManager");

const managerList = ref([]);

onMounted(async () => {
    let res = await userRest.queryManagerList({},null);
    managerList.value = res.data;
    if (!managerList.value) {
        managerList.value = [];
    }
});

const edit = (data,index)=>{
    refUpdateManager.value.init(mainPage.value,updateManagerPage.value,{data:data,process:'u',index:index,returnFunction:returnFunction});
    updateManagerPage.value.open(mainPage.value);
};

const del = async (data,index)=>{
    dialog.confirm("确定删除这个管理员吗？",async ()=>{
        let res = await userRest.deleteManager({managerId:data.managerPK.managerId},null);
        if (res.status=="OK") {
            managerList.value.splice(index,1);
            dialog.toastSuccess(`管理员${data.name}已删除`)
        }
    },null);
};

const returnFunction = (obj)=>{
    if (obj.process=="c") {
        managerList.value.push(obj.data);
        dialog.toastSuccess(`${obj.data.name}信息已更新`);
    } else if (obj.process=="u") {
        managerList.value[obj.index] = obj.data;
        dialog.toastSuccess(`${obj.data.name}信息已更新`);
    }
}
</script>

<style scoped lang="scss">

</style>
