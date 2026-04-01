<template>
	<view class="profile-container">
		<!-- 头像 -->
		<view class="avatar-container">
			<image class="avatar" src="/static/images/me-avatar.png" mode="aspectFill"></image>
		</view>

		<!-- 用户信息 -->
		<view class="info-container">
			<view class="info-item">
				<text class="label">昵称</text>
				<text class="value">{{nickname}}</text>
			</view>

			<view class="info-item">
				<text class="label">手机号</text>
				<text class="value">{{maskedPhone}}</text>
			</view>
		</view>

		<view class="logout">
			<view class="logout-btn" @click="logout">退出登录</view>
		</view>


	</view>
</template>

<script setup>
	import {
		ref,
		computed,
		onMounted
	} from 'vue'

	const userInfo = ref({})
	const nickname = ref('')

	// 计算属性 - 隐藏部分手机号
	const maskedPhone = computed(() => {
		if (userInfo.value.phone) {
			return userInfo.value.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
		}
		return ''
	})

	onMounted(() => {
		loadUserInfo()

		// 添加监听，当用户信息更新时重新加载
		uni.$on('loginSuccess', loadUserInfo)
	})

	function loadUserInfo() {
		const user = uni.getStorageSync('userInfo')
		if (user) {
			userInfo.value = user
			nickname.value = user.userName || user.phone || ''
		}
	}

	//退出登录
	const logout = () => {
		uni.showModal({
			title: '提示',
			content: '确定要退出登录吗？',
			success: (res) => {
				if (res.confirm) {
					// 清除用户信息和购物车数据
					uni.removeStorageSync('userInfo')
					uni.removeStorageSync('token')
					// 触发全局登出事件
					uni.$emit('userLoggedOut')


					uni.switchTab({
						url: '/pages/index/index'
					})
				}
			}
		})
	}
</script>

<style lang="scss" scoped>
	.profile-container {
		padding: 40rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}

	.avatar-container {
		display: flex;
		justify-content: center;
		margin: 80rpx 0;

		.avatar {
			width: 180rpx;
			height: 180rpx;
			border-radius: 50%;
			border: 4rpx solid #fff;
			box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
		}
	}

	.info-container {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 0 30rpx;
		margin-bottom: 40rpx;

		.info-item {
			display: flex;
			align-items: center;
			height: 100rpx;
			border-bottom: 1rpx solid #f0f0f0;

			&:last-child {
				border-bottom: none;
			}

			.label {
				width: 150rpx;
				font-size: 28rpx;
				color: #666;
			}

			.input,
			.value {
				flex: 1;
				font-size: 28rpx;
				color: #333;
			}

			.input {
				height: 100%;
			}
		}
	}

	.action-container {
		padding: 0 30rpx;

		.save-btn {
			height: 80rpx;
			line-height: 80rpx;
			background-color: #e64340;
			color: #fff;
			border-radius: 40rpx;
			font-size: 30rpx;
			margin-bottom: 30rpx;

			&[disabled] {
				background-color: #ccc;
			}
		}

	}

	.logout {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 120px;

		.logout-btn {
			font-size: 28rpx;
			color: #999;
			text-decoration: underline;
		}

	}
</style>