export default class Dialog {
    static confirmPopup = null;
    static toast = null;
    static dynDialog = null;
    static mydRef = null;
    static myLoading = null;

    static setup(confirmPopup, toast, dynDialog, mydRef, myLoading) {
        Dialog.confirmPopup = confirmPopup;
        Dialog.toast = toast;
        Dialog.dynDialog = dynDialog;
        Dialog.mydRef = mydRef;
        Dialog.myLoading = myLoading;
    }

    static alert(msg) {
        Dialog.mydRef?.value?.alert(msg);
    }

    static alertBack(msg, okfun) {
        Dialog.mydRef?.value?.alertBack(msg, okfun);
    }

    static confirm(msgtxt, okfun, cancelfun) {
        // const confirmPopup = useConfirm();
        Dialog.confirmPopup.require({
            model: true,
            header: "    ",
            message: msgtxt,
            icon: 'pi pi-exclamation-triangle',
            acceptProps: {
                label: '确定'
            },
            rejectProps: {
                label: '取消',
                severity: 'secondary',
                outlined: true
            },
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

    static openLoading(msg) {
        Dialog.myLoading?.value?.openLoading();
    }

    static closeLoading() {
        Dialog.myLoading?.value?.closeLoading();
    }

    static hideLoading() {
        this.closeLoading();
    }

    static openNotify(msg) {
        Dialog.toast.add({ severity: 'info', summary: 'Info', group:'center', detail: msg, life: 5000 });
    }

    static toastSuccess(msg) {
        Dialog.toast.add({ severity: 'success', summary: 'success', detail: msg, life: 5000 });
    }

    static toastError(msg) {
        Dialog.toast.add({ severity: 'error', summary: '错误', detail: msg, life: 5000 });
    }

    static toastNone(msg) {
        Dialog.toast.add({ severity: 'info', summary: '信息', detail: msg, life: 5000 });
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
