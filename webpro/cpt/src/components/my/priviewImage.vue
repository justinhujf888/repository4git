<template>
    <div>
        <Galleria ref="galleria" v-model:activeIndex="activeIndex" v-model:visible="shiShow" :value="files" :responsiveOptions="responsiveOptions" :numVisible="5" :circular="true" :fullScreen="fullScreen" :showItemNavigators="showItemNavigators" :showThumbnails="showThumbnails" :showIndicatorsOnItem="true" :thumbnailsPosition="thumbnailsPosition" :showItemNavigatorsOnHover="true" containerStyle="max-width: 640px" :pt="{
            root: {
                class: [{ 'flex flex-col': fullScreen }]
            },
            content: {
                class: ['relative', { 'flex-1 justify-center': fullScreen }]
            },
            nextButton: {
                class: 'mix-blend-difference'
            },
            prevButton: {
                class: 'mix-blend-difference'
            },
            thumbnails: {class:'absolute w-full left-0 bottom-0'}
        }">
            <template #item="slotProps">
<!--                <div class="h-lvh">-->
<!--                    <img :src="slotProps.item.tempMap.imgPath" :alt="slotProps.item.tempMap.name" class="block h-full" />-->
<!--                </div>-->
                <img :src="slotProps.item?.tempMap.imgPath" :alt="slotProps.item?.tempMap.name" :class="[{'w-full':!fullScreen},{'block':!fullScreen}]" />
            </template>
            <template #thumbnail="slotProps">
                <div class="grid gap-4 justify-center">
                    <img :src="slotProps.item?.tempMap.imgPath" :alt="slotProps.item?.tempMap.name" class="block w-20 h-20 object-cover" />
                </div>
            </template>
            <template #footerrr>
                <div class="flex items-stretch bg-surface-950 text-white h-10">
                    <button type="button" @click="onThumbnailButtonClick" class="bg-transparent border-none rounded-none hover:bg-white/10 text-white inline-flex justify-center items-center cursor-pointer px-3">
                        <i class="pi pi-th-large"></i>
                    </button>
<!--                    <button type="button" @click="toggleAutoSlide" class="bg-transparent border-none rounded-none hover:bg-white/10 text-white inline-flex justify-center items-center cursor-pointer px-3"><i :class="slideButtonIcon"></i></button>-->
                    <span v-if="files" class="flex items-center gap-4 ml-3">
                        <span class="text-sm">{{ activeIndex + 1 }}/{{ files.length }}</span>
                        <span class="font-bold text-sm">{{ files[activeIndex]?.tempMap.name }}</span>
<!--                        <span class="text-sm">{{ files[activeIndex].alt }}</span>-->
                    </span>
<!--                    <button type="button" @click="toggleFullScreen" class="bg-transparent border-none rounded-none hover:bg-white/10 text-white inline-flex justify-center items-center cursor-pointer px-3 ml-auto">-->
<!--                        <i :class="fullScreenIcon"></i>-->
<!--                    </button>-->
                </div>
            </template>
        </Galleria>
        <DataView v-if="shiShowImgGrid" :value="files" layout="grid" :pt="{
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
import { ref, onMounted, computed } from "vue";

// const props = defineProps(['files','activeIndex','fullScreen','showItemNavigators','showThumbnails','thumbnailsPosition','responsiveOptions',{shiShowImgGrid:{type:Object,default:true}},'obj']);

const props = defineProps({
    files: { type: Array, required: true },
    activeIndex: { type: Number, default: 0 },
    fullScreen: { type: Boolean, default: true },
    showItemNavigators: { type: Boolean, default: true },
    showThumbnails: { type: Boolean, default: false },
    thumbnailsPosition: { type: String, default: "bottom" },
    responsiveOptions: { type: Array },
    shiShowImgGrid: { type: Boolean, default: true },
    obj: { type: Object }
})

const galleria = ref();
const files = ref(props.files);
const activeIndex = ref(props.activeIndex);
const shiShow = ref(false);
const fullScreen = ref(props.fullScreen);
const showItemNavigators = ref(props.showItemNavigators);
const showThumbnails = ref(props.showThumbnails);
const thumbnailsPosition = ref(props.thumbnailsPosition);
const responsiveOptions = ref(props.responsiveOptions);
const shiShowImgGrid = ref(props.shiShowImgGrid);

let obj = props.obj;

onMounted(() => {
    // console.log(files.value);
    bindDocumentListeners();
});

const imageClick = (index) => {
    activeIndex.value = index;
    shiShow.value = true;
};

const imagesShow = (_files,_index) => {
    // console.log(_files,_index);
    files.value = _files;
    activeIndex.value = _index;
    shiShow.value = true;
};

const onThumbnailButtonClick = () => {
    showThumbnails.value  = !showThumbnails.value ;
};
const toggleFullScreen = () => {
    if (fullScreen.value ) {
        closeFullScreen();
    } else {
        openFullScreen();
    }
};
const onFullScreenChange = () => {
    fullScreen.value  = !fullScreen.value;
};
const openFullScreen = () =>{
    let elem = galleria.value.$el;

    if (elem.requestFullscreen) {
        elem.requestFullscreen();
    } else if (elem.mozRequestFullScreen) {
        /* Firefox */
        elem.mozRequestFullScreen();
    } else if (elem.webkitRequestFullscreen) {
        /* Chrome, Safari & Opera */
        elem.webkitRequestFullscreen();
    } else if (elem.msRequestFullscreen) {
        /* IE/Edge */
        elem.msRequestFullscreen();
    }
};
const closeFullScreen = () => {
    if (document.exitFullscreen) {
        document.exitFullscreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
    } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
    }
};
const bindDocumentListeners = () => {
    document.addEventListener('fullscreenchange', onFullScreenChange);
    document.addEventListener('mozfullscreenchange', onFullScreenChange);
    document.addEventListener('webkitfullscreenchange', onFullScreenChange);
    document.addEventListener('msfullscreenchange', onFullScreenChange);
};
const unbindDocumentListeners = () => {
    document.removeEventListener('fullscreenchange', onFullScreenChange);
    document.removeEventListener('mozfullscreenchange', onFullScreenChange);
    document.removeEventListener('webkitfullscreenchange', onFullScreenChange);
    document.removeEventListener('msfullscreenchange', onFullScreenChange);
};

const fullScreenIcon = computed(() => {
    return `pi ${fullScreen.value ? 'pi-window-minimize' : 'pi-window-maximize'}`;
});
const slideButtonIcon = computed(() => {
    return `pi ${isAutoPlay.value ? 'pi-pause' : 'pi-play'}`;
});

defineExpose({imagesShow});
</script>

<style scoped lang="scss">

</style>
