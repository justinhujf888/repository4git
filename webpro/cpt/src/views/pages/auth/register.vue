<template>
<!--    <FloatingConfigurator />-->
<!--    <Button label="test" @click="test"/>-->
    <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-[100vw] overflow-hidden">
        <div class="flex flex-col items-center justify-center">
            <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px">
                    <div class="text-center mb-8">
                        <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">{{Config.appName}}</div>
                        <span class="text-muted-color font-medium">注册</span>
                    </div>

                    <Form v-slot="$form" :resolver @submit="onFormSubmit">
                        <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">手机号码</label>
                        <InputMask name="phone" mask="99999999999" placeholder="请输入手机号码" class="w-full md:w-[30rem] mb-4" v-model="buyer.phone" />
                        <label for="vcord" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">验证码</label>
                        <InputGroup>
                            <InputText name="vcord" v-model="vcord" placeholder="请输入验证码" />
                            <div :class="btnDisabled ? '' : 'hidden'">
                                <InputGroupAddon>{{remaining}}s</InputGroupAddon>
                            </div>
                            <Button label="发送验证码" severity="secondary" :disabled="btnDisabled" @click="startCountdown"/>
                        </InputGroup>

                        <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-base mb-2 mt-8">密码</label>
                        <Password name="password1" v-model="buyer.password" placeholder="请输入密码" :toggleMask="true" class="mb-4" fluid :feedback="false"></Password>
                        <label for="password2" class="block text-surface-900 dark:text-surface-0 font-medium text-base mb-2">再次输入密码</label>
                        <Password name="password2" v-model="password" placeholder="请再次输入密码" :toggleMask="true" class="mb-4" fluid :feedback="false"></Password>
                        <Message v-if="buyer.password!=password" severity="error" size="small" variant="simple">两次密码输入不一致</Message>

                        <div class="row mt-12">
                            <Button type="submit" label="注册" class="w-full" _as="router-link" _to="/"></Button>
                        </div>

                        <div class="row mt-4">
                            <Button severity="warn" label="返回" class="w-full" _as="router-link" _to="/" @click="Page.navBack()"></Button>
                        </div>
                    </Form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import dialog from '@/api/uniapp/dialog';
import {Config} from "@/api/config";
import {Beans} from "@/api/dbs/beans";
import userRest from "@/api/dbs/userRest";
import primeUtil from "@/api/prime/util";
import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import { ref,shallowRef  } from 'vue';
import { useCountdown,useStorage } from '@vueuse/core';
import Page from "@/api/uniapp/page";

let errors = [];
const countdownSeconds = shallowRef(180);
const { remaining, start, stop, pause, resume } = useCountdown(countdownSeconds, {
    onComplete() {
        btnDisabled.value = false;
    },
    onTick() {

    },
})

const buyer = ref(Beans.buyer());
const password = ref('');
const vcord = ref('');
const btnDisabled = ref(false);

const startCountdown = ()=>{
    if (!buyer.value.phone || buyer.value.phone=="") {
        dialog.toastError("请输入手机号码");
        return;
    }
    btnDisabled.value = true;
    start(countdownSeconds);
};

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:buyer.value.phone,name:"phone"},
        {val:vcord.value,name:"vcord"},
        {val:buyer.value.password,name:"password1"},
        {val:password.value,name:"password2"}
    ]);

    primeUtil.buildFormValidError(errors.password2,"error","两次密码输入不一致",()=>{return buyer.value.password!=password.value},(error)=>{errors.password2 = error});

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        userRest.registBuyer(buyer.value,(data)=>{
            if (data.status=="OK") {
                useStorage("userId",buyer.value.phone);
                dialog.alertBack("您已成功注册",()=>{
                    Page.redirectTo("landing",null);
                });
            } else if (data.status=="ER_HAS") {
                dialog.toastError("此账号已被注册");
            }
        });
    }
};

function test() {
    Page.navigateTo("table",{});
}
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
