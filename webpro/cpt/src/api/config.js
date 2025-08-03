export const Config = {
    //单页应用的路由模式
    vueRouterMode: 'history',

    appId: '001',
    appName: '观赏鱼线上竞赛平台',
    appVersion: '0.2',

    //接口地址 http://localhost:8080/shop
    // apiBaseURL: "http://192.168.124.24:8090/light",
    apiBaseURL: import.meta.env.MODE === "production" ?	"http://localhost:8091/cpt" : "http://localhost:8091/cpt",

    desKey: 'vikehoo_public_key_%&%^*&^*',
    txMapKey: 'OWYBZ-QUQ6F-MU6J4-JYBLN-LL7E7-CNBE6'
};
