<template>
    <IftaLabel v-if="element.type=='headTitle' || element.type=='text'">
        <label for="name" class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <InputText name="name" class="w-full md:w-[30rem] mb-4" v-model="element.value" :disabled="readOnly"/>
    </IftaLabel>
</template>

<script setup>
import {inject} from 'vue';
const element = defineModel("element");
const mediaFiles = defineModel("mediaFiles");
const readOnly = inject("readOnly");

</script>

<style scoped lang="scss">

</style>