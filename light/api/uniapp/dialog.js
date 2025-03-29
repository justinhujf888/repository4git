export default class Dialog {
	static alert(msg) {
		uni.showModal({
			title: '提示',
			content: msg,
			showCancel: false
		});
	}
	static alertBack(msg, hasCancel, okfun, cancelfun) {
		uni.showModal({
		    title: '提示',
		    content: msg,
			showCancel: hasCancel,
		    success: function (res) {
		        if (res.confirm) {
		            okfun();
		        } else if (res.cancel) {
		            cancelfun();
		        }
		    }
		});
	}
	static confirm(msgtxt, okfun, cancelfun) {
		uni.showModal({
			title: '提示',
			content: msgtxt,
			success: function(res) {
				if (res.confirm) {
					okfun();
				} else if (res.cancel) {
					cancelfun();
				}
			}
		});
	}
	static openLoading(msg) {
		uni.showLoading({
			title: msg
		})
	}
	static closeLoading() {
		uni.hideLoading();
	}
	static hideLoading() {
		uni.hideLoading();
	}
	static openNotify(msg) {
	
	}
	static toastSuccess(msg) {
		uni.showToast({
			title: msg,
			icon: "success"
		});
	}
	static toastError(msg) {
	
	}
	static toastNone(msg) {
		uni.showToast({
			title: msg,
			icon: "none"
		});
	}
	static showApiErrorMsg() {
		uni.showModal({
			title: "系统错误",
			content: "发生系统错误，请稍后再试！",
			showCancel: false
		});
	}
	static previewImage(imgLists,currentImg) {
		uni.previewImage({
		  urls: imgLists,
		  current : currentImg
		});
	}
}
