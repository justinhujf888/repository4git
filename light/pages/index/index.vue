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
			<button v-if="viewStatus != 0" class="btnbg rounded-full w-7 h-7 text-white center" @tap="callBle()">
				<text class="text-xl gui-icons">&#xe635;</text>
			</button>
		</view>
		<view class="mx-2">
			<!-- <wd-button custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" @click="writeBleValue()">test</wd-button> -->
			<view v-if="viewStatus == 0" class="hwcenter mt-8">
				<wd-button size="large" custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" click="scan()" @click="callBle()">添加设备</wd-button>
			</view>
			<view v-else-if="viewStatus == 1" class="mt-2">
				<view v-for="device in preDeviceList" :key="device.id" class="bg-white rounded-xl px-2 py-4 mt-4 row">
					<view class="mx-2">
						<img src="../../static/device.png" mode="widthFix" class="w-25 h-25"></img>
					</view>
					<view class="flex-1 col">
						<view class="row mb-2">
							<text class="text-sm font-bold text-green-500 mr-1">·</text>
							<text class="text-sm font-semibold">{{device.name}}</text>
						</view>
						<wd-button size="small" custom-class="py-1 text-xs text-white w-15" custom-style="background: #6AAE36" @click="addDevice(device.deviceId)" click="scaned()">连接</wd-button>
					</view>
				</view>
			</view>
			<view v-else-if="viewStatus == 2" class="px-2">
				<view class="bg-white rounded-xl px-2 py-5 mt-4" @tap="page.navigateTo('../device/setup',{})">
					<view class="mx-10 center">
						<img src="../../static/device.png" mode="widthFix"></img>
					</view>
					<view class="between mx-5 mt-5">
						<text class="text-sm font-semibold">{{theDevice.name}}</text>
						<view class="row" v-if="theDevice.connected">
							<text class="text-sm font-bold mr-1">·</text>
							<text class="text-sm font-semibold">已连接</text>
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
	<tabbar :tabIndex="0"></tabbar>
</template>

<script setup>
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import lodash from "lodash";
	
	import { Blue } from '@/api/bluebooth/index.js';
	import { ConnectController } from '@/api/bluebooth/controller.js';
	import {BLUE_STATE} from "@/api/bluebooth/blueState.js";
	import hexTools from "@/api/hexTools.js";
	import page from "@/api/uniapp/page.js";
	import dialog from "@/api/uniapp/dialog.js";
	
	import cmdjson from "@/api/datas/cmd.json";
	
	import { useNotify } from '@/uni_modules/wot-design-uni';
	const { showNotify, closeNotify } = useNotify();
	// const {lodash} = getCurrentInstance().appContext.config.globalProperties;
	// const { proxy } = getCurrentInstance();
	const viewStatus = ref(-1);
	const deviceList = ref([]);
	const preDeviceList = ref([]);
	const theDevice = ref({});
		
	onLoad((option)=>{
		viewStatus.value = 0;
		preDeviceList.value = [];
		deviceList.value = [];
	});
	
	onUnload(()=>{
		// closeDevice();
	});
	
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
	
	function addDevice(deviceId) {
		dialog.openLoading("正在连接设备……");
		Blue.createBLEConnection(deviceId);
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
	    dialog.openLoading("扫描设备……");
	    preDeviceList.value = [];
	    Blue.callBle();
		ConnectController.addConnectStateListen((data)=>{
			console.log("addConnectStateListen",data);
			// this.state = data.label;
			if(data.deviceId && data.connected) {
				console.log("connected",data);
				Blue.setBleConnectDeviceID(data.deviceId);
				Blue.getBleCharacteristicsInfo("0000FFF1-0000-1000-8000-00805F9B34FB","0000FFF2-0000-1000-8000-00805F9B34FB");			
				if (lodash.findIndex(deviceList.value,(o)=>{return o.deviceId==data.deviceId})<0) {
					let device = lodash.find(preDeviceList.value,(o)=>{return o.deviceId==data.deviceId});
					device.connected = true;
					// deviceList.value.push(device);
					theDevice.value = device;
				} else {
					let device = lodash.find(deviceList.value,(o)=>{return o.deviceId==data.deviceId});
					device.connected = true;
					theDevice.value = device;
				}
				
				let cday = uni.dayjs();
				Blue.writeBLEValue(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))).buffer);
				setTimeout(()=>{
					viewStatus.value = 2;
					dialog.closeLoading();
				},5000);
			} 
			else if (data.deviceId && !data.connected) {
				Blue.createBLEConnection(data.deviceId);
			} else {
				viewStatus.value = 0;
				dialog.closeLoading();
			}
			if (data.label) {
				showNotify(data.label);
			}
		});
	   
	    ConnectController.addDevicesListen((device)=>{
			console.log("addDevicesListen",device);
			device.connected = false;
			preDeviceList.value.push(device);
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

