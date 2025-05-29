<template>
	<wd-popup v-model="show" position="bottom" :safe-area-inset-bottom="true" custom-style="border-radius:32rpx;" @close="handleClose">
		<view class="p-8 col h-48">
			<view class="text-xs text-gray-500 mt-4">
				<text>为了更好的为您提供服务，您的手机号码将做为登录凭证以及您参与线下活动与平台绑定的信息；我们承诺不会泄露您的个人信息。请点击“手机快捷登录”完成注册。</text>
			</view>
			<view class="row center mt-8">
				<view>
					<wd-button size="small" custom-style="background: #6AAE36" open-type="getPhoneNumber" @getphonenumber="getphonenumber">手机快捷登录</wd-button>
				</view>
				<view class="ml-6">
					<wd-button size="small" @click="cancel">暂不登录</wd-button>
				</view>
			</view>
		</view>
	</wd-popup>
</template>

<script setup>
	import { ref,watch,onMounted,getCurrentInstance, toRefs } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import {Beans} from "@/api/dbs/beans.js";
	import wxRest from "@/api/uniapp/wx.js";
	import {Config} from '@/api/config.js';
	import dialog from "@/api/uniapp/dialog.js";
	
	const props = defineProps({
		isShow: {
			type: Boolean,
			default: false
		}
	});
	const {isShow} = toRefs(props);
	
	const show = ref(false);
	
	onMounted(()=>{
		show.value = isShow.value;
		console.log("getUserInfo",wxRest.getUserInfo());
		if (!wxRest.getUserInfo()) {
			wxRest.getUserOpenId();
		} else if (!wxRest.getUserInfo().remark.userId) {
			wxRest.getUserOpenId();
		}
	});
	
	watch(isShow,(newValue,oldValue)=>{
		show.value = newValue;
	});
	
	const emit = defineEmits(["close","runAgain"]);
	const handleClose = ()=>{
		show.value = false;
		emit("close",show.value);
	};
	
	const cancel = ()=>{
		show.value = false;
		emit("close",show.value);
	};
	
	const open = ()=>{
		show.value = true;
	};
	
	const getphonenumber = (e)=>{
		if (e.errMsg == "getPhoneNumber:ok") {
			let userInfo = wxRest.getUserInfo();
			let buyer = Beans.buyer();
			buyer.wxid = userInfo.openid;
			buyer.wxopenid = userInfo.unionid;
			
			wxRest.decodeUserInfo4WxMp(Config.appId,e.encryptedData,
				userInfo.remark.session_key,
				e.iv,buyer,(data)=>{
					if (data.status=="FA_HASPHONE") {
						dialog.alertBack("此号码已经被注册",false,()=>{
							cancel();
						},null);
					} else if (data.status=="OK") {
						// console.log(data,userInfo);
						userInfo.remark.userId = data.buyer.phone;
						userInfo.unionid = data.buyer.wxopenid;
						wxRest.setUserInfo(userInfo);
						cancel();
						emit("runAgain",userInfo);
					}
			});
			
			// (async ()=>{
				
			// 	await new Promise(resolve => {
					
			// 	});
				
			// })();
		}
	};
	
	
</script>

<style>
</style>