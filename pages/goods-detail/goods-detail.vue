<template>
	<view class="goods-detail-container">
		<!-- 图片 -->
		<view class="image-container">
			<image class="goods-image" :src="product.image" mode="aspectFill"></image>
		</view>

		<!-- 商品名称和分割线 -->
		<view class="goods-name-container">
			<view class="goods-name">{{product.name}}</view>
			<view class="divider"></view>
		</view>

		<!-- 温度选择 -->
		<view class="option-section">
			<view class="option-row">
				<view class="section-title">温度</view>
				<view class="options">
					<view v-for="(item, index) in temperatureOptions" :key="index" class="option-item"
						:class="{selected: selectedTemperature === item}" @click="selectTemperature(item)">
						{{item}}
					</view>
				</view>
			</view>
		</view>

		<!-- 糖度选择 -->
		<view class="option-section">
			<view class="option-row">
				<view class="section-title">糖度</view>
				<view class="options">
					<view v-for="(item, index) in sugarOptions" :key="index" class="option-item"
						:class="{selected: selectedSugar === item}" @click="selectSugar(item)">
						{{item}}
					</view>
				</view>
			</view>
		</view>
		<view class="divider"></view>

		<!-- 商品详情 -->
		<view class="detail-section">
			<view class="section-title">商品详情</view>
			<view class="divider"></view>
			<view class="detail-content">{{product.description}}</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="action-bar-container">
			<view class="divider"></view>
			<view class="action-bar">
				<view class="price-container">
					<view class="price">￥{{product.price}}</view>
					<view class="selected-options">{{selectedTemperature}}/{{selectedSugar}}</view>
				</view>
				<view class="quantity-control">
					<view class="btn minus" @click="decreaseQuantity">-</view>
					<view class="quantity">{{quantity}}</view>
					<view class="btn plus" @click="increaseQuantity">+</view>
				</view>
			</view>
		</view>

		<!-- 底部按钮 -->
		<view class="footer-container">
			<view class="divider"></view>
			<view class="footer">
				<view class="btn buy-now" @click="buyNow">立即购买</view>
				<view class="btn add-to-cart" @click="addToCart">加入购物车</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'
	import {
		onLoad
	} from '@dcloudio/uni-app'
	import {
		request
	} from '@/utils/request'

	const product = ref({})
	const temperatureOptions = ['正常冰', '少冰', '热']
	const sugarOptions = ['正常糖', '七分糖', '五分糖', '三分糖', '无糖']
	const selectedTemperature = ref('正常冰')
	const selectedSugar = ref('正常糖')
	const quantity = ref(1)

	onLoad(async (options) => {
		try {

			//通过productId参数加载商品详情
			if (options.productId) {
				const res = await request.get(`/menu/product/${options.productId}`);
				if (res.code === 200) {
					product.value = res.data;
				} else {
					throw new Error(res.message || '获取商品详情失败');
				}
			} else {
				throw new Error('缺少商品ID参数');
			}
		} catch (error) {
			console.error('加载商品详情失败:', error);
			uni.showToast({
				title: '加载商品详情失败',
				icon: 'none'
			});
			setTimeout(() => uni.navigateBack(), 1500);
		}
	});

	//加入购物车
	async function addToCart() {
		// 先检查商品信息是否完整
		if (!product.value?.id) {
			console.error('商品信息不完整:', product.value);
			uni.showToast({
				title: '商品信息不完整，请稍后重试',
				icon: 'none'
			});
			return;
		}

		//检查登录状态，未登录则跳转到登录页
		const isLogin = !!uni.getStorageSync('userInfo');
		if (!isLogin) {
			uni.showModal({
				title: '提示',
				content: '请先登录后再添加商品到购物车',
				confirmText: '去登录',
				success: (res) => {
					if (res.confirm) {
						uni.navigateTo({
							url: '/pages/login/login'
						})
					}
				}
			})
			return
		}

		uni.showLoading({
			title: '添加中...'
		})

		try {
			const userInfo = uni.getStorageSync('userInfo')
			if (!userInfo || !userInfo.userId) {
				throw new Error('无法获取用户信息')
			}
			// 构造请求参数
			const params = new URLSearchParams();
			params.append('productId', product.value.id);
			params.append('temperature', selectedTemperature.value);
			params.append('sugar', selectedSugar.value);
			params.append('quantity', quantity.value);

			console.log('准备发送的请求参数:', params.toString()); // 调试日志
			
			//调用接口
			const res = await request.post(
				`/cart/${userInfo.userId}/add?${params.toString()}`
			);

			console.log('API响应:', res); // 调试日志

			if (res.code === 200) {
				uni.showToast({
					title: '已加入购物车',
					icon: 'success'
				})
				setTimeout(() => {
					uni.navigateBack()
				}, 1200)
			} else {
				throw new Error(res.message || '添加失败')
			}
		} catch (error) {
			console.error('添加到购物车失败:', {
				url: `/cart/${userInfo?.userId}/add`,
				params: {
					productId: product.value.id,
					temperature: selectedTemperature.value,
					sugar: selectedSugar.value,
					quantity: quantity.value
				},
				error: error.message
			});

			uni.showToast({
				title: error.message.includes('productId') ?
					'商品参数错误' : '添加失败，请稍后再试',
				icon: 'none',
				duration: 3000
			});
		} finally {
			uni.hideLoading();
		}
	}


	function selectTemperature(temp) {
		selectedTemperature.value = temp
	}

	function selectSugar(sugar) {
		selectedSugar.value = sugar
	}

	function increaseQuantity() {
		quantity.value += 1
	}

	function decreaseQuantity() {
		if (quantity.value > 1) {
			quantity.value -= 1
		}
	}

	function buyNow() {
		uni.showToast({
			title: '立即购买功能开发中',
			icon: 'none'
		})
	}
</script>

<style lang="scss" scoped>
	.goods-detail-container {
		padding-bottom: 220rpx;

		.image-container {
			position: relative;
			width: 100%;
			height: 500rpx;

			.goods-image {
				width: 100%;
				height: 100%;
			}

			.back-btn {
				position: absolute;
				top: 30rpx;
				left: 30rpx;
				z-index: 100;
				width: 60rpx;
				height: 60rpx;
				background: rgba(143, 140, 140, 0.5);
				border-radius: 50%;
				display: flex;
				justify-content: center;
				align-items: center;

				image {
					width: 30rpx;
					height: 30rpx;
				}
			}
		}

		.goods-name-container {
			padding: 30rpx;

			.goods-name {
				font-size: 36rpx;
				font-weight: bold;
				margin-bottom: 20rpx;
			}

			.divider {
				height: 2rpx;
				width: 100%;
				background-color: #f5f5f5;
				margin: 10rpx 0;
			}
		}

		.option-section {
			padding: 0 20rpx;
			margin-bottom: 20rpx;

			.option-row {
				display: flex;
				align-items: center;
				margin-bottom: 20rpx;

				.section-title {
					font-size: 30rpx;
					font-weight: bold;
					width: 120rpx;
				}

				.options {
					display: flex;
					flex-wrap: wrap;
					flex: 1;
					gap: 15rpx;

					.option-item {
						padding: 15rpx 20rpx;
						border: 1rpx solid #ddd;
						border-radius: 10rpx;
						font-size: 28rpx;
						width: 74px;

						&.selected {
							border-color: #e64340;
							color: #e64340;
							background-color: #ffeeee;
						}
					}
				}
			}
		}

		.detail-section {
			padding: 30rpx;

			.detail-content {
				font-size: 23rpx;
				color: #666;
				line-height: 1.6;
				margin-top: 20rpx;
			}
		}

		.action-bar-container {
			position: fixed;
			bottom: 140rpx;
			left: 0;
			right: 0;
			background: #fff;

			.action-bar {
				height: 100rpx;
				display: flex;
				align-items: center;
				justify-content: space-between;
				padding: 0 30rpx;

				.price-container {
					width: 100%;
					display: flex;
					flex-direction: column;

					.price {
						font-size: 34rpx;
						font-weight: bold;
						color: #000;
					}

					.selected-options {
						font-size: 24rpx;
						color: #666;
					}
				}

				.quantity-control {
					display: flex;
					align-items: center;
					border: 1rpx solid #ddd;
					border-radius: 30rpx;

					.btn {
						width: 70rpx;
						height: 70rpx;
						display: flex;
						justify-content: center;
						align-items: center;
						font-size: 36rpx;
						background-color: #fff;

						&.minus {
							color: #666;
						}

						&.plus {
							color: #e64340;
						}
					}

					.quantity {
						width: 80rpx;
						text-align: center;
						font-size: 30rpx;
						border-left: 1rpx solid #ddd;
						border-right: 1rpx solid #ddd;
					}
				}
			}
		}

		.footer-container {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background: #fff;

			.footer {
				height: 120rpx;
				display: flex;

				.btn {
					flex: 1;
					display: flex;
					justify-content: center;
					align-items: center;
					font-size: 25rpx;
					font-weight: bold;
					margin: 20rpx;
					border-radius: 20rpx;

					&.buy-now {
						background: #fff;
						color: #e64340;
						border: 1rpx solid #e64340;
					}

					&.add-to-cart {
						color: #fff;
						background-color: #e64340;
						border: 1rpx solid #e64340;
					}
				}
			}
		}
	}
</style>