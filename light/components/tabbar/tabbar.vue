<template>
	<wd-tabbar fixed :v-model="tabIndex" bordered safeAreaInsetBottom placeholder @change="handleChange">
	  <wd-tabbar-item title="">
		  <template #icon>
			  <text class="gui-icons text-base" :class="tabClass(0)">&#xe63b;</text>
		  </template>
	  </wd-tabbar-item>
	  <wd-tabbar-item title="" icon="user">
		  <template #icon>
				<text class="iconfont text-base" :class="tabClass(1)">&#xe67e;</text>
		  </template>
	  </wd-tabbar-item>
	</wd-tabbar>
</template>
<script setup>
	import { ref,getCurrentInstance, toRefs } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import page from "@/api/uniapp/page.js";
	
	const props = defineProps({
		tabIndex: {
			type: Number,
			default: 0
		}
	});
	const {tabIndex} = toRefs(props);
	
	const handleChange = (v)=>{
		// console.log(value.value);
		switch (v.value){
			case 0:
				page.reLaunch("/pages/index/index",{});
				break;
			case 1:
				page.reLaunch("/pages/user/index",{});
				break;
			case 11:
				page.reLaunch("/pages/device/setup",{});
				break;
			default:
				break;
		}
	};
	
	const tabClass = (v)=>{
		// console.log(v,tabIndex.value);
		if (v==tabIndex.value) {
			return "text-green-500";
		} else {
			return "text-gray-400"
		}
	}
</script>
<style>

</style>
