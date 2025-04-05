<template>
	<wd-tabbar fixed v-model="tabbar" bordered safeAreaInsetBottom placeholder @change="handleChange">
	  <wd-tabbar-item title="">
		  <template #icon>
			  <text class="gui-icons text-base text-green-500">&#xe63b;</text>
		  </template>
	  </wd-tabbar-item>
	  <wd-tabbar-item title="" icon="user">
		  <template #icon>
				<text class="iconfont text-base text-gray-400">&#xe67e;</text>
		  </template>
	  </wd-tabbar-item>
	</wd-tabbar>
</template>
<script setup>
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import page from "@/api/uniapp/page.js";
	
	const tabbar = ref(0);
	
	const handleChange = (v)=>{
		// console.log(value.value);
		switch (v.value){
			case 0:
				page.reLaunch("/pages/index/index",{});
				break;
			case 1:
				page.reLaunch("/pages/device/setup",{});
				break;
			default:
				break;
		}
	};
</script>
<style>

</style>
