<template>
<!-- 	<mp-searchbar value="search"></mp-searchbar>
	<mp-badge text="abc">abc</mp-badge>
	<mp-slidview></mp-slidview> -->
	<gui-page :customHeader="true">
		<template v-slot:gHeader>
			<!-- #ifdef MP -->
			<view style="height:20px;"></view>
			<!-- #endif -->
		</template>
		<!-- 请开始您的开发 ~ -->
		<template v-slot:gBody>
			<!-- #ifdef MP-WEIXIN -->

			<!-- #endif -->
			<view class="m-2">
				<view> 
					<logo></logo>
				</view>
				<view>
					<wd-button @tap="callBle">主要按钮</wd-button>
				</view>
				<view class="mt-2">
					<dsetup></dsetup>
				</view>
				<view style="height:30rpx;"></view>
			</view>
		</template>
	</gui-page>
</template>
<script>
export default {
	data() { 
		return {
			
		}
	},
	onLoad() {
		// this.callBle();
		return;
		console.log("abc");
		uni.openBluetoothAdapter({
		  complete(res) {
		    console.log(res);
			uni.startBluetoothDevicesDiscovery({
			  services: ['FEE7'],
			  complete(res1) {
			    console.log(res1)
			  }
			})
		  }
		})
	},
	methods: {
		async callBle() {
			navigator.bluetooth.requestDevice({
				// filters: [{ services: [0x1234, 0x12345678, '99999999-0000-1000-8000-00805f9b34fb'] }],
				acceptAllDevices: true,
				optionalServices: ['battery_service']
			})
			.then(device => device.gatt.connect())
			.then(server => server.getPrimaryService('heart_rate'))
			.then(service => service.getCharacteristic('heart_rate_measurement'))
			.then(characteristic => characteristic.startNotifications())
			.then(characteristic => {
			    characteristic.addEventListener('characteristicvaluechanged', handleCharacteristicValueChanged);
			    console.log('Notifications have been started.');
			})
			.catch(error => { console.error(error); });
			function handleCharacteristicValueChanged(event) {
			    const value = event.target.value;
			    console.log('Received ' + value);
			    // TODO: Parse Heart Rate Measurement value.
			    // See https://github.com/WebBluetoothCG/demos/blob/gh-pages/heart-rate-sensor/heartRateSensor.js
			}
		}
	}
}
</script>
<style scoped>

</style>
