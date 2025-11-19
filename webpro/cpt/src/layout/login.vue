<template>
    <div class="flex flex-col px-8 py-8 gap-6 rounded-2xl bg-gray-500" _style="background-image: radial-gradient(circle at left top, var(--p-primary-400), var(--p-primary-700))">
        <div class="inline-flex flex-col gap-2">
            <label for="username" class="text-primary-50 font-semibold">账号</label>
            <InputText v-model="managerId" id="username" class="!bg-white/20 !border-0 !p-4 !text-primary-50 w-80"></InputText>
        </div>
        <div class="inline-flex flex-col gap-2">
            <label for="password" class="text-primary-50 font-semibold">密码</label>
            <InputText v-model="password" id="password" class="!bg-white/20 !border-0 !p-4 !text-primary-50 w-80" type="password"></InputText>
        </div>
        <div class="flex items-center gap-4">
            <Button label="登录" variant="text" class="!p-4 w-full !text-primary-50 !border !border-white/30 hover:!bg-white/10"></Button>
<!--            <Button label="Cancel" variant="text" class="!p-4 w-full !text-primary-50 !border !border-white/30 hover:!bg-white/10"></Button>-->
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";
const managerId = ref("");
const password = ref("");
</script>

<style scoped lang="scss">

</style>