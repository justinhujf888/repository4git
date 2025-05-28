import {Config} from "@/api/config.js";
import util from "@/api/util.js";
import dialog from "@/api/uniapp/dialog.js";

export const Http = {
	httpclient_json(url, method, ds, ptype, returnfun, errorfun, loading) {
		if (loading) {
			dialog.openLoading("处理中");
		}
		let str = new Date(+new Date() + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '');
		ds.loginState = null;//login.getLoginState();
		ds.currentAppId = Config.appId;
		ds.appId = Config.appId;
		uni.request({
		    url: Config.apiBaseURL + url,
			method: "POST",
			// header: {timestamp:jsEncrypt.encrypt(str),createTime:str,nonceStr:util.encryptStoreInfo(str)},
		    data: util.encryptStoreInfo(encodeURIComponent(JSON.stringify(ds))),
		    header: {"self":"true"},
		    success: (res) => {
				// #ifdef APP-PLUS
				if(typeof(res.data.hasLogin)=="undefined") {
					returnfun(res);
				} else {
					if(!res.data.hasLogin) {
						dialog.alertBack("检测到您没有登陆,或者账号在其他设备登陆",false,()=>{
							
						},()=>{});
					} else {
						returnfun(res);
					}
				}
				// #endif
				// #ifdef MP
				returnfun(res);
				// #endif
		    },
			fail: (res) => {
				if (errorfun) {
					errorfun(res);
				}
				dialog.alert("系统错误，请通知系统管理员或者客服；谢谢！");
				console.log(res);
			},
			complete: (res) => {
				if (loading) {
					dialog.closeLoading();
				}
			}
		});
	}
}