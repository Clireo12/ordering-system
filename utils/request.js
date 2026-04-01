class Request {
	constructor() {
		this.baseUrl = 'http://localhost:8088';
	}

	async _request(url, method, data = {}, config = {}) {
		const requestUrl = this.baseUrl + url;

		try {
			const res = await uni.request({
				url: requestUrl,
				method,
				data,
				header: {
					'Content-Type': 'application/json',
					...config.header
				},
				timeout: 8000
			});

			// 检查HTTP状态码
			if (res.statusCode && res.statusCode >= 400) {
				throw new Error(`请求失败: ${res.statusCode}`);
			}

			// 返回的业务数据
			const responseData = res.data;

			if (!responseData) {
				throw new Error('响应数据为空');
			}

			return responseData;

		} catch (error) {
			console.error('请求错误:', {
				url,
				method,
				error: error.message
			});
			throw error;
		}
	}

	get(url, data = {}, config = {}) {
		return this._request(url, 'GET', data, config);
	}

	post(url, data = {}, config = {}) {
		return this._request(url, 'POST', data, config);
	}

	put(url, data = {}, config = {}) {
		return this._request(url, 'PUT', data, config);
	}

	delete(url, data = {}, config = {}) {
		return this._request(url, 'DELETE', data, config);
	}
}

export const request = new Request()