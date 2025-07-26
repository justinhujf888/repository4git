export const Config = {
	//单页应用的路由模式
	vueRouterMode: "history",

	// #ifdef MP
	appId: uni.getAccountInfoSync().miniProgram.appId,
	// #endif
	// #ifdef H5
	appId: "wxab8fc59cb9d4f7cb",
	// #endif
	appName: "light",
	appVersion: "0.2",

	//接口地址 http://localhost:8080/shop
	// apiBaseURL: "https://service.arkydesign.cn/pets",
	apiBaseURL: process.env.NODE_ENV === "production" ?	"https://light.arkydesign.cn/light" : "http://localhost:8090/light",

	desKey: "vikehoo_public_key_%&%^*&^*",
	txMapKey: "OWYBZ-QUQ6F-MU6J4-JYBLN-LL7E7-CNBE6"
}
