<template>
	<view class="m-1">
		<view class="px-14 bg-white">
			<wd-datetime-picker-view label="日期选择" type="time" customClass="bg-white"/>
		</view>
		
		<view class="flex flex-row flex-nowrap bg-white rounded-lg p-4">
			<view class="flex-1">
				<sliderc :barWidth="30" :barHeight="30" :barClass="['border-green-500','border-4','border-solid','bg-white','rounded-full']" :bglineClass="['border-gray-300','border-4','border-solid']" :bglineAClass="['bg-green-500','border-green-500','border-4','border-solid']" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" @changing="changeTest" @change="changed"></sliderc>
			</view>
			<view class="w-12 ml-2 center">
				<text class="text-color">{{changeValue}} %</text>
			</view>
		</view>
		<view>
			<text class="btn-style" bg="blue-500">test</text>
		</view>
		<!-- <view class="flex flex-row center">
			<view class="bg-green-500 w-20 p-2 m-1 text-white center rounded-2xl">开灯</view>
			<view class="bg-blue-500 w-28 p-2 m-1 text-white center rounded-2xl">关灯</view>
		</view>
		<view class="demo-banner demo-bg gui-dark-bg-level-3 gui-border-radius"></view>
		<view class="demo-space"></view>
		<view class="gui-flex gui-row gui-nowrap gui-space-between">
			<view 
			v-for="(item, index) in [1,2,3,4,5]" 
			:key="index" 
			class="demo-grid demo-bg gui-dark-bg-level-3 gui-border-radius"></view>
		</view>
		<view class="demo-space"></view>
		<view class="gui-flex gui-row gui-nowrap gui-space-between">
			<view 
			class="demo-card demo-bg gui-dark-bg-level-3 gui-border-radius" 
			style="height:406rpx;"></view>
			<view>
				<view
				class="demo-card demo-bg gui-dark-bg-level-3 gui-border-radius"></view>
				<view 
				style="margin-top:30rpx;"
				class="demo-card demo-bg gui-dark-bg-level-3 gui-border-radius"></view>
			</view>
		</view>
		<view class="demo-space"></view>
		<view class="gui-flex gui-row gui-nowrap gui-space-between">
			<view>
				<view
				class="demo-card demo-bg gui-dark-bg-level-3 gui-border-radius"></view>
				<view 
				style="margin-top:30rpx;"
				class="demo-card demo-bg gui-dark-bg-level-3 gui-border-radius"></view>
			</view>
			<view 
			class="demo-card demo-bg gui-dark-bg-level-3 gui-border-radius" 
			style="height:406rpx;"></view>
		</view> -->
	</view>
</template>

<script>
	// import { Locale } from 'wot-design-uni';

	export default {
		name:"dsetup",
		data() {
			return {
				changeValue: 0
			};
		},
		methods:{
			changeTest(e) {
				this.changeValue = e;
			},
			changed(e) {
				console.log(e);
			}
		}
	}
</script>

<style scoped>
.demo-bg{background-color:#EFF1F6;}
.demo-banner{height:288rpx;}
.demo-space{height:30rpx;}
.demo-grid{width:106rpx; height:106rpx;}
.demo-card{width:330rpx; height:188rpx;}
.text-color{color:#7993AF}

.btn-style{
	/* @apply p-1 bg-blue-500 rounded-2xl */
}
</style>