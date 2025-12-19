<template>
    <div v-if="element.type=='box'" class="p-2">
        <DataTable :value="element.value">
            <Column v-for="col of element.eltTypes" :field="col.key" :header="col.pre">
                <template #body="{data,index}">
                    <Image v-if="col.type=='image'" class="rounded w-16 h-16 object-cover" :src="data.tempMap.imgPath" style="max-width: 300px;" preview_ :pt="{image:{class:'!w-16 !h-16 !object-cover'}}"/>
                    <span v-else>{{data[col.key]}}</span>
                </template>
            </Column>
            <Column class="w-24">
                <template #body="{data,index}">
                    <Button icon="pi pi-trash" rounded @click="deleteRow(data,index)" severity="secondary"/>
                </template>
            </Column>
        </DataTable>
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
import oss from "@/api/oss";
const element = defineModel("element");
const mediaFiles = defineModel("mediaFiles",{default:[]});

function deleteRow(data,index) {
    // console.log(element.value.value,data,index);
    element.value.value.splice(index, 1);
}
</script>

<style scoped lang="scss">

</style>