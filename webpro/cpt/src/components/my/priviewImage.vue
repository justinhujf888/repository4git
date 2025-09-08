<template>
    <div>
        <Galleria v-model:activeIndex="activeIndex" v-model:visible="shiShow" :value="files" :responsiveOptions="responsiveOptions" :numVisible="files.length"
                  containerStyle="max-width: 850px" :circular="true" :fullScreen="fullScreen" :showItemNavigators="showItemNavigators" :showThumbnails="showThumbnails">
            <template #item="slotProps">
                <div class="h-screen">
                    <img :src="slotProps.item.tempMap.imgPath" :alt="slotProps.item.tempMap.name" class="block h-full" />
                </div>
            </template>
            <template #thumbnail="slotProps">
                <img :src="slotProps.item.tempMap.imgPath" :alt="slotProps.item.tempMap.name" class="block w-20 h-20 object-cover" />
            </template>
        </Galleria>
        <DataView :value="files" layout="grid" :pt="{
            emptyMessage:{
                class:'opacity-0'
            }
        }">
            <template #grid="slotProps">
                <div _class="grid grid-cols-12 gap-4" class="row flex-wrap">
                    <div v-for="(item, index) in slotProps.items" :key="index" _class="col-span-4 sm:col-span-2 md:col-span-2 xl:col-span-2 p-2">
                        <div class="p-1 border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 rounded flex flex-col">
                            <div class="bg-surface-50 flex justify-center rounded p-1">
                                <div class="w-20 h-20 sm:w-20 sm:h-20 md:w-28 md:h-28 xl:w-36 xl:h-36 mx-auto">
                                    <Image class="rounded w-full h-full object-cover" :src="item.tempMap.imgPath" :alt="item.name" style="max-width: 300px;" :pt="{image:{class:'!w-full !h-full object-cover'}}" @click="imageClick(index)"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </DataView>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

const props = defineProps(['files','activeIndex','fullScreen','showItemNavigators','showThumbnails','responsiveOptions','obj']);
const files = ref(props.files);
const activeIndex = ref(props.activeIndex);
const shiShow = ref(false);
const fullScreen = ref(props.fullScreen ? props.fullScreen : true);
const showItemNavigators = ref(props.showItemNavigators ? props.showItemNavigators : true);
const showThumbnails = ref(props.showThumbnails ? props.showThumbnails : true);
const responsiveOptions = ref(props.responsiveOptions ? props.responsiveOptions : null);

let obj = props.obj;

onMounted(() => {
    console.log(files.value);
});

const imageClick = (index) => {
    activeIndex.value = index;
    shiShow.value = true;
};
</script>

<style scoped lang="scss">

</style>
