<template>
    <div v-if="element.type=='headTitle' || element.type=='text' || element.type=='title'" class="row my-2">
        <span class="text-sm text-gray-300">{{element.pre}}</span>
        <span class="text-sm text-gray-300 mx-1">:</span>
        <span>{{element.value}}</span>
    </div>
    <div v-else-if="element.type=='image'">
        <priview-image :files="[element.value]"/>
    </div>
    <div v-else-if="element.type=='images'">
        <priview-image v-model:files="element.value"/>
    </div>
    <div v-else-if="element.type=='box'">
        <DataTable :value="element.value">
            <Column v-for="col of element.eltTypes" :field="col.key" :header="col.pre"></Column>
        </DataTable>
    </div>
</template>

<script setup>
import priviewImage from "@/components/my/priviewImage.vue";
const element = defineModel("element");

</script>

<style scoped lang="scss">

</style>