import { BLUE_STATE } from './blueState.js';
import { ConnectController } from './controller.js';
import lodash from "lodash";

let pserviceId = "";//"0000FFF0-0000-1000-8000-00805F9B34FB" "76617365-6570-6c61-6e74-776f726c6473";
let serviceFilter = null;
let bleConnectDeviceID = null;
let opened = false;
let csValue = {serviceId:null,readId:null,writeId:null};
if (uni.getStorageSync("blueOpened")) {
	opened = uni.getStorageSync("blueOpened");
} else {
	uni.setStorageSync("blueOpened",false);
	opened = false;
}

export const Blue = {
	/**
	 * 蓝牙连接状态：200-> 已连接；-1 ->未连接
	 */
	connectState: -1,
	
	setServiceFilter(ay) {
		serviceFilter = ay;
	},
	
	setBlueServiceId(id) {
		pserviceId = id;
	},
	
	getBlueServiceId() {
		return pserviceId;
	},
	
	getBleConnectDeviceID() {
		return bleConnectDeviceID;
	},
	setBleConnectDeviceID(deviceId) {
		bleConnectDeviceID = deviceId;
	},
	getBleCharacteristicInfo() {
		return csValue;
	},
	
	callBle() {
		if (!opened) {
			Blue.start();
		} else {
			Blue.closeBlue();
			Blue.start();
		}
	},
		
	/**
	 * 扫描蓝牙设备
	 * @param onlyBind 是否单独绑定操作，默认false
	 * @param module 设备模块，区分设备
	 */
	start() {
		wx.openBluetoothAdapter({
			success: ()=> {
				opened = true;
				uni.setStorageSync("blueOpened",opened);
				this.startBluetoothDevicesDiscovery()
			},
			fail: (res)=> {
				console.error('打开蓝牙适配器失败：', res)
				if (res.errCode === 10001) {
					if(res.state === 3) {
						ConnectController.connectStateListen({
							...BLUE_STATE.NOBLUETOOTHPERMISSION,
							...res
						});
					}else {
						ConnectController.connectStateListen({
							...BLUE_STATE.UNAVAILABLE,
							...res
						});
					}
			
				}
				// Android 系统特有，系统版本低于 4.3 不支持 BLE
				else if(res.errCode === 10009) {
					ConnectController.connectStateListen({
						...BLUE_STATE.VERSIONLOW,
						...res
					})
				}
				else if(res.errCode === 10008) {
					ConnectController.connectStateListen({
						...BLUE_STATE.SYSTEMERROR,
						...res
					});
				}
				else {
					ConnectController.connectStateListen({
						...res
					});
				}
			},
			complete: ()=> {
				wx.offBluetoothAdapterStateChange()
				wx.onBluetoothAdapterStateChange((res)=> {
					if(this._discoveryStarted) return
					if (res.available) {
						this._discoveryStarted = res.discovering
						ConnectController.connectStateListen(BLUE_STATE.SCANING)
						this.startBluetoothDevicesDiscovery()
					}else {
						// 蓝牙模块未开启
						ConnectController.connectStateListen(BLUE_STATE.UNAVAILABLE)
					}
				})
			}
		});
		
		// if (!opened) {
			
		// } else {
		// 	wx.offBluetoothAdapterStateChange()
		// 	wx.onBluetoothAdapterStateChange((res)=> {console.log("aaaaaa");
		// 		if(this._discoveryStarted) return
		// 		if (res.available) {
		// 			this._discoveryStarted = res.discovering
		// 			ConnectController.connectStateListen(BLUE_STATE.SCANING)
		// 			this.startBluetoothDevicesDiscovery()
		// 		}else {
		// 			// 蓝牙模块未开启
		// 			ConnectController.connectStateListen(BLUE_STATE.UNAVAILABLE)
		// 		}
		// 	})
		// }
	},
	/**
	 * 关闭蓝牙、初始化BLE状态
	 */
	closeBlue() {
		if(bleConnectDeviceID) {
			wx.closeBLEConnection({
				deviceId: bleConnectDeviceID,
			})
		}
		bleConnectDeviceID = null
		this.connectState = -1
		this._discoveryStarted = false
		wx.closeBluetoothAdapter()
		wx.offBluetoothAdapterStateChange()
		wx.offBLEConnectionStateChange()
		wx.stopBluetoothDevicesDiscovery();
		opened = false;
		uni.setStorageSync("blueOpened",opened);
	},
	/**
	 * 断开蓝牙
	*/
	closeBLEConnection() {
		wx.closeBLEConnection({
			deviceId: bleConnectDeviceID,
			success: ()=> {
				bleConnectDeviceID = null
				this.connectState = -1
				uni.showToast({
					icon: 'none',
					title: '蓝牙已断开连接'
				})
			},
			fail:(er)=>{
				console.log(er);
			}
		})
	},
		
	startBluetoothDevicesDiscovery() {
		if (this._discoveryStarted) {
			return
		}
		this._discoveryStarted = true
		console.log(serviceFilter);
		this.onBluetoothDeviceFound()
		wx.startBluetoothDevicesDiscovery({
			allowDuplicatesKey: false,
			interval: 1000,
			services: serviceFilter,
			success: (res)=> {
				// uni.getBluetoothDevices({
				//   success(res) {
				//     console.log("getBluetoothDevices",res)
				//   }
				// })
				
				console.log("startblueDiscovery",res);
				// this.onBluetoothDeviceFound()
				console.log('扫描中.....')
			},
			fail: (res)=> {
				console.error('搜索外围设备失败--', res)
				const { locationAuthorized } = uni.getSystemInfoSync()
				if(res.errCode === -1 && (res.errno === 1509008 || res.errno === 1509009) || !locationAuthorized) {
					this.closeBlue()
					ConnectController.connectStateListen({
						...BLUE_STATE.NOLOCATIONPERMISSION,
						...res
					});
				}
			}
		})
	},
	
	getBleCharacteristicsInfo(readId,writeId) {
		(async ()=>{
			
			await new Promise(resolve => {
				uni.getBLEDeviceServices({
					deviceId: bleConnectDeviceID,
					success: (res)=> {
						console.log(res);
						lodash.forEach(res.services,(o,i)=>{
							if (o.isPrimary && o.uuid.toUpperCase()==pserviceId.toUpperCase()) {
								csValue.serviceId = o.uuid;
							}
						});
						console.log("service uuid aa:"+csValue.serviceId);
						if (csValue.serviceId) resolve();
					},
					fail: (er)=> {
						console.log(er);
					},
				});
			});
			
			await new Promise(resolve => {
				uni.getBLEDeviceCharacteristics({
					deviceId: bleConnectDeviceID,
					serviceId: csValue.serviceId,
					success: (res)=> {
						console.log(res);
						lodash.forEach(res.characteristics,(o,i)=>{
							if (o.uuid.toUpperCase() == writeId) {
								csValue.writeId = o.uuid;
							}
							if (o.uuid.toUpperCase() == readId) {
								csValue.readId = o.uuid;
							}
						});
						console.log("Characteristics-----:",csValue);
						resolve();
					},
					fail: (er)=> {
						console.log(er);
					},
				});
			});
			
		})();
	},
	
	writeBLEValue(value) {
		uni.writeBLECharacteristicValue({
			deviceId: bleConnectDeviceID,
			serviceId: csValue.serviceId,
			characteristicId: csValue.writeId,
			value: value,
			writeType: "write",
			success:(res)=> {
				console.log(res);
			},
			fail: (er)=> {
				console.log(er);
			},
		});
	},
	
	readBLEValue() {
		uni.readBLECharacteristicValue({
			deviceId: bleConnectDeviceID,
			serviceId: csValue.serviceId,
			characteristicId: csValue.readId,
			success:(res)=> {
				console.log(res);
			},
			fail: (er)=> {
				console.log(er);
			},
		});
	},
		
	onBluetoothDeviceFound() {
		ConnectController.connectStateListen({
			...BLUE_STATE.SCANING
		});
		
		wx.onBluetoothDeviceFound((res)=> {
			res.devices.forEach(device=> {
				const deviceName = device.name || device.localName
				if (!deviceName) return
				ConnectController.devicesListen(device)
			})
		})
	},
		
	createBLEConnection(deviceId) {
		
		if (bleConnectDeviceID != null) {
			if (bleConnectDeviceID != deviceId) {
				closeBLEConnection();
			}
		}
		
		bleConnectDeviceID = deviceId;
		(async ()=>{
			
			await new Promise(resolve => {
				this.onBLEConnectionStateChange();
				setTimeout(()=>{
					console.log("bjs-onBLEConnectionStateChange");
					resolve();
				}, 2000);
			});
			
			await new Promise(resolve => {
				uni.createBLEConnection({
					deviceId: bleConnectDeviceID,
					timeout: 3000,
					success: (res)=> {
						console.log("bjs-createBLEConnection",res);
		
						uni.stopBluetoothDevicesDiscovery();
						resolve();
					},
					fail: (er)=> {
						ConnectController.connectStateListen({
							...BLUE_STATE.CONNECTFAILED,
							...er
						});
					},
				});
			});
			
			await new Promise(resolve => {
			// 	uni.onBLEConnectionStateChange(res=> {
			
			// 	});
			// 	setTimeout(()=>{
			// 		console.log("bjs-onBLEConnectionStateChange_0");
			// 		resolve();
			// 	}, 2000);
				this.onBLEConnectionStateChange();
				setTimeout(()=>{
					console.log("bjs-onBLEConnectionStateChange01");
					resolve();
				}, 2000);
			});
		
			await new Promise(resolve => {
				this.onNotifyBLECharacteristicValueChange();
				setTimeout(()=>{
					console.log("bjs-onNotifyBLECharacteristicValueChange");
					resolve();
				}, 2000);
			});
			
			await new Promise(resolve => {
				this.onBLECharacteristicValueChange();
				setTimeout(()=>{
					console.log("bjs-onBLECharacteristicValueChange");
					resolve();
				}, 2000);
			});
			
		})();
	},
		
	onBLEConnectionStateChange() {
		uni.onBLEConnectionStateChange(res=> {
			if (!res.connected) {
				ConnectController.connectStateListen({
					...BLUE_STATE.DISCONNECT,
					...res
				});
				this.connectState = -1;
				this.closeBlue();
			} else {
				ConnectController.connectStateListen({
					...BLUE_STATE.CONNECTSUCCESS,
					...res
				});
				this.connectState = 200;
			}
		})
	},
	
	onNotifyBLECharacteristicValueChange() {
		uni.notifyBLECharacteristicValueChange({
			state: true,
			deviceId: bleConnectDeviceID,
			serviceId: csValue.serviceId,
			characteristicId: csValue.readId,
				success(res) {
				console.log('notifyBLECharacteristicValueChange success', res.errMsg);
			}
		});
	},
	
	onBLECharacteristicValueChange() {
		uni.onBLECharacteristicValueChange((characteristic) => {
		  console.log('characteristic value comed:', characteristic.value);
		  ConnectController.characteristicValueChangeListen({
			  ...BLUE_STATE.READSUSSES,
			  ...characteristic
		  });
		});
	}
}
