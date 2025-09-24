<template>
    <animationPage>
        <div class="card">

        </div>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import {inject, onMounted, ref} from "vue";
import {useStorage} from "@vueuse/core";

const userId = useStorage("userId");
const workList = ref([]);

let host = inject("domain");

onMounted(() => {
    workRest.qyWorks({appId:host,userId:userId.value,masterCompetitionId:"2025"},(res)=>{
        if (res.status=="OK") {
            if (res.data) {
                workList.value = res.data;
                // console.log(masterCompetitionList.value);
            }
        }
    });
});
</script>

<style scoped lang="scss">

</style>