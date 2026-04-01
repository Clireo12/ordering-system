<template>
	<view class="my-container">
		<!-- 用户信息 -->
		<view class="user-area" @click="gotoProfile">
			<view class="user-box">
				<image class="avatar" src="/static/images/me-avatar.png"></image>
				<view class="name">{{ userDisplayName }}</view>
			</view>
			<view class="login-btn" @tap="login" v-if="!isLogin">授权登录</view>
		</view>

		<!-- 卡片 -->
		<view class="card-container">
			<view class="card">
				<view class="meta">
					<view class="name">雪王币</view>
					<view class="count">{{ isLogin ? userInfo.coin : '--' }}</view>
				</view>
				<image class="icon" src="/static/images/me-coin.png"></image>
			</view>
			<view class="card">
				<view class="meta">
					<view class="name">优惠券</view>
					<view class="count">{{ isLogin ? userInfo.coupons : '--' }}</view>
				</view>
				<image class="icon" src="/static/images/me-coupon.png"></image>
			</view>
			<view class="card">
				<view class="meta">
					<view class="name">礼品卡</view>
					<view class="count">点击查看</view>
				</view>
				<image class="icon" src="/static/images/me-gift.png"></image>
			</view>
		</view>

		<!-- 更多服务 -->
		<view class="list-items">
			<view class="list-title">更多服务</view>
			<view class="item">
				<view class="content">
					<image class="icon" src="/static/images/qr-code.png"></image>
					<view class="label">兑换中心</view>
				</view>
			</view>
			<view class="item">
				<view class="content">
					<image class="icon" src="/static/images/fuli.png"></image>
					<view class="label">雪王福利群</view>
				</view>
			</view>
			<view class="item">
				<view class="content">
					<image class="icon" src="/static/images/problem.png"></image>
					<view class="label">问题反馈</view>
				</view>
			</view>
			<view class="item">
				<view class="content">
					<image class="icon" src="/static/images/join.png"></image>
					<view class="label">加盟咨询</view>
				</view>
			</view>
			<view class="item">
				<view class="content">
					<image class="icon" src="/static/images/about.png"></image>
					<view class="label">关于我们</view>
				</view>
			</view>
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

	const userInfo = ref({ coin: 0, coupons: 0 }) // 初始化默认值
	const isLogin = ref(false)

	const userDisplayName = computed(() => {
		if (!isLogin.value) return '登录领取20元新人卷包'
		// 优先显示用户名，如果没有则显示"蜜雪会员"
		return userInfo.value.userName || '蜜雪会员'
	})

	// 统一更新登录状态的函数
	const updateLoginStatus = () => {
		const user = uni.getStorageSync('userInfo')
		isLogin.value = !!user
		if (user) {
			userInfo.value = user
		} else {
			// 未登录时重置用户信息
			userInfo.value = {
				coin: 0,
				coupons: 0
			} // 设置默认值
		}
	}

	// 在onMounted中获取用户信息
	onMounted(() => {
		updateLoginStatus()
		uni.$on('userInfoUpdated', updateLoginStatus) // 监听登录事件
		uni.$on('userLoggedOut', updateLoginStatus) // 监听登出事件
	})
	// 在 onUnmounted 中移除监听
	onUnmounted(() => {
		uni.$off('userInfoUpdated', updateLoginStatus)
		uni.$off('userLoggedOut', updateLoginStatus)
	})


	//跳转到登录页
	const login = () => {
		// 记录来源页面
		uni.setStorageSync('loginFrom', 'my')
		uni.navigateTo({
			url: '/pages/login/login'
		})
	}

	//跳转到个人资料页
	const gotoProfile = () => {
		if (!isLogin.value) {
			login()
			return
		}
		uni.navigateTo({
			url: '/pages/profile/profile'
		})
	}
</script>

<style lang="scss" scoped>
	.my-container {
		background-color: #fff;
		min-height: 100vh;
		padding-bottom: 40rpx;

		/* 用户区域 */
		.user-area {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 40rpx 30rpx;
			background-color: #fff;
			margin-bottom: 20rpx;

			.user-box {
				display: flex;
				align-items: center;

				.avatar {
					width: 100rpx;
					height: 100rpx;
					border-radius: 50%;
					margin-right: 20rpx;
				}

				.name {
					font-size: 28rpx;
					color: #333;
				}
			}

			.login-btn {
				width: 150rpx;
				height: 60rpx;
				background-color: #e64340;
				color: #fff;
				border-radius: 15rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				font-size: 26rpx;
			}
		}

		/* 卡片区域 */
		.card-container {
			display: flex;
			justify-content: space-between;
			padding: 0 5px;
			margin-bottom: 30rpx;

			.card {
				width: 210rpx;
				height: 120rpx;
				background: #f8f8f8;
				border-radius: 10px;
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-left: 5px;
				padding: 0 10px;
				box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

				.meta {
					display: flex;
					flex-direction: column;

					.name {
						font-size: 26rpx;
						color: rgb(129, 129, 129);
					}

					.count {
						font-size: 25rpx;
						color: #000;
						margin-bottom: 8rpx;
						font-weight: bold;
					}
				}

				.icon {
					width: 60rpx;
					height: 60rpx;
				}
			}
		}

		/* 更多服务 */
		.list-items {
			background-color: #fff;
			border-radius: 16rpx;
			padding: 0 20rpx;

			.list-title {
				font-size: 28rpx;
				color: #333;
				font-weight: bold;
				padding: 30rpx 0 20rpx;
			}

			.item {
				display: inline-block;
				width: 25%;
				padding: 30rpx 0;
				text-align: center;
			}

			.content {
				display: flex;
				flex-direction: column;
				align-items: center;

				.icon {
					width: 25px;
					height: 25px;
					border-radius: 50%;
					background-color: #f8f8f8;
					margin-bottom: 15rpx;
					display: flex;
					justify-content: center;
					align-items: center;
					padding: 10px;
				}

				.label {
					font-size: 12px;
					color: #333;
				}
			}
		}
	}
</style>