export const BLUE_STATE = {
	/**
	 * 蓝牙不可用  -1
	 */
	UNAVAILABLE: {
		label: '请检查手机蓝牙是否开启',
		lang:'page.setup.ctx.checkbuleopen',
		code: -1
	},
	/**
	 * 正在搜索设备...  12000
	 */
	SCANING: {
		label: '正在搜索设备...',
		lang:'page.setup.ctx.searchdevice',
		code: 12000
	},
	/**
	 * 已连接蓝牙  200
	 */
	CONNECTSUCCESS: {
		label: '已连接蓝牙',
		lang:'page.setup.ctx.blueconnected',
		code: 200
	},
	
	/**
	 * 蓝牙连接已断开  500
	 */
	DISCONNECT: {
		label: '蓝牙连接已断开',
		lang:'page.setup.ctx.bluedisced',
		code: 500
	},
	/**
	 * 连接失败, 请重试！  -2
	 */
	CONNECTFAILED: {
		label: '连接失败, 请重试！',
		lang:'page.setup.ctx.connagain',
		code: -2
	},
	/**
	 * 微信无位置权限  10007
	 */
	NOLOCATIONPERMISSION: {
		label: '您关闭了微信位置权限，请前往手机设置页打开权限后重试',
		lang:'',
		code: 10007
	},
	/**
	 * 微信无蓝牙权限  10006
	 */
	NOBLUETOOTHPERMISSION: {
		label: '您关闭了微信蓝牙权限，请前往手机设置页打开权限后重试',
		lang:'',
		code: 10006
	},
	/**
	 * 当前系统版本过低，请升级后重试！  10009
	 */
	VERSIONLOW: {
		label: '当前系统版本过低，请升级后重试！',
		lang:'',
		code: 10009
	},
	/**
	 * 系统异常，请稍后重试！  10008
	 */
	SYSTEMERROR: {
		label: '系统异常，请稍后重试！',
		lang:'page.setup.ctx.tryagainsys',
		code: 10008
	},
	/**
	 * 系统异常，请稍后重试！  10008
	 */
	READSUSSES: {
		label: '特征值读取成功',
		lang:'',
		code: 10010
	}
}