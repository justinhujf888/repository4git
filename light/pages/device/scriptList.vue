<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="flex center mt-1">
				<text class="text-base">我的方案</text>
			</view>
		</template>
	</wd-navbar>
	<wd-notify></wd-notify>
	<view class="my-2 mx-3">
		<view>
			<view v-if="scriptList?.length > 0">
				<view v-for="(script,index) in scriptList" :key="script.id" class="bg-white rounded-xl px-4 py-4 mt-4 row relative" :class="script.areUse==1 ? 'border-solid border-4 border-indigo-600 rounded-md' : ''" @tap="appScript(script,null)">
					<view class="flex-1 col justify-center items-start">
						<view class="row mb-2">
							<text class="text-sm font-semibold">{{script.name}}</text>
						</view>
					</view>
					<view class="ml-8 mr-2">
						<wd-button custom-class="mx-2" type="icon" icon="tools" @tap.stop="setupScript(script)"></wd-button>
						<wd-button custom-class="mx-2" type="icon" icon="edit" @tap.stop="theDeviceScript=script;renameShow=true"></wd-button>
						<wd-button custom-class="mx-2" type="icon" icon="delete" @tap.stop="delTheDeviceScript(script.id,index)"></wd-button>
					</view>
				</view>
			</view>
			<view v-else class="center mt-10">
				<text class="text-gray-500 text-xs">没有添加方案</text>
			</view>
			<view class="mt-5 row center">
				<wd-button size="small" custom-class="py-1 px-2 text-white" custom-style="background: #6AAE36" @click="theDeviceScript=Beans.deviceScript();renameShow=true">新增方案</wd-button>
			</view>
			
<!-- 			<wd-divider></wd-divider>
			
			<view class="mt-10">
				<wd-cell-group border>
					<wd-cell size="large" is-link title="进入设备控制台" :to='"./setup?param="+JSON.stringify({device:device,deviceScript:null})'></wd-cell>
				</wd-cell-group>
			</view>
			<text class="text-xs text-gray-400">如要修改已保存的方案，请点击上面需要修改的方案进入控制台。</text> -->
		</view>
	</view>
	
	<wd-popup v-model="renameShow" position="bottom" :safe-area-inset-bottom="true" custom-style="border-radius:32rpx;">
		<view class="col center text-gray-500 mt-10">
			<text class="mt-2 row center">设置方案名称</text>
			<view class="row mt-10">
				<text class="text-xs">方案名称</text>
				<input class="minput ml-2" v-model="theDeviceScript.name"/>
			</view>
			<view class="mt-10">
				<wd-button size="medium" @click="saveScript()" custom-style="background: #6AAE36">确定</wd-button>
			</view>
		</view>
	</wd-popup>
</template>

<script setup>
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	const {proxy} = getCurrentInstance();
	
	import lodash from "lodash";
	import { useNotify } from '@/uni_modules/wot-design-uni';
	import page from "@/api/uniapp/page.js";
	
	import dialog from "@/api/uniapp/dialog.js";
	import wxRest from "@/api/uniapp/wx.js";
	import deviceRest from "@/api/dbs/device.js";
	import { Beans } from '@/api/dbs/beans';
	import { Blue } from '@/api/bluebooth/index.js';
	import hexTools from "@/api/hexTools.js";
	
	const scriptList = ref([]);
	const theDeviceScript = ref({});
	const renameShow = ref(false);
	
	const { showNotify, closeNotify } = useNotify();
	
	const device = ref({});
	
	let userId = null;
		
	
	onLoad((option)=>{
		let param = JSON.parse(decodeURIComponent(option.param));
		device.value = param.device;
		userId = wxRest.getLoginState().userId;
		// console.log("device",device.value);
		deviceRest.qyDeviceScriptList(device.value.deviceId,(data)=>{
			if (data.status=="OK") {
				scriptList.value = data.deviceScriptList;
				if (!scriptList.value) {
					scriptList.value = [];
				}
			}
		});
	});
	
	onUnload(()=>{
		// closeDevice();
	});
	
	const saveScript = ()=>{
		let areNew = false;
		if (!theDeviceScript.value.id) {
			areNew = true;
			theDeviceScript.value.id = Beans.buildPId("");
			theDeviceScript.value.createDate = proxy.dayjs().valueOf();//.format("YYYY-MM-DD HH:mm:ss");
			theDeviceScript.value.areUse = 0;
		}
		theDeviceScript.value.device = device.value;
		// console.log(theDeviceScript.value);
		if (areNew) {
			deviceRest.updateTheDeviceScript(theDeviceScript.value,(data)=>{
				if (data.status=="OK") {
					scriptList.value.push(theDeviceScript.value);
					dialog.confirm("您还未设置设备参数，现在是否进行设备参数设定？",()=>{
						page.navigateTo('./setup',{device:device.value,deviceScript:theDeviceScript.value})
					},()=>{
						
					});
					renameShow.value = false;
				}
			});
		} else {
			deviceRest.renameDeviceScript(theDeviceScript.value.id,theDeviceScript.value.name,-1,device.value.deviceId,(data)=>{
				if (data.status=="OK") {
					dialog.alertBack("方案名称已修改，您可以点击设置图标进行设备参数设定。",false,()=>{
						let ds = lodash.find(scriptList.value,(o)=>{return o.id==theDeviceScript.value.id});
						if (ds) {
							ds.name = theDeviceScript.value.name;
							theDeviceScript.value = {};
							renameShow.value = false;
							showNotify("方案名称已修改");
						}
					},null);
				}
			});
		}
		
	};
	
	const delTheDeviceScript = (scriptId,index)=>{
		dialog.confirm("是否删除这个方案？",()=>{
			deviceRest.delTheDeviceScript(scriptId,(data)=>{
				if (data.status=="OK") {
					scriptList.value.splice(index,1);
					showNotify("方案已删除");
				}
			});
		},null);
	};
	
	let cmdList = null;
	const write2DeviceStep = (step,fun)=>{
		if (step>=cmdList.length-1) {
			if (fun) {
				fun();
			}
			dialog.hideLoading();
		} else {
			Blue.writeBLEValue(hexTools.bleBuffer(cmdList[step].cmd,parseInt(cmdList[step].v0),parseInt(cmdList[step].v1)).buffer);
			setTimeout(()=>{
				write2DeviceStep(step+1,fun);
			},1000);
		}
	};
	
	const writeScript = (script,fun)=>{
		dialog.openLoading("正在应用方案");
		cmdList = JSON.parse(script.script);
		write2DeviceStep(0,fun);
	};
	
	const appScript = (script,fun)=>{
		dialog.confirm("是否应用此方案？",()=>{
			if (!script.script) {
				dialog.toastNone("还未设置设备参数，您可以点击设置图标进行设备参数设定。");
				return;
			}
			writeScript(script,()=>{
				deviceRest.renameDeviceScript(script.id,null,1,device.value.deviceId,(data)=>{
					if (data.status=="OK") {
						lodash.forEach(scriptList.value,(v,i)=>{
							if (v.id==script.id) {
								v.areUse = 1;
							} else {
								v.areUse = 0;
							}
						});
						showNotify(`已应用${script.name}的方案配置`);
					}
				});
			});
		},null);
	};
	
	const setupScript = (script)=>{
		if (!script.script) {
			page.navigateTo('./setup',{device:device.value,deviceScript:script});
		} else {
			writeScript(script,()=>{
				page.navigateTo('./setup',{device:device.value,deviceScript:script});
			});
		}
	};
	
	const backScriptStr = (scriptId,scriptStr,deviceId)=>{
		if (scriptId) {
			deviceRest.reScriptDeviceScript(scriptId,scriptStr,deviceId,(data)=>{
				if (data.status=="OK") {
					showNotify("此方案设备参数已成功设置");
					lodash.forEach(scriptList.value,(v,i)=>{
						if (v.id==scriptId) {
							v.script = scriptStr;
							v.areUse = 1;
						} else {
							v.areUse = 0;
						}
					});
				}
			});
		} else {
			// for(let s of scriptList.value) {
			// 	if (s.script) {
			// 		let sb = JSON.parse(s.script);
			// 		s.tempMap.areUse = lodash.isEqual(sb,JSON.parse(scriptStr));
			// 	}
			// }
		}
	};
	
	defineExpose({
		backScriptStr
	});
</script>

<style>
</style>