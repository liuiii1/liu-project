const GET = 'get';
const POST = 'post';
const baseURL = "http://localhost:8080/project"
wx.setStorageSync('baseURL', baseURL)

// 防重入：多个请求同时 401 时只 reLaunch 一次
let isRedirecting = false;

module.exports = function (options) {
    return new Promise(function (resolve, reject) {
        let header = {
            'content-type': 'application/json'
        };
        const token = wx.getStorageSync('token')
        token ? header['Token'] = token : ''
        wx.request({
            url: baseURL + options.url,
            method: options.method,
            data: options.method === POST ? JSON.stringify(options.data) : options.data,
            header: header,
            success(res) {
                const pages = getCurrentPages();
                const currentPage = pages[pages.length - 1];
                const currentPath = currentPage?.route;

                if (res.data.code == 401 && !currentPath?.includes('forget') && !currentPath?.includes('login')) {
                    // 只清 token，保留其他本地数据（baseURL、userid 等）
                    wx.removeStorageSync('token')
                    wx.removeStorageSync('userId')
                    wx.removeStorageSync('userSession')

                    if (!isRedirecting) {
                        isRedirecting = true
                        wx.reLaunch({
                            url: '/pages/login/login',
                            complete: function () {
                                wx.showToast({
                                    title: '登录过期，请重新登录',
                                    icon: 'error',
                                    duration: 2000
                                })
                                isRedirecting = false
                            }
                        })
                    }
                    reject(res.data)
                    return
                }

                // 请求正常
                resolve(res.data)
            },
            fail: function (err) {
                reject(err)
                wx.showToast({
                    title: '网络繁忙，请稍后重试~',
                    icon: 'none'
                })
            },
            complete: function () {
                // 配对使用（loading消失）
            }
        })
    })
}
