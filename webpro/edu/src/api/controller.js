export const ConnectController = {
    /**
     * 通知监听回调
     */
    addNotificationListen(callBackFunc) {
        this.common._notificationCallBackFunc = callBackFunc;
    },
    notificationListen(params) {
        if (this.common._notificationCallBackFunc) {
            this.common._notificationCallBackFunc(params);
        }
    },

    common: {
        _notificationCallBackFunc: null
    }
};
