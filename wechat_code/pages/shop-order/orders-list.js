const {
list,
detail,
update,
info,
session,
confirmOrder,
} = require("../../api/index")
Page({
data: {
tabs: [],
activeTab: 0,
allList: [],
baseURL: "",
curList: "",
userInfo: "",
total:0,
page:1,
flag:false,
titles:['全部','已收货', '已取消', '已支付', '已退款'],
// 退款/退货理由弹窗
showReasonModal: false,
reasonModalTitle: '',
reasonModalPlaceholder: '',
reasonText: '',
reasonItem: null,   // 当前操作的订单
reasonType: ''       // 'refund' 或 'return'
},

async onLoad(options) {
const tabs = this.data.titles.map(item => ({
    title: item
}))
this.setData({
    tabs
})
    this.data.titles.forEach((item, index) => {
    if (options?.name == item) {
        this.setData({
            activeTab: index
        })
    }
})
let _that=this
_that.getData(options?.name)

},
onHide() {
wx.removeStorageSync('isAuth');
},
async onShow() {
let table = wx.getStorageSync("nowTable");
const res = await session(table)
this.setData({
    userInfo: res.data
})
getApp().globalData.userInfo = res.data
},
onUnload() {
},
async  getData(name){
const parame = {
    page: this.data.page,
    limit: 10,
    sort: 'addtime',
    order: "desc",
    userid:getApp().globalData.userInfo?.id || wx.getStorageSync('userId')
}
    if(this.data.titles.includes(name) && name!='全部'){
        parame['status']=name
    }
    const allListRes = await list('orders', parame)
    const result = this.data.allList.concat(allListRes?.data?.list || [])
    // 查询每个已收货订单的评价次数，满 2 次则隐藏评价按钮
    const userId = getApp().globalData.userInfo?.id || wx.getStorageSync('userId')
    const receivedOrders = result.filter(o => o.status === '已收货')
    if (receivedOrders.length > 0 && userId) {
        const commentChecks = receivedOrders.map(async (order) => {
            try {
                const res = await list('discussagriculturalproductinformation', {
                    page: 1,
                    limit: 10,
                    orderid: order.id,
                    userid: userId
                })
                const reviewList = res?.data?.list || []
                let count = reviewList.length
                if (count === 1 && reviewList[0].reply && String(reviewList[0].reply).trim().length > 0) {
                    count = 2
                }
                return { orderId: order.id, commentCount: count }
            } catch (e) {
                return { orderId: order.id, commentCount: 0 }
            }
        })
        const checkResults = await Promise.all(commentChecks)
        const countMap = {}
        checkResults.forEach(r => { countMap[r.orderId] = r.commentCount })
        result.forEach(order => {
            if (countMap[order.id] !== undefined) {
                order.commentCount = countMap[order.id]
                order.canComment = countMap[order.id] < 2
            } else {
                order.canComment = true
            }
        })
    }
    this.setData({
        allList: result,
        baseURL: wx.getStorageSync('baseURL') + "/",
        curList: result,
        total: allListRes.data.total,
    })
},
onReachBottom() {

if (this.data.total>this.data.curList.length) {
this.setData({
page:this.data.page+1
})
this.getData(this.data.tabs[this.data.activeTab].title); // 疯狂的请求的方法
}
},
//兑换
exchange(e){
var _that=this
const item = e.currentTarget.dataset.item;
wx.showModal({
    title: '提示',
    content: '是否兑换',
    success: async function(res) {
        if (res.confirm) {
            item.status = '已完成';
            // 设置订单状态为已完成
            let res =  await update('orders', item)
            if (res.code == 0) {
                _that.getData()
                wx.showToast({
                    title: '兑换成功',
                    icon:'none'
                })
                this.onLoad()
                this.onTabCLick(this.data.activeTab)

            }
        }
    }
});
},
logisticsTap(e) {
const item = e.currentTarget.dataset.item;
wx.setStorageSync('logistics', item)
wx.navigateTo({
    url: '/pages/shop-order/logistics',
})
},
// 去评价
orderDiscuss(e) {
const item = e.currentTarget.dataset.item;
getApp().globalData.detailId=item.goodid
getApp().globalData.commentOrderId=item.id
wx.navigateTo({
    url: `/pages/discuss${item.tablename}/update-and-add`,
})

},
// 退款 — 先弹窗填理由
refundPriceClick(e) {
    const item = e.currentTarget.dataset.item;
    this.setData({
        showReasonModal: true,
        reasonModalTitle: '申请退款',
        reasonModalPlaceholder: '请填写退款理由（如：商品质量问题、发错货等）',
        reasonText: '',
        reasonItem: item,
        reasonType: 'refund'
    });
},
// 退货 — 先弹窗填理由
returnGood(e) {
    const item = e.currentTarget.dataset.item;
    this.setData({
        showReasonModal: true,
        reasonModalTitle: '申请退货',
        reasonModalPlaceholder: '请填写退货理由（如：与描述不符、不想要了等）',
        reasonText: '',
        reasonItem: item,
        reasonType: 'return'
    });
},
// 弹窗：输入
onReasonInput(e) {
    this.setData({ reasonText: e.detail.value });
},
// 弹窗：关闭
closeReasonModal() {
    this.setData({ showReasonModal: false, reasonItem: null, reasonText: '' });
},
stopBubble() {},
// 弹窗：提交
async submitReason() {
    const reasonText = (this.data.reasonText || '').trim();
    if (!reasonText) {
        wx.showToast({ title: '请填写理由', icon: 'none' });
        return;
    }
    if (reasonText.length < 2) {
        wx.showToast({ title: '理由至少2个字', icon: 'none' });
        return;
    }
    const item = this.data.reasonItem;
    const type = this.data.reasonType;
    wx.showLoading({ title: '提交中...' });
    item.sfsh = '待审核';
    item.shhf = `用户申请${type === 'refund' ? '退款' : '退货'}：${reasonText}`;
    const res = await update('orders', item);
    wx.hideLoading();
    if (res.code == 0) {
        this.setData({ showReasonModal: false, reasonItem: null, reasonText: '' });
        wx.showToast({ title: '申请已提交', icon: 'none' });
        this.onLoad();
        this.onTabCLick(this.data.activeTab);
    }
},
cancelGroup(e) {
const order = e.currentTarget.dataset.item
wx.showModal({
    title: '提示',
    content: '是否取消拼单',
    complete: async (res) => {
        if (res.confirm) {
            const res = await info(order.tablename, order.goodid)
            let goodDetail = res.data;
            goodDetail.curpeople = goodDetail.curpeople - 1;
            const table = wx.getStorageSync('nowTable')
            await update(order.tablename, goodDetail)
            order.status = '已退款';
            // 设置订单状态为取消
            await update('orders', order)
            // 重新加载数据
            if (order.type == '1', order.type == '3') {
                // 退回用户余额
                const money = Number(this.data.userInfo.money) + Number(order.total);
                // 退回用户余额
                this.setData({
                    'userInfo.money': money
                })
            } else if (order.type == '2') {
                // 退回用户积分
                const jf = Number(this.data.userInfo.jf) + Number(order.total);
                this.setData({
                    'userInfo.jf': jf
                })
            }
            const resUser = await update(table, this.data.userInfo)
            if (resUser.code == 0) {
                this.onLoad()
                this.onTabCLick(this.data.activeTab)
            }
        }
    }
})

},

async onTabCLick(e) {
let index
if (typeof e === 'number') {
    index = e
} else if (e && e.currentTarget && e.currentTarget.dataset && e.currentTarget.dataset.index !== undefined) {
    index = Number(e.currentTarget.dataset.index)
} else {
    index = 0
}
const parame = {
    page: 1,
    limit: 10,
    sort: 'addtime',
    order: "desc",
    userid:getApp().globalData.userInfo?.id || wx.getStorageSync('userId')
}
if(index>0) {
    parame["status"] = this.data.tabs[index]?.title;
}
const res = await list('orders', parame)
let dataList = (res?.data?.list || [])
// 统计评论数
try {
const commentRes = await list('discussagriculturalproductinformation', {
    page: 1, limit: 100,
    userid: getApp().globalData.userInfo?.id || wx.getStorageSync('userId')
})
const commentMap = {}
;(commentRes?.data?.list || []).forEach(c => {
    if (!commentMap[c.refid]) commentMap[c.refid] = 0
    commentMap[c.refid] += 1
    if (c.reply) commentMap[c.refid] += 1
})
dataList.forEach(item => {
    item.commentCount = commentMap[item.goodid] || 0
})
} catch(e) {
dataList.forEach(item => {
    item.commentCount = 0
})
}
this.setData({
    activeTab: index,
    allList: dataList,
    curList: dataList
});
},
//评价

// 确认收货（已支付 → 已收货）
confirmReceive(e) {
const item = e.currentTarget.dataset.item;
wx.showModal({
    title: '提示',
    content: '确认已收到货?',
    success: async (res) => {
        if (res.confirm) {
            wx.showLoading({ title: '处理中...' })
            try {
                const result = await confirmOrder({ id: item.id })
                wx.hideLoading()
                if (result.code == 0) {
                    wx.showToast({ title: '确认收货成功', icon: 'success' })
                    // 自动跳转到"已收货"tab
                    const targetIndex = this.data.tabs.findIndex(t => t.title === '已收货')
                    this.setData({ allList: [], page: 1 })
                    this.onTabCLick(targetIndex >= 0 ? targetIndex : 1)
                } else {
                    wx.showToast({ title: result.msg || '操作失败', icon: 'none' })
                }
            } catch (err) {
                wx.hideLoading()
                wx.showToast({ title: '网络错误，请重试', icon: 'none' })
            }
        }
    }
})
},
})