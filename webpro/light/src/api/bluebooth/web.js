import { BLUE_STATE } from './blueState.js';
import { ConnectController } from './controller.js';
import lodash from "lodash";
// import hexTools from "@/api/hexTools.js";

let pserviceId = "";//"0000FFF0-0000-1000-8000-00805F9B34FB" "76617365-6570-6c61-6e74-776f726c6473";
let serviceFilter = null;
let bleConnectDeviceID = null;
let opened = false;
let csValue = {serviceId:null,readId:null,writeId:null};
let primaryService = null;
let readCharacteristic = null;
let writeCharacteristic = null;
if (uni.getStorageSync("blueOpened")) {
	opened = uni.getStorageSync("blueOpened");
} else {
	uni.setStorageSync("blueOpened",false);
	opened = false;
}

let blueService = null;

export const Blue = {
	/**
	 * 蓝牙连接状态：200-> 已连接；-1 ->未连接
	 */
	connectState: -1,

	setBlueService(bs) {
		blueService = bs;
	},

	setServiceFilter(ay) {
		serviceFilter = ay;
		// lodash.forEach(ay,(v,i)=>{
		// 	serviceFilter.push(v.toLowerCase());
		// });
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
	
	callBle(dtList) {
		if (!opened) {
			Blue.start(dtList);
		} else {
			Blue.closeBlue();
			Blue.start(dtList);
		}
	},
		
	/**
	 * 扫描蓝牙设备
	 * @param onlyBind 是否单独绑定操作，默认false
	 * @param module 设备模块，区分设备
	 */
	async start(dtList) {
		try {
			opened = true;
			uni.setStorageSync("blueOpened",opened);
			await blueService.requestDevice();
			await blueService.device.value?.gatt.connect();
			// console.log(blueService.device.value,await blueService.server.value?.getPrimaryServices());
			lodash.forEach(await blueService.server.value?.getPrimaryServices(),(v,i)=>{
				if (v.isPrimary && lodash.findIndex(dtList,(o)=>{return o==v.uuid})>-1) {
					primaryService = v;
					csValue.serviceId = primaryService.uuid;
				}
			});
			ConnectController.connectStateListen({
				...BLUE_STATE.CONNECTSUCCESS,
				...{deviceId:blueService.device.value.id,name:blueService.device.value.name,serviceId:csValue.serviceId}
			});
			this.connectState = 200;
		} catch (e) {

		}

		// const device = await navigator.bluetooth.requestDevice({
		// 	filters: [{services: serviceFilter}],
		// 	optionalServices:["76617365-6570-6c61-6e74-776f726c6473"]
		// });
		// const server = await device.gatt.connect();
		// console.log(await server.getPrimaryServices());
		//"76617365-6570-6c61-6e74-776f726c6473"


	},
	/**
	 * 关闭蓝牙、初始化BLE状态
	 */
	closeBlue() {
		if(bleConnectDeviceID) {
			// wx.closeBLEConnection({
			// 	deviceId: bleConnectDeviceID,
			// })
		}
		bleConnectDeviceID = null
		this.connectState = -1
		this._discoveryStarted = false
		// wx.closeBluetoothAdapter()
		// wx.offBluetoothAdapterStateChange()
		// wx.offBLEConnectionStateChange()
		// wx.stopBluetoothDevicesDiscovery();
		opened = false;
		uni.setStorageSync("blueOpened",opened);
	},
	/**
	 * 断开蓝牙
	*/
	async closeBLEConnection() {
		await blueService.device.value?.gatt.disconnect();
		uni.showToast({
			icon: 'none',
			title: '蓝牙已断开连接'
		});
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
	
	async getBleCharacteristicsInfo(readId,writeId) {
		lodash.forEach(await primaryService.getCharacteristics(),(o,i)=>{
			if (o.uuid.toLowerCase() == writeId) {
				writeCharacteristic = o;
				csValue.writeId = o.uuid;
			}
			if (o.uuid.toLowerCase() == readId) {
				readCharacteristic = o;
				csValue.readId = o.uuid;
			}
		});
		console.log("Characteristics-----:",csValue);
		this.onBLECharacteristicValueChange(readCharacteristic);
	},
	
	writeBLEValue(value) {
		writeCharacteristic.writeValue(value).then(() => {
			// 写入成功
		})
		.catch(error => {
			// 写入特征时出错
		});
		// uni.writeBLECharacteristicValue({
		// 	deviceId: bleConnectDeviceID,
		// 	serviceId: csValue.serviceId,
		// 	characteristicId: csValue.writeId,
		// 	value: value,
		// 	writeType: "write",
		// 	success:(res)=> {
		// 		console.log(res);
		// 	},
		// 	fail: (er)=> {
		// 		console.log(er);
		// 	},
		// });
	},
	
	readBLEValue() {
		readCharacteristic.readValue()
			.then(value => {
				// 读取到的值
			})
			.catch(error => {
				// 读取特征时出错
			});
		// uni.readBLECharacteristicValue({
		// 	deviceId: bleConnectDeviceID,
		// 	serviceId: csValue.serviceId,
		// 	characteristicId: csValue.readId,
		// 	success:(res)=> {
		// 		console.log(res);
		// 	},
		// 	fail: (er)=> {
		// 		console.log(er);
		// 	},
		// });
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
			
			// await new Promise(resolve => {
			// // 	uni.onBLEConnectionStateChange(res=> {
			
			// // 	});
			// // 	setTimeout(()=>{
			// // 		console.log("bjs-onBLEConnectionStateChange_0");
			// // 		resolve();
			// // 	}, 2000);
			// 	this.onBLEConnectionStateChange();
			// 	setTimeout(()=>{
			// 		console.log("bjs-onBLEConnectionStateChange01");
			// 		resolve();
			// 	}, 2000);
			// });
		
			// await new Promise(resolve => {
			// 	this.onNotifyBLECharacteristicValueChange();
			// 	setTimeout(()=>{
			// 		console.log("bjs-onNotifyBLECharacteristicValueChange");
			// 		resolve();
			// 	}, 2000);
			// });
			
			// await new Promise(resolve => {
			// 	this.onBLECharacteristicValueChange();
			// 	setTimeout(()=>{
			// 		console.log("bjs-onBLECharacteristicValueChange");
			// 		resolve();
			// 	}, 2000);
			// });
			
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
	
	onBLECharacteristicValueChange(characteristic) {
		if (characteristic.properties.notify) {
			return characteristic.startNotifications() // 开始监听通知
				.then(_ => {
					characteristic.addEventListener('characteristicvaluechanged', (event)=>{
						// let data = hexTools.arrayBuffer2hexArray(event.target.value.buffer).map(str => "0x"+str.toUpperCase());
						console.log('characteristic value comed:', event);
						ConnectController.characteristicValueChangeListen({
							...BLUE_STATE.READSUSSES,
							value:event.target.value.buffer
						});
					});
					console.log('Notifications started');
				})
				.catch(error => {
					console.log('Argh! ' + error);
				});
		} else {
			console.log('Notifications not supported');
		}
		// uni.onBLECharacteristicValueChange((characteristic) => {
		//   console.log('characteristic value comed:', characteristic.value);
		//   ConnectController.characteristicValueChangeListen({
		// 	  ...BLUE_STATE.READSUSSES,
		// 	  ...characteristic
		//   });
		// });
	}
}
