export const Beans = {
	buyer() {
		return {
			phone: "",
			name: "",
			loginName: "",
			password: "",
			wxid: "",
			wxopenid: "",
			area: "",
			address: "",
			tel: "",
			headImgUrl: "",
			wxNickName: "",
			wxNickEm: "",
			amb: 0,
			sex: 0,
			description: "",
			createDate: null,
			deviceList: []
		}
	},
	
	device() {
		return {
			deviceId: "",
			name: "",
			lat: "",
			lng: "",
			createDate: null,
			buyer: this.buyer(),
			deviceType: this.deviceType()
		}
	},
	
	deviceType() {
		return {
			id: "",
			name: "",
			appId: "",
			serviceId: "",
			characteristicsReadIds: "",
			characteristicsWriteIds: "",
			deviceList: []
		}
	},
	
	deviceScript() {
		return {
			id: "",
			name: "",
			script: "",
			device: this.device(),
			createDate: null
		}
	}
}