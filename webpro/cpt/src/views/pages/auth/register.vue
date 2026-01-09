<template>
<!--    <FloatingConfigurator />-->
<!--    <Button label="test" @click="test"/>-->
    <div class="overflow-hidden">
        <Form v-slot="$form" :resolver @submit="onFormSubmit" class="grid gap-y-4">
            <IftaLabel>
                <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium">手机号码</label>
                <InputMask name="phone" mask="99999999999" placeholder="请输入手机号码" class="w-full" v-model="buyer.phone" />
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

            <div class="flex justify-end gap-2 mt-5 !border-btn">
                <Button type="submit" label="注册" _as="router-link" _to="/" class="!bg-green-600 !rounded-full"></Button>
                <Button severity="warn" label="返回" class="!bg-orange-500 !border-0 !rounded-full" _as="router-link" _to="/" @click="cancel"></Button>
            </div>
        </Form>
    </div>
</template>

<script setup>
import dialog from '@/api/uniapp/dialog';
import {Beans} from "@/api/dbs/beans";
import userRest from "@/api/dbs/userRest";
import otherRest from "@/api/dbs/otherRest";
import primeUtil from "@/api/prime/util";
// import FloatingConfigurator from '@/components/FloatingConfigurator.vue';
import {ref, shallowRef, onMounted} from 'vue';
import { useCountdown,useStorage } from '@vueuse/core';
import Page from "@/api/uniapp/page";
import util from "@/api/util";
import checker from "@/api/check/checker";
import useGlobal from "@/api/hooks/useGlobal";
const emit = defineEmits(["afterLogin","cancel"]);
const siteDatas = ref(null);
(async ()=>{
    siteDatas.value = await useGlobal.siteDatas();
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
let loginToken = "";

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
                loginToken = data.loginToken;
                useStorage("userId",buyer.value.phone);
                useStorage("loginToken",loginToken);
                dialog.alertBack("您已成功注册",()=>{
                    afterLogin();
                });
            } else if (data.status=="ER_HAS") {
                dialog.toastError("此账号已被注册");
            }
        });
    }
};

const afterLogin = ()=>{
    emit("afterLogin",buyer.value.phone,loginToken);
}
const cancel = ()=>{
    emit("cancel");
}

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
