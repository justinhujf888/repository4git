import {Beans} from "@/api/dbs/beans.js";
import {Http} from "@/api/http.js";
import dialog from "@/api/uniapp/dialog.js";
import {Config} from '@/api/config.js';

export default {
	async qyDeviceTypeList(typeId,serviceId,name,onfun) {
		return await Http.callHttpFunction('/r/device/qyDeviceTypeList',{"typeId":typeId,"serviceId":serviceId,"name":name},onfun);
		// Http.httpclient_json('/r/device/qyDeviceTypeList', 'post',{"typeId":typeId,"serviceId":serviceId,"name":name}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async qyBuyerDeviceList(userId,onfun) {
		return await Http.callHttpFunction('/r/device/qyBuyerDeviceList',{"userId":userId},onfun);
		// Http.httpclient_json('/r/device/qyBuyerDeviceList', 'post',{"userId":userId}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async qyDeviceScriptList(userId,deviceId,deviceTypeId,onfun) {
		return await Http.callHttpFunction('/r/device/qyDeviceScriptList',{"userId":userId,"deviceId":deviceId,"deviceTypeId":deviceTypeId},onfun);
		// Http.httpclient_json('/r/device/qyDeviceScriptList', 'post',{"userId":userId,"deviceId":deviceId,"deviceTypeId":deviceTypeId}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async updateTheDeviceScript(deviceScript,onfun) {
		return await Http.callHttpFunction('/r/device/updateTheDeviceScript',{"deviceScript":deviceScript},onfun);
		// Http.httpclient_json('/r/device/updateTheDeviceScript', 'post',{"deviceScript":deviceScript}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async addBuyerDevice(device,onfun) {
		return await Http.callHttpFunction('/r/device/addBuyerDevice',{"device":device},onfun);
		// Http.httpclient_json('/r/device/addBuyerDevice', 'post',{"device":device}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async delBuyerDevice(userId,deviceId,onfun) {
		return await Http.callHttpFunction('/r/device/delBuyerDevice',{"userId":userId,"deviceId":deviceId},onfun);
		// Http.httpclient_json('/r/device/delBuyerDevice', 'post',{"userId":userId,"deviceId":deviceId}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async renameBuyerDevice(userId,deviceId,name,onfun) {
		return await Http.callHttpFunction('/r/device/renameBuyerDevice',{"userId":userId,"deviceId":deviceId,"name":name},onfun);
		// Http.httpclient_json('/r/device/renameBuyerDevice', 'post',{"userId":userId,"deviceId":deviceId,"name":name}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async delTheDeviceScript(scriptId,onfun) {
		return await Http.callHttpFunction('/r/device/delTheDeviceScript',{"id":scriptId},onfun);
		// Http.httpclient_json('/r/device/delTheDeviceScript', 'post',{"id":scriptId}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async renameDeviceScript(id,name,areUse,deviceId,onfun) {
		return await Http.callHttpFunction('/r/device/renameDeviceScript',{"id":id,"name":name,"areUse":areUse,"deviceId":deviceId},onfun);
		// Http.httpclient_json('/r/device/renameDeviceScript', 'post',{"id":id,"name":name,"areUse":areUse,"deviceId":deviceId}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	async reScriptDeviceScript(scriptId,script,deviceId,onfun) {
		return await Http.callHttpFunction('/r/device/reScriptDeviceScript',{"scriptId":scriptId,"script":script,"deviceId":deviceId},onfun);
		// Http.httpclient_json('/r/device/reScriptDeviceScript', 'post',{"scriptId":scriptId,"script":script,"deviceId":deviceId}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},
	
	async testProcess(onfun) {
		return await Http.callHttpFunction('/r/device/tempProcess',{},onfun);
		// Http.httpclient_json('/r/device/tempProcess', 'post',{}, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	},

	async test(ds,onfun) {
		return await Http.callHttpFunction('/r/other/test',ds,onfun);
		// Http.httpclient_json('/r/other/test', 'post',ds, "json",
		// 	(res)=>{
		// 		if (res.data.status == "FA_ER") {
		// 			dialog.showApiErrorMsg();
		// 		} else {
		// 			onfun(res.data);
		// 		}
		// 	},
		// 	null, true);
	}
}