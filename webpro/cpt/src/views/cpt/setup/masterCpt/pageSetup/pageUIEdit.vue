<template>
    <div>
        <div v-for="pageEls of pageJson">
            <Fieldset :legend="pageEls.name" class="!mt-5" :pt="{legendLabel:{class:'text-orange-300'}}">
                <div v-for="item of pageEls.setup">
                    <div v-if="item.type=='box'" class="p-2 ml-8">
                        <span class="text-green-600 text-sm">{{item.pre}}</span>
                        <Divider/>
                        <buildUIElement v-for="boxItem of item.eltTypes" :element="boxItem" />
                    </div>
                    <buildUIElement v-else :element="item" />
                </div>
            </Fieldset>
        </div>
    </div>
</template>

<script setup>
import buildUIElement from "@/views/cpt/setup/masterCpt/pageSetup/buildUIElement.vue";
import {provide} from 'vue';
const pageJson = defineModel("pageJson");
const readOnly = defineModel("readOnly",{default:false});
provide("readOnly",readOnly);
</script>

<style scoped lang="scss">

</style>