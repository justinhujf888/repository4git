<template>
    <div v-if="element.type=='box'" class="p-2 ml-8">
        <span class="text-green-600 text-sm">{{element.pre}}</span>
        <Divider/>
        <buildUIElement v-for="boxItem of element.eltTypes" :element="boxItem" />
    </div>
    <IftaLabel v-else-if="element.type=='headTitle' || element.type=='text' || element.type=='title'">
        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <InputText class="w-full md:w-[30rem] mb-4" v-model="element.value"/>
    </IftaLabel>
    <div v-else-if="element.type=='image'">
        <priview-image :files="[element.value]"/>
    </div>
    <IftaLabel v-else-if="element.type=='date'">
        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <DatePicker class="w-full md:w-[30rem] mb-4" v-model="element.value" :dateFormat="element.format"/>
    </IftaLabel>
</template>

<script setup>
import priviewImage from "@/components/my/priviewImage.vue";
import buildUIElement from "@/views/cpt/setup/masterCpt/pageSetup/buildUIElement.vue";
const element = defineModel("element");
const mediaFiles = defineModel("mediaFiles",{default:[]});
</script>

<style scoped lang="scss">

</style>