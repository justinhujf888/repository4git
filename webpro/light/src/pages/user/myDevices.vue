<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="flex center mt-1">
				<text class="text-base">个人中心</text>
			</view>
		</template>
	</wd-navbar>
	<view class="my-2 mx-3">
        <wd-notify></wd-notify>
		<view>
			<text class="text-gray-600 text-sm">我的设备</text>
			<view v-if="deviceList?.length > 0">
				<view v-for="(device,index) in deviceList" :key="device.deviceId" class="bg-white rounded-xl px-2 py-4 mt-4 row relative">
					<view class="mx-2">
						<img :src="'../../static/' + device.tempMap.img" mode="widthFix" class="w-20 h-20"></img>
					</view>
					<view class="flex-1 col justify-center items-start">
						<view class="col mb-2">
							<text class="text-sm font-semibold">{{device.name}}</text>
<!--                            <text class="text-xs">{{device.deviceId}}</text>-->
						</view>
					</view>
					<view class="row top-2 right-4">
						<text class="text-sm gui-icons ml-4 mt-1" @tap="openRenameModel(device)">&#xe69e;</text>
						<text class="text-xl text-red-500 gui-icons ml-8" @tap="delBuyerDevice(device.deviceId,index)">&#xe602;</text>
					</view>
				</view>
			</view>
			<view v-else class="center mt-10">
				<text class="text-gray-500 text-xs">没有添加设备</text>
			</view>
		</view>
	</view>
	<wd-popup v-model="renameShow" position="bottom" :safe-area-inset-bottom="true" custom-style="border-radius:32rpx;">
		<view class="col center text-gray-500 mt-10">
			<text class="mt-2 row center">修改设备名称</text>
			<view class="row mt-10">
				<text class="text-xs">设备名称</text>
				<input class="minput ml-2" v-model="theDevice.name"/>
			</view>
			<view class="mt-10">
				<wd-button size="medium" @click="rename()" custom-style="background: #6AAE36">确定</wd-button>
			</view>
		</view>
	</wd-popup>
</template>

<script setup>
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	
	import lodash from "lodash";
	import { useNotify } from '@/uni_modules/wot-design-uni';
	import page from "@/api/uniapp/page.js";
	
	import dialog from "@/api/uniapp/dialog.js";
	import wxRest from "@/api/uniapp/wx.js";
	import deviceRest from "@/api/dbs/device.js";
    import cmdjson from "@/api/datas/cmd.json";
	
	const deviceList = ref([]);
	const theDevice = ref({});
	
	const renameShow = ref(false);
	
	const { showNotify, closeNotify } = useNotify();
	
	let userId = null;
		
	
	onLoad((option)=>{
		userId = wxRest.getLoginState().userId;
		deviceRest.qyBuyerDeviceList(userId,(data)=>{
			if (data.status=="OK") {
				deviceList.value = data.deviceList;
				if (!deviceList.value) {
					deviceList.value = [];
				}
                for(let d of deviceList.value) {
                    d.tempMap = {};
                    d.tempMap.remark = d.remark ? JSON.parse(d.remark) : {};
                    d.tempMap.deviceName = d.tempMap.remark.deviceName;
                    d.tempMap.img = getDeviceImg(d.tempMap.remark.deviceName);
                    console.log("qyBuyerDeviceList",d.tempMap.img);
                }
			}
		});
	});
	
	onUnload(()=>{
		// closeDevice();
	});
	
	const openRenameModel = (device)=>{
		renameShow.value = true;
		theDevice.value = lodash.cloneDeep(device);
	};
	
	const rename = ()=>{
		deviceRest.renameBuyerDevice(userId,theDevice.value.deviceId,theDevice.value.name,(data)=>{
			if (data.status=="OK") {
				let de = lodash.find(deviceList.value,(o)=>{return o.deviceId==theDevice.value.deviceId});
				if (de) {
					de.name = theDevice.value.name;
					theDevice.value = {};
					renameShow.value = false;
					showNotify("设备名称已修改");
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
</script>

<style>
</style>