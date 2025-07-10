<script setup>
import dialog from '@/api/uniapp/dialog';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from 'primevue/useconfirm';
import { useDialog } from 'primevue/usedialog';
import myDialog from '@/components/my/alertDialog.vue';
import { useTemplateRef } from 'vue';

const toast = useToast();
const confirmPopup = useConfirm();
const dynDialog = useDialog();
const mydRef = useTemplateRef('mydRef');
dialog.setup(confirmPopup, toast, dynDialog, mydRef);
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
