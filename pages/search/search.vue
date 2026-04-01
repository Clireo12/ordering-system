<template>
	<view class="search-page">
		<!-- 搜索框 -->
		<view class="search-bar-container">
			<uni-search-bar placeholder="请输入商品名" v-model="searchKeyword" @confirm="handleSearch" @cancel="goBack"
				:focus="true">
			</uni-search-bar>
		</view>

		<!-- 搜索结果显示 -->
		<view v-if="searchResults.length > 0" class="search-results">
			<view class="result-item" v-for="item in searchResults" :key="item.id" @click="goToDetail(item)">
				<image class="product-image" :src="item.image" mode="aspectFill"></image>
				<view class="product-info">
					<text class="result-name">{{item.name}}</text>
					<text class="result-description">{{item.description}}</text>
					<text class="result-price">￥{{item.price}}</text>
				</view>
			</view>
		</view>

		<!-- 无结果提示 -->
		<view v-else-if="noResultsMessage" class="no-results">
			<text>{{noResultsMessage}}</text>
		</view>

		<!-- 历史搜索和热门推荐（只在没有搜索时显示） -->
		<view v-else>
			<!-- 历史搜索 -->
			<view v-if="historyKeywords.length > 0" class="history-section">
				<view class="section-title">
					<text>历史搜索</text>
					<text class="clear-btn" @click="clearHistory">清空</text>
				</view>
				<view class="history-tags">
					<view class="tag" v-for="(keyword, index) in historyKeywords" :key="index"
						@click="searchFromHistory(keyword)">
						{{keyword}}
					</view>
				</view>
			</view>

			<!-- 热门推荐 -->
			<view class="hot-section">
				<view class="section-title">热门推荐</view>
				<view class="hot-tags">
					<view class="tag" v-for="(item, index) in hotKeywords" :key="index"
						@click="goToHotProductDetail(item)">
						{{item.name}}
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		onMounted
	} from 'vue'
	import {
		onLoad
	} from '@dcloudio/uni-app'
	import {
		request
	} from '@/utils/request'

	const searchKeyword = ref('')
	const searchResults = ref([])
	const historyKeywords = ref([])
	const noResultsMessage = ref('')
	const hotKeywords = ref([{
			name: '茉莉奶绿',
			id: 4
		},
		{
			name: '冰鲜柠檬水',
			id: 3
		},
		{
			name: '草莓啵啵',
			id: 13
		},
		{
			name: '葡萄冰美式',
			id: 21
		},
		{
			name: '美式咖啡',
			id: 20
		},
		{
			name: '珍珠奶茶',
			id: 6
		}
	])
	const goBack = () => {
		uni.navigateBack()
	}

	// 搜索方法
	const handleSearch = async () => {
		const keyword = searchKeyword.value.trim()
		if (!keyword) {
			uni.showToast({
				title: '请输入搜索关键词',
				icon: 'none'
			})
			return
		}

		try {
			uni.showLoading({
				title: '搜索中...'
			});
			const encodedKeyword = encodeURIComponent(keyword); // 编码中文字符

			const res = await request.get(`/menu/search?keyword=${encodedKeyword}`);
			
			//直接拼接参数，避免uni.request的params编码问题

			console.log('完整响应数据:', res);
			uni.hideLoading()

			if (res.code === 200) {
				// 按商品名称去重
				const uniqueResults = [];
				const seenNames = new Set();

				for (const item of res.data) {
					if (!seenNames.has(item.name)) {
						seenNames.add(item.name);
						uniqueResults.push(item);
					}
				}
				searchResults.value = uniqueResults;
				if (searchResults.value.length === 0) {
					noResultsMessage.value = '没有找到相关商品'
					console.log('没有找到匹配商品')
				} else {
					noResultsMessage.value = ''
				}
				saveSearchHistory(keyword)
			} else {
				searchResults.value = []
				noResultsMessage.value = res.message || '搜索失败，请稍后再试'
				uni.showToast({
					title: noResultsMessage.value,
					icon: 'none'
				})
			}
		} catch (error) {
			uni.hideLoading()
			searchResults.value = []
			noResultsMessage.value = '搜索失败，请检查网络连接'
			uni.showToast({
				title: '搜索失败，请稍后重试',
				icon: 'none'
			})
			console.error('搜索失败:', error)
		}
	}

	// 保存搜索历史
	const saveSearchHistory = (keyword) => {
		if (!keyword.trim()) return

		let history = uni.getStorageSync('searchHistory') || []
		history = history.filter(item => item !== keyword)
		history.unshift(keyword)
		if (history.length > 10) {
			history = history.slice(0, 10)
		}

		uni.setStorageSync('searchHistory', history)
		historyKeywords.value = history
	}

	// 从历史记录搜索
	const searchFromHistory = (keyword) => {
		searchKeyword.value = keyword
		handleSearch()
	}

	// 清空历史记录
	const clearHistory = () => {
		uni.removeStorageSync('searchHistory')
		historyKeywords.value = []
	}

	// 跳转到商品详情
	const goToDetail = (product) => {
		if (!product?.id) {
			console.error('商品ID不存在:', product);
			uni.showToast({
				title: '商品信息不完整',
				icon: 'none'
			});
			return;
		}

		// 直接传递商品ID，让详情页自己去获取完整信息
		uni.navigateTo({
			url: `/pages/goods-detail/goods-detail?productId=${product.id}`
		});
	}

	// 热门推荐点击
	const goToHotProductDetail = (item) => {
		// 直接传递商品ID，让详情页自己去获取完整信息
		console.log('点击热门商品:', item); // 调试日志
		uni.navigateTo({
			url: `/pages/goods-detail/goods-detail?productId=${item.id}`
		});
	};

	// 加载历史记录
	onMounted(() => {
		const history = uni.getStorageSync('searchHistory') || []
		historyKeywords.value = history
	})
</script>

<style scoped>
	.search-page {
		padding: 20rpx;
	}

	.search-bar-container {
		margin-bottom: 30rpx;
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
		display: flex;
		justify-content: space-between;
	}

	.clear-btn {
		color: #999;
		font-size: 24rpx;
		font-weight: normal;
	}

	.hot-tags,
	.history-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.tag {
		background-color: #f5f5f5;
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
	}

	.search-results {
		margin-bottom: 40rpx;
	}

	.result-item {
		padding: 20rpx;
		border-bottom: 1rpx solid #f1f1f2;
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.product-image {
		width: 120rpx;
		height: 120rpx;
		border-radius: 8rpx;
	}

	.product-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 10rpx;
	}

	.result-name {
		font-size: 28rpx;
		font-weight: bold;
	}

	.result-description {
		font-size: 24rpx;
		color: #999;
	}

	.result-price {
		color: #e64340;
		font-weight: bold;
		font-size: 28rpx;
	}

	.no-results {
		padding: 40rpx;
		text-align: center;
		font-size: 28rpx;
		color: #999;
	}
</style>