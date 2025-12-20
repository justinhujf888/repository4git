<template>
    <div>
        <div class="row m-4 gap-2">
            <div class="col w-28 gap-4 center" v-for="color in colorHEX">
                <Checkbox v-model="color.use" binary />
                <ColorPicker v-model="color.value" format="hex" />
                <InputText v-model="color.value" class="w-full"/>
            </div>
        </div>
        <div>
            <IftaLabel>
                <label class="block text-surface-900 dark:text-surface-0 text-base font-medium mb-2">渐变文字</label>
                <InputText class="w-full md:w-[30rem] mb-4" v-model="text"/>
            </IftaLabel>
        </div>
        <div>
            {{text}}
            <jb-text :colors="colorArray" class="text-5xl">{{text}}</jb-text>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";
import jbText from "@/components/my/form/jbText.vue";

const colorHEX = ref([{use:false,value:""},{use:false,value:""},{use:false,value:""},{use:false,value:""}]);
const text = ref("");
const colorArray = ref([]);

const buildJBColor = ()=>{
    for(let i = 0; i < colorHEX.value.length; i++) {
        if (colorHEX.value[i].use) {
            colorArray.value.push(`#${colorHEX.value[i].value}`);
        }
    }
    return colorArray.value;
}
</script>

<style scoped lang="scss">

</style>