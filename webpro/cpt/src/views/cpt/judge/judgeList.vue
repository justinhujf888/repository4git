<template>
    <div>

    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import dialog from '@/api/uniapp/dialog';
import userRest from "@/api/dbs/userRest";

const judgeList = ref([]);

let pageSize = 30;
let currentPage = 0;

onMounted(() => {
    userRest.queryJudgeList({},pageSize,currentPage,(data)=>{
        if (data.status=="OK") {
            judgeList.value = data.judgeList;
        }
    });
});
</script>

<style scoped lang="scss">

</style>
