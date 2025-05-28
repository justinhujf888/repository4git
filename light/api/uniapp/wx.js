import {Http} from "@/api/http.js";
import dialog from "@/api/uniapp/dialog.js";
import {Config} from '@/api/config.js';
import util from "@/api/util.js";

export default {
	buildState() {
		return  {
			hasLogin: false,
			loginProvider: null,
			userId: null,
			openid: null,
			uniId: null,
			userInfo: null,
			param: null,
			url: null,
			code: null,
			loginToken: null,
			password: null,
			org: null
		}
	},
	clearLoginInfo() {
		// window.sessionStorage.removeItem("wxid");
		// window.sessionStorage.removeItem("userInfo");
		uni.removeStorageSync("wxId");
		uni.removeStorageSync("userInfo");
		uni.removeStorageSync("state");
	},
	getWxId() {
		if (uni.getStorageSync("wxId")) {
			return util.decryptStoreInfo(uni.getStorageSync("wxId"));
		} else {
			return null;
		}
	},
	getUserInfo() {
		// alert(window.sessionStorage.getItem("userInfo"));
		// return JSON.parse(window.sessionStorage.getItem("userInfo"));
		if (uni.getStorageSync("userInfo")) {
			return JSON.parse(util.decryptStoreInfo(uni.getStorageSync("userInfo")));
		} else {
			return null;
		}
	},
	getLoginState() {
		if (uni.getStorageSync("state")) {
			return JSON.parse(util.decryptStoreInfo(uni.getStorageSync("state")));
		} else {
			return null;
		}
	},
	checkLogin() {
		let state = this.getLoginState();
		if (state?.hasLogin) {
			return true;
		} else {
			return false;
		}
	},
	async getUserOpenId() {
		return await new Promise((resolve, reject) => {
			if (!this.getLoginState() || !this.getLoginState().hasLogin) {
				uni.login({
					provider: 'weixin',
					success: (loginRes) => {
						console.log("index2:"+JSON.stringify(loginRes));
						this.getUserOpenIdUseCode(Config.appId,loginRes.code, (userInfo) => {
							console.log("index3:"+JSON.stringify(userInfo));
							this.setUserInfo(userInfo);
						});
					},
				});
			}
			resolve();
		});
	},
	getUserOpenIdUseCode(appId,code,okfun) {
		Http.httpclient_json('/r/wx/wxLoginSec4WxMp', 'post', {
				"appId":appId,"code": code
			}, "json",
			(res)=>{
				if (res.data.status == "OK") {
					let userInfo = {
						"errcode": null,
						"openid": res.data.jr.openid,
						
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
						"unionid": res.data.jr.unionid,
						"groupid": null,
						"remark": {session_key:res.data.jr.session_key,expires_in:res.data.jr.expires_in},
						"tagid_list": null,
						"subscribe_scene": null,
						"qr_scene": null,
						"qr_scene_str": null,
						"success": true
					};
					
					// 小程序; 要获得用户手机才能认为真的登陆
					uni.setStorageSync('wxId',util.encryptStoreInfo(userInfo.openid));
					// uni.setStorageSync('userInfo',util.encryptStoreInfo(userInfo));
					// console.log(JSON.stringify(res));
					// console.log(JSON.stringify(userInfo));
					okfun(userInfo);
				} else if (res.data.status == "FA_ER") {
					console.log("发生系统错误，请联系客服或者稍后再试！");
				}
			},
			(error)=>{
				console.log(error);
			}, true);
	},
	setUserInfo(userInfo) {
		let state = this.buildState();
		state.userInfo = userInfo;
		state.uniId = userInfo.unionid;
		state.userId = userInfo.remark.userId;
		state.hasLogin = state.userId ? true : false;
		uni.setStorageSync('userInfo',util.encryptStoreInfo(JSON.stringify(userInfo)));
		uni.setStorageSync('state',util.encryptStoreInfo(JSON.stringify(state)));
	},
	decodeUserInfo4WxMp(appId,encryptedData,sessionKey,iv,buyer,onfun) {
		Http.httpclient_json('/r/wx/decodeUserInfo4WxMp', 'post', {
				"appId": appId,
				"encryptedData": encryptedData,
				"sessionKey": sessionKey,
				"iv":iv,
				"buyer":buyer
			}, "json",(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			(error)=>{
				dialog.showApiErrorMsg();
			}, true);
	}
}