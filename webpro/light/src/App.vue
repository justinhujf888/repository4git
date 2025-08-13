<script>
	import { setNotifyDefaultOptions } from '@/uni_modules/wot-design-uni';
	export default {
		onLaunch: function() {
			// #ifdef APP-PLUS
			const domModule = uni.requireNativePlugin('dom')
			domModule.addRule('fontFace', {
				'fontFamily': "graceuiiconfont",
				'src': "url('https://at.alicdn.com/t/c/font_823462_whtuj4ktcl.ttf?t=1703075468532')"
			});
			console.log("字体加载成功");
			// #endif

			// #ifdef MP-WEIXIN
			wx.onNeedPrivacyAuthorization(function(e){
				console.log(e);
			})
			// #endif
			setNotifyDefaultOptions({
				safeHeight: 60,
				// onClick: (event) => console.log('onClick', event),
				// onClosed: () => console.log('onClosed'),
				// onOpened: () => console.log('onOpened')
			})

			// 隐藏原生tabBar
            // #ifdef MP || H5
			uni.hideTabBar();
            // #endif
		},
		onShow: function(){},
		onHide: function(){}
	}
</script>
<style lang="scss">
	// @import "@/wxcomponents/weui/index.wxss";
	@import '@/static/icons/iconfont.css';
	.minput{border-bottom: #000101 solid 1px;color: #000101;}

    .sliderbg {background-color: #40484b}
    .silderborder {border-color: #40484b}
</style>
<!-- #ifndef APP-NVUE -->
<style lang="scss">
/* 加载框架核心样式 */
@import "./Grace6/css/grace.scss";
/* 加载深色模式适配样式 */
@import "./Grace6/css/graceDark.scss";
/* 加载自定义样式 */
@import "./custom/custom.scss";
page{background:#F8F8F8; }
</style>
<!-- #endif -->
<!-- #ifdef APP-NVUE -->
<style lang="scss">
/* 加载框架核心样式 */
@import "./Grace6/css/grace.scss";
/* 加载自定义样式 */
@import "./custom/custom.scss";
.gui-icons{font-family:graceuiiconfont; font-style:normal;}
</style>
<!-- #endif -->