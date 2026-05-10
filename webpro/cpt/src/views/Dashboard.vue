<script setup>
import BestSellingWidget from '@/components/dashboard/BestSellingWidget.vue';
import NotificationsWidget from '@/components/dashboard/NotificationsWidget.vue';
import RecentSalesWidget from '@/components/dashboard/RecentSalesWidget.vue';
import RevenueStreamWidget from '@/components/dashboard/RevenueStreamWidget.vue';
import StatsWidget from '@/components/dashboard/StatsWidget.vue';
// import {OSS} from "@/api/oss.mjs";
// import OSS from "ali-oss";
const clear = ()=>{
    localStorage.removeItem("managerId");
    localStorage.removeItem("managerInfo");
    localStorage.removeItem("userType");
};
</script>

<template>
<!--    <div class="grid grid-cols-12 gap-8">-->
<!--        <StatsWidget />-->

<!--        <div class="col-span-12 xl:col-span-6">-->
<!--            <RecentSalesWidget />-->
<!--            <BestSellingWidget />-->
<!--        </div>-->
<!--        <div class="col-span-12 xl:col-span-6">-->
<!--            <RevenueStreamWidget />-->
<!--            <NotificationsWidget />-->
<!--        </div>-->
<!--    </div>-->
<!--    <Button label="清除缓存" @click="clear"/>-->
</template>
