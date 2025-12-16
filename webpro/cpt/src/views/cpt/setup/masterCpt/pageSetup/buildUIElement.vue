<template>
    <IftaLabel v-if="element.type=='headTitle' || element.type=='text'">
        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <InputText class="w-full md:w-[30rem] mb-4" v-model="element.value" :disabled="readOnly"/>
    </IftaLabel>
    <div v-else-if="element.type=='image'" class="col">
        <IftaLabel>
            <Select class="w-full md:w-[30rem] mb-4" v-model="element.value" :options="mediaFiles" :disabled="readOnly"/>
            <label>{{element.pre}}</label>
        </IftaLabel>
    </div>
</template>

<script setup>
import {inject} from 'vue';
const element = defineModel("element");
const mediaFiles = defineModel("mediaFiles",{default:[]});
const readOnly = inject("readOnly");

</script>

<style scoped lang="scss">

</style>