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
	<wd-notify></wd-notify>
	<view class="relative px-4">
		<view class="mt-5 col justify-center items-center text-gray-400">
			<text class="text-base">运作中</text>
			<text class="iconfont text-6xl text-green-500 mt-1">&#xe858;</text>
			<view class="row justify-center items-center w-full text-base">
				<view class="col justify-center items-center" :class="rday==1 ? '' : 'opacity-25'" @tap="setRDay">
					<text class="iconfont text-3xl">&#xe61f;</text>
					<text class="text-base">日光</text>
				</view>
				<view class="col justify-center items-center flex-1 mt-2">
					<view class="row justify-center items-center" @tap="page.navigateTo('./kgtime',{times:{onTime:times.onTime,offTime:times.offTime}})">
						<text>开灯 {{times.onTime}}</text>
						<text class="ml-2">关灯 {{times.offTime}}</text>
						<text class="gui-icons ml-2">&#xe69e;</text>
					</view>
					<view class="row justify-center items-center mt-2" @tap="syncTime">
						<text class="rounded-2xl py-1 px-4 bg-gray-400 text-white text-base">手动同步时钟</text>
					</view>
				</view>
				<view class="col justify-center items-center" :class="rday==0 ? '' : 'opacity-25'" @tap="setRDay">
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
									<sliderc :ref="sds" :id="item.id" :obj="{groupId:group.id,id:item.id}" :max="item.max" :min="item.min" :barWidth="30" :barHeight="30" :barClass="item.style.barClass" :bglineClass="item.style.bglineClass" :bglineAClass="item.style.bglineAClass" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" :num="item.value" @changing="sdsChanging" @change=""></sliderc>
								</view>
								<view class="row w-16 justify-end items-end text-gray-400 -mt-2">
									<text>{{item.value}}</text>
									<text v-if="item.afe" class="ml-2">{{item.afe}}</text>
									<text v-else class="ml-1">%</text>
								</view>
							</view>
						</view>
						<view v-else-if="item.type=='textGroup'" class="col text-base between" :class="item.ly==0 ? 'mt-8' : 'mt-5'">
							<view v-for="(txt,txti) in item.info" :key="txt.id" class="row my-2">
								<text class="mr-2 -mt-1">{{txt.prx}}</text>
								<!-- <input :id="item.id + '_' + txt.id" class="ml-2 minput w-6" v-model="txt.value" type="number" :min="txt.min" :max="txt.max" input="checkInput"/> -->
								<view class="flex-1">
									<sliderc :id="txt.id" :obj="{groupId:group.id,id:item.id,infoId:txt.id}" :max="txt.max" :min="txt.min" :barWidth="30" :barHeight="30" :barClass="['border-gray-300','border-4','border-solid','bg-white','rounded-full']" :bglineClass="['border-gray-300','border-4','border-solid']" :bglineAClass="['bg-gray-300','border-gray-400','border-4','border-solid']" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" :num="txt.value" @changing="infoChanging" @change=""></sliderc>
								</view>
								<text class="-mt-1">{{txt.value}} {{txt.afe}}</text>
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
	import { ConnectController } from '@/api/bluebooth/controller.js';
	import hexTools from "@/api/hexTools.js";
	
	import lodash from "lodash";
	import dialog from "@/api/uniapp/dialog.js";
	import cmdjson from "@/api/datas/cmd.json";
	
	const {proxy} = getCurrentInstance();
	
	import { useNotify } from '@/uni_modules/wot-design-uni';
	const { showNotify, closeNotify } = useNotify();
	
	const times = ref({onTime:"8:00",offTime:"18:00"});
	const pgElmList = ref([]);
	const sdsArray = ref([]);
	const sds = (el) => {
		sdsArray.value.push(el);
	}
	const rday = ref(1);
	
	let cday = null;
	let intervalId = null;
	let loadingTime = null;
	let isWriteCmd = false;
	
	const loopLoading = ()=>{
		intervalId = setInterval(()=>{
			if (uni.dayjs().isAfter(cday.add(5,"second"))) {
				dialog.closeLoading();
				clearInterval(intervalId);
				dialog.alertBack("设置完成",false,()=>{
					page.navBack();
				},null)
			}
		},1000);
	};
	
	onLoad((option)=>{
		pgElmList.value = [
			{id:0,name:"light",els:[
				{id:"01",cmd:"0x04",rcmd:"0x14",isRun:true,ly:0,type:"slider",name:"全光谱",value:40,min:0,max:100,style:{barClass:['border-green-500','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-green-500','border-green-500','border-4','border-solid']}},
				{id:"02",cmd:"0x07",rcmd:"0x17",isRun:true,ly:0,type:"slider",name:"UVA",value:40,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"03",cmd:"0x06",rcmd:"0x16",isRun:true,ly:0,type:"slider",name:"UVB",value:40,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"04",cmd:"0x10",rcmd:"0x10",ly:0,isRun:false,type:"slider",name:"开灯关灯渐变时长（日出日落）",value:0,min:0,max:120,afe:"分",style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}}
			]},
			{id:1,name:"fs",els:[
				{id:"10",cmd:"0x08",rcmd:"0x18",isRun:true,ly:0,type:"slider",name:"日间风扇（开灯时风扇）",value:35,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"11",dId:"12",cmd:"",ly:1,type:"switch",name:"定时循环",value:false,style:{}},
				{id:"12",cmd:"0x0B",rcmd:"0x1B",isRun:false,ly:1,type:"textGroup",isRun:false,info:[
					{id:"120",prx:"运转时长",value:0,afe:"分钟",min: 0, max: 15},
					{id:"121",prx:"停歇时长",value:0,afe:"分钟",min: 0, max: 15}
				]},
				{id:"13",cmd:"0x09",rcmd:"0x19",isRun:true,ly:0,type:"slider",name:"夜间风扇（关灯时风扇）",value:25,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"14",dId:"15",cmd:"",ly:1,type:"switch",name:"定时循环",value:true,style:{}},
				{id:"15",cmd:"0x0C",rcmd:"0x1C",isRun:true,ly:1,type:"textGroup",isRun:true,info:[
					{id:"150",prx:"运转时长",value:0,afe:"分钟",min: 0, max: 15},
					{id:"151",prx:"停歇时长",value:0,afe:"分钟",min: 0, max: 15}
				]}
			]},
			{id:2,name:"cw",els:[
				{id:"20",dId:"21",cmd:"",ly:0,type:"switch",name:"开灯除雾",value:true,style:{}},
				{id:"21",cmd:"0x0D",rcmd:"0x1D",isRun:true,ly:1,type:"textGroup",isRun:true,info:[
					{id:"210",prx:"除雾时长",value:4,afe:"分钟",min: 0, max: 60},
				]},
				{id:"22",dId:"23",cmd:"",ly:0,type:"switch",name:"月光模式",value:false,style:{}},
				{id:"23",cmd:"0x0F",rcmd:"0x1F",isRun:true,ly:1,type:"slider",name:"亮度",value:0,min:0,max:100,style:{barClass:['border-indigo-500','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-indigo-300','border-4','border-solid'],bglineAClass:['bg-indigo-300','border-findigo-500','border-4','border-solid']}}
			]}
		];
		
		ConnectController.addCharacteristicValueChangeListen((characteristic)=>{
			console.log("addCharacteristicValueChangeListen_",hexTools.arrayBuffer2hex(characteristic.value));
			console.log("addCharacteristicValueChangeListen_ay",hexTools.arrayBuffer2hexArray(characteristic.value));
			if (isWriteCmd) {
				cday = uni.dayjs();
				let array = hexTools.arrayBuffer2hexArray(characteristic.value);
				let cmd = lodash.find(cmdjson.commands,(o)=>{return o.command.toUpperCase()==("0x"+array[1]).toUpperCase()});
				if (cmd) {
					if (array[2].toUpperCase()=="FE" && array[3].toUpperCase()=="00") {
						showNotify(cmd.description+":成功设置");
					} else {
						showNotify(cmd.description+":设置失败");
					}
				}
			} else {
				let array = hexTools.arrayBuffer2hexArray(characteristic.value);
				let cmd = lodash.find(cmdjson.commands,(o)=>{return o.command.toUpperCase()==("0x"+array[1]).toUpperCase()});
				findPgElmList("rcmd",("0x"+array[1]).toUpperCase(),(it)=>{
					
				},true);
			}
		});
	});
	
	const checkInput = (e)=>{
		// if(value>max) value=max;
		// if(value<min) value=min;
		// return value;
		let value = e.detail.value;
		if(value>100) value=100;
		if(value<0) value=0;
		console.log(value);
		return value;
	};
	
	const sdsChanging = (value)=>{
		// console.log(value);
		// findPgElmList("id",value.id,(it)=>{
		// 	it.value = value.v;
		// },true);
		let g = lodash.find(pgElmList.value,(o)=>{return o.id==value.obj.groupId});
		let it = lodash.find(g.els,(o)=>{return o.id==value.obj.id});
		it.value = value.v;
	};
	
	const infoChanging = (value)=>{
		let g = lodash.find(pgElmList.value,(o)=>{return o.id==value.obj.groupId});
		let item = lodash.find(g.els,(o)=>{return o.id==value.obj.id});
		let it = lodash.find(item.info,(o)=>{return o.id==value.obj.infoId});
		it.value = value.v;
	};
	
	const setTimes = (v)=>{
		times.value = v;
	};
	
	const setRDay = ()=>{
		rday.value = rday.value==0 ? 1 : 0;
	};
	
	const syncTime = ()=>{
		// console.log(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))));
		Blue.writeBLEValue(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))).buffer);
	};
	
	let findPgElmList = (field,value,fun,firstOnly)=>{
		// lodash.forEach(pgElmList.value,(o,g)=>{
		// 	lodash.forEach(o.els,(it,j)=>{
		// 		if (it[field]==value) {
		// 			fun(it);
		// 		}
		// 	});
		// });
		for(let g of pgElmList.value) {
			for(let it of g.els) {
				if (it[field]==value) {
					fun(it);
					if (firstOnly) {
						break;
					}
				}
			}
		}
	};
	
	const save = ()=>{
		dialog.openLoading("与设备交互中……");
		cday = uni.dayjs();
		loopLoading();
		
		let onTimeAy = times.value.onTime.split(":");
		let offTimeAy  = times.value.offTime.split(":");
		Blue.writeBLEValue(hexTools.bleBuffer("0x02",parseInt(onTimeAy[0]),parseInt(onTimeAy[1])).buffer);
		setTimeout(()=>{
			Blue.writeBLEValue(hexTools.bleBuffer("0x03",parseInt(offTimeAy[0]),parseInt(offTimeAy[1])).buffer);
		},3000);
		
		setTimeout(()=>{
			Blue.writeBLEValue(hexTools.bleBuffer("0x0E",0,parseInt(rday)).buffer);
		},3000);
		
		
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
							setTimeout(()=>{
								Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,0,parseInt(v.value)).buffer);
							},3000);
						}
						if (v.type=="textGroup") {
							let d1 = 0;
							let d2 = 0;
							if (v.info.length>1) {
								d1 = v.info[0].value;
								d2 = v.info[1].value;
							} else {
								d1 = 0;
								d2 = v.info[0].value;
							}
							setTimeout(()=>{
								Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,parseInt(d1),parseInt(d2)).buffer);
							},3000);
						}
					} else {
						setTimeout(()=>{
							Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,0,0).buffer);
						},3000);
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