import router from '@/router/index';

export default class Page {
	static navigateTo(url, paramJson) {
		// console.log(encodeURIComponent(JSON.stringify(paramJson)));
        if (paramJson) {
            router.push({ name:url, params:{ param:encodeURIComponent(JSON.stringify(paramJson)) } });
        } else {
            router.push({ name:url });
        }
	}
	static redirectTo(url, paramJson) {
		// alert(url + "?param=" + encodeURIComponent(JSON.stringify(paramJson)));
        if (paramJson) {
            router.replace({ name:url, params:{ param:encodeURIComponent(JSON.stringify(paramJson)) } });
        } else {
            router.replace({ name:url });
        }
	}
	static reLaunch(url, paramJson) {
		uni.reLaunch({
			url: url + "?param=" + encodeURIComponent(JSON.stringify(paramJson))
		})
	}
	static navBack() {
        router.back();
	}
}
