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
		<view class="mt-5 col justify-center items-center text-gray-400">
			<text class="text-base">运作中</text>
			<text class="iconfont text-6xl text-green-500 mt-1">&#xe858;</text>
			<view class="row justify-center items-center w-full text-xl">
				<view class="col justify-center items-center">
					<text class="iconfont text-3xl">&#xe61f;</text>
					<text class="text-base">日光</text>
				</view>
				<view class="col justify-center items-center flex-1 mt-2">
					<view class="row justify-center items-center">
						<text>开灯9:00  关灯17:30</text>
						<text class="gui-icons ml-2">&#xe69e;</text>
					</view>
					<view class="row justify-center items-center mt-2">
						<text class="rounded-2xl py-1 px-4 bg-gray-400 text-white text-base">手动同步时钟</text>
					</view>
				</view>
				<view class="col justify-center items-center">
					<text class="iconfont text-3xl">&#xe655;</text>
					<text class="text-base">月光</text>
				</view>
			</view>
		</view>
		<view class="mt-2">
			<view v-for="(group,i) in pgElmList" :key="group.id" class="mt-4 bg-white rounded-xl p-4 text-gray-500">
				<view v-for="(item,i) in group.els" :key="group.id" class="mt-6 mb-6">
					<view class="">
						<view v-if="item.type=='slider'" class="col text-xl" :class="item.ly==0 && i>0 ? 'mt-10' : 'mt-5'">
							<text class="text-xl" :class="item.ly==0 ? 'text-gray-900' : ''">{{item.name}}</text>
							<view class="row mt-5">
								<view class="flex-1">
									<sliderc :barWidth="30" :barHeight="30" :barClass="item.style.barClass" :bglineClass="item.style.bglineClass" :bglineAClass="item.style.bglineAClass" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" :num="item.value" @changing="" @change=""></sliderc>
								</view>
								<view class="row w-14 justify-end items-end text-gray-400 -mt-2">
									<text>{{item.value}}</text>
									<text class="ml-1">%</text>
								</view>
							</view>
						</view>
						<view v-else-if="item.type=='textGroup'" class="row text-xl between" :class="item.ly==0 ? 'mt-8' : 'mt-5'">
							<view v-for="(txt,txti) in item.info" :key="txt.id" class="row">
								<text>{{txt.prx}}</text>
								<text class="ml-2">{{txt.value}}</text>
								<text class="ml-2">{{txt.afe}}</text>
							</view>
						</view>
						<view v-else-if="item.type=='switch'" class="row text-xl between" :class="item.ly==0  && i>0 ? 'mt-10' : 'mt-5'">
							<text class="text-xl" :class="item.ly==0 ? 'text-gray-900' : ''">{{item.name}}</text>
							<wd-switch v-model="item.value" active-color="#13ce66" />
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import page from "@/api/uniapp/page.js";
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import lodash from "lodash";
	
	const pgElmList = ref([]);
	
	
	onLoad((option)=>{
		pgElmList.value = [
			{id:0,name:"light",els:[
				{id:"0",ly:0,type:"slider",name:"全光谱",value:80,style:{barClass:['border-green-500','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-green-500','border-green-500','border-4','border-solid']}},
				{id:"1",ly:0,type:"slider",name:"UVA",value:10,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"2",ly:0,type:"slider",name:"UVB",value:30,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"3",ly:0,type:"slider",name:"开灯关灯渐变时长（日出日落）",value:50,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}}
			]},
			{id:1,name:"fs",els:[
				{id:"0",ly:0,type:"slider",name:"日间风扇（开灯时风扇）",value:80,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"00",ly:1,type:"switch",name:"定时循环",value:false,style:{}},
				{id:"01",ly:1,type:"textGroup",info:[
					{id:0,prx:"运转时长",value:"---",afe:""},
					{id:1,prx:"停歇时长",value:"---",afe:""}
				]},
				{id:"1",ly:0,type:"slider",name:"夜间风扇（关灯时风扇）",value:30,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"10",ly:1,type:"switch",name:"定时循环",value:true,style:{}},
				{id:"11",ly:1,type:"textGroup",info:[
					{id:0,prx:"运转时长",value:"10",afe:"分钟"},
					{id:1,prx:"停歇时长",value:"2",afe:"分钟"}
				]}
			]},
			{id:2,name:"cw",els:[
				{id:"0",ly:0,type:"switch",name:"开灯除雾",value:true,style:{}},
				{id:"00",ly:1,type:"textGroup",info:[
					{id:0,prx:"除雾时长",value:"30",afe:"秒"},
				]},
				{id:"1",ly:0,type:"switch",name:"月光模式",value:true,style:{}},
				{id:"10",ly:1,type:"slider",name:"亮度",value:30,style:{barClass:['border-fuchsia-500','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-fuchsia-300','border-4','border-solid'],bglineAClass:['bg-fuchsia-500','border-fuchsia-500','border-4','border-solid']}}
			]}
		];
		// console.log(pgElmList.value);
	});
</script>

<style>

</style>