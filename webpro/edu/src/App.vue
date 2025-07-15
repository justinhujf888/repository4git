<script setup>
import oss from "@/api/oss";
import dialog from '@/api/uniapp/dialog';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from 'primevue/useconfirm';
import { useDialog } from 'primevue/usedialog';
import myDialog from '@/components/my/alertDialog.vue';
import { useTemplateRef,onMounted,onUnmounted } from 'vue';

const toast = useToast();
const confirmPopup = useConfirm();
const dynDialog = useDialog();
const mydRef = useTemplateRef('mydRef');

onMounted(() => {
    oss.genClient(null);
    dialog.setup(confirmPopup, toast, dynDialog, mydRef);
    console.log("app onMounted runed");
});

onUnmounted(() => {
    console.log("app onUnmounted runed");
});

</script>

<template>
    <DynamicDialog />
    <ConfirmDialog />
    <myDialog ref="mydRef"></myDialog>
    <router-view />
</template>

<style>
@import '@/static/icons/iconfont.css';
</style>
