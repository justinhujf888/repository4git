<template>
	<view class="relative px-3">
		<wd-notify></wd-notify>
<!--  #ifdef H5  -->
        <my-language></my-language>
<!--  #endif      -->
<!--		 <button @tap="tempProcess" class="mt-10">testtttttttttttttttt</button>-->
		<view class="mt-14 p-1">
			<view class="flex flex-row">
				<text class="text-base font-semibold">{{$t('page.index.title')}}</text>
			</view>
		</view>				
		<view class="between mt-10 w-full">
			<view class="flex-1">
				<text v-if="false" class="text-gray-400 text-base gui-icons">上午 10:30 &#xe69e;</text>
			</view>
<!--            #ifdef MP-->
			<button v-if="viewStatus != 0" class="btnbg rounded-full w-7 h-7 text-white center" @tap="refreshDevices()">
				<text class="text-xl gui-icons">&#xe635;</text>
			</button>
<!--            #endif-->
		</view>
		<view class="mx-2">
<!-- 			<wd-upload ref="uploader" :auto-upload="false" :file-list="fileList" image-mode="aspectFill" :action="host" :build-form-data="buildFormData" @change="handleChange" @fail="ossFail" @success="ossSuccess"></wd-upload>
			<wd-button size="small" custom-class="py-2 text-xs text-white" @click="uploadClick()">开始上传</wd-button> -->
			<!-- <wd-button custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" @click="writeBleValue()">test</wd-button> -->
			<view v-if="viewStatus == 0" class="mt-32 center">
				<wd-button size="large" custom-class="py-2 text-xs text-white" custom-style="background: #6AAE36" click="scan()" @click="callBle()">{{$t('page.index.addDevice')}}</wd-button>
			</view>
			<view v-else-if="viewStatus == 1" class="mt-2">
				<view>
					<text class="text-gray-600 text-sm">{{$t('page.index.myDevices')}}</text>
					<view v-if="deviceList?.length > 0">
						<view v-for="(device,index) in deviceList" :key="device.deviceId" class="bg-white rounded-xl px-2 py-4 mt-4 row relative">
							<view class="mx-2">
								<img :src="'../../static/'+device.tempMap.img" mode="widthFix" class="w-25 h-25"/>
							</view>
							<view class="flex-1 col">
								<view class="row mb-2">
									<!-- <text class="text-sm font-bold text-green-500 mr-1">·</text> -->
									<text class="text-sm font-semibold">{{device.name}}</text>
								</view>
								<view v-if="device.tempMap.near">
									<wd-button size="small" custom-class="py-1 text-xs text-white w-15" custom-style="background: #6AAE36" :loading="device.tempMap.connecting" :disabled="device.tempMap.connecting" @click="addDevice(device)" click="page.navigateTo('../device/scriptList',{device:device})">{{$t('page.index.connect')}}</wd-button>
								</view>
								<view v-else>
									<text class="text-gray-400 text-xs"></text>
								</view>
							</view>
							<view class="top-3 right-4 absolute">
								<text class="text-sm gui-icons ml-4" @tap="openRenameModel(device)">&#xe69e;</text>
								<!-- <text class="text-sm gui-icons ml-4" @tap="delBuyerDevice(device.deviceId,index)">&#xe794;</text> -->
							</view>
						</view>
					</view>
					<view v-else class="center mt-10">
						<text class="text-gray-500 text-xs">{{$t('page.index.noDevices')}}</text>
					</view>
				</view>
				<wd-divider></wd-divider>
				<view class="mt-2">
					<text class="text-gray-600 text-sm">{{$t('page.index.serOthDevices')}}</text>
					<view v-if="preDeviceList?.length > 0">
						<view v-for="device in preDeviceList" :key="device.deviceId" class="bg-white rounded-xl px-2 py-4 mt-4 row">
							<view class="mx-2">
								<img :src="'../../static/'+device.tempMap.img" mode="widthFix" class="w-25 h-25"/>
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
						<text class="text-gray-500 text-xs">{{$t('page.index.noSerOthDevices')}}</text>
					</view>
                    <view class="w-full mt-10"></view>
				</view>
			</view>
			<view v-else-if="viewStatus == 2" class="px-2">
				<view class="bg-white rounded-xl px-2 py-5 mt-4">
					<view class="mx-10 center" @tap="connedToScriptPage()">
						<img :src="'../../static/'+theDevice.tempMap.img" mode="widthFix" class="w-40"></img>
					</view>
					<wd-divider></wd-divider>
					<view class="between mx-3 mt-5">
						<view class="row items-center gap-1 flex-1">
							<text class="text-sm font-semibold">{{theDevice.name}}</text>
                            <!-- #ifdef MP -->
							<text class="text-sm gui-icons ml-4" @tap="openRenameModel(theDevice)">&#xe69e;</text>
                            <!-- #endif -->
						</view>
						<view class="row items-center w-32" v-if="theDevice.tempMap.connected">
							<text class="text-sm font-bold mr-1">·</text>
							<text class="text-sm font-semibold mr-2">{{$t("page.index.connected")}}</text>
							<view class="row" @click.stop="closeConnection()">
								<wd-button size="small" type="error" custom-class="py-1 px-2 text-xs text-white w-15 self-end">{{$t("page.index.connectClose")}}</wd-button>
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
<!--    <view class="h-50"></view>-->
<!-- #ifdef MP	-->
	<wd-popup v-model="renameShow" position="bottom" :safe-area-inset-bottom="true" custom-style="border-radius:32rpx;">
		<view class="col center text-gray-500 mt-10">
			<text class="mt-2 row center">{{$t('page.index.editDeviceName')}}</text>
			<view class="row mt-10">
				<text class="text-xs">{{$t('page.index.deviceName')}}</text>
				<input class="minput ml-2" v-model="deviceName"/>
			</view>
			<view class="mt-10 mb-20">
				<wd-button custom-style="background: #6AAE36" size="small" @click="rename()"><text class="mx-4">{{$t('page.index.saveDeviceName')}}</text></wd-button>
			</view>
		</view>
	</wd-popup>
	
	<my-wxLogin :isShow="loginShow" @close="loginClose" @runAgain="loginRunAgain"></my-wxLogin>

	<tabbar :tabIndex="0"></tabbar>
<!--  #endif  -->
</template>

<script setup>
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import lodash from "lodash";
	
	import wxRest from "@/api/uniapp/wx.js";
	import deviceRest from "@/api/dbs/device.js";
	
	// #ifdef MP
    import { Blue } from '@/api/bluebooth/index.js';
    // #endif
    // #ifdef H5
    import { useBluetooth } from '@vueuse/core';
    import { Blue } from '@/api/bluebooth/web.js';
    // #endif
	import { ConnectController } from '@/api/bluebooth/controller.js';
	import {BLUE_STATE} from "@/api/bluebooth/blueState.js";
	import hexTools from "@/api/hexTools.js";
	import page from "@/api/uniapp/page.js";
	import dialog from "@/api/uniapp/dialog.js";
	
	import cmdjson from "@/api/datas/cmd.json";
	
	import { useNotify } from '@/uni_modules/wot-design-uni';
	import { Beans } from '@/api/dbs/beans';

	const { showNotify, closeNotify } = useNotify();
	const { proxy } = getCurrentInstance();
	const viewStatus = ref(-1);
	const deviceList = ref([]);
	const preDeviceList = ref([]);
	const theDevice = ref({});
	
	const renameShow = ref(false);
	const loginShow = ref(false);
	const deviceName = ref("");
	
	let userId = null;
	let deviceTypeList = null;
	let location = {}
	
	
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

	function tempProcess() {
		// deviceRest.testProcess((data)=>{
		// 	console.log(data);
		// });
        // page.navigateTo("../device/setup",{});

        // uni.setLocale("zh-Hans");
        proxy.$i18n.locale = uni.getLocale()=="en" ? "zh-Hans" : "en";
        uni.setLocale(proxy.$i18n.locale);
        // console.log(uni.getLocale());

        // proxy.$changeLang(uni.getLocale()=="en" ? "zh-Hans" : "en");
        // console.log(uni.getLocale());
	}
	
	function getUniqueId(bf) {
	    let buffer = bf.slice(4, 10);
	    let mac = Array.prototype.map.call(new Uint8Array(buffer), x => ('00' + x.toString(16)).slice(-2)).join('');
	    return mac.toUpperCase()
	}
	
	const init = ()=>{
		viewStatus.value = 0;
		preDeviceList.value = [];
		deviceList.value = [];
		(async ()=>{
			await new Promise(resolve => {
                // #ifdef MP
				deviceRest.qyDeviceTypeList(null,null,null,(data)=>{
					if (data.status=="OK") {
						deviceTypeList = data.deviceTypeList;
						lodash.forEach(deviceTypeList,(v,i)=>{
							// {serviceId:{scan:"00007365-0000-1000-8000-00805F9B34FB",uuid:"76617365-6570-6c61-6e74-776f726c6473"}}
							v.tempMap = {};
							v.tempMap.services = {};
							v.tempMap.services.serviceId = JSON.parse(v.serviceId).serviceId;
							v.tempMap.services.rcy = JSON.parse(v.characteristicsReadIds);
							v.tempMap.services.wcy = JSON.parse(v.characteristicsWriteIds);
							// console.log("qyDeviceTypeList",v);
						});
						resolve();
					}
				});
            //     #endif
            //     #ifdef H5
                deviceTypeList = JSON.parse(`[{"temp":null,"tempMap":{"services":{"serviceId":{"scan":"00007365-0000-1000-8000-00805F9B34FB","uuid":"76617365-6570-6c61-6e74-776f726c6473"},"rcy":["7661fff1-6570-6c61-6e74-776f726c6473"],"wcy":["7661fff2-6570-6c61-6e74-776f726c6473"]}},"id":"0","name":"Breath neo","appId":"wxab8fc59cb9d4f7cb","serviceId":"{\\"serviceId\\":{\\"scan\\":\\"00007365-0000-1000-8000-00805F9B34FB\\",\\"uuid\\":\\"76617365-6570-6c61-6e74-776f726c6473\\"}}","characteristicsReadIds":"[\\"7661fff1-6570-6c61-6e74-776f726c6473\\"]","characteristicsWriteIds":"[\\"7661fff2-6570-6c61-6e74-776f726c6473\\"]","deviceList":null}]`);
                resolve();
            //     #endif
			});
			
			await new Promise(resolve => {
                // #ifdef MP
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
									d.tempMap.connected = false;
									d.tempMap.connecting = false;
									d.tempMap.isDB = true;
                                    d.tempMap.remark = d.remark ? JSON.parse(d.remark) : {};
                                    d.tempMap.deviceName = d.tempMap.remark.deviceName;
                                    d.tempMap.img = getDeviceImg(d.tempMap.remark.deviceName);
									d.deviceType = lodash.find(deviceTypeList,(o)=>{return o.id==d.deviceType.id});
									// console.log("qyBuyerDeviceList",d);
								}
							}
							setTimeout(()=>{
								viewStatus.value = 1;
								resolve();
							},1500);
						}
					});
				} else {
					resolve();
				};
            //     #endif
            //     #ifdef H5
                deviceList.value = [];
                resolve();
            //     #endif
			});
			
			await new Promise(resolve => {
				ConnectController.addConnectStateListen((data)=>{
					console.log("addConnectStateListen",data);
                    // #ifdef MP
					let pages = getCurrentPages();
					
					if (data.lang) {
						showNotify(proxy.$t(data.lang));
					} else if (data.label) {
                        showNotify(data.label);
                    }
					// this.state = data.label;
					if (data.deviceId) {
						let device = null;
						if (lodash.findIndex(deviceList.value,(o)=>{return o.deviceId==data.deviceId})<0) {
							device = lodash.find(preDeviceList.value,(o)=>{return o.deviceId==data.deviceId});
							device.tempMap.isDB = false;
							// deviceList.value.push(device);
						} else {
							device = lodash.find(deviceList.value,(o)=>{return o.deviceId==data.deviceId});
							device.tempMap.isDB = true;
						}
						theDevice.value = device;
						console.log("theDevice",theDevice.value);
					}
					

					
					if(data.code==BLUE_STATE.CONNECTSUCCESS.code || data.connected) {
						console.log("connected",data);
						theDevice.value.tempMap.connecting = true;
						theDevice.value.tempMap.connected = true;
						
						Blue.setBleConnectDeviceID(theDevice.value.deviceId);
				
						console.log("read",theDevice.value.deviceType.tempMap.services.rcy,"write",theDevice.value.deviceType.tempMap.services.wcy);
						Blue.getBleCharacteristicsInfo(theDevice.value.deviceType.tempMap.services.rcy[0].toUpperCase(),theDevice.value.deviceType.tempMap.services.wcy[0].toUpperCase());
							
						// Blue.getBleCharacteristicsInfo("7661fff1-6570-6c61-6e74-776f726c6473".toUpperCase(),"7661fff2-6570-6c61-6e74-776f726c6473".toUpperCase());
						
						// let cday = uni.dayjs();
						// Blue.writeBLEValue(hexTools.bleBuffer("0x01",parseInt(cday.format("HH")),parseInt(cday.format("mm"))).buffer);
						setTimeout(()=>{
							viewStatus.value = 2;
						},1000);
					} 
					else if (data.code==BLUE_STATE.CONNECTFAILED.code) {
						// Blue.createBLEConnection(data.deviceId);
						// viewStatus.value = 0;
						if (pages[pages.length - 1].route!="pages/index/index") {
							dialog.alertBack(proxy.$t("page.setup.ctx.bluediscmsg"),false,()=>{
								page.reLaunch("/pages/index/index",{});
							},null);
						} else {
							if (theDevice) {
								theDevice.value.tempMap.connecting = false;
								theDevice.value.tempMap.connected = false;
							}
							viewStatus.value = 1;
						}
					}
					else if (data.code==BLUE_STATE.DISCONNECT.code) {
						if (pages[pages.length - 1].route!="pages/index/index") {
							dialog.alertBack(proxy.$t("page.setup.ctx.bluediscmsg"),false,()=>{
								page.reLaunch("/pages/index/index",{});
							},null);
						} else {
							if (theDevice) {
								theDevice.value.tempMap.connecting = false;
								theDevice.value.tempMap.connected = false;
							}
							viewStatus.value = 1;
						}
					} else {
						console.log("connect other status",data);
					}
					dialog.closeLoading();
                //     #endif
                //     #ifdef H5
                    let device = null;
                    if (lodash.findIndex(deviceList.value,(o)=>{return o.deviceId==data.deviceId})<0) {
                        console.log("ok");
                        device = Beans.device();
                        device.deviceId = data.deviceId;
                        device.name = data.name;
                        device.remark = JSON.stringify({os:uni.getSystemInfoSync().osName});
                        device.deviceType = lodash.find(deviceTypeList,(o)=>{return o.tempMap.services.serviceId.uuid.toLowerCase()==data.serviceId.toLowerCase()});
                        let buyer = Beans.buyer();
                        buyer.phone = userId;
                        device.buyer = buyer;
                        device.tempMap = {};
                        device.tempMap.deviceName = device.name;
                        device.tempMap.isDB = false;
                        device.tempMap.connected = true;
                        device.tempMap.connecting = true;
                        device.tempMap.near = true;
                        device.tempMap.img = getDeviceImg(device.name);
                        // deviceList.value.push(device);
                    } else {
                        device = lodash.find(deviceList.value,(o)=>{return o.deviceId==data.deviceId});
                        device.tempMap.isDB = true;
                        device.tempMap.connected = true;
                        device.tempMap.connecting = true;
                        device.tempMap.near = true;
                        device.tempMap.img = getDeviceImg(device.name);
                    }
                    theDevice.value = device;
                    console.log("theDevice",theDevice.value);
                    // 7661fff1-6570-6c61-6e74-776f726c6473   7661fff2-6570-6c61-6e74-776f726c6473
                    Blue.getBleCharacteristicsInfo(theDevice.value.deviceType.tempMap.services.rcy[0].toLowerCase(),theDevice.value.deviceType.tempMap.services.wcy[0].toLowerCase());
                    // Blue.getBleCharacteristicsInfo("7661fff1-6570-6c61-6e74-776f726c6473","7661fff2-6570-6c61-6e74-776f726c6473");
                    viewStatus.value = 2;
                //     #endif
				});
					   
				ConnectController.addDevicesListen((device)=>{
					console.log("addDevicesListen",device);
					device.connected = false;
					let dbDevice = Beans.device();
					dbDevice.deviceId = device.deviceId;
                    dbDevice.name = device.name;
					dbDevice.remark = JSON.stringify({os:uni.getSystemInfoSync().osName,deviceName:dbDevice.name});
					dbDevice.deviceType = lodash.find(deviceTypeList,(o)=>{return o.tempMap.services.serviceId.scan==device.advertisServiceUUIDs[0]});
					let buyer = Beans.buyer();
					buyer.phone = userId;
					dbDevice.buyer = buyer;
					dbDevice.tempMap = {};
                    dbDevice.tempMap.deviceName = dbDevice.name;
                    dbDevice.tempMap.img = getDeviceImg(dbDevice.name);
					dbDevice.tempMap.connected = false;
					dbDevice.tempMap.connecting = false;
					dbDevice.tempMap.near = true;
					
					//该设备是否保存到了数据库
					let index = lodash.findIndex(deviceList.value,(o)=>{return o.deviceId==dbDevice.deviceId});
					if (index < 0) {
						dbDevice.tempMap.isDB = false;
						preDeviceList.value.push(dbDevice);
					} else {
                        deviceList.value[index].tempMap.img = dbDevice.tempMap.img;
						deviceList.value[index].tempMap.isDB = true;
						deviceList.value[index].tempMap.near = true;
						deviceList.value[index].tempMap.connecting = false;
					}
					
					viewStatus.value = 1;
					dialog.closeLoading();
				});

                callBle();
				resolve();
			});
			
		})(); 
	};
	
	const refreshDevices = ()=>{
		for(let d of deviceList.value) {
			d.tempMap = {};
			d.tempMap.isDB = true;
			d.tempMap.near = false;
			d.tempMap.connected = false;
			d.tempMap.connecting = false;
            d.tempMap.remark = d.remark ? JSON.parse(d.remark) : {};
            d.tempMap.deviceName = d.tempMap.remark.deviceName;
            d.tempMap.img = getDeviceImg(d.tempMap.remark.deviceName);
		}
		callBle();
		viewStatus.value = 1;
	};

	onLoad((option)=>{
        uni.hideTabBar();
        // #ifdef MP
		userId = wxRest.getLoginState()?.userId;
		if (wxRest.getLoginState().userInfo.openid=="oalrT5KZGWw-V2scb_RYyS3FSDyw" || wxRest.getLoginState().userInfo.openid=="oalrT5F3SNZATiUERY6cDDl84a8I") {
			userId = "13268990066";
			let userInfo = {
				"errcode": null,
				"openid": wxRest.getLoginState().userInfo.openid,
				
				"errmsg": null,
				"subscribe": null,
				"nickname": null,
				"sex": null,
				"language": null,
				"city": null,
				"province": null,
				"country": null,
				"headimgurl": null,
				"subscribe_time": null,
				
				"nickname_emoji": null,
				"privilege": [],
				"unionid": "",
				"groupid": null,
				"remark": {userId: userId},
				"tagid_list": null,
				"subscribe_scene": null,
				"qr_scene": null,
				"qr_scene_str": null,
				"success": true
			};
			wxRest.setUserInfo(userInfo);
		}
        // #endif
        // #ifdef H5
        userId = "13268990066";
        let userInfo = {
            "errcode": null,
            "openid": "",

            "errmsg": null,
            "subscribe": null,
            "nickname": null,
            "sex": null,
            "language": null,
            "city": null,
            "province": null,
            "country": null,
            "headimgurl": null,
            "subscribe_time": null,

            "nickname_emoji": null,
            "privilege": [],
            "unionid": "",
            "groupid": null,
            "remark": {userId: userId},
            "tagid_list": null,
            "subscribe_scene": null,
            "qr_scene": null,
            "qr_scene_str": null,
            "success": true
        };
        wxRest.setUserInfo(userInfo);
        // #endif
		
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
		deviceName.value = theDevice.value.name;
	};
	
	const rename = ()=>{
		theDevice.value.name = deviceName.value;
		console.log("rename device info:",theDevice.value);
		if (!theDevice.value.tempMap.isDB) {
			theDevice.value.lat = location.lat;
			theDevice.value.lng = location.lng;
			deviceRest.addBuyerDevice(theDevice.value,(data)=>{
				if (data.status=="OK") {
					theDevice.value.tempMap.isDB = true;
					deviceList.value.push(theDevice.value);
					deviceRest.renameBuyerDevice(userId,theDevice.value.deviceId,theDevice.value.name,(data)=>{
						if (data.status=="OK") {
							let de = lodash.find(deviceList.value,(o)=>{return o.deviceId==theDevice.value.deviceId});
							if (de) {
								de.name = theDevice.value.name;
								renameShow.value = false;
								if (viewStatus.value!=2) {
									theDevice.value = {};
								}
								showNotify(proxy.$t("page.index.deviceNameUpdated"));
							}
						}
					});
				}
			});
		} else {
			deviceRest.renameBuyerDevice(userId,theDevice.value.deviceId,theDevice.value.name,(data)=>{
				if (data.status=="OK") {
					let de = lodash.find(deviceList.value,(o)=>{return o.deviceId==theDevice.value.deviceId});
					if (de) {
						de.name = theDevice.value.name;
						renameShow.value = false;
						if (viewStatus.value!=2) {
							theDevice.value = {};
						}
						showNotify(proxy.$t("page.index.deviceNameUpdated"));
					}
				}
			});
		}
		
	};
	
	const delBuyerDevice = (deviceId,index)=>{
		dialog.confirm(proxy.$t("page.index.isDelDevice"),()=>{
			deviceRest.delBuyerDevice(userId,deviceId,(data)=>{
				if (data.status=="OK") {
					deviceList.value.splice(index,1);
				}
			});
		},null);
	};
	
	const connedToScriptPage = ()=>{
        // #ifdef MP
		if (!theDevice.value.tempMap.isDB) {
			theDevice.value.lat = location.lat;
			theDevice.value.lng = location.lng;
			deviceRest.addBuyerDevice(theDevice.value,(data)=>{
				if (data.status=="OK") {
					deviceList.value.push(theDevice.value);
					page.navigateTo('../device/scriptList',{device:theDevice.value});
				}
			});
		} else {
			page.navigateTo('../device/scriptList',{device:theDevice.value});
		}
    //     #endif

    //     #ifdef H5
            page.navigateTo('../device/setup',{device:theDevice.value});
    //     #endif
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
			dialog.openLoading(proxy.$t("page.index.connecting"));
			// console.log("connect...",device.advertisServiceUUIDs);
			// Blue.setBlueServiceId(device.advertisServiceUUIDs[0]);
			// Blue.createBLEConnection(device.deviceId);
			// 换成了dbDrivice
			console.log("connect...",device.deviceType.tempMap.services);
			device.tempMap.connecting = true;
			Blue.setBlueServiceId(device.deviceType.tempMap.services.serviceId.uuid);
			Blue.createBLEConnection(device.deviceId);
		} else {
			showNotify("设备不在有效的范围，请重新刷新搜索。");
		}
	}

	const closeConnection = ()=>{
		dialog.confirm(proxy.$t("page.setup.ctx.confdisc"),()=>{
			Blue.closeBLEConnection();
			// callBle();
			init();
		},null);
	};
	
	function preCallBle() {
		// 0000fff0-0000-1000-8000-00805f9b34fb 0000fff0-0000-1000-8000-00805f9b34fb 0000fff1-0000-1000-8000-00805f9b34fb
		// {"serviceId":{"scan":"00007365-0000-1000-8000-00805F9B34FB","uuid":"76617365-6570-6c61-6e74-776f726c6473"}} ["7661fff1-6570-6c61-6e74-776f726c6473"] ["7661fff2-6570-6c61-6e74-776f726c6473"]
		// {"serviceId":{"scan":"0000fff0-0000-1000-8000-00805f9b34fb","uuid":"0000fff0-0000-1000-8000-00805f9b34fb"}} ["0000fff1-0000-1000-8000-00805f9b34fb"] ["0000fff2-0000-1000-8000-00805f9b34fb"]
		preDeviceList.value = [];
		let serviceFilter = [];
        // #ifdef MP
		for(let d of deviceTypeList) {
			serviceFilter.push(d.tempMap.services.serviceId.scan.toUpperCase());
		}
		Blue.setServiceFilter(serviceFilter);
    //     #endif
    //     #ifdef H5
        let serviceList = [];
        for(let d of deviceTypeList) {
            serviceFilter.push(d.tempMap.services.serviceId.scan.toLowerCase());
            serviceList.push(d.tempMap.services.serviceId.uuid.toLowerCase());
        }
        Blue.setServiceFilter(serviceFilter);
        Blue.setBlueService(useBluetooth({
            // acceptAllDevices: true,
            filters: [{ services: serviceFilter }],
            optionalServices:serviceList
        }));
    //     #endif
	}
		
	async function callBle() {
		
		// #ifdef H5
        preCallBle();
        let dtList = [];
        lodash.forEach(deviceTypeList,(v,i)=>{
            dtList.push(v.tempMap.services.serviceId.uuid);
        });
        Blue.callBle(dtList);

		// const device = await navigator.bluetooth.requestDevice({
		// 	filters: [{ services: [Blue.getBlueServiceId().toLowerCase()] }],
		// });
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
	};

    const getDeviceImg = (deviceName)=>{
        let imgElt = lodash.find(cmdjson.deviceImage,(o)=>{
            return lodash.trim(o.name)==lodash.trim(deviceName);
        });
        if (imgElt) {
            return imgElt.path;
        } else {
            return null;
        }
    };
	
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
