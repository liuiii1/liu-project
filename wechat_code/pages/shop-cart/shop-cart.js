const {
    list,
    update,
    deleteData,info
} = require("../../api/index.js")
Page({

    /**
     * 页面的初始数据
     */
    data: {
        index: [],
        goodsNum: '',
        userInfo: {},
        hasUserInfo: false,
        canIUse: wx.canIUse('button.open-type.getUserInfo'),
        slideProductList: [],
        cartList: [],
        baseURL: "",
        allSelect: "circle",
        buynumber: 0,
        count: 0,
        lastX: 0,
        lastY: 0,
        text: "没有滑动",

    },

    change(e) {
        var that = this
        var index = e?.currentTarget.dataset.index
        var select = e?.currentTarget.dataset.select
        console.log('select', select, index);
        if (!e?.currentTarget) {
            return
        }
        console.log('index', index);
        if (select == "circle") {
            var stype = "success"
        } else {
            var stype = "circle"
        }
        var newList = that.data.slideProductList
        newList[index].select = stype

        let arrIndex = this.data.index
        arrIndex.includes(index) ? '' : arrIndex.push(index)
        that.setData({
            slideProductList: newList,
            index: arrIndex
        })

        that.countNum()
        that.count()
    },
    async addtion(e) {
        var that = this
        var index = e?.currentTarget.dataset.index
        var buynumber = e?.currentTarget.dataset.buynumber
        const item=this.data.slideProductList[index]
        const {data}=await info(item.tablename,item.goodid)
        if(data.onelimittimes && parseInt(item.buynumber) + 1 >data.onelimittimes){
            wx.showToast({
                title: '每人单次只能购买'+data.onelimittimes+'件',
                icon:'none',
                duration: 1000
            })
            return
        }
        if (data.alllimittimes && parseInt(item.buynumber) + 1 > data.alllimittimes) {
            wx.showToast({
                title: '库存不足',
                icon:'none',
                duration: 1000
            });
            return
        }
        //默认99件
                    buynumber++
                var newList = that.data.slideProductList
        newList[index].buynumber = buynumber
        that.setData({
            goodsNum: buynumber,
            slideProductList: newList
        })
        // 修复：加法后也需要持久化到后端（原来漏掉了）
        const updatedItem = newList[index];
        delete updatedItem.select;
        update('cart', updatedItem);
        that.countNum()
        that.count()
    },
    inputNum: function (e) {
        var buynumber = e.detail.value;
        this.setData({
            goodsNum: buynumber
        })
    },
    // 点击删除按钮（直接删除当前项）
    async deleteGoods(e) {
        var id = e.currentTarget.dataset.id;
        if (!id) return;
        wx.showModal({
            title: '移除购物车',
            content: '确定删除该商品？',
            complete: async (res) => {
                if (res.confirm) {
                    await deleteData('cart', [id]);
                    this.onLoad();
                    wx.showToast({ title: '已删除', icon: 'none' });
                }
            }
        });
    },
    //减法
    subtraction(e) {
        var that = this
        const item = e.currentTarget.dataset.item
        var index = e.currentTarget.dataset.index
        var buynumber = item.buynumber
        var newList = that.data.slideProductList
        if (buynumber <= 1) { //当数量为1件时，再次点击移除该商品
            wx.showModal({
                title: '移除购物车？',
                content: '',
                complete: async (res) => {
                    if (res.confirm) {
                        await deleteData('cart', [item.id])
                        this.onLoad()
                    }
                }
            })

            return;
        } else {
            buynumber--
            newList[index].buynumber = buynumber
        }
        that.setData({
            goodsNum: buynumber,
            slideProductList: newList
        })
        // 修复：原来硬编码 data[0]，现在只更新当前被修改的那一条
        const updatedItem = newList[index];
        delete updatedItem.select;
        update('cart', updatedItem);
        that.countNum()
        that.count()
    },
    //全选
    allSelect: function (e) {
        var that = this
        var allSelect = e.currentTarget.dataset.select //先判断是否选中
        var newList = that.data.slideProductList
        console.log(newList)
        if (allSelect == "circle") {
            for (var i = 0; i < newList.length; i++) {
                newList[i].select = "success"
            }
            var select = "success"
        } else {
            for (var i = 0; i < newList.length; i++) {
                newList[i].select = "circle"
            }
            var select = "circle"
        }
        console.log('newList', newList);
        that.setData({
            slideProductList: newList,
            allSelect: select
        })
        that.countNum()
        that.count()
    },

    countNum: function () { //计算数量
        var that = this

        var newList = that.data.slideProductList
        var allNum = 0
        for (var i = 0; i < newList.length; i++) {
            if (newList[i].select == "success") {
                allNum += parseInt(newList[i].buynumber)
            }
        }
        let allSelect = newList.length == allNum ? 'success' : 'circle'
        that.setData({
            allSelect,
            buynumber: allNum
        })
    },

    count: function () { //计算金额方法
        var that = this
        var newList = that.data.slideProductList
        var newCount = 0
        for (var i = 0; i < newList.length; i++) {
            if (newList[i].select == "success") {
                newCount += newList[i].buynumber * newList[i].price
            }
        }
        console.log('newCount', newCount);
        that.setData({
            count: newCount.toFixed(2)
        })
    },
    settlementTap() {
        if (this.data.buynumber <= 0) {
            wx.showToast({
                title: '请选择商品',
                icon: 'error'
            })
            return
        }
        let myarr = []
        let indexarr = this.data.index
        console.log('  this.data.slideProductList', this.data.slideProductList);
        this.data.slideProductList.map((v, index) => {
            if (v.select == "success") {
                myarr.push(v)
            }
        })
        console.log('myarr', myarr);
        wx.setStorageSync('orderGoods', myarr)
        getApp().globalData.total = this.data.count
        wx.navigateTo({
            url: '/pages/shop-order/orders-confirm?type=1',
        })
    },



    /**
     * 生命周期函数--监听页面加载
     */
    async onLoad(options) {
        const obj = {
            page: 1,
            limit: 20,
            userid: wx.getStorageSync('userid')
        }
        const res = await list('cart', obj)
        const baseURL = wx.getStorageSync('baseURL') + '/'
        this.setData({
            cartList: res.data.list,
        })
        // res.data.list['select'] = 'circle'
        const slideProductList = res.data.list.map(v => {
            v['select'] = 'circle'
            return v
        })
        // select: "circle",
        this.setData({
            slideProductList,
            baseURL
        })
        console.log("slideProductList", this.data.slideProductList);
        var width = wx.getSystemInfoSync().windowWidth
        var height = wx.getSystemInfoSync().windowHeight
        height = height - 35 - 53;
        this.setData({
            height: height,
            buynumber: 0
        })
        this.countNum()
        this.count()
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

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
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})