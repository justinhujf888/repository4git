<template>
<!--    <FloatingConfigurator />-->
    <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-[100vw] overflow-hidden">
        <div class="flex flex-col items-center justify-center">
            <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px">
                    <div class="text-center mb-8">
                        <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">Welcome to {{Config.appName}}</div>
                        <span class="text-muted-color font-medium">Sign in to continue</span>
                    </div>

                    <div>
                        <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">手机号码</label>
                        <InputMask id="phone" mask="99999999999" placeholder="请输入手机号码" class="w-full md:w-[30rem] mb-4" v-model="buyer.phone" />
                        <label for="vcord" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">验证码</label>
                        <InputGroup>
                            <InputNumber id="vcord" v-model="vcord" placeholder="请输入验证码" />
                            <div :class="btnDisabled ? '' : 'hidden'">
                                <InputGroupAddon>{{remaining}}s</InputGroupAddon>
                            </div>
                            <Button label="发送验证码" severity="secondary" :disabled="btnDisabled" @click="startCountdown"/>
                        </InputGroup>

                        <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-base mb-2 mt-8">密码</label>
                        <Password id="password1" v-model="buyer.password" placeholder="请输入密码" :toggleMask="true" class="mb-4" fluid :feedback="false"></Password>

                        <label for="password2" class="block text-surface-900 dark:text-surface-0 font-medium text-base mb-2">再次输入密码</label>
                        <Password id="password2" v-model="password" placeholder="请再次输入密码" :toggleMask="true" class="mb-4" fluid :feedback="false"></Password>

                        <div class="row mt-12">
                            <Button label="Sign In" class="w-full" as="router-link" to="/"></Button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import dialog from '@/api/uniapp/dialog';
import {Config} from "@/api/config";
import {Beans} from "@/api/dbs/beans"
import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import { ref,shallowRef  } from 'vue';
import { useCountdown } from '@vueuse/core';

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
        dialog.toastNone("请输入手机号码");
        return;
    }
    btnDisabled.value = true;
    start(countdownSeconds);
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
