<template>
    <div v-if="element.type=='headTitle' || element.type=='text' || element.type=='title'" class="row my-2">
        <span class="text-sm text-gray-300">{{element.pre}}</span>
        <span class="text-sm text-gray-300 mx-1">:</span>
        <span>{{element.value}}</span>
    </div>
    <div v-else-if="element.type=='image'">
<!--        <priview-image :files="[element.value]"/>-->
        <span class="text-green-600 text-sm">{{element.pre}}</span>
        <div v-if="element.value.img" class="w-20 h-20 sm:w-20 sm:h-20 md:w-28 md:h-28 xl:w-36 xl:h-36 mx-auto">
            <Image class="rounded w-full" :src="element.value?.tempMap?.imgPath" style="max-width: 300px;" preview_ :pt="{image:{class:'!w-full'}}"/>
        </div>
    </div>
    <div v-else-if="element.type=='link'">
        <button class="bg-blue-500 text-sm p-2">{{element.value.text}}</button>
        <span>{{element.value.url}}</span>
    </div>
    <div v-else-if="element.type=='images'">
        <span class="text-green-600 text-sm">{{element.pre}}</span>
        <priview-image v-model:files="element.value"/>
    </div>
    <div v-else-if="element.type=='html'" class="col">
        <div v-html="element.value"></div>
    </div>
    <div v-else-if="element.type=='slot'" class="py-4">
        <span>{{element.pre}}</span>
    </div>
    <div v-else-if="element.type=='box'">
        <DataTable :value="element.value">
            <Column v-for="col of element.eltTypes" :field="col.key" :header="col.pre">
                <template #body="{data,index}">
                    <div v-if="col.type=='image'" class="center w-16">
                        <img :src="data[col.key].tempMap?.imgPath" class="rounded-full w-16 h-16 object-content"/>
                    </div>
                    <div v-else-if="col.type=='jbText'" class="center w-16">
                        <jb-text :colors="buildColors(data[col.key].colors)" :fromto="`${data[col.key].des}deg`" class="text-xl siyuansongBold">{{data[col.key].text}}</jb-text>
                    </div>
                    <div v-else-if="col.type=='html'">
                        <div v-html="data[col.key]"></div>
                    </div>
                    <span v-else>{{data[col.key]}}</span>
                </template>
            </Column>
        </DataTable>
    </div>
</template>

<script setup>
import priviewImage from "@/components/my/priviewImage.vue";
import lodash from 'lodash-es';
import { onMounted } from 'vue';
import oss from '@/api/oss';
import pj from '@/datas/pageJson';
const element = defineModel("element");
onMounted(async ()=>{
    // if (element.value.type=="image") {
    //     element.value.value.tempMap = {imgPath:await oss.buildPathAsync(element.value.value.img,true,null)};
    // } else if (element.value.type=="box") {
    //     lodash.forEach(element.value.eltTypes,async (ev)=>{
    //         if (ev.type=="image") {
    //             ev.value.tempMap = {};
    //             for (let item of element.value.value) {
    //                 item[ev.key].tempMap = {imgPath:await oss.buildPathAsync(item[ev.key].img,true,null)};
    //             }
    //         }
    //     });
    // }
    await pj.processPageImageJson(element.value);
});

function buildColors(cry) {
    let ay = [];
    lodash.forEach(cry,(value,index) => {
        ay.push("#"+value);
    });
    return ay;
}
</script>

<style scoped lang="scss">

</style>
