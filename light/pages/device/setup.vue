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
					<view class="row justify-center items-center" @tap="page.navigateTo('./kgtime',{times:{onTime:times.onTime,offTime:times.offTime}})">
						<text>开灯{{times.onTime}}  关灯{{times.offTime}}</text>
						<text class="gui-icons ml-2">&#xe69e;</text>
					</view>
					<view class="row justify-center items-center mt-2" @tap="syncTime">
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
									<sliderc :ref="sds" :id="item.id" :barWidth="30" :barHeight="30" :barClass="item.style.barClass" :bglineClass="item.style.bglineClass" :bglineAClass="item.style.bglineAClass" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" :num="item.value" @changing="sdsChanging" @change=""></sliderc>
								</view>
								<view class="row w-14 justify-end items-end text-gray-400 -mt-2">
									<text>{{item.value}}</text>
									<text class="ml-1">%</text>
								</view>
							</view>
						</view>
						<view v-else-if="item.type=='textGroup'" class="row text-base between" :class="item.ly==0 ? 'mt-8' : 'mt-5'">
							<view v-for="(txt,txti) in item.info" :key="txt.id" class="row">
								<text>{{txt.prx}}</text>
								<!-- <text class="ml-2">{{txt.value}}</text> -->
								<input class="ml-2 minput w-6" v-model="txt.value" type="number"/>
								<text class="ml-2">{{txt.afe}}</text>
							</view>
						</view>
						<view v-else-if="item.type=='switch'" class="row text-xl between" :class="item.ly==0  && i>0 ? 'mt-10' : 'mt-5'">
							<text class="text-xl" :class="item.ly==0 ? 'text-gray-900' : ''">{{item.name}}</text>
							<wd-switch v-model="item.value" active-color="#13ce66"/>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<view class="center mt-8 mb-8">
			<wd-button custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" @click="save">保存</wd-button>
		</view>
	</view>
</template>

<script setup>
	import page from "@/api/uniapp/page.js";
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import { Blue } from '@/api/bluebooth/index.js';
	import hexTools from "@/api/hexTools.js";
	
	import lodash from "lodash";
	// import dayjs from "@/api/dayjs.min.js";
	
	const {proxy} = getCurrentInstance();
	
	const times = ref({onTime:"9:00",offTime:"17:30"});
	const pgElmList = ref([]);
	const sdsArray = ref([]);
	const sds = (el) => {
		sdsArray.value.push(el);
	}
	
	onLoad((option)=>{
		pgElmList.value = [
			{id:0,name:"light",els:[
				{id:"01",cmd:"0x04",isRun:true,ly:0,type:"slider",name:"全光谱",value:80,style:{barClass:['border-green-500','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-green-500','border-green-500','border-4','border-solid']}},
				{id:"02",cmd:"0x07",isRun:true,ly:0,type:"slider",name:"UVA",value:10,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"03",cmd:"0x06",isRun:true,ly:0,type:"slider",name:"UVB",value:30,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"04",cmd:"",ly:0,isRun:false,type:"slider",name:"开灯关灯渐变时长（日出日落）",value:50,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}}
			]},
			{id:1,name:"fs",els:[
				{id:"10",cmd:"0x08",isRun:true,ly:0,type:"slider",name:"日间风扇（开灯时风扇）",value:80,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"11",dId:"12",cmd:"",ly:1,type:"switch",name:"定时循环",value:false,style:{}},
				{id:"12",cmd:"0x0B",isRun:false,ly:1,type:"textGroup",isRun:false,info:[
					{id:0,prx:"运转时长",value:"---",afe:""},
					{id:1,prx:"停歇时长",value:"---",afe:""}
				]},
				{id:"13",cmd:"0x09",isRun:true,ly:0,type:"slider",name:"夜间风扇（关灯时风扇）",value:30,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"14",dId:"15",cmd:"",ly:1,type:"switch",name:"定时循环",value:true,style:{}},
				{id:"15",cmd:"0x0c",isRun:true,ly:1,type:"textGroup",isRun:true,info:[
					{id:0,prx:"运转时长",value:"10",afe:"分钟"},
					{id:1,prx:"停歇时长",value:"2",afe:"分钟"}
				]}
			]},
			{id:2,name:"cw",els:[
				{id:"20",dId:"21",cmd:"",ly:0,type:"switch",name:"开灯除雾",value:true,style:{}},
				{id:"21",cmd:"0x0D",isRun:true,ly:1,type:"textGroup",isRun:true,info:[
					{id:0,prx:"除雾时长",value:"30",afe:"秒"},
				]},
				{id:"22",dId:"23",cmd:"",ly:0,type:"switch",name:"月光模式",value:false,style:{}},
				{id:"23",cmd:"",isRun:false,ly:1,type:"slider",name:"亮度",value:30,isRun:true,style:{barClass:['border-fuchsia-500','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-fuchsia-300','border-4','border-solid'],bglineAClass:['bg-fuchsia-500','border-fuchsia-500','border-4','border-solid']}}
			]}
		];
	});
	
	const sdsChanging = (value) => {
		// console.log(value);
		for(let g of pgElmList.value) {
			for(let v of g.els) {
				if (v.id==value.id) {
					v.value = value.v;
					break;
				}
			}
		}
	// lodash.forEach(pgElmList.value,(g,j)=>{
		// 	lodash.forEach(g.els,(v,i)=>{
		// 		console.log(v.id,value.id);
		// 		if (v.id==value.id) {
		// 			v.value = value.v;
		// 			return;
		// 		}
		// 	});
		// });
	};
	
	const setTimes = (v)=>{
		times.value = v;
		let onTimeAy = v.onTime.split(":");
		let offTimeAy  = v.offTime.split(":");
		Blue.writeBLEValue(hexTools.bleBuffer("0x02",parseInt(onTimeAy[0]),parseInt(onTimeAy[1])).buffer);
		setTimeout(()=>{
			Blue.writeBLEValue(hexTools.bleBuffer("0x03",parseInt(offTimeAy[0]),parseInt(offTimeAy[1])).buffer);
		},2000);
	};
	
	const syncTime = ()=>{
		let cday = uni.dayjs();
		// console.log(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))));
		Blue.writeBLEValue(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))).buffer);
	};
	
	let findArray = (id,value)=>{
		lodash.forEach(pgElmList.value,(o,g)=>{
			lodash.forEach(o.els,(it,j)=>{
				if (it.id==id) {
					it.isRun = value;
				}
			});
		});
	};
	
	const save = ()=>{
		for(let g of pgElmList.value) {
			for(let v of g.els) {
				if (v.type=="switch") {
					findArray(v.dId,v.value);
				}
			}
		}
		// console.log(pgElmList.value);return;
		
		for(let g of pgElmList.value) {
			for(let v of g.els) {
				if (v.cmd!="") {
					if (v.isRun) {
						if (v.type=="slider") {
							Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,0,parseInt(v.value)).buffer);
						}
						if (v.type=="textGroup") {
							
						}
					} else {
						Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,0,0).buffer);
					}
				}
			}
		}
	}
	
	defineExpose({
		setTimes
	});
</script>

<style>
	
</style>