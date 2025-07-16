export const Config = {
    //单页应用的路由模式
    vueRouterMode: 'history',

    appId: 'wxab8fc59cb9d4f7cb',
    appName: 'cls',
    appVersion: '0.2',

    //接口地址 http://localhost:8080/shop
    // apiBaseURL: "http://192.168.124.24:8090/light",
    apiBaseURL: import.meta.env.MODE === "production" ?	"" : "http://localhost:8090/light",

    desKey: 'vikehoo_public_key_%&%^*&^*',
    txMapKey: 'OWYBZ-QUQ6F-MU6J4-JYBLN-LL7E7-CNBE6'
};
