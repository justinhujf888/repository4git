<template>
    <div>
        <div class="row m-4 gap-2">
            <div class="col w-28 gap-4 center" v-for="color in colorHEX">
                <Checkbox v-model="color.use" binary />
                <ColorPicker v-model="color.value" format="hex" />
                <InputText v-model="color.value" class="w-full"/>
            </div>
        </div>
        <div class="m-5 p-2 col gap-4">
            <span>角度</span>
            <Slider :min="0" :max="180" v-model="des"/>
        </div>
        <div>
            <IftaLabel>
                <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">渐变文字</label>
                <InputText class="w-full md:w-[30rem] mb-4" v-model="text"/>
            </IftaLabel>
        </div>
        <div class="mb-5">
            <jb-text :colors="buildJBColor()" :fromto="`${des}deg`" class="text-5xl siyuansongBold">{{text}}</jb-text>
        </div>
    </div>
</template>

<script setup>
import {onMounted, ref, watch} from "vue";
import jbText from "@/components/my/form/jbText.vue";

const colors = defineModel("colors", {default:[]});
const colorHEX = ref([{use:false,value:""},{use:false,value:""},{use:false,value:""},{use:false,value:""}]);
const text = defineModel("text",{default:""});
let colorArray = [];
const des = defineModel("des",{default:0});
// const props = defineProps({
//     colors: {type:Array, default:[]}
// });

onMounted(() => {
    for(let i=0;i<colors.value.length;i++){
        colorHEX.value[i].use = true;
        colorHEX.value[i].value = colors.value[i];
    }
});

watch(()=>colors.value,(newValue)=>{
    for(let i=0;i<newValue.length;i++){
        colorHEX.value[i].use = true;
        colorHEX.value[i].value = newValue[i];
    }
});

const buildJBColor = ()=>{
    colorArray = [];
    for(let i = 0; i < colorHEX.value.length; i++) {
        if (colorHEX.value[i].use) {
            colorArray.push(`#${colorHEX.value[i].value}`);
            colors.value[i] = colorHEX.value[i].value;
        }
    }
    return colorArray;
}
</script>

<style scoped lang="scss">

</style>