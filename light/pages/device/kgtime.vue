<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="justify-center items-center">
				<text class="text-base">开关灯定时</text>
			</view>
		</template>
	</wd-navbar>
	<view class="relative">
		<view class="px-2 mx-2">
			<view class="mt-4 bg-white rounded-xl p-4 text-gray-400">
				<view class="row">
					<text class="iconfont text-3xl">&#xe61f;</text>
					<text class="text-base mt-2 ml-2">开灯时间</text>
				</view>
				<view class="mt-4">
					<view class="between text-base justify-center items-center">
						<text class="mx-10">小时</text>
						<text class="mx-5">:</text>
						<text class="mx-10">分钟</text>
					</view>
					<wd-datetime-picker-view type="time" v-model="times.onTime" customClass="bg-white" @change="onTime"/>
				</view>
			</view>
			
			<view class="mt-4 bg-white rounded-xl p-4 text-gray-400">
				<view class="row">
					<text class="iconfont text-3xl">&#xe655;</text>
					<text class="text-base mt-2 ml-2">关灯时间</text>
				</view>
				<view class="mt-4">
					<view class="between text-base justify-center items-center">
						<text class="mx-10">小时</text>
						<text class="mx-5">:</text>
						<text class="mx-10">分钟</text>
					</view>
					<wd-datetime-picker-view type="time" v-model="times.offTime" customClass="bg-white" @change="offTime"/>
				</view>
			</view>
		</view>
		
		<view class="mt-2 mb-8 fixed w-full">
			<view class="center">
				<wd-button custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" @click="save">保存</wd-button>
			</view>
		</view>
	</view>
</template>

<script setup>
	import page from "@/api/uniapp/page.js";
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	
	const times = ref({onTime:"",offTime:""});
	
	onLoad((option)=>{
		let param = JSON.parse(decodeURIComponent(option.param));
		times.value.onTime = param.times.onTime;
		times.value.offTime = param.times.offTime;
	});
	
	const onTime = (v)=>{
		times.value.onTime = v.value;
	};
	const offTime = (v)=>{
		times.value.offTime = v.value;
	};
	const save = ()=>{
		uni.prePage().setTimes(times.value);
		page.navBack();
	}
</script>

<style>
</style>