<template>
	<wd-notify></wd-notify>
	<view class="relative h-screen px-3">
		<view class="mt-14 p-1">
			<view class="flex flex-row">
				<text class="text-base font-semibold">VASEE 生态智能</text>
			</view>
		</view>
		<view class="between mt-10 w-full">
			<view class="flex-1">
				<text v-if="false" class="text-gray-400 text-base gui-icons">上午 10:30 &#xe69e;</text>
			</view>
			<button v-if="viewStatus != 0" class="btnbg rounded-full w-7 h-7 text-white center" @tap="refreshDevices()">
				<text class="text-xl gui-icons">&#xe635;</text>
			</button>
		</view>
		<view class="mx-2">
<!-- 			<wd-upload ref="uploader" :auto-upload="false" :file-list="fileList" image-mode="aspectFill" :action="host" :build-form-data="buildFormData" @change="handleChange" @fail="ossFail" @success="ossSuccess"></wd-upload>
			<wd-button size="small" custom-class="py-2 text-xs text-white" @click="uploadClick()">开始上传</wd-button> -->
			<!-- <wd-button custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" @click="writeBleValue()">test</wd-button> -->
			<view v-if="viewStatus == 0" class="hwcenter mt-8">
				<wd-button size="large" custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" click="scan()" @click="callBle()">添加设备</wd-button>
			</view>
			<view v-else-if="viewStatus == 1" class="mt-2">
				<view>
					<text class="text-gray-600 text-sm">我的设备</text>
					<view v-if="deviceList?.length > 0">
						<view v-for="(device,index) in deviceList" :key="device.deviceId" class="bg-white rounded-xl px-2 py-4 mt-4 row relative">
							<view class="mx-2">
								<img src="../../static/device.png" mode="widthFix" class="w-25 h-25"></img>
							</view>
							<view class="flex-1 col">
								<view class="row mb-2">
									<!-- <text class="text-sm font-bold text-green-500 mr-1">·</text> -->
									<text class="text-sm font-semibold">{{device.name}}</text>
								</view>
								<view v-if="true || device.tempMap.near">
									<wd-button size="small" custom-class="py-1 text-xs text-white w-15" custom-style="background: #6AAE36" :loading="device.tempMap.connecting" click="addDevice(device)" @click="page.navigateTo('../device/scriptList',{deviceId:'7815DD45-293D-76A2-5F45-79425575F1A2'})">连接</wd-button>
								</view>
								<view v-else>
									<text class="text-gray-400 text-xs">设备不可连接</text>
								</view>
							</view>
<!-- 							<view class="row top-2 right-4">
								<text class="text-sm gui-icons ml-4" @tap="openRenameModel(device)">&#xe69e;</text>
								<text class="text-sm gui-icons ml-4" @tap="delBuyerDevice(device.deviceId,index)">&#xe794;</text>
							</view> -->
						</view>
					</view>
					<view v-else class="center mt-10">
						<text class="text-gray-500 text-xs">没有添加设备</text>
					</view>
				</view>
				<wd-divider></wd-divider>
				<view class="mt-2">
					<text class="text-gray-600 text-sm">搜索其它设备</text>
					<view v-if="preDeviceList?.length > 0">
						<view v-for="device in preDeviceList" :key="device.deviceId" class="bg-white rounded-xl px-2 py-4 mt-4 row">
							<view class="mx-2">
								<img src="../../static/device.png" mode="widthFix" class="w-25 h-25"></img>
							</view>
							<view class="flex-1 col">
								<view class="row mb-2">
									<text class="text-sm font-bold text-green-500 mr-1">·</text>
									<text class="text-sm font-semibold">{{device.name}}</text>
								</view>
								<wd-button size="small" custom-class="py-1 text-xs text-white w-15" custom-style="background: #6AAE36" @click="addDevice(device)" click="scaned()">连接</wd-button>
							</view>
						</view>
					</view>
					<view v-else class="center mt-10">
						<text class="text-gray-500 text-xs">没有搜索到设备</text>
					</view>
				</view>
			</view>
			<view v-else-if="viewStatus == 2" class="px-2">
				<view class="bg-white rounded-xl px-2 py-5 mt-4">
					<view class="mx-10 center" @tap="page.navigateTo('../device/scriptList',{deviceId:theDevice.deviceId})">
						<img src="../../static/device.png" mode="widthFix"></img>
					</view>
					<wd-divider></wd-divider>
					<view class="between mx-5 mt-5">
						<text class="text-sm font-semibold">{{theDevice.name}}</text>
						<view class="row items-center" v-if="theDevice.tempMap.connected">
							<text class="text-sm font-bold mr-1">·</text>
							<text class="text-sm font-semibold mr-2">已连接</text>
							<view class="row" @click.stop="closeConnection()">
								<wd-button size="small" type="error" custom-class="py-1 px-2 text-xs text-white w-15 self-end">断开连接</wd-button>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view v-if="false" class="">
			<button v-if="viewStatus != 1" class="btnbg rounded-full w-7 h-7 text-white center" @tap="scan()">
				<text class="text-xl">✚</text>
			</button>
		</view>
	</view>
	
	<wd-popup v-model="renameShow" position="top" :safe-area-inset-bottom="true" custom-style="border-radius:32rpx;">
		<view class="col center text-gray-500 mt-40">
			<text class="mt-2 row center">修改设备名称</text>
			<view class="row mt-10">
				<text class="text-xs">设备名称</text>
				<input class="minput ml-2" v-model="theDevice.name"/>
			</view>
			<view class="mt-10">
				<wd-button size="small" @click="rename()">确定</wd-button>
			</view>
		</view>
	</wd-popup>
	
	<my-wxLogin :isShow="loginShow" @close="loginClose" @runAgain="loginRunAgain"></my-wxLogin>
	
	<tabbar :tabIndex="0"></tabbar>
</template>

<script setup>
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import lodash from "lodash";
	
	import wxRest from "@/api/uniapp/wx.js";
	import deviceRest from "@/api/dbs/device.js";
	
	import { Blue } from '@/api/bluebooth/index.js';
	import { ConnectController } from '@/api/bluebooth/controller.js';
	import {BLUE_STATE} from "@/api/bluebooth/blueState.js";
	import hexTools from "@/api/hexTools.js";
	import page from "@/api/uniapp/page.js";
	import dialog from "@/api/uniapp/dialog.js";
	
	import cmdjson from "@/api/datas/cmd.json";
	
	import { useNotify } from '@/uni_modules/wot-design-uni';
	import { Beans } from '../../api/dbs/beans';
	const { showNotify, closeNotify } = useNotify();
	const { proxy } = getCurrentInstance();
	const viewStatus = ref(-1);
	const deviceList = ref([]);
	const preDeviceList = ref([]);
	const theDevice = ref({});
	
	const renameShow = ref(false);
	const loginShow = ref(false);
	
	let userId = null;
	let deviceTypeList = null;
	let location = {};
	
	
	// const uploader = ref();
	// let host = "https://pets-oss.oss-cn-zhangjiakou.aliyuncs.com";
	// const fileList = ref([]);
	// const handleChange = (files)=>{
	// 	fileList.value = files;
	// }
	// const buildFormData = ({ file, formData, resolve }) => {
	//   let imageName = file.url.substring(file.url.lastIndexOf('/') + 1) // 从图片路径中截取图片名称
	//   // #ifdef H5
	//   // h5端url中不包含扩展名，可以拼接一下name
	//   imageName = imageName + file.name
	//   // #endif
	  
	//   const key = `${imageName}` // 图片上传到oss的路径(拼接你的文件夹和文件名)
	//   const success_action_status = '200' // 将上传成功状态码设置为200，默认状态码为204
	  
	//   // deviceRest.genAliOssAccessInfo((data)=>{
	//   // 	console.log(data);
	//   // 	// signature = data.signatureInfo.accessKey;
	//   // 	ossAccessKeyId = data.signatureInfo.accessId // 你的AccessKey ID
	//   // 	securityToken = data.signatureInfo.securityToken;
	//   // });
	//   deviceRest.genSignature((data)=>{
	//   	// console.log(data);
	//   	ossAccessKeyId = data.signatureInfo.accessId;
	//   	policy = data.signatureInfo.policy;
	//   	signature = data.signatureInfo.signature;
	//   	securityToken = data.signatureInfo.securityToken;
	//   	host = data.signatureInfo.bucketUrl;
		
	// 	formData = {
	// 	  ...formData,
	// 	  key: key,
	// 	  OSSAccessKeyId: ossAccessKeyId,
	// 	  policy: policy,
	// 	  signature: signature,
	// 			'x-oss-security-token': securityToken,
	// 	  success_action_status: success_action_status
	// 	}
	// 	console.log(formData);
	// 	resolve(formData) // 组装成功后返回 formData，必须返回
	//   });
	// }
	// const uploadClick = ()=>{
	// 	uploader.value?.submit();
	// };
	// const ossSuccess = (e)=>{
	// 	console.log(e);
	// };
	// const ossFail = (e)=>{
	// 	console.log(e);
	// };
	
	// let signature = "";	let ossAccessKeyId="";let securityToken = "";let policy = "";
	
	const init = ()=>{
		viewStatus.value = 0;
		preDeviceList.value = [];
		deviceList.value = [];
		
		(async ()=>{
			await new Promise(resolve => {
				deviceRest.qyDeviceTypeList(null,null,null,(data)=>{
					if (data.status=="OK") {
						deviceTypeList = data.deviceTypeList;
						lodash.forEach(deviceTypeList,(v,i)=>{
							// {serviceId:{scan:"00007365-0000-1000-8000-00805F9B34FB",uuid:"76617365-6570-6c61-6e74-776f726c6473"}}
							v.tempMap = {};
							v.tempMap.services = JSON.parse(v.serviceId);
						});
						callBle();
						resolve();
					}
				});
			});
			
			await new Promise(resolve => {
				if (userId) {
					deviceRest.qyBuyerDeviceList(userId,(data)=>{
						if (data.status=="OK") {
							deviceList.value = data.deviceList;
							if (!deviceList.value) {
								deviceList.value = [];
							}
							if (deviceList.value?.length > 0) {
								for(let d of deviceList.value) {
									d.tempMap = {};
									d.tempMap.near = false;
									d.tempMap.deviceType = lodash.find(deviceTypeList,(o)=>{return o.id==d.deviceType.id});
								}
								viewStatus.value = 1;
							}
							resolve();
						}
					});
				} else {
					resolve();
				};
			});
			
		})(); 
	};
	
	const refreshDevices = ()=>{
		callBle();
		deviceRest.qyBuyerDeviceList(userId,(data)=>{
			if (data.status=="OK") {
				deviceList.value = data.deviceList;
				if (!deviceList.value) {
					deviceList.value = [];
				}
				if (deviceList.value?.length > 0) {
					for(let d of deviceList.value) {
						d.tempMap = {};
						d.tempMap.near = false;
						d.tempMap.deviceType = lodash.find(deviceTypeList,(o)=>{return o.id==d.deviceType.id});
					}
				}
			}
		});
	};
	
	onLoad((option)=>{
		// console.log("wxInfo",wxRest.getLoginState());
		userId = wxRest.getLoginState()?.userId;
		
		uni.getLocation({
			type: 'wgs84',
			success: (res)=>{
				location.lng = res.longitude;
				location.lat = res.latitude;
				console.log("location",location);
			}
		});

		init();
		// wxRest.clearLoginInfo();
	});
	
	onUnload(()=>{
		// closeDevice();
	});
	
	const loginClose = (e)=>{
		loginShow.value = e;
	};
	
	const loginRunAgain = (e)=>{
		page.reLaunch("/pages/index/index",{});
	};
	
	const openRenameModel = (device)=>{
		renameShow.value = true;
		theDevice.value = device;
	};
	
	const rename = ()=>{
		deviceRest.renameBuyerDevice(userId,theDevice.value.deviceId,theDevice.value.name,(data)=>{
			if (data.status=="OK") {
				let de = lodash.find(deviceList.value,(o)=>{return o.deviceId==theDevice.value.deviceId});
				if (de) {
					de.name = theDevice.value.name;
					theDevice.value = {};
					renameShow.value = false;
				}
			}
		});
	};
	
	const delBuyerDevice = (deviceId,index)=>{
		dialog.confirm("是否删除这个设备？",()=>{
			deviceRest.delBuyerDevice(userId,deviceId,(data)=>{
				if (data.status=="OK") {
					deviceList.value.splice(index,1);
				}
			});
		},null);
	};
	
	function aaa() {
		let a = "";
		(async ()=>{
			await new Promise(resolve => {
				setTimeout(function(){
					console.log("0");
					a = 0;
					resolve('一块碗和一双筷子');
				}, 2000);
			});
			
			await new Promise(resolve => {
				setTimeout(function(){
					console.log("1");
					a = 1;
					resolve('一块碗和一双筷子');
				}, 2000);
			});
			
		})((d)=>{console.log(d)});
		
		console.log("2");
		a = 2;
	}
	
	function test() {
		// let b = stringToHex("Hello");
		// let a = hexCharCodeToStr("48656c6c6f");
		// console.log(hexTools.hex2ByteString(b),a,b);
		let a = parseInt("0x01",16);
		let b = parseInt("0x0B",16);
		let c = parseInt("0x1E",16);
		let d = parseInt("0xFF",16);
		let w = parseInt("0x14F",16);
		let e = hexTools.num2HexArray(11);
		let sum = a + b + c;
		let x = hexTools.num2Hex(sum ^ d);
		let h = parseInt(b,16);
		let j = "0x"+hexTools.num2Hex(11).toUpperCase();
		let nn = 26;
		let n = nn.toString(16);
		
		let ay1 = new Uint8Array([0x14F]);
		let ay2 = new Uint8Array(["0x4F"]);
		
		let ay = hexTools.bleBuffer(0x01,11,30);
		
		console.log(ay,hexTools.arrayBuffer2hexArray(ay));
		
		const hexStrings = ["68", "06", "AA", "01", "05", "A0", "0F", "00", "16"];
		const intArray = hexStrings.map(str => "0x"+str);
		console.log(hexStrings,intArray);
		lodash.forEach([1,3],(v,i)=>{});
	}
	
	function scan() {
		preDeviceList.value = [];
		preDeviceList.value.push({id:"IDC2533",name:"Justin Device",connected:false});
		preDeviceList.value.push({id:"IDC2533",name:"Justin Device",connected:false});
		viewStatus.value = 1;
	}
	function scaned() {
		theDevice.value = {id:"IDC2533",name:"Justin Device",connected:true};
		viewStatus.value = 2;
	}
	
	function closeDevice() {
		Blue.closeBlue();
	}
	
	function addDevice(device) {
		if (device.tempMap?.near) {
			dialog.openLoading("正在连接设备……");
			// console.log("connect...",device.advertisServiceUUIDs);
			// Blue.setBlueServiceId(device.advertisServiceUUIDs[0]);
			// Blue.createBLEConnection(device.deviceId);
			// 换成了dbDrivice
			console.log("connect...",device.tempMap.deviceType.tempMap.services.serviceId);
			Blue.setBlueServiceId(device.tempMap.deviceType.tempMap.services.serviceId.uuid);
			Blue.createBLEConnection(device.deviceId);
		} else {
			showNotify("设备不在有效的范围，请重新刷新搜索。");
		}
	}

	const closeConnection = ()=>{
		dialog.confirm("是否断开设备连接",()=>{
			Blue.closeBLEConnection();
			callBle();
		},null);
	};
	
	function preCallBle() {
		// 0000fff0-0000-1000-8000-00805f9b34fb 0000fff0-0000-1000-8000-00805f9b34fb 0000fff1-0000-1000-8000-00805f9b34fb
		// {"serviceId":{"scan":"00007365-0000-1000-8000-00805F9B34FB","uuid":"76617365-6570-6c61-6e74-776f726c6473"}} ["7661fff1-6570-6c61-6e74-776f726c6473"] ["7661fff2-6570-6c61-6e74-776f726c6473"]
		// {"serviceId":{"scan":"0000fff0-0000-1000-8000-00805f9b34fb","uuid":"0000fff0-0000-1000-8000-00805f9b34fb"}} ["0000fff0-0000-1000-8000-00805f9b34fb"] ["0000fff1-0000-1000-8000-00805f9b34fb"]
		preDeviceList.value = [];
		let serviceFilter = [];
		for(let d of deviceTypeList) {
			serviceFilter.push(d.tempMap.services.serviceId.scan.toUpperCase());
		}
		Blue.setServiceFilter(serviceFilter);
	}
		
	async function callBle() {
		
		// #ifdef H5
		const device = await navigator.bluetooth.requestDevice({
			filters: [{ services: [Blue.getBlueServiceId().toLowerCase()] }],
		});
		// navigator.bluetooth.requestDevice({
			// filters: [{ services: [Blue.getBlueServiceId()] }],
			// acceptAllDevices: true,
			// optionalServices: ["0000FFF0-0000-1000-8000-00805F9B34FB"]
		// });
		// .then(device => device.gatt.connect())
		// .then(server => server.getPrimaryService('heart_rate'))
		// .then(service => service.getCharacteristic('heart_rate_measurement'))
		// .then(characteristic => characteristic.startNotifications())
		// .then(characteristic => {
		//     characteristic.addEventListener('characteristicvaluechanged', handleCharacteristicValueChanged);
		//     console.log('Notifications have been started.');
		// })
		// .catch(error => { console.error(error); });
		// function handleCharacteristicValueChanged(event) {
		//     const value = event.target.value;
		//     console.log('Received ' + value);
		//     // TODO: Parse Heart Rate Measurement value.
		//     // See https://github.com/WebBluetoothCG/demos/blob/gh-pages/heart-rate-sensor/heartRateSensor.js
		// }
		// #endif
	   
	   
	   // console.log(Blue._discoveryStarted);
	   // #ifdef MP
		if (!userId) {
			loginShow.value = true;
			return;
		}
	    preCallBle();
		
		setTimeout(()=>{
			viewStatus.value = 1;
		},4000);
		
		
	    Blue.callBle();
		ConnectController.addConnectStateListen((data)=>{
			console.log("addConnectStateListen",data);
			// this.state = data.label;
			if (!data.deviceId) {
				return;
			}
			
			let device = null;
			if (lodash.findIndex(deviceList.value,(o)=>{return o.deviceId==data.deviceId})<0) {
				device = lodash.find(preDeviceList.value,(o)=>{return o.deviceId==data.deviceId});
				if (!device.tempMap) {
					device.tempMap = {};
				}
				device.tempMap.isDB = false;
				// deviceList.value.push(device);
			} else {
				device = lodash.find(deviceList.value,(o)=>{return o.deviceId==data.deviceId});
				if (!device.tempMap) {
					device.tempMap = {};
				}
				device.tempMap.isDB = true;
			}
			theDevice.value = device;
			console.log("theDevice",theDevice.value);
			
			if(data.connected) {
				console.log("connected",data);
				theDevice.value.tempMap.connecting = true;
				theDevice.value.tempMap.connected = true;
				
				Blue.setBleConnectDeviceID(theDevice.value.deviceId);

				Blue.getBleCharacteristicsInfo(JSON.parse(theDevice.value.deviceType.characteristicsReadIds)[0].toUpperCase(),JSON.parse(theDevice.value.deviceType.characteristicsWriteIds)[0].toUpperCase());	
				// Blue.getBleCharacteristicsInfo("7661fff1-6570-6c61-6e74-776f726c6473".toUpperCase(),"7661fff2-6570-6c61-6e74-776f726c6473".toUpperCase());
				
				// let cday = uni.dayjs();
				// Blue.writeBLEValue(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))).buffer);
				setTimeout(()=>{
					if (!theDevice.value.tempMap.isDB) {
						theDevice.value.lat = location.lat;
						theDevice.value.lng = location.lng;
						deviceRest.addBuyerDevice(theDevice.value,(data)=>{
							if (data.status=="OK") {
								deviceList.value.push(theDevice.value);
								viewStatus.value = 2;
							}
						});
					} else {
						viewStatus.value = 2;
					}
					
				},1000);
			} 
			else if (data.code==BLUE_STATE.CONNECTFAILED.code) {
				// Blue.createBLEConnection(data.deviceId);
				// viewStatus.value = 0;
				theDevice.value.tempMap.connecting = false;
				theDevice.value.tempMap.connected = false;
			}
			else if (data.code==BLUE_STATE.DISCONNECT.code) {
				theDevice.value.tempMap.connecting = false;
				theDevice.value.tempMap.connected = false;
				viewStatus.value = 1;
			}
			if (data.label) {
				showNotify(data.label);
			}
			dialog.closeLoading();
		});
	   
	    ConnectController.addDevicesListen((device)=>{
			console.log("addDevicesListen",device);
			device.connected = false;
			let dbDevice = Beans.device();
			dbDevice.deviceId = device.deviceId;
			dbDevice.name = device.name;
			dbDevice.deviceType = lodash.find(deviceTypeList,(o)=>{return o.tempMap.services.serviceId.scan==device.advertisServiceUUIDs[0]});
			let buyer = Beans.buyer();
			buyer.phone = userId;
			dbDevice.buyer = buyer;
			dbDevice.tempMap = {};
			dbDevice.tempMap.connected = false;
			dbDevice.tempMap.connecting = false;
			dbDevice.tempMap.near = true;
			dbDevice.tempMap.deviceType = dbDevice.deviceType;
			
			//该设备是否保存到了数据库
			let index = lodash.findIndex(deviceList.value,(o)=>{return o.deviceId==dbDevice.deviceId});
			if (index < 0) {
				preDeviceList.value.push(dbDevice);
			} else {
				deviceList.value[index].tempMap.near = true;
				deviceList.value[index].tempMap.connecting = false;
			}
			
			viewStatus.value = 1;
			dialog.closeLoading();
		});
		
		// #endif
	}
	
	let f = 0;
	let ff = 30;let i = 0;
	let cd = [
		{cmd:"0x04",d1:0,d2:ff},
		{cmd:"0x05",d1:0,d2:ff},
		{cmd:"0x06",d1:0,d2:ff},
		{cmd:"0x07",d1:0,d2:ff},
		{cmd:"0x0E",d1:0,d2:f}
	];
	function writeBleValue() {
		//new Uint8Array([0xA6, 0x0E, 0x00, 0x00, 0xF1]).buffer
		//new Uint8Array(["0xA6", "0x0E", "0x00", "0x01", "0xF0"]).buffer
		//
		if (i>4) i=0;
		f = f==0 ? 1 : 0;
		ff = ff==30 ? 80 : 30;
		Blue.writeBLEValue(hexTools.bleBuffer(cd[i].cmd,cd[i].d1,cd[i].d2).buffer);
		i++;
	}
	
	defineExpose({
		
	});
</script>

<style lang="scss">
	.page-class {
	  :deep() {
	    .custom-bg {
	      background-color:rgba(113, 68, 68, 0);
	    }
	  }
	}
</style>
