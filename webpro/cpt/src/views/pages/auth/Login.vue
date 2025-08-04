<template>
<!--    <FloatingConfigurator />-->
    <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-[100vw] overflow-hidden">
        <div class="flex flex-col items-center justify-center">
            <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px">
                    <div class="text-center mb-8">
                        <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">{{Config.appName}}</div>
                        <span class="text-muted-color font-medium">登录</span>
                    </div>

                    <Form v-slot="$form" :resolver @submit="onFormSubmit">
                        <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">手机号码</label>
                        <InputMask name="phone" mask="99999999999" placeholder="请输入手机号码" class="w-full md:w-[30rem] mb-4" v-model="buyer.phone" />
                         <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-base mb-2 mt-4">密码</label>
                        <Password name="password1" v-model="buyer.password" placeholder="请输入密码" :toggleMask="true" class="mb-4" fluid :feedback="false"></Password>
                        <div class="flex items-center justify-end mt-2 mb-8 gap-8">
                            <router-link :to="{name:'forgotpw'}">
                                <span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary">忘记密码</span>
                            </router-link>
                        </div>

                        <div class="row mt-12">
                            <Button type="submit" label="登录" class="w-full" _as="router-link" _to="/"></Button>
                        </div>
                        <div class="row mt-4">
                            <Button severity="warn" label="返回" class="w-full !bg-orange-400 !border-0" _as="router-link" _to="/" @click="Page.navBack()"></Button>
                        </div>
                    </Form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import { ref } from 'vue';
import {Config} from "@/api/config";
import {Beans} from "@/api/dbs/beans";
import userRest from "@/api/dbs/userRest";
import primeUtil from "@/api/prime/util";
import Page from "@/api/uniapp/page";
import dialog from '@/api/uniapp/dialog';
import { useStorage } from '@vueuse/core';

const buyer = ref(Beans.buyer());
const password = ref('');

let errors = [];

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:buyer.value.phone,name:"phone"},
        {val:buyer.value.password,name:"password1"}
    ]);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        userRest.buyerLogin(buyer.value.phone,buyer.value.password,(data)=>{
            if (data.status=="OK") {
                useStorage("userId",buyer.value.phone);
                useStorage("loginToken",data.loginToken);
                dialog.alertBack("您已成功登录",()=>{
                    Page.redirectTo("landing",null);
                });
            } else if (data.status=="ER_NOHAS") {
                dialog.toastError("您输入的账号不存在");
            } else if (data.status=="ER_PW") {
                dialog.toastError("您输入的密码错误");
            }
        });
    }
};

</script>

<style scoped>
.pi-eye {
    transform: scale(1.6);
    margin-right: 1rem;
}

.pi-eye-slash {
    transform: scale(1.6);
    margin-right: 1rem;
}
</style>
