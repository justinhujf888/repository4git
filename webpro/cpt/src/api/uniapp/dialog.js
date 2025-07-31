export default class Dialog {
    static confirmPopup = null;
    static toast = null;
    static dynDialog = null;
    static mydRef = null;

    static setup(confirmPopup, toast, dynDialog, mydRef) {
        Dialog.confirmPopup = confirmPopup;
        Dialog.toast = toast;
        Dialog.dynDialog = dynDialog;
        Dialog.mydRef = mydRef;
    }

    static alert(msg) {
        Dialog.mydRef.value.alert(msg);
    }

    static alertBack(msg, okfun) {
        Dialog.mydRef.value.alertBack(msg, okfun);
    }

    static confirm(msgtxt, okfun, cancelfun) {
        // const confirmPopup = useConfirm();
        Dialog.confirmPopup.require({
            model: true,
            message: msgtxt,
            icon: 'pi pi-exclamation-circle',
            accept: () => {
                okfun();
            },
            reject: () => {
                if (cancelfun) {
                    cancelfun();
                }
            }
        });
    }

    static openLoading(msg) {}

    static closeLoading() {}

    static hideLoading() {}

    static openNotify(msg) {
        Dialog.toast.add({ severity: 'info', summary: 'Info', detail: msg, life: 5000 });
    }

    static toastSuccess(msg) {
        Dialog.toast.add({ severity: 'success', summary: 'success', detail: msg, life: 5000 });
    }

    static toastError(msg) {
        Dialog.toast.add({ severity: 'error', summary: 'Error', detail: msg, life: 5000 });
    }

    static toastNone(msg) {
        Dialog.toast.add({ severity: 'info', summary: 'Info', detail: msg, life: 5000 });
    }

    static showApiErrorMsg() {
        // Dialog.mydRef.value.setHeader("系统错误");
        // Dialog.mydRef.value.alert("发生系统错误，请稍后再试！");
        this.toastError('发生系统错误，请稍后再试！');
    }

    static previewImage(imgLists, currentImg) {
        // uni.previewImage({
        //   urls: imgLists,
        //   current : currentImg
        // });
    }
}
