import {Beans} from "@/api/dbs/beans.js";
import {Http} from "@/api/http.js";
import dialog from "@/api/uniapp/dialog.js";
import {Config} from '@/api/config.js';

export default {
	qyDeviceTypeList(typeId,serviceId,name,onfun) {
		Http.httpclient_json('/r/device/qyDeviceTypeList', 'post',{"typeId":typeId,"serviceId":serviceId,"name":name}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	qyBuyerDeviceList(userId,onfun) {
		Http.httpclient_json('/r/device/qyBuyerDeviceList', 'post',{"userId":userId}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	qyDeviceScriptList(deviceId,onfun) {
		Http.httpclient_json('/r/device/qyDeviceScriptList', 'post',{"deviceId":deviceId}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	updateTheDeviceScript(deviceScript,onfun) {
		Http.httpclient_json('/r/device/updateTheDeviceScript', 'post',{"deviceScript":deviceScript}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	addBuyerDevice(device,onfun) {
		Http.httpclient_json('/r/device/addBuyerDevice', 'post',{"device":device}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	delBuyerDevice(userId,deviceId,onfun) {
		Http.httpclient_json('/r/device/delBuyerDevice', 'post',{"userId":userId,"deviceId":deviceId}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	renameBuyerDevice(userId,deviceId,name,onfun) {
		Http.httpclient_json('/r/device/renameBuyerDevice', 'post',{"userId":userId,"deviceId":deviceId,"name":name}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	delTheDeviceScript(scriptId,onfun) {
		Http.httpclient_json('/r/device/delTheDeviceScript', 'post',{"id":scriptId}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	renameDeviceScript(id,name,areUse,deviceId,onfun) {
		Http.httpclient_json('/r/device/renameDeviceScript', 'post',{"id":id,"name":name,"areUse":areUse,"deviceId":deviceId}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	reScriptDeviceScript(scriptId,script,deviceId,onfun) {
		Http.httpclient_json('/r/device/reScriptDeviceScript', 'post',{"scriptId":scriptId,"script":script,"deviceId":deviceId}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	
	
	genAliOssAccessInfo(onfun) {
		Http.httpclient_json('/r/other/genAliOssAccessInfo', 'post',{}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	},
	genSignature(onfun) {
		Http.httpclient_json('/r/other/genSignature', 'post',{}, "json",
			(res)=>{
				if (res.data.status == "FA_ER") {
					dialog.showApiErrorMsg();
				} else {
					onfun(res.data);
				}
			},
			null, true);
	}
}