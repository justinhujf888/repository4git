<template>
    <Form v-slot="$form" :resolver @submit="onFormSubmit">
        <div class="flex flex-col px-8 py-8 gap-6 rounded-2xl bg-gray-500" _style="background-image: radial-gradient(circle at left top, var(--p-primary-400), var(--p-primary-700))">
            <div class="inline-flex flex-col gap-2">
                <label for="username" class="text-primary-50 font-semibold">账号</label>
                <InputText v-model="managerId" name="username" class="!bg-white/20 !border-0 !p-4 !text-primary-50 w-80"></InputText>
                <Message v-if="$form.username?.invalid" severity="error" size="small" variant="simple">{{ $form.username.error?.message}}</Message>
            </div>
            <div class="inline-flex flex-col gap-2">
                <label for="password" class="text-primary-50 font-semibold">密码</label>
                <InputText v-model="password" name="password" class="!bg-white/20 !border-0 !p-4 !text-primary-50 w-80" type="password"></InputText>
                <Message v-if="$form.password?.invalid" severity="error" size="small" variant="simple">{{ $form.password.error?.message}}</Message>
            </div>
            <div class="flex items-center gap-4">
                <Button type="submit" label="登录" variant="text" class="!p-4 w-full !text-primary-50 !border !border-white/30 hover:!bg-white/10"></Button>
                <!--            <Button label="Cancel" variant="text" class="!p-4 w-full !text-primary-50 !border !border-white/30 hover:!bg-white/10"></Button>-->
            </div>
        </div>
    </Form>
</template>

<script setup>
import { inject, ref } from 'vue';
import primeUtil from '@/api/prime/util';
import userRest from '@/api/dbs/userRest';
import { useStorage } from '@vueuse/core';
import dialog from '@/api/uniapp/dialog';
import Page from '@/api/uniapp/page';
import util from '@/api/util';

const emit = defineEmits(["afterLogin"]);
const managerId = ref("");
const password = ref("");

let errors = [];
let host = inject("domain");

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:managerId.value,name:"username",label:"登录账号"},
        {val:password.value,name:"password",label:"密码"}
    ]);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        userRest.managerLogin({managerId:managerId.value,password:password.value},(data)=>{
            if (data.status=="OK") {
                useStorage("managerId",managerId.value);
                util.intoStorgeCry("managerInfo",JSON.stringify(data.manager));
                emit("afterLogin",data.manager);
            } else if (data.status=="ER_NOHAS") {
                dialog.toastError("您输入的账号或密码错误");
            } else if (data.status=="ER_PW") {
                dialog.toastError("您输入的账号或密码错误");
            }
        });
    }
};
</script>

<style scoped lang="scss">

</style>
