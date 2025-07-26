<template>
	<wd-switch v-model="sv" active-color="#13ce66" @change="switchChange"/>
</template>
<script setup>
	import { ref,watch,onMounted,getCurrentInstance, toRefs } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	
	const props = defineProps({
		value: {
			type: Boolean,
			default: false
		},
		obj: {
			type: Object,
			default: {}
		}
	});
	
	const {value,obj} = toRefs(props);
	
	const sv = ref(true);
	
	onMounted(()=>{
		sv.value = value.value;
	});
	
	watch(value,(newValue,oldValue)=>{
		sv.value = newValue;
	});
	
	const emit = defineEmits(["change"]);
	
	const switchChange = (v)=>{
		emit("change",{v:v.value,obj:obj.value});
	};
</script>
<style scoped>

</style>