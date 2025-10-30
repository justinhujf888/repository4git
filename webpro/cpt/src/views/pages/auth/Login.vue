<template>
<!--    <FloatingConfigurator />-->
    <div class="bg-surface-500 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-[100vw] overflow-hidden">
        <div class="flex flex-col items-center justify-center">
<!--            <Button label="test" @click="test"/>-->
            <div _style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                <div class="xs:w-dvw xs:h-dvh md:w-full md:h-auto dark:bg-surface-900 py-20 px-8 sm:px-20 bg-surface-900" _style="border-radius: 53px">
                    <div class="text-center mb-8">
                        <div class="text-surface-50 dark:text-surface-0 text-3xl font-medium mb-4">{{siteDatas?.siteInfo.siteCompetition.name}}</div>
                        <span class="text-muted-color font-medium">登录</span>
                    </div>

                    <Form v-slot="$form" :resolver @submit="onFormSubmit" class="grid gap-y-4">
                        <IftaLabel>
                            <label for="phone" class="block text-surface-900 dark:text-surface-0 text-base font-medium">手机号码</label>
                            <InputMask name="phone" mask="99999999999" placeholder="请输入手机号码" class="w-full md:w-[30rem]" v-model="buyer.phone" />
                            <Message v-if="$form.phone?.invalid && $form.phone.error?.type=='error'" severity="error" size="small" variant="simple">{{ $form.phone.error?.message}}</Message>
                        </IftaLabel>

                        <IftaLabel>
                            <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-base z-30">密码</label>
                            <Password name="password1" v-model="buyer.password" placeholder="请输入密码" :toggleMask="true" fluid :feedback="false"></Password>
                            <div class="flex items-center justify-end mt-2 mb-8 gap-8">
                                <router-link :to="{name:'forgotpw'}">
                                    <span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary">忘记密码</span>
                                </router-link>
                            </div>
                        </IftaLabel>

                        <div class="row mt-12 !border-btn">
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
import { ref,getCurrentInstance } from 'vue';
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

const buyer = ref(Beans.buyer());
const password = ref('');
const siteDatas = ref(null);
const {proxy} = getCurrentInstance();
(async ()=>{
    siteDatas.value = await proxy.$getSiteDatas();
})();

let errors = [];

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
