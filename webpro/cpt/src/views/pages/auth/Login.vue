<template>
<!--    <FloatingConfigurator />-->
    <div class="overflow-hidden">
        <Form v-slot="$form" :resolver @submit="onFormSubmit" class="grid gap-y-4">
            <IftaLabel>
                <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium">手机号码</label>
                <InputMask name="phone" mask="99999999999" placeholder="请输入手机号码" class="w-full" v-model="buyer.phone" />
                <Message v-if="$form.phone?.invalid && $form.phone.error?.type=='error'" severity="error" size="small" variant="simple">{{ $form.phone.error?.message}}</Message>
            </IftaLabel>

            <IftaLabel>
                <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-base z-30">密码</label>
                <Password name="password1" v-model="buyer.password" placeholder="请输入密码" :toggleMask="true" fluid :feedback="false"></Password>
                <div class="col justify-end mt-2 mb-8 gap-2">
                    <span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary" @click="forgot">忘记密码</span>
                    <span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary" @click="regist">没有账号，我要注册</span>
                </div>
            </IftaLabel>

            <div class="flex justify-end gap-2 mt-5 !border-btn">
                <Button type="submit" label="登录" _as="router-link" _to="/" class="!bg-green-600 !rounded-full"></Button>
                <Button severity="warn" label="返回" class="!bg-orange-500 !border-0 !rounded-full" _as="router-link" _to="/" @click="cancel"></Button>
            </div>
    </Form>
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
import checker from "@/api/check/checker";
import { useStorage } from '@vueuse/core';
import axios from "axios";
import util from "@/api/util";
import useGlobal from "@/api/hooks/useGlobal";

const buyer = ref(Beans.buyer());
const password = ref('');
const siteDatas = ref(null);
(async ()=>{
    siteDatas.value = await useGlobal.siteDatas();
})();
const emit = defineEmits(["afterLogin","cancel","forgot","regist"]);

let errors = [];
let loginToken = "";

const afterLogin = ()=>{
    emit("afterLogin",buyer.value.phone);
}
const cancel = ()=>{
    emit("cancel");
}
const forgot = ()=>{
    emit("forgot");
}
const regist = ()=>{
    emit("regist");
}

const resolver = ({ values }) => {
    errors = primeUtil.checkFormRequiredValid([
        {val:buyer.value.phone,name:"phone"},
        {val:buyer.value.password,name:"password1"}
    ]);

    primeUtil.buildFormValidError(errors.phone,"error","手机格式错误",()=>{
        return !checker.check({"phone":buyer.value.phone},[{name:"phone",checkType:"phone",checkRule:"",errorMsg:"手机格式错误"}]);
        },(error)=>{errors.phone = error});

    // let rule = [{name:"phone",checkType:"phone",checkRule:"",errorMsg:"手机格式错误"}];
    // let res = checker.check({"phone":buyer.value.phone},rule);
    // console.log("res",res,checker.error);

    return {
        values, // (Optional) Used to pass current form values to submit event.
        errors
    };
};
const onFormSubmit = ({ valid }) => {
    if (valid) {
        userRest.buyerLogin(buyer.value.phone,buyer.value.password,(data)=>{
            if (data.status=="OK") {
                loginToken = data.loginToken;
                useStorage("userId",buyer.value.phone);
                useStorage("loginToken",loginToken);
                dialog.alertBack("您已成功登录",()=>{
                    // Page.redirectTo("landing",null);
                    afterLogin();
                });
            } else if (data.status=="ER_NOHAS") {
                dialog.toastError("您输入的账号或密码错误");
            } else if (data.status=="ER_PW") {
                dialog.toastError("您输入的账号或密码错误");
            }
        });
    }
};

function test() {
    axios({
        baseURL: "http://localhost:8091/cpt",
        url: "/r/other/test",
        method: "post", // header: {timestamp:jsEncrypt.encrypt(str),createTime:str,nonceStr:util.encryptStoreInfo(str)},
        headers: {
            'Content-Type': 'application/json'
        },
        data: util.encryptStoreInfo(encodeURIComponent(JSON.stringify({appId:"abcde"})))
    })
        .then((res) => {
            console.log(res);
        })
        .catch((error) => {
            // dialog.alert("系统错误，请通知系统管理员或者客服；谢谢！");
            console.log(error);
            if (error.response) {
                // 请求已发出，服务器回复状态码非 2xx
                console.error('服务器返回错误：', error.response.status);
            } else if (error.request) {
                // 请求已发出，但没有收到响应
                console.error('没有收到响应：', error.request);
            } else {
                // 其他错误（例如，配置错误）
                console.error('请求配置错误：', error.message);
            }
        });
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
