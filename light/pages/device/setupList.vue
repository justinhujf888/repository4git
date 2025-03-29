<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="row justify-center items-center">
				<text class="text-base">Breath One</text>
				<text class="gui-icons text-gray-400 ml-2">&#xe69e;</text>
			</view>
		</template>
	</wd-navbar>
	<view class="relative h-screen px-6">
		<view>
			<view v-for="(s,i) in setupList" :key="s.id" class="row bg-white rounded-xl py-5 px-2 mt-4" @tap="page.navigateTo('./setup',{})">
				<text class="text-xl text-green-500 gui-icons px-4">√</text>
				<text class="text-base flex-1">{{s.name}}</text>
				<text class="text-xl text-gray-400 gui-icons px-4">&#xe69e;</text>
				<text class="text-xl text-gray-400 gui-icons px-4">&#xe684;</text>
			</view>
		</view>
		
		<view class="row justify-center items-center mt-6">
			<view class="w-7">
				<button class="btnbg rounded-full w-7 h-7 text-white center" @tap="">
					<text class="text-xl">✚</text>
				</button>
			</view>
			<text class="text-base font-semibold ml-2">添加新的设定</text>
		</view>
	</view>
</template>

<script setup>
	import page from "@/api/uniapp/page.js";
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import lodash from "lodash";
	
	const setupList = ref([]);
	const selIndex = ref([]);
	
	onLoad((option)=>{
		setupList.value.push({id:"0",name:"20缸默认"});
		setupList.value.push({id:"1",name:"25缸默认"});
		setupList.value.push({id:"2",name:"30缸默认"});
	});
	
	
</script>

<style>
</style>