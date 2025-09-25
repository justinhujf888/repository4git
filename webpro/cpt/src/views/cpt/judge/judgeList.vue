<template>
    <div class="relative">
        <animationPage ref="mainPage" :show="true">
            <div class="card">
                <div class="flex flex-wrap items-center justify-between">
                    <span class="text-base">评委资料管理</span>
                    <Button label="新增评委资料" @click="refUpdateJudge.init(mainPage,updateJudgePage,{process:'c',returnFunction:returnFunction});updateJudgePage.open(mainPage)"/>
                </div>
                <DataTable :value="judgePageUtil.content" header="Flex Scroll" resizableColumns showGridlines stripedRows paginator :rows="Config.pageSize" :totalRecords="judgePageUtil.totalElements" :first="judgePageUtil.number" :lazy="true" tableStyle="min-width: 50rem" paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport" currentPageReportTemplate="【{first} - {last}】  记录总数：{totalRecords}" :pageLinkSize="5" @page="updateFirst" :pt="
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
                    <Column field="headImgUrl" header="" class="w-10">
                        <template #body="{ data,index }">
                            <div class="center w-16">
                                <img :alt="data.name" :src="data.tempMap.imgPath" class="rounded-full w-16 h-16 object-content" @click="refPriviewImage.imagesShow(judgePageUtil.content,index)"/>
                            </div>
                        </template>
                    </Column>
                    <Column field="name" header="姓名" class="w-36"></Column>
                    <Column field="phone" header="手机号码" class="w-36"></Column>
                    <!--            <Column field="engName" header="Eng Name"></Column>-->
                    <Column header="简介">
                        <template #body="{data}">
                            <p class="truncate w-full text-start text-wrap">{{data.description}}</p>
                        </template>
                    </Column>
                    <Column class="w-16 !text-end">
                        <template #body="{ data,index }">
                            <Button icon="pi pi-pencil" @click="edit(data,index)" severity="secondary" rounded></Button>
                        </template>
                    </Column>
                </DataTable>
                <priviewImage ref="refPriviewImage" v-if="judgePageUtil.content?.length>0" :files="judgePageUtil.content" :shiShowImgGrid="false" _class="hidden"/>
            </div>
        </animationPage>
        <animationPage ref="updateJudgePage">
            <updateJudge ref="refUpdateJudge"/>
        </animationPage>
    </div>
</template>

<script setup>
import { onMounted, ref, useTemplateRef } from 'vue';
import animationPage from "@/components/my/animationPage.vue";
import updateJudge from "@/views/cpt/judge/updateJudge.vue";
import priviewImage from "@/components/my/priviewImage.vue";
import dialog from '@/api/uniapp/dialog';
import userRest from "@/api/dbs/userRest";
import {Config} from "@/api/config";
import lodash from "lodash";
import oss from "@/api/oss";

const mainPage = useTemplateRef("mainPage");
const updateJudgePage = useTemplateRef("updateJudgePage");
const refUpdateJudge = useTemplateRef("refUpdateJudge");
const refPriviewImage = useTemplateRef("refPriviewImage");

const judgePageUtil = ref({});
const currentPage = ref(0);

onMounted(() => {
    loadDatas();
});

function loadDatas() {
    userRest.queryJudgeList({pageSize:Config.pageSize,currentPage:currentPage.value},(res)=>{
        if (res.status=="OK") {
            if (res.data!=null) {
                judgePageUtil.value = res.data;
                lodash.forEach(judgePageUtil.value.content,(v)=>{
                    v.tempMap = {};
                    v.tempMap.imgPath = oss.buildImgPath(v.headImgUrl);
                });
                // console.log(judgePageUtil.value);
            }
        }
    });
}

function updateFirst(e) {
    console.log(e);
    currentPage.value = e.page;
    loadDatas();
}

function edit(data,index) {
    refUpdateJudge.value.init(mainPage.value,updateJudgePage.value,{data:data,process:'u',index:index,returnFunction:returnFunction});
    updateJudgePage.value.open(mainPage.value);
}

function returnFunction(obj) {
    if (obj.process=="c") {
        judgePageUtil.value.content.push(obj.data);
        dialog.toastSuccess(`${obj.data.name}的资料已更新`);
    } else if (obj.process=="u") {
        judgePageUtil.value.content[obj.index] = obj.data;
        dialog.toastSuccess(`${obj.data.name}的资料已更新`);
    }
}
</script>

<style scoped lang="scss">

</style>
