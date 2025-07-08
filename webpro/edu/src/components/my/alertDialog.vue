<template>
    <Dialog v-model:visible="app_alertDialogShow" :closable="false" :style="{ width: '25rem' }" modal :header="app_dialogHead" ___pt:mask:class="backdrop-blur-sm">
        <div class="flex flex-row justify-center items-center">
            <text>{{ app_msg }}</text>
        </div>
        <div class="flex justify-end">
            <Button type="button" label="确定" @click="close"></Button>
        </div>
    </Dialog>
</template>

<script setup>
import { ref } from 'vue';
import Dialog from 'primevue/dialog';

const app_alertDialogShow = ref(false);
const app_msg = ref('');
const app_dialogHead = ref('');
let fun = null;

const setHeader = (txt) => {
    app_dialogHead.value = txt;
};

const alert = (msg) => {
    app_msg.value = msg;
    app_alertDialogShow.value = true;
};

const alertBack = (msg, f) => {
    app_msg.value = msg;
    app_alertDialogShow.value = true;
    fun = f;
};

const close = () => {
    app_alertDialogShow.value = false;
    if (fun) {
        fun();
    }
};

defineExpose({ alert, alertBack, setHeader });
</script>

<style scoped lang="scss"></style>
