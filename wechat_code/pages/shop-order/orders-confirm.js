// pages/shop-order/submit.js
const {
    list,
    info,
    add,
    update,
    deleteData,
    session, page, createOrder
} = require("../../api/index")
const utils = require("../../utils/index.js")
Page({

    /**
     * 页面的初始数据
     */
    data: {
        address: "",
        orderGoods: "",
        types: "",
        userInfo: "",
        remark: "",
        baseURL: "",
        total: "",
    },

    /**
     * 生命周期函数--监听页面加载
     */
    async onLoad(options) {
        let table = wx.getStorageSync("nowTable");
        const userInfoRes = await session(table)
        var address = wx.getStorageSync('address');

        if (!address) {
            const res = await list("address", data)
            res?.data?.list?.map((v, index) => {
                if (v.isdefault == '是') {
                    address = [v]
                }
            })
        }
        if (options) {
            this.setData({
                types: options?.type,
                seat: options?.seat,
            })
        }
        this.setData({
            address,
            userInfo: userInfoRes.data,
            baseURL: wx.getStorageSync('baseURL') + "/",
            total: getApp().globalData.total
        })
        const data = {
            userid: this.data.userInfo.id
        }
        const orderGoods = wx.getStorageSync('orderGoods');
        this.setData({ orderGoods })
    },


    addressTap() {
        wx.navigateTo({
            url: '/pages/address/list',
        })
    },

    /**
     * 【重构后】统一事务下单
     * 原来：forEach async 多次调后端（容易失败且无事务）
     * 现在：一次性 POST 到 /orders/createOrder，后端在一个事务中完成
     */
    async payClick() {
        this.doCreateOrder(true);
    },

    /**
     * 稍后支付：创建未支付订单，不扣余额不扣库存
     * 后端会发一条 RabbitMQ 延迟消息，15 分钟未支付自动取消
     */
    async payLaterClick() {
        this.doCreateOrder(false);
    },

    async doCreateOrder(payNow) {
        let table = wx.getStorageSync("nowTable");
        if (this.data.seat != 1 && this.data.address.length == 0) {
            wx.showToast({
                title: '请选择地址',
                icon: "none"
            })
            return;
        }
        const orderGoods = this.data.orderGoods;
        if (!orderGoods || orderGoods.length === 0) {
            wx.showToast({ title: '没有可购买的商品', icon: 'none' });
            return;
        }

        try {
            wx.showLoading({ title: '正在下单...' });

            // 构造后端需要的请求体
            const goods = orderGoods.map(item => ({
                tablename: item.tablename,
                goodid: item.goodid,
                buynumber: item.buynumber,
                cartId: item.id // 购物车条目ID，下单后删除
            }));

            const body = {
                payNow: payNow,
                address: this.data.address[0]?.address,
                tel: this.data.address[0]?.phone,
                consignee: this.data.address[0]?.name,
                remark: this.data.remark,
                type: this.data.types ? this.data.types : "1",
                goods: goods
            };

            // 一次请求完成所有操作
            const res = await createOrder(body);

            wx.hideLoading();

            if (res.code === 0) {
                wx.showToast({
                    title: payNow ? '购买成功' : '订单已创建',
                    icon: "success"
                });
                setTimeout(function () {
                    wx.navigateBack({
                        delta: 1,
                        complete: () => {
                            wx.removeStorageSync('address');
                            wx.removeStorageSync('orderGoods');
                            const pages = getCurrentPages();
                            if (pages.length >= 2) {
                                const prePage = pages[pages.length - 2];
                                prePage.onLoad();
                            }
                        }
                    })
                }, 1500);
            } else {
                wx.showToast({
                    title: res.msg || '下单失败',
                    icon: 'none'
                });
            }
        } catch (e) {
            wx.hideLoading();
            wx.showToast({
                title: '网络异常，请重试',
                icon: 'none'
            });
        }
    },

    //沙箱支付
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.onLoad()
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})
