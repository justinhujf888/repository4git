<template>
    <div>
        <div v-for="pageEls of pageJson">
            <Fieldset :legend="pageEls.name">
                <Panel v-for="item of pageEls.setup">

                </Panel>
            </Fieldset>
        </div>
    </div>
</template>

<script setup>
const pageJson = defineModel("pageJson");
</script>

<style scoped lang="scss">

</style>