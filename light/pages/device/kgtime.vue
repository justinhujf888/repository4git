<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="justify-center items-center">
				<text class="text-base">开关灯定时</text>
			</view>
		</template>
	</wd-navbar>
	<wd-notify></wd-notify>
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
		
		<view class="absolute right-5 top-1">
			<wd-button size="small" custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" @click="save">保存</wd-button>
		</view>
	</view>
</template>

<script setup>
	import page from "@/api/uniapp/page.js";
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import { useNotify } from '@/uni_modules/wot-design-uni';
	
	const { showNotify, closeNotify } = useNotify();
	
	const times = ref({onTime:"",offTime:""});
	
	let cvalue = null;
	
	onLoad((option)=>{
		let param = JSON.parse(decodeURIComponent(option.param));
		times.value = param.times;
		cvalue = param.cvalue;
		// console.log(times.value,cvalue);
	});
	
	const onTime = (v)=>{
		times.value.onTime = v.value;
	};
	const offTime = (v)=>{
		times.value.offTime = v.value;
	};
	const save = ()=>{
		let onTimeAy = times.value.onTime.split(":");
		let offTimeAy  = times.value.offTime.split(":");
		let onTime_h = parseInt(onTimeAy[0]);
		let onTime_s = parseInt(onTimeAy[1]);
		let offTime_h = parseInt(offTimeAy[0]);
		let offTime_s = parseInt(offTimeAy[1]);
		
		if (uni.prePage().checkKgTimeAndC(onTime_h,onTime_s,offTime_h,offTime_s,cvalue)) {
			times.value = {onTime:times.value.onTime,offTime:times.value.offTime,values:{onTime:{v0:onTime_h,v1:onTime_s},offTime:{v0:offTime_h,v1:offTime_s}}};
			uni.prePage().setTimes(times.value);
			page.navBack();
		} else {
			showNotify("设定错误请重新确认。");
		}
	}
</script>

<style>
</style>