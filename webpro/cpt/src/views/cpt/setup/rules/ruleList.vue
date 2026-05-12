<template>
    <animationPage ref="mainPage" :show="true">
        <div class="card">
            <Button label="新增角色" @click="refUpdateRule.init(mainPage,updateRulePage,{process:'c'});updateRulePage.open(mainPage)"/>
        </div>
    </animationPage>

    <animationPage ref="updateRulePage" _class="w-full absolute top-0 z-40">
        <update-rule ref="refUpdateRule"/>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import { onMounted, useTemplateRef } from 'vue';
import userRest from '@/api/dbs/userRest';
import UpdateRule from '@/views/cpt/setup/rules/updateRule.vue';

const mainPage = useTemplateRef("mainPage");
const updateRulePage = useTemplateRef("updateRulePage");
const refUpdateRule = useTemplateRef("refUpdateRule");

onMounted(async () => {
    let res = await userRest.queryRuleList({},null);
    console.log(res);
});
</script>

<style scoped lang="scss">

</style>
