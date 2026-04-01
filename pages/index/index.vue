<template>
	<view class="index-layout">
		<!-- 轮播图 -->
		<uni-swiper-dot class="uni-swiper-dot-box" :info="swiperList" :current="currentSwiperIndex" mode="round"
		    :dots-styles="dotsStyles" field="imageUrl">
		    <swiper class="swiper-box" @change="handleSwiperChange" :current="currentSwiperIndex" :autoplay="true"
		        :interval="3000" circular>
		        <swiper-item v-for="(item, index) in swiperList" :key="index">
		            <image class="swiper-image" :src="item.imageUrl" mode="aspectFill" 
		                   @click="handleSwiperTap(index)"></image>
		        </swiper-item>
		    </swiper>
		</uni-swiper-dot>

		<!-- 用户 -->
		<view class="member-area">
			<view class="vip-box" @click="gotoProfile">
				<image class="meta-image" src="/static/images/me-avatar.png" mode="aspectFill" />
			</view>
			<view class="meta">
				<view class="title-area">
					<text class="phone">{{isLogin ? userName : '尊敬的用户'}}</text>
				</view>
				<!-- <view v-if="isLogin" class="progress-container">
					<view class="progress-bg">
						<view class="progress-bar"></view>
					</view>
					<view class="progress-text">83/100</view>
				</view> -->
				<view class="tips">{{isLogin?'会员特权：周二抵现日，雪王币当钱花':'登录领取20元新人卷包'}}</view>
			</view>

			<!-- 登录状态显示 -->
			<view v-if="isLogin" class="wallet-box">
				<view class="coin-box">
					<view class="coin-text">{{userInfo.coin}}</view>
					<view class="text">雪王币</view>
				</view>
				<view class="coupon-box">
					<view class="coupon-text">{{userInfo.coupons}}</view>
					<view class="text">优惠券</view>
				</view>
			</view>
			<!-- 未登录显示 -->
			<view class="login-btn" v-if="!isLogin" @click="gotoLogin">授权登录</view>
		</view>

		<view class="banner">
			<!-- 点餐 -->
			<view class="menu-banner">
				<view class="menu-item" @click="gotomenu">
					<image class="menu-icon" src="/static/images/ziqu.png"></image>
					<view class="menu-title">到店自取</view>
					<view class="menu-desc">下单免排队</view>
				</view>
				<view class="menu-item" @click="gotomenu">
					<image class="menu-icon" src="/static/images/waisong.jpg"></image>
					<view class="menu-title">雪王外送</view>
					<view class="menu-desc">甜蜜送到家</view>
				</view>
			</view>

			<!-- 活动 -->
			<view class="banner-list">
				<view class="banner-item" v-for="(item, index) in bannerList" :key="index">
					<image class="banner-image" :src="item.image"></image>
					<view class="banner-title">{{item.title}}</view>
					<view class="banner-text">{{item.desc}}</view>
				</view>
			</view>
		</view>

		<!-- 咨询 -->
		<view class="consult">
			<view class="consult-title">甜蜜咨询</view>
			<image src="/static/images/consult.jpg" mode="aspectFill" />
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		computed,
		onMounted,
		onUnmounted
	} from 'vue'

	
	// 用户信息
	const userInfo = ref({})
	const isLogin = ref(false)
	const currentSwiperIndex = ref(0)

	// 用户名
	const userName = computed(() => {
		// 优先显示用户名，如果没有则显示"蜜雪会员"
		return userInfo.value.userName || '蜜雪会员'
	})

	const updateLoginStatus = () => {
		const user = uni.getStorageSync('userInfo')
		isLogin.value = !!user
		if (user) {
			userInfo.value = user
		}
	}

	// 在onMounted中获取用户信息
	onMounted(() => {
		const user = uni.getStorageSync('userInfo')
		if (user) {
			userInfo.value = user
			isLogin.value = true
		}
		updateLoginStatus()
		uni.$on('userInfoUpdated', updateLoginStatus) // 监听登录事件
		uni.$on('userLoggedOut', updateLoginStatus) // 监听登出事件
	})
	// 在 onUnmounted 中移除监听
	onUnmounted(() => {
		uni.$off('userInfoUpdated', updateLoginStatus)
		uni.$off('userLoggedOut', updateLoginStatus)
	})

	const dotsStyles = ref({
		backgroundColor: '#fff',
		border: '1px #fff solid',
		selectedBackgroundColor: '#e64340',
		selectedBorder: '1px #e6514c solid'
	})

	// 轮播图数据
	const swiperList = ref([
	    {
	        imageUrl: '/static/images/swiper-test.jpg',
	        canClick: false  // 第一张不可点击
	    },
	    {
	        imageUrl: '/static/images/swiper-2.jpg',
	        canClick: true  
	    },
	    {
	        imageUrl: '/static/images/swiper-3.jpg',
	        canClick: true    
	    }
	])
	
	// 轮播图点击事件
	const handleSwiperTap = (index) => {
	    if (swiperList.value[index].canClick) {
	        uni.switchTab({
	            url: '/pages/menu/menu'
	        })
	    }
	}
	const handleSwiperChange = (e) => {
		currentSwiperIndex.value = e.detail.current
	}

	//跳转到个人资料页
	const gotoProfile = () => {
		if (!isLogin.value) {
			gotoLogin()
			return
		}
		uni.navigateTo({
			url: '/pages/profile/profile'
		})
	}

	// 跳转登录页
	const gotoLogin = () => {
		uni.navigateTo({
			url: '/pages/login/login'
		})
	}

	// 活动卡片数据
	const bannerList = ref([{
			image: '/static/images/banner-1.jpg',
			title: '礼品卡',
			desc: '送TA心意'
		},
		{
			image: '/static/images/banner-2.jpg',
			title: '进群有礼',
			desc: '周四抽免单'
		},
		{
			image: '/static/images/banner-3.jpg',
			title: '雪王魔法铺',
			desc: '0元兑周边'
		},
		{
			image: '/static/images/banner-4.jpg',
			title: '每日抽奖',
			desc: '会员免费抽'
		}
	])

	// 跳转菜单页
	const gotomenu = () => {
		uni.switchTab({
			url: '/pages/menu/menu'
		})
	}
</script>

<style lang="scss" scoped>
	.index-layout {
		background-color: #f0f0f0;
		position: relative;

		.uni-swiper-dot-box {
			position: relative;
			height: 400rpx;

			.swiper-box {
				height: 100%;

				.swiper-image {
					width: 100%;
					height: 100%;
				}
			}

			// Custom dot styles
			::v-deep .uni-swiper-dot {
				width: 12rpx;
				height: 12rpx;
				margin: 0 6rpx;
			}

			::v-deep .uni-swiper-dot-active {
				width: 24rpx;
				border-radius: 6rpx;
			}
		}



		.member-area {
			width: 688rpx;
			height: 120rpx;
			border-radius: 16rpx;
			background-color: #fff;
			box-shadow: 0 0 8rpx rgba(0, 0, 0, 0.1);
			position: relative;
			left: 50%;
			bottom: 10px;
			transform: translateX(-50%);
			z-index: 3;
			display: flex;
			align-items: center;
			padding: 0 20rpx;
			justify-content: space-between;

			.vip-box {
				width: 80rpx;
				height: 80rpx;
				display: flex;
				justify-content: center;
				align-items: center;

				.meta-image {
					width: 80rpx;
					height: 80rpx;
					border-radius: 50%;
				}
			}

			.meta {
				flex: 1;
				margin-left: 10px;

				.title-area {
					display: flex;
					align-items: center;
					margin-bottom: 8rpx;

					.phone {
						font-size: 20rpx;
						color: #333;
						margin-right: 5rpx;
						font-weight: bold;
					}
				}

				.tips {
					font-size: 18rpx;
					color: #999;
				}
			}


			.wallet-box {
				width: 110px;
				display: flex;
				justify-content: space-between;
				align-items: center;

				.coin-box {
					justify-content: space-between;
					margin-left: 30rpx;
					text-align: center;

					.coin-text {
						
						text-align: center;
						font-size: 12px;
						font-weight: bold;
						color: #e64340;
					}

					.text {
						display: flex;
						font-size: 12px;
					}

				}

				.coupon-box {
					justify-content: space-between;
					margin-left: 10rpx;
					text-align: center;

					.coupon-text {
						text-align: center;
						font-size: 12px;
						font-weight: bold;
						color: #e64340;
					}

					.text {
						display: flex;
						font-size: 12px;
					}
				}

			}

			.login-btn {
				width: 140rpx;
				height: 60rpx;
				font-size: 20rpx;
				color: #fff;
				background: #e64340;
				border-radius: 15rpx;
				display: flex;
				justify-content: center;
				align-items: center;

				&.active {
					opacity: 0.6;
				}
			}
		}

		.banner {
			width: 720rpx;
			margin: 0 auto;
			align-items: center;

			.menu-banner {
				display: flex;
				justify-content: space-between;
				background: #fff;
				border-bottom: 1rpx solid #f0f0f0;

				.menu-item {
					width: 48%;
					display: flex;
					flex-direction: column;
					align-items: center;

					.menu-icon {
						width: 120px;
						height: 120px;
					}

					.menu-title {
						font-size: 16px;
						font-weight: bold;
						color: #333;
						margin-bottom: 8rpx;
					}

					.menu-desc {
						font-size: 14px;
						color: #999;
						margin-bottom: 5rpx;
					}
				}
			}

			.banner-list {
				display: flex;
				justify-content: space-between;

				.banner-item {
					width: 25%;
					background: #fff;
					overflow: hidden;
					display: flex;
					flex-direction: column;
					align-items: center;

					.banner-image {
						width: 30px;
						height: 30px;
						align-items: center;
						justify-content: center;
					}

					.banner-title {
						font-size: 12px;
						font-weight: bold;
						color: #333;
						padding: 8rpx 6rpx 5rpx;
					}

					.banner-text {
						font-size: 10px;
						color: #999;
						padding: 0 5rpx 10rpx;
					}
				}
			}
		}

		.consult {
			width: 720rpx;
			margin: 20rpx auto 0;
			box-shadow: 10rpx 4rpx 12rpx rgba(0, 0, 0, 0.05);
			border-radius: 15px;
			padding: 20rpx;
			align-items: center;

			.consult-title {
				font-size: 28rpx;
				font-weight: bold;
				color: #333;
				margin-bottom: 20rpx;
			}

			image {
				width: 100%;
				height: 200rpx;
				border-radius: 12rpx;
			}
		}
	}
</style>