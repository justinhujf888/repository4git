<template>
    <animationPage :show="true" class="w-full absolute top-0 z-40">
        <div class="card !p-2">
            <div class="col center w-full p-2">
                <Form v-slot="$form" :resolver @submit="onFormSubmit" class="lg:w-4/5 w-full grid gap-x-2 gap-y-4">
                    <IftaLabel>
                        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2 z-30">年份赛事</label>
                        <Select name="name" v-model="masterCompetition" :options="masterCompetitionList" optionLabel="name" placeholder="选择要发布的年份赛事" fluid />
                    </IftaLabel>

                    <div class="row mt-12 center gap-4">
                        <Button type="submit" label="提交" class="px-8" _as="router-link" _to="/"></Button>
                    </div>
                </Form>
            </div>
        </div>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import {inject, onMounted, reactive, ref} from "vue";
import workRest from '@/api/dbs/workRest';
import primeUtil from "@/api/prime/util";
import dialog from "@/api/uniapp/dialog";
import {Beans} from "@/api/dbs/beans";

const masterCompetitionList = ref([]);
const masterCompetition = ref(Beans.masterCompetition());

let host = inject("domain");
let errors = [];

onMounted(() => {
    workRest.qyMasterSiteCompetition({siteCompetitionId:host},(res)=>{
        if (res.status=="OK") {
            if (res.data) {
                masterCompetitionList.value = res.data;
            }
        }
    });
});

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:masterCompetition.value.name,name:"name"}
    ]);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        workRest.buildCacheCpt({siteCompetitionId:host,masterCompetitionId:masterCompetition.value.id},(res)=>{
            if (res.status=="OK") {
                dialog.toastSuccess("赛事已成功发布");
            }
        });
    }
};
</script>

<style scoped lang="scss">

</style>