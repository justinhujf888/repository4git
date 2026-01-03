<template>
<!--    <jb-text :colors="['#ff000','#ff000']" fromto="68 deg" class="text-5xl siyuansongBold">abc</jb-text>-->
    <div v-if="element.type=='box'" class="p-2">
        <DataTable :value="element.value" :reorderableColumns="true" @rowReorder="onRowReorder">
            <Column rowReorder headerStyle="width: 3rem" :reorderableColumn="false" />
            <Column v-for="col of element.eltTypes" :field="col.key" :header="col.pre">
                <template #body="{data,index}">
                    <div v-if="col.type=='image'" class="center w-16">
                        <img :src="data[col.key].tempMap?.imgPath" class="rounded-full w-16 h-16 object-content"/>
                    </div>
                    <div v-else-if="col.type=='jbText'" class="center w-16">
                        <jb-text :colors="buildColors(data[col.key].colors)" :fromto="`${data[col.key].des}deg`" class="text-xl siyuansongBold">{{data[col.key].text}}</jb-text>
                    </div>
                    <span v-else>{{data[col.key]}}</span>
                </template>
            </Column>
            <Column class="w-24">
                <template #body="{data,index}">
                    <Button icon="pi pi-trash" rounded @click="deleteRow(element,data,index)" severity="secondary"/>
                    <Button icon="pi pi-pencil" rounded @click="updateRow(element,data,index)" severity="secondary"/>
                </template>
            </Column>
        </DataTable>
    </div>
    <IftaLabel v-else-if="element.type=='headTitle' || element.type=='text' || element.type=='title'">
        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <InputText class="w-full md:w-[30rem] mb-4" v-model="element.value"/>
    </IftaLabel>
    <IftaLabel v-else-if="element.type=='textArea'">
        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <Textarea class="w-full md:w-[30rem] mb-4" v-model="element.value"/>
    </IftaLabel>
    <IftaLabel v-else-if="element.type=='link'">
        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <InputText class="w-full md:w-[30rem] mb-4" v-model="element.value.url" placeholder="url"/>
        <InputText class="w-full md:w-[30rem] mb-4" v-model="element.value.text" placeholder="按钮文字"/>
    </IftaLabel>
    <div v-else-if="element.type=='image'" class="col p-2">
        <div class="between">
            <span class="text-green-600 text-sm">{{element.pre}}</span>
            <Button label="选择图片" size="small" @click="openPop"/>
        </div>
        <Divider/>
        <div v-if="element.value.img" class="w-20 h-20 sm:w-20 sm:h-20 md:w-28 md:h-28 xl:w-36 xl:h-36 mx-auto">
            <Image class="rounded w-full h-full object-cover" :src="element.value?.tempMap?.imgPath" style="max-width: 300px;" preview_ :pt="{image:{class:'!w-full !h-full !object-cover'}}"/>
        </div>
    </div>
    <IftaLabel v-else-if="element.type=='date'">
        <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">{{element.pre}}</label>
        <DatePicker class="w-full md:w-[30rem] mb-4" v-model="element.value" :dateFormat="element.format"/>
    </IftaLabel>
    <div v-else-if="element.type=='jbText'" class="col">
        <span>渐变文字</span>
        <Divider/>
        <updateJBText v-model:colors="element.value.colors" v-model:text="element.value.text" v-model:des="element.value.des"></updateJBText>
        <Divider/>
    </div>

    <Popover ref="op">
        <ScrollPanel class="w-full h-96" :dt="{
                bar: {
                    background: '{primary.color}'
                }

            }">
            <image-grid v-model:mediaFiles="mediaFiles" v-model:selFiles="element.value" :selCount="1" :funCheckHasIndex="imageGridCheckHas"></image-grid>
        </ScrollPanel>
    </Popover>
</template>

<script setup>
import priviewImage from "@/components/my/priviewImage.vue";
import oss from "@/api/oss";
import jbText from "@/components/my/form/jbText.vue";
import imageGrid from "@/components/my/imageGrid.vue";
import lodash from "lodash-es";
import { onMounted, ref, useTemplateRef } from 'vue';
import updateJBText from "@/components/my/form/updateJBText.vue";
import pj from '@/datas/pageJson';
const element = defineModel("element");
const mediaFiles = defineModel("mediaFiles");
const pop = useTemplateRef("op");

const emit = defineEmits(["deleteRow","updateRow"]);

onMounted(async ()=>{
    await pj.processPageImageJson(element.value);
});

function deleteRow(element,data,index) {
    // console.log(element.value.value,data,index);
    emit("deleteRow",element,data,index);
    // element.value.value.splice(index, 1);
}

function updateRow(element,data, index) {
    emit("updateRow",element,data,index);
}

function openPop(event) {
    pop.value.toggle(event);
}

const imageGridCheckHas = (file,mediaFiles,selFiles,selCount)=>{
    // console.log("selFiles",selFiles,"file",file);
    if (selCount>1) {
        return lodash.findIndex(selFiles,(o)=>{return o.id==file.id});
    } else {
        return selFiles.id==file.id ? 0 : -1;
    }
}

function buildColors(cry) {
    let ay = [];
    lodash.forEach(cry,(value,index) => {
        ay.push("#"+value);
    });
    return ay;
}

const onRowReorder = (event) => {
    element.value.value = event.value;
};
</script>

<style scoped lang="scss">

</style>
