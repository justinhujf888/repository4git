<template>
    <div>
        <video ref="refVideo" controls>
            <source :src="src" :type="type">
        </video>
    </div>
</template>

<script setup>
import {onMounted, useTemplateRef,ref} from "vue";

const props = defineProps({
    // files: { type: Array, required: true },
    src: { type: String, default: "" },
    type: { type: String, default: "video/mp4" },
    obj: { type: Object }
});
const refVideo = useTemplateRef("refVideo");
const duration = ref(0);

onMounted(()=>{
    if (refVideo.value) {
        refVideo.value.addEventListener("loadedmetadata", ()=>{
            console.log(props.src,refVideo.value.duration);
            duration.value = refVideo.value.duration;
        });
    }
});

const getVideoInfo = ()=>{
    return {
        duration: duration.value
    }
}

defineExpose({getVideoInfo});
</script>

<style scoped lang="scss">

</style>