<template>
	<view class="login-container">
		<image class="logo" src="/static/images/me-avatar.png"></image>
		<view class="title">蜜雪冰城</view>
		<view class="subtitle">登录享受更多优惠</view>

		<!-- 动态表单 -->
		<view class="form-container">
			<!-- 用户名输入框 - 仅注册时显示 -->
			<view class="form-item" v-if="showRegister">
				<input v-model="userName" placeholder="请输入用户名" placeholder-class="placeholder" />
			</view>

			<!-- 手机号输入框 -->
			<view class="form-item">
				<input type="number" v-model="phone" placeholder="请输入手机号" maxlength="11"
					placeholder-class="placeholder" />
			</view>

			<!-- 密码输入框 -->
			<view class="form-item">
				<input type="text" v-model="password" placeholder="请输入密码" placeholder-class="placeholder"
					:password="!showPassword" />
				<view class="password-toggle" @click="togglePasswordShow">
					<image class="eye-icon"
						:src="showPassword ? '/static/images/eye-open.png' : '/static/images/eye-close.png'" />
				</view>
			</view>

			<!-- 主按钮 - 根据状态显示登录/注册 -->
			<button class="login-btn" @click="showRegister ? handleRegister() : handlePhoneLogin()">
				<text>{{ showRegister ? '注册' : '登录' }}</text>
			</button>

			<!-- 切换按钮 -->
			<button class="toggle-btn" @click="showRegister = !showRegister">
				<text>{{ showRegister ? '已有账号？去登录' : '没有账号？去注册' }}</text>
			</button>
		</view>

		<view class="agreement">
			登录即表示同意<text class="link">《用户协议》</text>和<text class="link">《隐私政策》</text>
		</view>

		<view class="skip-login" @click="skipLogin">暂不登录</view>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'
	import {
		request
	} from '@/utils/request'

	const phone = ref('')
	const password = ref('')
	const userName = ref('')
	const showRegister = ref(false) // 默认显示登录表单
	const showPassword = ref(false) // 控制密码显示状态

	// 切换密码显示状态
	const togglePasswordShow = () => {
		showPassword.value = !showPassword.value
	}

	// 手机号注册
	const handleRegister = async () => {
		if (!phone.value || !password.value || !userName.value) {
			uni.showToast({
				title: '请填写完整信息',
				icon: 'none'
			})
			return
		}

		if (!/^1[3-9]\d{9}$/.test(phone.value)) {
			uni.showToast({
				title: '手机号格式不正确',
				icon: 'none'
			})
			return
		}

		if (password.value.length < 6 || password.value.length > 12) {
			uni.showToast({
				title: '密码长度需要在6-12位之间',
				icon: 'none'
			})
			return
		}

		uni.showLoading({
			title: '注册中...'
		})

		try {
			const res = await request.post('/user/register', {
				userName: userName.value,
				phone: phone.value,
				password: password.value
			})

			uni.hideLoading()
			uni.showToast({
				title: '注册成功，请登录',
				icon: 'success'
			})

			/// 注册成功后切换到登录表单
			showRegister.value = false
			// 清空密码框
			password.value = ''
		} catch (error) {
			uni.hideLoading()
			uni.showToast({
				title: error.message || '注册失败',
				icon: 'none'
			})
		}
	}

	// 手机号登录
	const handlePhoneLogin = async () => {
		if (!phone.value) {
			uni.showToast({
				title: '请输入手机号',
				icon: 'none'
			})
			return
		}

		if (!password.value) {
			uni.showToast({
				title: '请输入密码',
				icon: 'none'
			})
			return
		}

		if (!/^1[3-9]\d{9}$/.test(phone.value)) {
			uni.showToast({
				title: '手机号格式不正确',
				icon: 'none'
			})
			return
		}

		uni.showLoading({
			title: '登录中...',
			mask: true
		})

		try {
			const response = await request.post('/user/login', {
				phone: phone.value,
				password: password.value
			})

			console.log('完整响应:', response)

			// 检查响应结构
			if (!response || typeof response !== 'object') {
				throw new Error('服务器返回无效响应')
			}

			// 处理成功情况 (code 200)
			if (response.code === 200) {

				// 存储用户信息到本地
				uni.setStorageSync('userInfo', response.data)

				// 触发全局事件通知其他页面
				uni.$emit('userInfoUpdated')
				uni.$emit('loginSuccess')

				uni.showToast({
					title: '登录成功',
					icon: 'success'
				})
				// 根据来源页面跳转
				const loginFrom = uni.getStorageSync('loginFrom')
				if (loginFrom) {
					uni.removeStorageSync('loginFrom')
					if (loginFrom === 'cart') {
						uni.switchTab({
							url: '/pages/order/order'
						})
					} else {
						uni.navigateBack()
					}
				} else {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}
			} else {
				// 处理错误情况
				throw new Error(response.message || `登录失败，错误码: ${response.code}`)
			}
		} catch (error) {
			uni.hideLoading()
			let errorMsg = '登录失败，请重试'
			if (error.message.includes('不存在')) {
				errorMsg = error.message
			} else if (error.message.includes('密码错误')) {
				errorMsg = '密码错误，请重新输入'
			} else if (error.message) {
				errorMsg = error.message
			}

			uni.showToast({
				title: errorMsg,
				icon: 'none',
				duration: 3000
			})
		}
	}

	function skipLogin() {
		const loginFrom = uni.getStorageSync('loginFrom')
		if (loginFrom === 'my') {
			uni.removeStorageSync('loginFrom')
			uni.switchTab({
				url: '/pages/my/my'
			})
		} else {
			uni.switchTab({
				url: '/pages/index/index'
			})
		}
	}
</script>

<style lang="scss" scoped>
	.login-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 60rpx 40rpx;
		height: 100vh;
		box-sizing: border-box;

		.logo {
			width: 200rpx;
			height: 200rpx;
			margin-bottom: 40rpx;
		}

		.title {
			font-size: 40rpx;
			font-weight: bold;
			margin-bottom: 20rpx;
		}

		.subtitle {
			font-size: 28rpx;
			color: #999;
			margin-bottom: 60rpx;
		}

		.form-container {
			width: 100%;
			margin-bottom: 40rpx;

			.form-item {
				position: relative;
				margin-bottom: 30rpx;

				input {
					width: 100%;
					height: 90rpx;
					padding: 0 30rpx;
					padding-right: 80rpx;
					border: 1rpx solid #eee;
					border-radius: 45rpx;
					font-size: 28rpx;
					background-color: #f8f8f8;
				}

				.password-toggle {
					position: absolute;
					right: 5rpx;
					top: 50%;
					transform: translateY(-50%);
					width: 60rpx;
					height: 60rpx;
					display: flex;
					align-items: center;
					justify-content: center;

					.eye-icon {
						width: 40rpx;
						height: 40rpx;
					}
				}

				.placeholder {
					color: #ccc;
				}
			}
		}

		.login-btn {
			width: 100%;
			height: 90rpx;
			line-height: 90rpx;
			background-color: #e64340;
			color: #fff;
			border-radius: 45rpx;
			font-size: 32rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			margin-bottom: 20rpx;
			border: none;

			&:active {
				opacity: 0.8;
			}
		}

		.toggle-btn {
			background: transparent;
			color: #e64340;
			border: none;
			margin-top: 20rpx;
			font-size: 28rpx;
		}

		.agreement {
			font-size: 24rpx;
			color: #999;
			margin-bottom: 60rpx;

			.link {
				color: #576B95;
			}
		}

		.skip-login {
			font-size: 28rpx;
			color: #999;
			text-decoration: underline;
		}
	}
</style>