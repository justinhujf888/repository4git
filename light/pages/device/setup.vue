<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="justify-center items-center">
				<text class="text-base">20缸默认</text>
				<text class="gui-icons text-gray-400 ml-2">&#xe69e;</text>
			</view>
		</template>
	</wd-navbar>
	<!-- &#xe858; &#xe655;-->
	<view class="relative h-screen px-4">
		<view class="mt-5 col justify-center items-center text-cyan-900 text-base leading-8">
			<text class="">运作中</text>
			<text class="iconfont text-2xl bg-green-500">&#xe611;</text>
			<view class="row justify-center items-center w-full">
				<view class="col justify-center items-center">
					<text class="iconfont text-2xl">&#xe61f;</text>
					<text>日光</text>
				</view>
				<view class="col justify-center items-center flex-1">
					<view class="row justify-center items-center">
						<text>开灯9:00  关灯17:30</text>
						<text class="gui-icons ml-2">&#xe69e;</text>
					</view>
					<view class="row justify-center items-center">
						<text class="rounded-lg p-1 bg-cyan-700 text-white text-xs">手动同步时钟</text>
					</view>
				</view>
				<view class="col justify-center items-center">
					<text class="iconfont text-2xl">&#xe611;</text>
					<text>月光</text>
				</view>
			</view>
		</view>
		<view>
			<sliderc :barWidth="30" :barHeight="30" :barClass="['border-green-500','border-4','border-solid','bg-white','rounded-full']" :bglineClass="['border-gray-300','border-4','border-solid']" :bglineAClass="['bg-green-500','border-green-500','border-4','border-solid']" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" @changing="changeTest" @change="changed"></sliderc>
		</view>
	</view>
</template>

<script setup>
	import page from "@/api/uniapp/page.js";
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import lodash from "lodash";
	
	
	onLoad((option)=>{
		
	});
</script>

<style>

</style>