const {
    update,
    session
} = require('../../api/index.js')
Page({

    /**
     * 页面的初始数据
     */
    data: {
        changeSuccess: false,
        money: 0,
        user: null,
        selectedAmount: null,
        amounts: [
            { value: 50, gift: '' },
            { value: 100, gift: '5' },
            { value: 200, gift: '15' },
            { value: 500, gift: '50' },
            { value: 1000, gift: '120' },
            { value: 2000, gift: '300' }
        ]
    },

    /**
     * 生命周期函数--监听页面加载
     */
    async onLoad(options) {
        let table = wx.getStorageSync("nowTable");
        let res = await session(table);
        const userData = res.data || {}
        if (userData.money !== undefined && userData.money !== null) {
            userData.money = Number(userData.money).toFixed(2)
        }
        this.setData({
            user: userData
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },
    back() {
        wx.navigateBack({ delta: 2 });
    },
    // 选择预设金额
    selectAmount(e) {
        const val = parseFloat(e.currentTarget.dataset.value);
        this.setData({
            selectedAmount: val,
            money: val
        });
    },
    // 自定义输入时取消选中
    onCustomInput(e) {
        const val = e.detail.value;
        this.setData({
            selectedAmount: parseFloat(val) || null,
            money: val
        });
    },
    async onResetPass() {
        let table = wx.getStorageSync("nowTable");
        if (this.data.money <= 0) {
            wx.showToast({
                title: '请输入正确的充值数目',
                icon: 'none'
            })
            return;
        }
        let money = parseFloat(this.data.user.money) + parseFloat(this.data.money);
        this.setData({
            'user.money': money.toFixed(2)
        })
        let res = await update(table, this.data.user)
        this.setData({
            changeSuccess: !this.data.changeSuccess
        })

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