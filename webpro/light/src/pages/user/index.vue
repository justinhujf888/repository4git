<template>
	<wd-navbar fixed placeholder safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="flex center mt-1">
				<text class="text-base">个人中心</text>
			</view>
		</template>
	</wd-navbar>
	<view class="my-2 mx-3 mt-5">
		<view class="row center bg-white rounded-xl text-base text-gray-600 mb-2">
			<view class="m-2">
<!--				<wd-img :width="100" :height="100" round src="../../../../static/logo.png" />-->
				 <img src="@/static/logo.png" class="w-20 h-20 rounded-full"/>
			</view>
			<view class="flex-1">
				<text v-if="userId">{{userId}}</text>
				<text v-else>未登录</text>
			</view>
		</view>
		
		<view v-if="userId">
			<wd-cell-group border>
			  <wd-cell size="large" title="我的设备" is-link value="" to="/pages/user/myDevices"/>
			  <wd-cell size="large" title="隐私政策" is-link value="" />
			</wd-cell-group>
		</view>
		
		<view v-if="userId" class="mt-14">
			<wd-button block size="large" custom-class="bg-write text-gray-600" @click="logout()" custom-style="background: #6AAE36">退出登录</wd-button>
		</view>
		<view v-else class="mt-14">
			<wd-button block size="large" custom-class="bg-write text-gray-600" @click="login()" custom-style="background: #6AAE36">登    录</wd-button>
		</view>
	</view>
	<my-wxLogin :isShow="loginShow" @close="loginClose" @runAgain="loginRunAgain"></my-wxLogin>
	<tabbar :tabIndex="1"></tabbar>
</template>

<script setup>
	import { ref,getCurrentInstance } from 'vue';
	import { onShow, onHide,onLoad,onUnload } from "@dcloudio/uni-app";
	import wxRest from "@/api/uniapp/wx.js";
	import dialog from "@/api/uniapp/dialog.js";
	import page from "@/api/uniapp/page.js";
	
	const userId = ref("");
	const loginShow = ref(false);
	
	
	onLoad((option)=>{
		userId.value = wxRest.getLoginState()?.userId;
	});
	
	onUnload(()=>{
		// closeDevice();
	});
	
	const logout = ()=>{
		dialog.confirm("是否退出登录？",()=>{
			wxRest.clearLoginInfo();
			page.reLaunch("/pages/index/index",{});
		},null);
	};
	
	const login = ()=>{
		loginShow.value = true;
	};
	
	const loginClose = (e)=>{
		loginShow.value = e;
	};
	
	const loginRunAgain = (e)=>{
		page.reLaunch("/pages/index/index",{});
	};
</script>

<style>
</style>