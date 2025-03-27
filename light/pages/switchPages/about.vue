<template>
	<gui-page :customHeader="true">
		<template v-slot:gHeader>
			<!-- #ifdef MP -->
			<view style="height:20px;"></view>
			<!-- #endif -->
		</template>
		<!-- 请开始您的开发 ~ -->
		<template v-slot:gBody>
			<view class="gui-padding">
				<view>
					<logo></logo>
				</view>
				<view class="gui-margin-top gui-img-in">
					<image 
					style="width:690rpx;" 
					class="gui-border-radius" 
					mode="widthFix"	
					src="https://hcoder.oss-cn-beijing.aliyuncs.com/statics/images/202102/1613809803765335.png"></image>
				</view>
			</view>
			<view class="gui-margin-top gui-padding-x">
				<text class="gui-text gui-secondary-text">Grace 是一套基于 uni-app 的优秀前端框架，提供了极其丰富的组件、布局、界面及模板，会大幅度提高您的前端布局速度。\n\n
Grace 来自于近6年上百个小程序及APP项目的总结。我们是一线开发者，更深度地参与项目开发，所以能够为您提供更好的框架。\n\n
Grace 使用用户过万，通过1万+开发者的反馈加上我们长期的高频更新，框架已经极其稳定，兼容性极好！\n\n
Grace 第一版发布于 2018年09月，4年多来我们一直坚持聆听并快速响应，快速更新，目前已经更新到 6.0 版本，因为努力更值得信赖。\n\n
收费意味着责任、更快的反应速度、更多的成熟组件、更久远的持续更新，我们会用努力和坚持给您超值的开发体验。
</text>
			</view>
			<view style="height:30rpx;"></view>
		</template>
	</gui-page>
</template>
<script>
export default {
	data() {
		return { 
			
		}
	},
	methods: {
		
	}
}
</script>
<style scoped>
.gui-secondary-text{line-height:52rpx;}
</style>