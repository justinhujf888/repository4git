export default class Page {
	static navigateTo(url, paramJson) {
		// console.log(url + "?param=" + encodeURIComponent(JSON.stringify(paramJson)));
		if (paramJson.isTabBar == null) {
			uni.navigateTo({
				url: url + "?param=" + encodeURIComponent(JSON.stringify(paramJson))
			});
		}
		else {
			uni.reLaunch({
				url: url + "?param=" + encodeURIComponent(JSON.stringify(paramJson))
			})
		}
	}
	static redirectTo(url, paramJson) {
		// alert(url + "?param=" + encodeURIComponent(JSON.stringify(paramJson)));
		if (paramJson.isTabBar == null) {
			uni.redirectTo({
				url: url + "?param=" + encodeURIComponent(JSON.stringify(paramJson))
			});
		}
		else {
			uni.reLaunch({
				url: url + "?param=" + encodeURIComponent(JSON.stringify(paramJson))
			})
		}
	}
	static reLaunch(url, paramJson) {
		uni.reLaunch({
			url: url + "?param=" + encodeURIComponent(JSON.stringify(paramJson))
		})
	}
	static navBack() {
		uni.navigateBack({});
	}
}
