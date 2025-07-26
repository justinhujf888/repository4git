/**
 * 公用模块
 * 蓝牙连接控制器
*/
export const ConnectController = {
	/**
	 * 蓝牙连接状态
	 */
	addConnectStateListen(callBackFunc) {
		this.common._connectStateCallBackFunc = callBackFunc
	},
	connectStateListen(params) {
		if (this.common._connectStateCallBackFunc) {
			this.common._connectStateCallBackFunc(params)
		}
	},
	
	/**
	 * 设备列表监听
	 */
	addDevicesListen(callBackFunc) {
		this.common._devicesCallBackFunc = callBackFunc
	},
	devicesListen(params) {
		if (this.common._devicesCallBackFunc) {
			this.common._devicesCallBackFunc(params)
		}
	},
	
	/**
	 * 读取特征值监听
	 */
	addCharacteristicValueChangeListen(callBackFunc) {
		this.common._characteristicValueChangeFunc = callBackFunc;
	},
	characteristicValueChangeListen(params) {
		if (this.common._characteristicValueChangeFunc) {
			this.common._characteristicValueChangeFunc(params)
		}
	},
	

	common: {
		_connectStateCallBackFunc: null,
		_devicesCallBackFunc: null,
		_characteristicValueChangeFunc: null,
	}
}
