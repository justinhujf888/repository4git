<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="leftClick()">
		<template #title>
			<view class="justify-center items-center">
				<text class="text-sm">{{device?.name}}-{{deviceScript?.name}}</text>
				<!-- <text class="gui-icons text-gray-400 ml-2">&#xe69e;</text> -->
			</view>
		</template>
	</wd-navbar>
	<!-- &#xe858; &#xe655;-->
	<wd-notify></wd-notify>
	<view v-if="isWriteCmd" class="relative px-4">
<!-- 		<view class="absolute left-2 top-1">
			<wd-button @click="test">0x11</wd-button>
		</view> -->
		<view class="mt-5 col justify-center items-center text-gray-400">
			<!-- <text class="text-base">{{rday==1 ? '照明开启中' : '照明关闭'}}</text> -->
			<view class="row justify-center items-center mt-2 text-sm">
				<text>当前时钟</text>
				<text class="ml-1">{{currentTime}}</text>
				<text class="rounded-2xl py-1 px-4 btn1 text-white ml-1" @tap="syncTime">手动同步时钟</text>
			</view>
			<wd-button size="large" custom-class="py-1 px-2 text-6xl text-white mt-4" :custom-style="rday==1 ? 'background: #7993AF' : 'background: #6AAE36'" @click="setRDay">{{rday==1 ? '关闭照明' : '打开照明'}}</wd-button>
			<view class="row justify-center items-center w-full text-base">
				<view class="col justify-center items-center" :class="rday==1 ? '' : 'opacity-15'">
					<text class="iconfont text-3xl text-green-500">&#xe61f;</text>
					<text class="text-base text-green-500 font-semibold">日光</text>
				</view>
				<view class="col justify-center items-center flex-1 mt-2">
					<view class="row justify-center items-center" @tap="goKgtime">
						<text>开灯 {{times.onTime}}</text>
						<text class="ml-2">关灯 {{times.offTime}}</text>
						<text class="gui-icons ml-2">&#xe69e;</text>
					</view>
				</view>
				<view class="col justify-center items-center" :class="rday==0 ? '' : 'opacity-15'">
					<text class="iconfont text-3xl">&#xe655;</text>
					<text class="text-base font-semibold">月光</text>
				</view>
			</view>
		</view>
		<view class="mt-2">
			<view v-for="(group,i) in pgElmList" :key="group.id" class="mt-2 mb-2 bg-white rounded-xl p-4 text-gray-500">
				<view v-for="(item,i) in group.els" :key="group.id" class="mt-6 mb-6">
					<view class="">
						<view v-if="item.type=='slider'" class="col text-xl" :class="item.ly==0 && i>0 ? 'mt-10' : 'mt-5'">
							<text class="text-xl" :class="item.ly==0 ? 'text-gray-900' : ''">{{item.name}}</text>
							<view class="row mt-5">
								<view class="flex-1">
									<sliderc :id="item.id" :obj="{groupId:group.id,id:item.id}" :max="item.max" :min="item.min" :barWidth="50" :barHeight="50" :barClass="item.style.barClass" :bglineClass="item.style.bglineClass" :bglineAClass="item.style.bglineAClass" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" :num="item.value" @changing="sdsChanging" @change="sdsChange"></sliderc>
								</view>
								<view class="row w-16 justify-end items-end text-gray-400 -mt-2">
									<text>{{item.value}}</text>
									<text v-if="item.afe" class="ml-2">{{item.afe}}</text>
									<text v-else class="ml-1">%</text>
								</view>
							</view>
						</view>
						<view v-else-if="item.type=='textGroup'" :sstyle="item.isRun ? 'pointer-events:' : 'pointer-events:none'" class="col text-base between" :class="item.ly==0 ? 'mt-8' : 'mt-5'">
							<view v-for="(txt,txti) in item.info" :key="txt.id" class="row my-2">
								<text class="mr-2 -mt-1">{{txt.prx}}</text>
								<!-- <input :id="item.id + '_' + txt.id" class="ml-2 minput w-6" v-model="txt.value" type="number" :min="txt.min" :max="txt.max" input="checkInput"/> -->
								<view v-if="txt.type=='slider'" class="flex-1" :class="item.isRun ? '' : 'bg-gray-200'">
									<sliderc :id="txt.id" :obj="{groupId:group.id,id:item.id,infoId:txt.id,cmd:item.cmd}" :max="txt.max" :min="txt.min" :canSlide="item.isRun" :barWidth="50" :barHeight="50" :barClass="['border-gray-300','border-4','border-solid','bg-white','rounded-full']" :bglineClass="['border-gray-300','border-4','border-solid']" :bglineAClass="['bg-gray-300','border-gray-400','border-4','border-solid']" :bglineSize="12" :borderHeight="8" :borderWidth="4" barText="" :num="txt.value" @changing="infoChanging" @change="infoChange"></sliderc>
								</view>
								<view class="row justify-start items-center pl-2 w-16 -mt-1">
									<text class="w-6 text-right">{{txt.value}}</text>
									<text class="ml-1 flex-1">{{txt.afe}}</text>
								</view>
							</view>
						</view>
						<view v-else-if="item.type=='radioGroup'" :sstyle="item.isRun ? 'pointer-events:' : 'pointer-events:none'" class="col text-base between" :class="item.ly==0 ? 'mt-8' : 'mt-5'">
							<text class="text-xl" :class="item.ly==0 ? 'text-gray-900' : ''">{{item.name}}</text>
							<wd-radio-group v-model="item.value" size="small" inline shape="button" @change="radioChange">
								<wd-radio v-for="(txt,txti) in item.info" :key="txt.id" :value="txt.value">{{txt.value}}</wd-radio>
							</wd-radio-group>
						</view>
						<view v-else-if="item.type=='switch'" class="row text-xl between" :class="item.ly==0  && i>0 ? 'mt-10' : 'mt-5'">
							<text class="text-xl" :class="item.ly==0 ? 'text-gray-900' : ''">{{item.name}}</text>
							<dsetup :value="item.value" :obj="{groupId:group.id,id:item.id}" @change="switchChange"/>
						</view>
					</view>
				</view>
			</view>
		</view>
		
<!-- 		<view class="center mt-8 mb-8">
			<wd-button custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" @click="save">保存</wd-button>
		</view> -->
	</view>
	
	<view v-if="isWriteCmd && deviceScript">
		<wd-tabbar fixed bordered safeAreaInsetBottom placeholder>
			<wd-tabbar-item title="">
				<template #icon>
					<view>
						<wd-button custom-class="py-1 px-2 text-white" custom-style="#6AAE36" @click="saveScript">保存到方案</wd-button>
					</view>
				</template>
			</wd-tabbar-item>
		</wd-tabbar>
	</view>
	
</template>

<script setup>
	import page from "@/api/uniapp/page.js";
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import { Blue } from '@/api/bluebooth/index.js';
	import { ConnectController } from '@/api/bluebooth/controller.js';
	import hexTools from "@/api/hexTools.js";
	import { Beans } from '@/api/dbs/beans';
	import deviceRest from "@/api/dbs/device.js";
	
	import lodash from "lodash";
	import isBetween from 'dayjs/plugin/isBetween';
	import dialog from "@/api/uniapp/dialog.js";
	import cmdjson from "@/api/datas/cmd.json";
	
	const {proxy} = getCurrentInstance();
	
	import { useNotify } from '@/uni_modules/wot-design-uni';
	const { showNotify, closeNotify } = useNotify();
	
	const device = ref({});
	const isWriteCmd = ref(false);
	const currentTime = ref("");
	const times = ref({onTime:"8:00",offTime:"18:00",values:{onTime:{v0:parseInt(8),v1:parseInt(0)},offTime:{v0:parseInt(18),v1:parseInt(0)}}});
	const fengsanZuanSu = ref(0);
	const pgElmList = ref([]);
	
	const sdsArray = ref([]);
	const sds = (el) => {
		sdsArray.value.push(el);
	}
	
	const rday = ref(1);
	
	let oldValue = null;
	let cday = null;
	let intervalId = null;
	let loadingTime = null;
	let deviceScript = null;
	// 这里的areScript;0：直接进入控制台；1：从已经有的方案进入控制台
	
	
	// let csTestdata = ["0xA6", "0x11", "0x00", "0x01", "0xE1", "0xA6", "0x12", "0x00", "0x01", "0xE1", "0xA6", "0x13", "0x00", "0x01", "0xE1", "0xA6", "0x14", "0x00", "0x01", "0xE1", "0xA6", "0x15", "0x00", "0x01", "0xE1", "0xA6", "0x16", "0x00", "0x01", "0xE1", "0xA6", "0x17", "0x00", "0x01", "0xE1", "0xA6", "0x18", "0x00", "0x01", "0xE1", "0xA6", "0x19", "0x00", "0x01", "0xE1", "0xA6", "0x1A", "0x00", "0x01", "0xE1", "0xA6", "0x1B", "0x00", "0x01", "0xE1", "0xA6", "0x1C", "0x00", "0x01", "0xE1", "0xA6", "0x1D", "0x00", "0x01", "0xE1", "0xA6", "0x1E", "0x00", "0x01", "0xE1", "0xA6", "0x1F", "0x00", "0x01", "0xE1", "0xA6", "0x20", "0x00", "0x01", "0xE1"];
	let stepIndex = 0;
	let say = [];
	let readInfoArray = [];
	const readDeviceInfo = (fun)=>{
		// for(let c of cmdjson.query_commands) {
		// 	setTimeout(()=>{
		// 		Blue.writeBLEValue(hexTools.bleBuffer(c.command,0,0).buffer);
		// 	},800);
		// }
		if (stepIndex < cmdjson.query_commands.length) {
			Blue.writeBLEValue(hexTools.bleBuffer(cmdjson.query_commands[stepIndex].command,0,0).buffer);
			stepIndex = stepIndex + 1;
		} else {
			if (fun) {
				fun();
			}
		}
	};
	
	const loopLoading = ()=>{
		intervalId = setInterval(()=>{
			let now = proxy.dayjs();
			let td1 = proxy.dayjs(now);
			let td2 = proxy.dayjs(now);
			
			if (cday && now.isAfter(cday.add(8,"second"))) {
				console.log("group",now.format("HH:mm:ss"),readInfoArray);
				isWriteCmd.value = true;
				let array = lodash.chunk(readInfoArray,5);
				console.log("group2",now.format("HH:mm:ss"),array);
				
				if (array?.length < 1) {
					dialog.alertBack("没有读取到设备数据，请重试",false,()=>{
						page.navBack();
					},null);
				}
				let checkf = true;
				lodash.forEach(array,(v,i)=>{
					if (v[0]!="0xA6") {
						checkf = false;
					}
				});
				if (!checkf) {
					dialog.alertBack("设备数据错误，请重试或联系客服",false,()=>{
						page.navBack();
					},null);
				}
				
				for(let ay of array) {
					if (ay[0]=="0xA6") {
						if (lodash.findIndex(cmdjson.query_commands,(o)=>{return o.command==ay[1]}) > -1) {
							//read command
							if (ay[1]=="0x11") {
								let td = proxy.dayjs();
								td = td.hour(parseInt(ay[2],16));
								td = td.minute(parseInt(ay[3],16));
								currentTime.value = td.format("HH:mm");
							} else if (ay[1]=="0x12") {
								td1 = td1.hour(parseInt(ay[2],16));
								td1 = td1.minute(parseInt(ay[3],16));
								times.value.onTime = td1.format("HH:mm");
							} else if (ay[1]=="0x13") {
								td2 = td2.hour(parseInt(ay[2],16));
								td2 = td2.minute(parseInt(ay[3],16));
								times.value.offTime = td2.format("HH:mm");
							} else if (ay[1]=="0x1A") {
								fengsanZuanSu.value = parseInt(ay[3],16);
							} else if (ay[1]=="0x1E") {
								rday.value = parseInt(ay[3],16);
							} else {
								findPgElmList("rcmd",ay[1],(it)=>{
									if (it.cmd) {
										if (it.type=="textGroup") {
											if (it.info.length > 1) {
												it.info[0].value = parseInt(ay[2],16);
												it.info[1].value = parseInt(ay[3],16);
											} else {
												it.info[0].value = parseInt(ay[3],16);
											}
										} else {
											it.value = parseInt(ay[3],16);
										}
									}
								},true);
							}
						}
					}
				}
				
				lodash.forEach(pgElmList.value,(g,i)=>{
					lodash.forEach(g.els,(o,j)=>{
						if (o.type=="switch") {
							let it = lodash.find(g.els,(x)=>{return o.dId==x.id});
							if (it.type=="textGroup") {
								if (it.info.length > 1) {
									o.value = !(it.info[0].value==0 && it.info[1].value==0);
								} else {
									o.value = !(it.info[0].value == 0);
								}
								it.isRun = o.value;
							}
						}
					});
				});
				
				oldValue = lodash.find(pgElmList.value[0].els,(o)=>{return o.cmd=="0x10"}).value;
				dialog.closeLoading();
				clearInterval(intervalId);
				//判断灯光状态与定时设定
				proxy.dayjs.extend(isBetween);
				if (proxy.dayjs().isBetween(td1,td2,null,"[]")!=(rday.value==1)) {
					uni.showModal({
						content: '按照您当前的设定方案，现在应是关灯状态，您可以保持开灯状态或按程序设定进入关灯状态',
						showCancel: true,
						confirmText: '保持开灯',
						cancelText: '我要关灯',
						success: function (res) {
							if (res.confirm) {
								
							} else if (res.cancel) {
								rday.value = (rday.value==0 ? 1 : 0);
								Blue.writeBLEValue(hexTools.bleBuffer("0x0E",0,parseInt(rday.value)).buffer);
							}
						}
					});
					// dialog.confirm("当前灯光状态与定时设定不符，是否仍保持当前状态？",()=>{},()=>{
					// 	rday.value = (rday.value==0 ? 1 : 0);
					// 	Blue.writeBLEValue(hexTools.bleBuffer("0x0E",0,parseInt(rday.value)).buffer);
					// });
				}
				//end
			}
		},1000);
	};
	
	onLoad((option)=>{
		let param = JSON.parse(decodeURIComponent(option.param));
		device.value = param.device;
		deviceScript = param.deviceScript;
		// console.log("device",device.value);
		pgElmList.value = [
			{id:0,name:"light",els:[
				{id:"01",cmd:"0x04",exCmd:["0x05"],rcmd:"0x14",isRun:true,ly:0,type:"slider",name:"全光谱",value:40,min:0,max:100,style:{barClass:['border-green-500','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-green-500','border-green-500','border-4','border-solid']}},
				{id:"02",cmd:"0x07",rcmd:"0x17",isRun:true,ly:0,type:"slider",name:"UVA",value:40,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"03",cmd:"0x06",rcmd:"0x16",isRun:true,ly:0,type:"slider",name:"UVB",value:40,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"04",cmd:"0x10",rcmd:"0x20",ly:0,type:"radioGroup",name:"开灯关灯渐变时长（日出日落）",value:30,isRun:true,info:[
					{id:"050",prx:"",value:0,afe:"",type:"radio"},
					{id:"051",prx:"",value:30,afe:"",type:"radio"},
					{id:"052",prx:"",value:60,afe:"",type:"radio"},
					{id:"053",prx:"",value:90,afe:"",type:"radio"},
					{id:"054",prx:"",value:120,afe:"",type:"radio"}
				]}
				// {id:"06",cmd:"0x10",rcmd:"0x20",ly:0,isRun:false,type:"slider",name:"开灯关灯渐变时长（日出日落）",value:30,min:30,max:31,afe:"分",style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}}
			]},
			{id:1,name:"fs",els:[
				{id:"10",cmd:"0x08",rcmd:"0x18",isRun:true,ly:0,type:"slider",name:"日间风扇（开灯时风扇）",value:35,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"11",dId:"12",cmd:"",ly:1,type:"switch",name:"定时循环",value:false,style:{}},
				{id:"12",cmd:"0x0B",rcmd:"0x1B",ly:1,type:"textGroup",isRun:false,info:[
					{id:"120",prx:"运转时长",value:0,afe:"分钟",type:"slider",min: 0, max: 15},
					{id:"121",prx:"停歇时长",value:0,afe:"分钟",type:"slider",min: 0, max: 15}
				]},
				{id:"13",cmd:"0x09",rcmd:"0x19",isRun:true,ly:0,type:"slider",name:"夜间风扇（关灯时风扇）",value:25,min:0,max:100,style:{barClass:['border-gray-900','border-4','border-solid','bg-white','rounded-full'],bglineClass:['border-gray-300','border-4','border-solid'],bglineAClass:['bg-gray-900','border-gray-900','border-4','border-solid']}},
				{id:"14",dId:"15",cmd:"",ly:1,type:"switch",name:"定时循环",value:true,style:{}},
				{id:"15",cmd:"0x0C",rcmd:"0x1C",ly:1,type:"textGroup",isRun:true,info:[
					{id:"150",prx:"运转时长",value:0,afe:"分钟",type:"slider",min: 0, max: 15},
					{id:"151",prx:"停歇时长",value:0,afe:"分钟",type:"slider",min: 0, max: 15}
				]}
			]},
			{id:2,name:"cw",els:[
				{id:"20",dId:"21",cmd:"",ly:0,type:"switch",name:"开灯除雾",value:true,style:{}},
				{id:"21",cmd:"0x0D",rcmd:"0x1D",ly:1,type:"textGroup",isRun:true,info:[
					{id:"210",prx:"除雾时长",value:4,afe:"分钟",type:"slider",min: 0, max: 60},
				]},
				{id:"22",dId:"23",cmd:"",ly:0,type:"switch",name:"月光模式",value:false,style:{}},
				{id:"23",cmd:"0x0F",rcmd:"0x1F",isRun:false,ly:1,type:"textGroup",name:"亮度",value:0,min:0,max:100,style:{},info:[
					{id:"230",prx:"亮度",value:0,afe:"%",type:"slider",min: 0, max: 100}
				]}
			]}
		];
		
		// 按照逻辑应该执行下面语句，但考虑到没有读取设备数据之前，不让页面显示，所以取消。
		// isWriteCmd.value = true;
		syncTime();
		
		dialog.openLoading("读取设备信息……");
		setTimeout(()=>{
			ConnectController.addCharacteristicValueChangeListen((characteristic)=>{
				cday = uni.dayjs();
				// console.log("addCharacteristicValueChangeListen_",characteristic.value);
				
				// for test
				// let data = characteristic.value.map(str => "0x"+str.toUpperCase());
				
				let data = hexTools.arrayBuffer2hexArray(characteristic.value).map(str => "0x"+str.toUpperCase());
				console.log("characteristic array",cday.format("HH:mm:ss"),data);
				console.log("cmd----",data);

				if (isWriteCmd.value) {
					say.push(...data);
					if (say.length>=5) {
						let cmd = lodash.find(cmdjson.commands,(o)=>{return o.command==say[1]});
						//write command
						if (cmd) {
							if (say[2]=="0xFE" && say[3]=="0x00") {
								showNotify(cmd.description+":成功设置");
							} else {
								showNotify(cmd.description+":设置失败");
							}
						}
						say = [];
					}
				} else {
					lodash.forEach(data,(v,i)=>{
						say.push(v);
						readInfoArray.push(v);
					});
					if (say.length>=5) {
						say = [];
						readDeviceInfo(null);
					}
					
					// readInfoArray.push(...data);
					// setTimeout(()=>{
					// 	readDeviceInfo(null);
					// },500);
				}
				
				// if (isWriteCmd.value) {
				// 	cday = uni.dayjs();
				// 	let cmd = lodash.find(cmdjson.commands,(o)=>{return o.command.toUpperCase()==("0x"+array[1]).toUpperCase()});
				// 	if (cmd) {
				// 		if (array[2].toUpperCase()=="FE" && array[3].toUpperCase()=="00") {
				// 			showNotify(cmd.description+":成功设置");
				// 		} else {
				// 			showNotify(cmd.description+":设置失败");
				// 		}
				// 	}
				// } else {
				// 	let cmd = lodash.find(cmdjson.commands,(o)=>{return o.command.toUpperCase()==("0x"+array[1]).toUpperCase()});
				// 	findPgElmList("rcmd",("0x"+array[1]).toUpperCase(),(it)=>{
						
				// 	},true);
				// }
			});
			
			isWriteCmd.value = false;
			say = [];
			readInfoArray = [];
			readDeviceInfo(null);
			loopLoading();
		},2000);
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
	
	const sdsChange = (value)=>{
		let g = lodash.find(pgElmList.value,(o)=>{return o.id==value.obj.groupId});
		let it = lodash.find(g.els,(o)=>{return o.id==value.obj.id});
		Blue.writeBLEValue(hexTools.bleBuffer(it.cmd,0,parseInt(it.value)).buffer);
		if (it.exCmd) {
			for(let cmd of it.exCmd) {
				setTimeout(()=>{
					Blue.writeBLEValue(hexTools.bleBuffer(cmd,0,parseInt(it.value)).buffer);
				},1000);
			}
		}
	};
	
	const infoChanging = (value)=>{
		let g = lodash.find(pgElmList.value,(o)=>{return o.id==value.obj.groupId});
		let item = lodash.find(g.els,(o)=>{return o.id==value.obj.id});
		let it = lodash.find(item.info,(o)=>{return o.id==value.obj.infoId});
		it.value = value.v;
	};
	
	const infoChange = (value)=>{
		let g = lodash.find(pgElmList.value,(o)=>{return o.id==value.obj.groupId});
		let item = lodash.find(g.els,(o)=>{return o.id==value.obj.id});
		// console.log(item.info);
		if (item.info.length==2) {
			Blue.writeBLEValue(hexTools.bleBuffer(item.cmd,parseInt(item.info[0].value),item.info[1].value).buffer);
		} else {
			Blue.writeBLEValue(hexTools.bleBuffer(item.cmd,0,item.info[0].value).buffer);
		}
	};
	
	const switchChange = (value)=>{
		// console.log(value.v,value.obj);
		let g = lodash.find(pgElmList.value,(o)=>{return o.id==value.obj.groupId});
		let it = lodash.find(g.els,(o)=>{return o.id==value.obj.id});
		let itt = lodash.find(g.els,(o)=>{return o.id==it.dId});
		// console.log(it,itt);
		
		itt.isRun = value.v;
		it.value = value.v;
		// findPgElmList("id",itt.id,(o)=>{
		// 	o.isRun = value.v;
		// });
		
		if (value.v) {
			if (itt.info.length==2) {
				Blue.writeBLEValue(hexTools.bleBuffer(itt.cmd,parseInt(itt.info[0].value),itt.info[1].value).buffer);
			} else {
				Blue.writeBLEValue(hexTools.bleBuffer(itt.cmd,0,itt.info[0].value).buffer);
			}
		} else {
			Blue.writeBLEValue(hexTools.bleBuffer(itt.cmd,0,0).buffer);
		}
		
		// findPgElmList("type","switch",(it)=>{
		// 	findPgElmList("id",it.dId,(itt)=>{
		// 		itt.isRun = it.value;
		// 		if (it.value) {
		// 			if (itt.info.length==2) {
		// 				setTimeout(()=>{
		// 					Blue.writeBLEValue(hexTools.bleBuffer(itt.cmd,parseInt(itt.info[0].value),itt.info[1].value).buffer);
		// 				},1000);
		// 			} else {
		// 				setTimeout(()=>{
		// 					Blue.writeBLEValue(hexTools.bleBuffer(itt.cmd,0,itt.info[0].value).buffer);
		// 				},1000);
		// 			}
		// 		} else {
		// 			setTimeout(()=>{
		// 				Blue.writeBLEValue(hexTools.bleBuffer(itt.cmd,0,0).buffer);
		// 			},1000);
		// 		}
		// 	},true);
		// },false);
		// if (!v.value) {
			
		// }
	};
	
	const radioChange = (e)=>{
		let c = lodash.find(pgElmList.value[0].els,(o)=>{return o.cmd=="0x10"});
		let onTimeAy = times.value.onTime.split(":");
		let offTimeAy  = times.value.offTime.split(":");
		let onTime_h = parseInt(onTimeAy[0]);
		let onTime_s = parseInt(onTimeAy[1]);
		let offTime_h = parseInt(offTimeAy[0]);
		let offTime_s = parseInt(offTimeAy[1]);
		// console.log(oldValue,c);
		if (checkKgTimeAndC(onTime_h,onTime_s,offTime_h,offTime_s,c.value)) {
			oldValue = c.value;
			Blue.writeBLEValue(hexTools.bleBuffer(c.cmd,0,parseInt(c.value)).buffer);
		} else {
			c.value = oldValue;
			showNotify("设定错误请重新确认。");
		}
	};
	
	const goKgtime = ()=>{
		// let onTimeAy = times.value.onTime.split(":");
		// let offTimeAy  = times.value.offTime.split(":");
		// let onTime_h = parseInt(onTimeAy[0]);
		// let onTime_s = parseInt(onTimeAy[1]);
		// let offTime_h = parseInt(offTimeAy[0]);
		// let offTime_s = parseInt(offTimeAy[1]);
		// page.navigateTo('./kgtime',{times:{onTime:times.onTime,offTime:times.offTime,values:{onTime:{v0:onTime_h,v1:onTime_s},offTime:{v0:offTime_h,v1:offTime_s}}}});
		
		let c = lodash.find(pgElmList.value[0].els,(o)=>{return o.cmd=="0x10"});
		page.navigateTo('./kgtime',{times:{onTime:times.value.onTime,offTime:times.value.offTime},cvalue:c.value});
	};
	
	const setTimes = (v)=>{
		times.value = v;
		Blue.writeBLEValue(hexTools.bleBuffer("0x02",times.value.values.onTime.v0,times.value.values.onTime.v1).buffer);
		setTimeout(()=>{
			Blue.writeBLEValue(hexTools.bleBuffer("0x03",times.value.values.offTime.v0,times.value.values.offTime.v1).buffer);
		},3000);
	};
	
	const checkKgTimeAndC = (onTime_h,onTime_s,offTime_h,offTime_s,cvalue)=>{
		let onFen = onTime_h*60 + onTime_s;
		let offFen = offTime_h*60 + offTime_s;
		if (onFen>offFen) {
			if (offFen+1440-onFen >= 2*cvalue) {
				return true;
			} else {
				return false;
			}
		} else {
			if (offFen-onFen >= 2*cvalue) {
				return true;
			} else {
				return false;
			}
		}
	};
	
	const setRDay = ()=>{
		rday.value = rday.value==0 ? 1 : 0;
		setTimeout(()=>{
			Blue.writeBLEValue(hexTools.bleBuffer("0x0E",0,parseInt(rday.value)).buffer);
		},1000);
	};
	
	const syncTime = ()=>{
		// console.log(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))));
		cday = uni.dayjs();
		Blue.writeBLEValue(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))).buffer);
		currentTime.value = cday.format("HH:mm");
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
	
	const test = ()=>{
		// isWriteCmd.value = false;
		// Blue.writeBLEValue(hexTools.bleBuffer("0x11",0,0).buffer);
		// setTimeout(()=>{
		// 	isWriteCmd.value = true;
		// },9000);
		let a = lodash.find(pgElmList.value[0].els,(o)=>{return o.id=="04"});
		a.min = 30;a.max = 31;
		console.log(a);
	}
	
	const readAfterScript = ()=>{
		let scriptArray = [];
		scriptArray.push({"cmd":"0x02","v0":times.value.values.onTime.v0,"v1":times.value.values.onTime.v1});
		scriptArray.push({"cmd":"0x03","v0":times.value.values.offTime.v0,"v1":times.value.values.offTime.v1});
		scriptArray.push({"cmd":"0x0E","v0":0,"v1":parseInt(rday.value)});
		// scriptArray.push({"cmd":"0x0A","v0":0,"v1":parseInt(fengsanZuanSu.value.value)});
		
		for(let g of pgElmList.value) {
			for(let v of g.els) {
				if (v.cmd!="") {
					if (v.isRun) {
						if (v.type=="slider" || v.type=="radioGroup") {
							// setTimeout(()=>{
							// 	Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,0,parseInt(v.value)).buffer);
							// },3000);
							scriptArray.push({"cmd":v.cmd,"v0":0,"v1":v.value});
							if (v.exCmd) {
								for(let cmd of v.exCmd) {
									scriptArray.push({"cmd":cmd,"v0":0,"v1":v.value});
								}
							}
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
							// setTimeout(()=>{
							// 	Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,parseInt(d1),parseInt(d2)).buffer);
							// },3000);
							scriptArray.push({"cmd":v.cmd,"v0":d1,"v1":d2});
						}
					} else {
						// setTimeout(()=>{
						// 	Blue.writeBLEValue(hexTools.bleBuffer(v.cmd,0,0).buffer);
						// },3000);
						scriptArray.push({"cmd":v.cmd,"v0":0,"v1":0});
					}
				}
			}
		}
		// console.log(scriptArray);
		return scriptArray;
	};
	
	const saveScript = ()=>{
		// console.log(pgElmList.value);return;
		proxy.$prePage().backScriptStr(deviceScript.id,JSON.stringify(readAfterScript()),device.value.deviceId);
		page.navBack();
	}
	
	const leftClick = ()=>{
		proxy.$prePage().backScriptStr(null,JSON.stringify(readAfterScript()),device.value.deviceId);
		page.navBack();
	};
	
	defineExpose({
		setTimes,checkKgTimeAndC
	});
</script>

<style>
	.btn1 {
		background-color: #ABBCCF;
	}
	.btn2 {
		background-color: #7993AF;
	}
</style>