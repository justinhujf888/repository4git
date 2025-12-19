<template>
    <div>
        <div v-for="pageEls of pageJson">
            <Fieldset :legend="pageEls.name" class="!mt-5" :pt="{legendLabel:{class:'text-orange-300'}}">
                <div v-for="element of pageEls.setup">
                    <div v-if="element.type=='box'" class="p-2">
                        <div class="between">
                            <span class="text-green-600 text-sm">{{element.pre}}</span>
                            <Button v-if="!readOnly" label="新增" size="small" @click="openDialog(element)"/>
                        </div>
                        <Divider/>
                        <buildUIElement :element="element" />
                    </div>
                    <div v-else>
                        <buildUIElement v-if="readOnly" :element="element" />
                        <pageUIEdit v-else :element="element"/>
                    </div>
                </div>
            </Fieldset>
        </div>
        <Dialog v-model:visible="showDialog" modal>
            <pageUIEdit v-for="boxItem of eltTypes" :element="boxItem"/>
            <div class="center gap-4">
                <Button label="确定" size="small" @click="saveelm"/>
                <Button severity="warn" label="取消" size="small" @click="cancelelm"/>
            </div>
        </Dialog>
        <Popover ref="op">

        </Popover>
    </div>
</template>

<script setup>
import pageUIEdit from "@/views/cpt/setup/masterCpt/pageSetup/pageUIEdit.vue";
import buildUIElement from "@/views/cpt/setup/masterCpt/pageSetup/buildUIElement.vue";
import {ref, useTemplateRef} from "vue";
import lodash from "lodash-es";
const pageJson = defineModel("pageJson",{default:{}});
const readOnly = defineModel("readOnly",{default:true});
const eltTypes = ref([]);
const element = ref({});
const showDialog = ref(false);
const op = useTemplateRef("op");

function openDialog(_element) {
    element.value = _element;
    eltTypes.value = lodash.cloneDeep(element.value.eltTypes);
    showDialog.value = true;
}
function openPop(event,_eltTypes) {
    eltTypes.value = _eltTypes;
    op.value.toggle(event);
}
function saveelm() {
    if (!element.value.value) {
        element.value.value = [];
    }
    let obj = {};
    lodash.forEach(eltTypes.value,(v)=>{
        obj[v.key] = v.value ? v.value : "";
    });
    element.value.value.push(obj);
    eltTypes.value = {};
    showDialog.value = false;
}
function cancelelm() {
    showDialog.value = false;
}
</script>

<style scoped lang="scss">

</style>
