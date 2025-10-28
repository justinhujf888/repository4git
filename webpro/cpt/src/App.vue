<script setup>
import oss from "@/api/oss";
import lodash from "lodash-es";
import dialog from '@/api/uniapp/dialog';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from 'primevue/useconfirm';
import { useDialog } from 'primevue/usedialog';
import myDialog from '@/components/my/alertDialog.vue';
import { useTemplateRef,onMounted,onUnmounted } from 'vue';
import { Config } from '@/api/config';
import {ConnectController} from "@/api/controller";
import loading from "@/components/my/loading.vue";
import 'animate.css';

const toast = useToast();
const confirmPopup = useConfirm();
const dynDialog = useDialog();
const mydRef = useTemplateRef('mydRef');
const myLoading = useTemplateRef("myLoading");

var source = null;
onMounted(() => {
    oss.checkToken();
    dialog.setup(confirmPopup, toast, dynDialog, mydRef, myLoading);
    if (typeof (EventSource) !== "undefined") {return;
        source = new EventSource(Config.apiBaseURL + "/r/notifications");
        // 当通往服务器的连接被打开
        source.onopen = function(event) {
            console.log("连接开启！");
        };
        // 当接收到消息。只能是事件名称是 message
        // source.onmessage = function(event) {
        //     // console.log(event);
        //     // console.log("\n" + 'lastEventId:'+event.lastEventId+';data:'+event.data);
        //     ConnectController.notificationListen(event.data);
        // };
        //可以是任意命名的事件名称
        source.addEventListener('notifications', function(event) {
            console.log(event.data);
            if (event.data) {
                let jdata = JSON.parse(event.data);
                if (jdata.type=="publcMsg") {
                    if (jdata.mode) {
                        dialog.alert(jdata.data);
                    } else {
                        dialog.toastNone(jdata.data);
                    }
                } else {
                    ConnectController.notificationListen(jdata);
                }

            }
        });
        // 当错误发生
        source.onerror = function(event) {
            // console.log("连接错误！",event);
            // source.close();
        };
    } else {
        console.log("Sorry, your browser does not support server-sent events...");
    }
    console.log("app onMounted runed");
});

onUnmounted(() => {
    console.log("app onUnmounted runed");
    if (source!=null) {
        source.close();
    }
});

</script>

<template>
    <loading ref="myLoading"></loading>
    <DynamicDialog />
    <ConfirmDialog />
    <myDialog ref="mydRef"></myDialog>
    <Toast />
<!--    <router-view/>-->
    <router-view v-slot="{ Component }">
        <transition name="fade">
            <component :is="Component" />
        </transition>
    </router-view>
</template>

<style>
@import '@/static/icons/iconfont.css';
@import "@/static/css/animate.css";
</style>
