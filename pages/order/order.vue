<template>
	<view class="cart-container">
		<!-- 购物车标题 -->
		<view class="cart-header">
			<text class="title">已选商品</text>
			<text class="manage-btn" @click="toggleManageMode">
				{{ isManageMode ? '退出管理' : '管理' }}
			</text>
		</view>

		<!-- 购物车内容 -->
		<scroll-view class="cart-content" scroll-y :scroll-with-animation="true" v-if="cart.list.length > 0">
			<view class="cart-item" v-for="(item, index) in cart.list" :key="item.id">
				<radio value="" color="#e64340" :checked="item.selected" @click="toggleSelected(index)" />
				<image class="item-image" :src="item.image" mode="aspectFill"></image>
				<view class="item-info">
					<text class="item-name">{{ item.name }}</text>
					<text class="item-spec">{{ item.specs }}</text>
					<text class="item-price">￥{{ (item.price * item.count).toFixed(2) }}</text>
				</view>
				<view class="quantity-control" v-if="!isManageMode">
					<view class="btn minus" @click="decreaseCartItem(index)">-</view>
					<view class="quantity">{{ item.count }}</view>
					<view class="btn plus" @click="increaseCartItem(index)">+</view>
				</view>
			</view>
		</scroll-view>


		<!-- 空购物车状态 -->
		<view class="empty-cart" v-else>
			<image class="empty-image" src="/static/images/order.jpg"></image>
			<text class="empty-text">{{isLogin ? '购物车空空如也~' : '请登录后查看购物车'}}</text>
			<button class="go-shopping" @click="handleEmptyCartClick">
				{{isLogin ? '去喝一杯' : '去登录'}}
			</button>
		</view>

		<!-- 底部结算栏/管理栏 - 固定在底部 -->
		<view class="checkout-bar pos-app" v-if="cart.list.length > 0">
			<view class="select-all">
				<radio value="" color="#e64340" :checked="allSelected" @click="toggleAllSelected" />
				<text @click="toggleAllSelected">全选</text>
			</view>
			<view class="total-info">
				<text class="total-text" v-if="!isManageMode">合计:</text>
				<text class="total-price" v-if="!isManageMode">￥{{ selectedTotalPrice.toFixed(2) }}</text>
				<view class="checkout-btn" @click="handleCheckoutClick">
					{{ isManageMode ? "删除" : "结算" }}
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
	import {
		onShow
	} from '@dcloudio/uni-app'

	import {
		request
	} from '@/utils/request'

	const cart = ref({
		list: [],
		totalPrice: 0
	})

	const isManageMode = ref(false)
	const isLoading = ref(false)

	const isLogin = computed(() => {
		const user = uni.getStorageSync('userInfo');
		return user && user.userId;
	})

	// 获取用户信息
	const userInfo = computed(() => {
		return uni.getStorageSync('userInfo') || null
	})

	// 全选/取消全选
	const toggleAllSelected = () => {
		const newState = !allSelected.value
		cart.value.list.forEach(item => item.selected = newState)
		calculateTotal()
	}

	// 切换商品选中状态
	const toggleSelected = (index) => {
		cart.value.list[index].selected = !cart.value.list[index].selected
		calculateTotal()
	}

	const allSelected = computed(() => {
		return cart.value.list.length > 0 &&
			cart.value.list.every(item => item.selected)
	})

	// 计算选中的商品项数
	const selectedItemCount = computed(() => {
		return cart.value.list.filter(item => item.selected).length
	})

	// 计算选中商品总价
	const selectedTotalPrice = computed(() => {
		return cart.value.list.reduce((total, item) => {
			return item.selected ? total + (item.price * item.count) : total
		}, 0)
	})

	// 页面显示时加载数据
	onShow(() => {
		// 每次显示页面时检查用户状态
		const storedUser = uni.getStorageSync('userInfo')
		if (storedUser) {
			userInfo.value = storedUser
			if (!cart.value.list.length) {
				loadCart()
			}
		} else {
			userInfo.value = null
			cart.value = {
				list: [],
				totalPrice: 0
			}
		}
	})

	// 监听登录状态变化
	onMounted(() => {
		uni.$on('loginSuccess', (userData) => {
			// 更新本地用户信息
			userInfo.value = userData
			// 加载购物车
			loadCart()
		})

		uni.$on('userLoggedOut', () => {
			userInfo.value = null
			cart.value = {
				list: [],
				totalPrice: 0
			}
			isManageMode.value = false
		})
	})

	// 加载购物车数据
	const loadCart = async () => {
		// 是否已登录
		if (!userInfo.value?.userId) {
			cart.value = {
				list: [],
				totalPrice: 0
			}
			return
		}
		if (!userInfo.value?.userId) return

		isLoading.value = true
		try {
			const res = await request.get(`/cart/${userInfo.value.userId}`)
			if (res.code === 200) {
				cart.value.list = res.data.items.map(item => ({
					id: item.id,
					productId: item.productId,
					name: item.productName,
					image: item.productImage,
					price: item.price,
					count: item.quantity,
					specs: `${item.temperature}/${item.sugar}`,
					selected: false // 默认不选中
				}))
				calculateTotal() // 计算总价
			} else {
				throw new Error(res.message || '获取购物车失败')
			}
		} catch (error) {
			console.error('加载购物车失败:', error)
			uni.showToast({
				title: '加载购物车失败',
				icon: 'none'
			})
		} finally {
			isLoading.value = false
		}
	}

	// 计算总价
	const calculateTotal = () => {
		cart.value.totalPrice = cart.value.list
			.filter(item => item.selected)
			.reduce((total, item) => total + (item.price * item.count), 0)
	}

	// 增加商品数量
	const increaseCartItem = async (index) => {
		const item = cart.value.list[index]
		try {
			const res = await request.put(
				`/cart/${userInfo.value.userId}/update/${item.id}?quantity=${item.count + 1}`)
			if (res.code === 200) {
				// 更新本地数据
				cart.value.list[index].count = item.count + 1
				calculateTotal()
			} else {
				throw new Error(res.message || '更新失败')
			}
		} catch (error) {
			console.error('增加数量失败:', error)
			uni.showToast({
				title: '增加数量失败',
				icon: 'none'
			})
		}
	}

	// 减少商品数量
	const decreaseCartItem = async (index) => {
		const item = cart.value.list[index]
		if (item.count > 1) {
			try {
				const res = await request.put(
					`/cart/${userInfo.value.userId}/update/${item.id}?quantity=${item.count - 1}`)
				if (res.code === 200) {
					// 更新本地数据
					cart.value.list[index].count = item.count - 1
					calculateTotal()
				} else {
					throw new Error(res.message || '更新失败')
				}
			} catch (error) {
				console.error('减少数量失败:', error)
				uni.showToast({
					title: '减少数量失败',
					icon: 'none'
				})
			}
		} else {
			await deleteCartItem(index)
		}
	}

	// 删除单个商品
	const deleteCartItem = async (index) => {
		const item = cart.value.list[index]
		try {
			const res = await request.delete(`/cart/${userInfo.value.userId}/remove/${item.id}`)
			if (res.code === 200) {
				await loadCart()
			} else {
				throw new Error(res.message || '删除失败')
			}
		} catch (error) {
			console.error('删除商品失败:', error)
			uni.showToast({
				title: '删除商品失败',
				icon: 'none'
			})
		}
	}

	// 删除选中项
	const deleteSelected = async () => {
		const selectedIds = cart.value.list
			.filter(item => item.selected)
			.map(item => item.id)

		if (selectedIds.length === 0) {
			uni.showToast({
				title: '请选择要删除的商品',
				icon: 'none'
			})
			return
		}

		// 添加确认弹框
		uni.showModal({
			title: '提示',
			content: '确定要删除选中的商品吗？',
			success: async (res) => {
				if (res.confirm) {
					try {
						uni.showLoading({
							title: '删除中...'
						})
						await Promise.all(
							selectedIds.map(id =>
								request.delete(`/cart/${userInfo.value.userId}/remove/${id}`)
							)
						)
						await loadCart()
						uni.hideLoading()
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						})
					} catch (error) {
						uni.hideLoading()
						console.error('删除失败:', error)
						uni.showToast({
							title: '删除失败',
							icon: 'none'
						})
					}
				}
			}
		})
	}

	// 清空购物车
	const clearCart = async () => {
		uni.showModal({
			title: '提示',
			content: '确定要清空购物车吗？',
			async success(res) {
				if (res.confirm) {
					try {
						const res = await request.delete(
							`/cart/${userInfo.value.userId}/clear`)
						if (res.code === 200) {
							await loadCart()
						} else {
							throw new Error(res.message || '清空失败')
						}
					} catch (error) {
						console.error('清空购物车失败:', error)
						uni.showToast({
							title: '清空失败',
							icon: 'none'
						})
					}
				}
			}
		})
	}

	// 切换管理模式
	const toggleManageMode = () => {
		isManageMode.value = !isManageMode.value
		// 退出管理时取消所有选中状态
		if (!isManageMode.value) {
			cart.value.list.forEach(item => {
				item.selected = false
			})
		}
	}
	//处理结算和删除
	const handleCheckoutClick = () => {
		if (isManageMode.value) {
			deleteSelected();
		} else {
			onCheckout();
		}
	}

	// 结算
	const onCheckout = () => {
		if (selectedItemCount.value === 0) {
			uni.showToast({
				title: '请选择要结算的商品',
				icon: 'none',
				duration: 2000
			});
			return;
		}

		// 获取选中的商品
		const selectedItems = cart.value.list.filter(item => item.selected)
		uni.showToast({
			title: '结算功能开发中',
			icon: 'none'
		})
	}

	//处理空购物车时点击和未登录时
	const handleEmptyCartClick = () => {
		if (isLogin.value) {
			goToMenu();
		} else {
			gotoLogin();
		}
	}

	const gotoLogin = () => {
		// 记录来源页面为购物车
		uni.setStorageSync('loginFrom', 'cart')
		uni.navigateTo({
			url: '/pages/login/login'
		})
	}

	// 返回菜单页
	const goToMenu = () => {
		uni.switchTab({
			url: '/pages/menu/menu'
		})
	}
</script>

<style lang="scss" scoped>
	.cart-container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;

		.cart-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 20rpx 30rpx;
			background-color: #fff;
			position: relative;

			.title {
				font-size: 36rpx;
				font-weight: bold;
			}

			.manage-btn {
				font-size: 28rpx;
				color: #666;
			}

			.clear-btn {
				font-size: 28rpx;
				color: #666;
				display: flex;
				align-items: center;
				position: absolute;
				right: 30rpx;
			}
		}

		.cart-content {
			flex: 1;
			padding: 10px 10rpx;
			margin-bottom: 100rpx;
			box-sizing: border-box;
			overflow: hidden;

			.cart-item {
				display: flex;
				align-items: center;

				background-color: #fff;
				margin-bottom: 20rpx;
				border-radius: 16rpx;
				padding: 10rpx;

				radio {
					margin-right: 20rpx;
				}

				.item-image {
					width: 160rpx;
					height: 160rpx;
					border-radius: 16rpx;
					margin-right: 20rpx;
				}

				.item-info {
					flex: 1;
					display: flex;
					flex-direction: column;

					.item-name {
						font-size: 30rpx;
						margin-bottom: 10rpx;
					}

					.item-spec {
						font-size: 26rpx;
						color: #999;
						margin-bottom: 20rpx;
					}

					.item-price {
						font-size: 28rpx;
						color: #e64340;
						font-weight: bold;
					}
				}

				.quantity-control {
					display: flex;
					margin-top: 55px;
					align-items: center;

					.btn {
						width: 50rpx;
						height: 50rpx;
						border-radius: 50%;
						background: #f5f5f5;
						display: flex;
						justify-content: center;
						align-items: center;
						font-size: 35rpx;
					}

					.minus {
						color: #999;
					}

					.plus {
						color: #e64340;
					}

					.quantity {
						margin: 0 20rpx;
						min-width: 40rpx;
						text-align: center;
						font-weight: bold;
					}
				}
			}
		}

		.empty-cart {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding-bottom: 100rpx;

			.empty-image {
				width: 300rpx;
				height: 300rpx;
				margin-bottom: 40rpx;
			}

			.empty-text {
				font-size: 30rpx;
				color: #999;
				margin-bottom: 40rpx;
			}

			.go-shopping {
				width: 300rpx;
				height: 80rpx;
				line-height: 80rpx;
				background-color: #e64340;
				color: #fff;
				font-size: 30rpx;
				border-radius: 40rpx;
				border: none;

				&::after {
					border: none;
				}
			}
		}

		/* 底部结算栏 - 固定定位 */
		.checkout-bar {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 20rpx 30rpx;
			background-color: #fff;
			border-top: 1rpx solid #eee;
			width: 100%;
			box-sizing: border-box;

			.select-all {
				display: flex;
				align-items: center;

				radio {
					margin-right: 10rpx;
				}

				text {
					font-size: 28rpx;
				}
			}

			.total-info {
				display: flex;
				align-items: center;

				.total-text {
					font-size: 28rpx;
					margin-right: 10rpx;
				}

				.total-price {
					font-size: 32rpx;
					font-weight: bold;
					color: #e64340;
					margin-right: 30rpx;
				}

				.checkout-btn {
					padding: 0 40rpx;
					height: 70rpx;
					line-height: 70rpx;
					background-color: #e64340;
					color: #fff;
					font-size: 28rpx;
					border-radius: 35rpx;
					position: relative;
					z-index: 100;

					&::after {
						display: none;
					}
				}
			}
		}

		/* 条件编译 根据不同平台选择偏移量 */
		/* #ifdef H5 */
		.pos-app {
			position: fixed;
			bottom: 100rpx;
		}

		/* #endif */
		/* #ifndef H5 */
		.pos-app {
			position: fixed;
			bottom: 0;
		}

		/* #endif */
	}
</style>