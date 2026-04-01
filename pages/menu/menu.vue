<template>
	<view class="menu-page">
		<!-- 头部搜索 -->
		<view class="search-input">
			<uni-search-bar placeholder="请输入商品名"  @focus="gotoSearchPage" :disabled="true">
			</uni-search-bar>
		</view>

		<!-- 店铺信息 -->
		<view class="menu-info">
			<view class="store-info">
				<view class="store-name">
					<uni-icons type="star" class="star" size="20"></uni-icons>
					蜜雪冰城（新高路店）
					<uni-icons type="right" class="right"></uni-icons>
				</view>
				<view class="store-location">
					<uni-icons type="location" class="location-icon" size="20"></uni-icons>
					<view class="distance-info">
						直线距离 <text style="color: red; margin-left: 4rpx">471m</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 导航栏 -->
		<view class="nav-bar">
			<view v-for="(item, index) in navItems" :key="index" class="nav-item"
				:class="{active: currentNavIndex === index}" @click="switchNav(index)">
				{{item}}
				<view class="nav-line" v-if="currentNavIndex === index"></view>
			</view>
		</view>

		<view class="divider"></view>

		<!-- 菜单 -->
		<view class="menu-container">
			<!-- 左侧分类 -->
			<scroll-view class="sidebar" scroll-y :show-scrollbar="false" :enhanced="true">
				<view v-for="(category, index) in categories" :key="index" class="sidebar-item"
					:class="{active: current === index}" @click="handleSidebarTap(index)">
					<view class="left-line" v-if="current === index"></view>
					<view class="item-label">{{category.name}}</view>
				</view>
			</scroll-view>

			<!-- 右侧商品 -->
			<scroll-view class="menu-list" scroll-y :scroll-with-animation="true" :scroll-top="scrollTop"
				@scroll="onScroll" id="product-scroll">
				<!-- 轮播图 -->
				<uni-swiper-dot class="uni-swiper-dot-box" :info="swiperList" :current="currentSwiperIndex" mode="round"
					:dots-styles="dotsStyles" field="imageUrl">
					<swiper class="swiper-box" @change="handleSwiperChange" :current="currentSwiperIndex"
						:autoplay="true" :interval="3000" circular>
						<swiper-item v-for="(item, index) in swiperList" :key="index">
							<image class="swiper-image" :src="item.imageUrl" mode="aspectFill"
								@click="handleSwiperTap(index)"></image>
						</swiper-item>
					</swiper>
				</uni-swiper-dot>


				<!-- 商品列表 -->
				<view class="list">
					<view v-for="(category, index) in categories" :key="category.id" class="section"
						:id="'section-'+index">
						<view class="section-header" :class="{sticky: currentStickyIndex === index}">
							{{category.name}}
						</view>
						<view class="section-list">
							<view class="item" v-for="product in category.products" :key="product.id"
								@click="goToGoodsDetail(product)">
								<image :src="product.image" mode="aspectFill"></image>
								<view class="info">
									<view class="name">{{product.name}}</view>
									<view class="description">{{product.description}}</view>
									<view class="price">￥{{product.price}}</view>
								</view>
								<view class="add-icon" @click="goToGoodsDetail(product)">
									<uni-icons type="plus" size="40" color="white"></uni-icons>
								</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

	</view>
</template>

<script setup>
	import {
		ref,
		onMounted,
		nextTick
	} from 'vue';
	import {
		request
	} from '@/utils/request';
	import {
		onShow
	} from '@dcloudio/uni-app'

	const keyword = ref('')
	const cancelSeach = () => {
		keyword.value = ''

	}
	const currentSwiperIndex = ref(0)
	const navItems = ref(["经典菜单", "咖啡轻乳"])
	const currentNavIndex = ref(0)
	const categories = ref([])
	const current = ref(0)
	const currentStickyIndex = ref(0)
	const scrollTop = ref(0)
	const sectionHeights = ref([])
	const windowHeight = ref(0)

	// 轮播图数据
	const swiperList = ref([{
			imageUrl: '/static/images/swiper-test.jpg',
			canClick: false // 第一张不可点击
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

	const dotsStyles = ref({
		backgroundColor: '#fff',
		border: '1px #fff solid',
		selectedBackgroundColor: '#e64340',
		selectedBorder: '1px #e6514c solid'
	})

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

	//加载菜单数据
	async function loadMenuData() {
		let loadingShown = false;
		try {
			uni.showLoading({
				title: '加载中...'
			});
			loadingShown = true;

			// 添加请求日志
			console.log('开始请求菜单数据...');

			const res = await request.get('/menu');
			console.log('API响应数据：', res)

			// 数据检查
			if (!res || res.code !== 200 || !res.data || !Array.isArray(res.data)) {
				const errMsg = res?.message || '无效的响应数据';
				console.error('数据格式错误:', errMsg);
				throw new Error(errMsg);
			}

			// 确保数据格式正确
			const transformedData = res.data.map(category => ({
				...category,
				products: Array.isArray(category.products) ?
					category.products.map(product => ({
						...product,
						price: parseFloat(product.price) || 0
					})) : []
			}));

			categories.value = transformedData;

			 // 等待数据渲染到DOM
			await nextTick();
			//计算分类位置
			await calculateSectionPositions();

		} catch (error) {
			console.error('加载菜单失败:', error);
			categories.value = [];
			uni.showToast({
				title: '菜单加载失败，请稍后重试',
				icon: 'none',
				duration: 2000
			});
		} finally {
			if (loadingShown) {
				uni.hideLoading();
			}
		}
	}

	function switchNav(index) {
		currentNavIndex.value = index
	}

	//进入搜索页面
	const gotoSearchPage = () => {
	  uni.navigateTo({
	    url: '/pages/search/search'
	  })
	}

	//左侧分类点击事件
	function handleSidebarTap(index) {
		current.value = index
		currentStickyIndex.value = index

		// 确保sectionHeights已经计算完成
		if (sectionHeights.value.length === 0) {
			console.warn('分类位置信息未计算完成')
			return
		}

		// 找到对应的位置信息
		const targetSection = sectionHeights.value.find(item => item.index === index)
		if (!targetSection) {
			console.warn(`未找到分类${index+1}的位置信息`)
			return
		}

		// 设置滚动位置，使右侧滚动到对应位置
		scrollTop.value = targetSection.top
		console.log(`跳转到分类 ${index+1}，位置: ${targetSection.top}`)
	}

	//右侧商品滚动监听
	function onScroll(e) {
		//获取当前滚动位置
		const currentScrollTop = e.detail.scrollTop
		let currentIndex = 0

		// 遍历所有分类的位置信息
		for (let i = 0; i < sectionHeights.value.length; i++) {
			const section = sectionHeights.value[i]
			// 增加50rpx的偏移量提前切换分类高亮
			if (currentScrollTop >= section.top - 50) {
				currentIndex = section.index
			} else {
				break
			}
		}

		// 同步左侧高亮
		if (currentIndex !== currentStickyIndex.value) {
			currentStickyIndex.value = currentIndex
			current.value = currentIndex
		}
	}

	// 计算各分类位置的方法
	function calculateSectionPositions() {
		return new Promise((resolve) => {
			setTimeout(() => {
				const query = uni.createSelectorQuery()
				query.selectAll('.section').boundingClientRect()
				query.select('#product-scroll').boundingClientRect()

				query.exec((res) => {
					if (res[0] && res[1]) {
						const scrollViewTop = res[1].top
						sectionHeights.value = res[0].map((item, index) => {
							return {
								index,
								top: item.top - scrollViewTop,
								height: item.height
							}
						})
						console.log('分类位置计算完成:', JSON.parse(JSON.stringify(sectionHeights.value)))
					} else {
						console.warn('未能获取元素位置信息')
					}
					resolve()
				})
			}, 500) // 增加延迟确保DOM完全渲染
		})
	}

	//跳转到商品详情页
	function goToGoodsDetail(product) {
		 uni.navigateTo({
		    url: `/pages/goods-detail/goods-detail?productId=${product.id}`
		  })
	}

	onMounted(async () => {
		try {
			await loadMenuData()
			const res = await uni.getSystemInfo()
			windowHeight.value = res.windowHeight
		} catch (error) {
			console.error('初始化失败:', error)
		}
	})
</script>

<style lang="scss" scoped>
	.menu-page {
		display: flex;
		flex-direction: column;
		height: 100vh;

		.header {
			display: flex;
			align-items: center;
			margin-top: 52px;
			margin-bottom: 34rpx;

			.search-input {
				display: flex;
				align-items: center;
				background-color: #f1f1f2;
				width: 100%;
				height: 60rpx;
				border-radius: 10rpx;
				padding: 0 18rpx;
				box-sizing: border-box;
			}
		}

		.menu-info {
			padding: 0 30rpx;
			display: flex;
			justify-content: space-between;

			.store-info {
				margin-top: 4rpx;

				.store-name {
					display: flex;
					align-items: center;
					font-size: 32rpx;
					font-weight: bold;
					margin-bottom: 10rpx;

					.star {
						margin-right: 10rpx;
						color: #a6a6a6;
					}

					.right {
						width: 16rpx;
						height: 28rpx;
					}
				}

				.store-location {
					display: flex;
					align-items: center;

					.location-icon {
						margin-right: 10rpx;
					}

					.distance-info {
						color: #a6a6a6;
						font-size: 22rpx;
						display: flex;
					}
				}
			}
		}

		.nav-bar {
			display: flex;
			margin-top: 15rpx;
			justify-content: flex-start;

			.nav-item {
				flex: none;
				text-align: center;
				font-size: 14px;
				color: rgb(77, 73, 73);
				padding: 10px 10px;
				position: relative;
				margin-right: 15px;

				&.active {
					font-weight: bold;
					color: #000;
				}

				.nav-line {
					position: absolute;
					bottom: 0;
					left: 50%;
					transform: translateX(-50%);
					width: 28px;
					height: 5rpx;
					background-color: #e64340;
					border-radius: 2rpx;
				}
			}
		}

		.divider {
			height: 1rpx;
			background-color: #f1f1f2;
		}

		.menu-container {
			display: grid;
			grid-template-columns: 160rpx 1fr;
			gap: 20rpx;
			height: 100%;
			padding-right: 32rpx;
			height: calc(100vh - 200rpx - var(--status-bar-height) - var(--window-top));
			padding-bottom: 120rpx;

			.sidebar {
				height: 100%;
				background-color: #f8f8f8;
				box-sizing: border-box;

				.sidebar-item {
					display: flex;
					flex-direction: column;
					justify-content: center;
					align-items: center;
					height: 60px;
					position: relative;
					padding-left: 8rpx;

					&.active {
						background-color: #fff;
						font-weight: bold;
						color: #000;
					}

					.left-line {
						position: absolute;
						left: 2px;
						width: 4rpx;
						height: 24px;
						background-color: #e64340;
						border-radius: 2rpx;
						top: 50%;
						transform: translateY(-50%);
					}

					.item-label {
						font-size: 26rpx;
						color: #333;
						text-align: center;
						white-space: nowrap;
						overflow: hidden;
						text-overflow: ellipsis;
						max-width: 100%;
					}
				}
			}

			.menu-list {
				height: 100%;
				position: relative;
				overflow-y: auto;

				.swiper-container {
					width: 100%;
					height: 400rpx;
					position: relative;
					margin-bottom: 30rpx;

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

				}

				.section {
					margin-bottom: 40rpx;

					.section-header {
						font-size: 28rpx;
						font-weight: bold;
						margin-bottom: 20rpx;
						padding: 15rpx 20rpx;
						background-color: #f7f7f7;
						position: relative;
						z-index: 1;

						&.sticky {
							position: sticky;
							top: 0;
							z-index: 2;
							background-color: #fff;
							box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
						}
					}

					.section-list {
						background-color: #fff;
						border-radius: 16rpx;
						overflow: hidden;

						.item {
							display: flex;
							padding: 20rpx;
							position: relative;
							border-bottom: 1rpx solid #f1f1f2;

							image {
								width: 160rpx;
								height: 160rpx;
								border-radius: 8rpx;
								margin-right: 20rpx;
							}

							.info {
								flex: 1;
								display: flex;
								flex-direction: column;
								justify-content: space-between;

								.name {
									font-size: 28rpx;
									font-weight: bold;
									color: #333;
								}

								.description {
									font-size: 24rpx;
									color: #999;
									margin: 10rpx 0;
								}

								.price {
									font-size: 28rpx;
									color: #000;
									font-weight: bold;
								}
							}

							.add-icon {
								position: absolute;
								display: flex;
								width: 40rpx;
								height: 40rpx;
								border-radius: 23rpx;
								background-color: #e64340;
								right: 20rpx;
								bottom: 20rpx;
								justify-content: center;
								align-items: center;


							}
						}
					}
				}
			}
		}
	}
</style>