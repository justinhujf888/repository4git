<template>
	<wd-navbar fixed placeholder leftArrow safeAreaInsetTop @click-left="page.navBack()">
		<template #title>
			<view class="justify-center items-center">
<!-- 				<text class="text-base">20缸默认</text>
				<text class="gui-icons text-gray-400 ml-2">&#xe69e;</text> -->
			</view>
		</template>
	</wd-navbar>
	<tabbar :tabIndex="1"></tabbar>
</template>

<script>
</script>

<style>
</style>