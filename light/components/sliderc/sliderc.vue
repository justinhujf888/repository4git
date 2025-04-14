<template>
	<view 
	class="my-sg-slider" 
	
	ref="myslider" 
	id="myslider" 
	:style="{
		height:barHeight+borderHeight+'rpx'
	}">
		<view 
		class="my-sg-slider-line ml-2" 
		:class="bglineClass" 
		:style="{
			opacity:0.6,
			height:bglineSize+'rpx', 
			marginTop:((barHeight - bglineSize) / 2)+'rpx', 
			borderRadius:borderRadius
		}"></view>
		<view 
		class="my-sg-slider-a-line" 
		:class="bglineAClass" 
		:style="{
			width:(left+2)+'px',
			top:((barHeight - bglineSize) / 2)+'rpx', 
			height:bglineSize+'rpx', 
			borderRadius:borderRadius}"></view>
		<text @touchstart="touchstart" 
	@touchmove.stop.prevent="touchmove" 
	@touchend="touchend" 
		class="my-sg-slider-bar" 
		:class="barClass" 
		:style="{
			width:barWidth+'rpx', 
			height:barHeight+'rpx', 
			'line-height':barHeight+'rpx', 
			left:left+'px', 
			fontSize:barTextSize, 
			borderRadius:borderRadius
		}">{{barText}}</text>
	</view>
</template>
<script>
// #ifdef APP-NVUE
const dom = weex.requireModule('dom');
// #endif
export default{
	name  : "sliderc",
	props : {
		barHeight    : {type:Number,  default:38},
		barWidth     : {type:Number,  default:158},
		barClass     : {type:Array,   default:function(){return ['gui-bg-primary', 'gui-color-white'];}},
		bglineSize   : {type:Number,  default:2},
		bglineClass  : {type:Array,   default:function(){return ['gui-bg-primary'];}},
		bglineAClass : {type:Array,   default:function(){return ['gui-bg-primary'];}},
		barText      : {type:String,  default:''},
		barTextSize  : {type:String,  default:'20rpx'},
		borderRadius : {type:String,  default:'32rpx'},
		canSlide     : {type:Boolean, default:true},
		borderHeight    : {type:Number,  default:0},
		borderWidth     : {type:Number,  default:0},
		num     : {type:Number,  default:0},
		min     : {type:Number,  default:0},
		max     : {type:Number,  default:100},
		id : {type:String,  default:''},
		obj: {type:Object,  default:null},
		onChanging: {}
	},
	data() {
		return {
			left       : 0,
			startLeft  : 0,
			width      : 0,
			barWidthPX : 30
		}
	},
	return: ()=>{
		id,num
	},
	mounted:function(){
		this.init();
	},
	watch: {
		num(newVal, oldVal) {
			this.setProgress(newVal);
		}
	},
	methods:{
		init : function(){
			// #ifdef APP-NVUE
			var el = this.$refs.myslider;
			dom.getComponentRect(el, (res) => {
				if(!res.result || res.size.width < 5){
					setTimeout(()=>{this.init();}, 100);
					return;
				}
				this.startLeft  = res.size.left;
				this.width      = res.size.width;
				this.barWidthPX = uni.upx2px(this.barWidth);
			});
			// #endif
			// #ifndef APP-NVUE
			uni.createSelectorQuery().in(this).select('#myslider').fields(
				{size: true, rect:true}, (res) => {
					if(res == null){
						setTimeout(()=>{this.init();}, 100);
						return;
					}
					this.startLeft  = res.left;
					this.width      = res.width;
					this.barWidthPX = uni.upx2px(this.barWidth);
				}
			).exec();
			// #endif
			this.setProgress(this.num);
		},
		touchstart : function (e) {
			if(!this.canSlide){return ;}
			var touch = e.touches[0] || e.changedTouches[0];
			this.changeBar(touch.pageX);
		},
		touchmove : function (e) {
			if(!this.canSlide){return ;}
			var touch = e.touches[0] || e.changedTouches[0];
			this.changeBar(touch.pageX);
		},
		touchend : function (e) {
			if(!this.canSlide){return ;}
			var touch = e.touches[0] || e.changedTouches[0];
			this.changeBar(touch.pageX, true);
		},
		changeBar : function(x,e){
			var left = x - this.startLeft;
			let v = left;
			if(left <= this.min){
				this.left = this.min;
				v = this.min;
				this.$emit('changing', {v:v,id:this.id,obj:this.obj});
			}else if(left + this.barWidthPX > this.width){
				left = this.width - this.barWidthPX-this.borderWidth;
				this.left = left;
				v = this.max;
				this.$emit('changing', {v:v,id:this.id,obj:this.obj});
			}else{
				this.left = left;
				var scale = this.left / (this.width - this.barWidthPX);
				v = Math.round(scale * this.max);
				this.$emit('changing', {v:v,id:this.id,obj:this.obj});
				if (e) {
					this.$emit('change', {v:v,id:this.id,obj:this.obj});
				}
			}
		},
		setProgress : function (value){
			if(this.width < 1){ setTimeout(()=>{this.setProgress(value), 300}); return ;}
			if(value < this.min){value = this.min;}
			if(value > this.max){value = this.max;}
			this.left = ( value / this.max ) * (this.width - this.barWidthPX);
		}
	},
	emits:['change']
}
</script>
<style scoped>
.my-sg-slider{overflow:hidden; position:relative;}
.my-sg-slider-a-line{position:absolute; left:0; top:0;}
.my-sg-slider-bar{position:absolute; left:0; top:0; font-size:20rpx; text-align:center; color:#323232; overflow:hidden;}
</style>