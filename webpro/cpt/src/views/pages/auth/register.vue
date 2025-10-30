<template>
<!--    <FloatingConfigurator />-->
<!--    <Button label="test" @click="test"/>-->
    <div class="bg-surface-500 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-[100vw] overflow-hidden">
        <div class="flex flex-col items-center justify-center">
            <div _style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                <div class="xs:w-dvw xs:h-dvh md:w-full md:h-auto dark:bg-surface-900 py-20 px-8 sm:px-20 bg-surface-900" _style="border-radius: 53px">
                    <div class="text-center mb-8">
                        <div class="text-surface-50 dark:text-surface-0 text-3xl font-medium mb-4">{{siteDatas?.siteInfo.siteCompetition.name}}</div>
                        <span class="text-muted-color font-medium">注册</span>
                    </div>

                    <Form v-slot="$form" :resolver @submit="onFormSubmit" class="grid gap-y-4">
                        <IftaLabel>
                            <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium">手机号码</label>
                            <InputMask name="phone" mask="99999999999" placeholder="请输入手机号码" class="w-full md:w-[30rem]" v-model="buyer.phone" />
                            <Message v-if="$form.phone?.invalid && $form.phone.error?.type=='error'" severity="error" size="small" variant="simple">{{ $form.phone.error?.message}}</Message>
                        </IftaLabel>

                        <IftaLabel>
                            <label for="vcord" class="block text-surface-900 dark:text-surface-0 text-base font-medium z-30">验证码</label>
                            <InputGroup>
                                <InputText name="vcord" v-model="vcord" placeholder="请输入验证码" />
                                <div :class="btnDisabled ? '' : 'hidden'">
                                    <InputGroupAddon>{{remaining}}s</InputGroupAddon>
                                </div>
                                <Button label="发送验证码" severity="secondary" :disabled="btnDisabled" @click="startCountdown"/>
                            </InputGroup>
                        </IftaLabel>

                        <IftaLabel>
                            <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-base z-30">密码</label>
                            <Password name="password1" v-model="buyer.password" placeholder="请输入密码" :toggleMask="true" fluid :feedback="false"></Password>
                        </IftaLabel>
                        <IftaLabel>
                            <label for="password2" class="block text-surface-900 dark:text-surface-0 font-medium text-base z-30">再次输入密码</label>
                            <Password name="password2" v-model="password" placeholder="请再次输入密码" :toggleMask="true" fluid :feedback="false"></Password>
                            <Message v-if="buyer.password!=password" severity="error" size="small" variant="simple">两次密码输入不一致</Message>
                        </IftaLabel>

                        <div class="row mt-12">
                            <Button type="submit" label="注册" class="w-full" _as="router-link" _to="/"></Button>
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
import dialog from '@/api/uniapp/dialog';
import {Beans} from "@/api/dbs/beans";
import userRest from "@/api/dbs/userRest";
import otherRest from "@/api/dbs/otherRest";
import primeUtil from "@/api/prime/util";
import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import {ref, shallowRef, onMounted, getCurrentInstance} from 'vue';
import { useCountdown,useStorage } from '@vueuse/core';
import Page from "@/api/uniapp/page";
import util from "@/api/util";
import checker from "@/api/check/checker";

const siteDatas = ref(null);
const {proxy} = getCurrentInstance();
(async ()=>{
    siteDatas.value = await proxy.$getSiteDatas();
})();
const remaining = ref(0);
const buyer = ref(Beans.buyer());
const password = ref('');
const vcord = ref('');
const btnDisabled = ref(false);

let errors = [];
let totalSeconds = 180;
let countdownSeconds = 180;

let countDown = null;
// let { remaining, start, stop, pause, resume } = useCountdown(countdownSeconds, {
//     onComplete() {
//         btnDisabled.value = false;
//     },
//     onTick() {
//         useStorage("totalSeconds",remaining.value);
//     },
// });

let phoneCode = "";

onMounted(()=>{
    let shiRun = false;
    // console.log(localStorage.getItem("totalSeconds"));
    if (localStorage.getItem("totalSeconds")) {
        totalSeconds = parseInt(util.decryptStoreInfo(localStorage.getItem("totalSeconds")));
        btnDisabled.value = true;
        shiRun = true;
    }
    // console.log(totalSeconds);
    countdownSeconds = shallowRef(totalSeconds);
    countDown = useCountdown(countdownSeconds, {
        onComplete() {
            localStorage.removeItem("totalSeconds");
            btnDisabled.value = false;
        },
        onTick() {
            remaining.value = countDown.remaining.value;
            localStorage.setItem("totalSeconds",util.encryptStoreInfo(`${remaining.value}`));
        },
    });
    if (shiRun) {
        countDown.start(countdownSeconds);
    }
});

const startCountdown = ()=>{
    if (!buyer.value.phone || buyer.value.phone=="") {
        dialog.toastError("请输入手机号码");
        return;
    }

    (async ()=>{

        await new Promise((resolve,reject) => {
            userRest.buyerLogin(buyer.value.phone,buyer.value.password,(data)=>{
                if (data.status=="OK") {
                    dialog.toastError("您输入的账号已被注册");
                } else if (data.status=="ER_NOHAS") {
                    resolve();
                } else if (data.status=="ER_PW") {
                    dialog.toastError("您输入的账号已被注册");
                }
            });
        });

        await new Promise((resolve,reject) => {
            otherRest.sendSmsPublic(buyer.value.phone,"regist",JSON.stringify({code:vcord.value}),(data)=>{
                // console.log(data);
                if (data.status=="OK") {
                    phoneCode = data.smsInfo.templateParam;
                    totalSeconds = 180;
                    btnDisabled.value = true;
                    countdownSeconds = shallowRef(totalSeconds);
                    countDown.start(countdownSeconds);
                    resolve();
                }
            });
        });
    })();
};

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:buyer.value.phone,name:"phone"},
        {val:vcord.value,name:"vcord"},
        {val:buyer.value.password,name:"password1"},
        {val:password.value,name:"password2"}
    ]);

    primeUtil.buildFormValidError(errors.phone,"error","手机格式错误",()=>{
        return !checker.check({"phone":buyer.value.phone},[{name:"phone",checkType:"phone",checkRule:"",errorMsg:"手机格式错误"}]);
    },(error)=>{errors.phone = error});
    primeUtil.buildFormValidError(errors.password2,"error","两次密码输入不一致",()=>{return buyer.value.password!=password.value},(error)=>{errors.password2 = error});

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};

const onFormSubmit = ({ valid }) => {
    if (valid) {
        let pi = JSON.parse(util.decryptStoreInfo(phoneCode));
        if (pi.code!=vcord.value) {
            dialog.toastError("验证码输入错误");
            return;
        }
        userRest.registBuyer(buyer.value,(data)=>{
            if (data.status=="OK") {
                useStorage("userId",buyer.value.phone);
                useStorage("loginToken",data.loginToken);
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
    // Page.navigateTo("table",{});
    countDown.start();
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
