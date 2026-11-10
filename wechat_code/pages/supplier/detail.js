
const {
 update, add, page, list, detail, save,
session, mlist,info,lists,query
} = require("../../api/index.js")
const utils = require("../../utils/index.js")
Page({
data: {
title:'',
user:{},
pageIsAuth:false,
token:  '',
baseURL:'',
id: getApp().globalData.detailId,
userId:getApp().globalData.userInfo.id,
detailList: {},
payAuth:"",
picture:"",
goodname:"",

},
async    onLoad(option) {
let authobj={}
authobj['pageIsAuth']= wx.getStorageSync('isAuth')
this.setData(authobj)
let myid=option?.id?option?.id:getApp().globalData.detailId;
const user=wx.getStorageSync("userSession");
this.setData({
user,
id: myid,
token:   wx.getStorageSync('token'),
baseURL:   wx.getStorageSync('baseURL') + '/'
})
await this.handleUpdateData()
},
//立即预订
orderTap(){
if (this.data.activeSeat.length <= 0) {
wx.showToast({
title: '请选择需要预订的位置',
icon: 'none'
})
return
}
const userInfo=getApp().globalData.userInfo
const activeSeat = this.data.activeSeat.join(',') + ',' + this.data.detailList.selected
let data = {
orderid: this.createOrderId(),
tablename: 'supplier',
userid:userInfo.id,
goodid: this.data.detailList.id,
buynumber:  this.data.activeSeat.length,
total: 0,
discounttotal: 0,
address: this.data.activeSeat,
status: '已支付',
discountprice: this.data.detailList.vipprice,
}
if(this.data.detailList?.price){
data['status'] = '未支付'
data['price'] = this.data.detailList.price
data['total'] = parseFloat(data['price'] * activeSeat.length).toFixed(2)
wx.showModal({
title: '提示',
content: '是否预订选中座位',
complete: async (res) => {
if (res.confirm) {
wx.setStorageSync('orderGoods',[data])
wx.navigateTo({
url: "/pages/shop-order/orders-confirm?type=1&seat=1"
})
}
}
})
}

}
,
chapterClick(e) {
const  item = e.currentTarget.dataset.item;
const  user=this.data.user;

if(!this.data.userId){
        wx.showToast({
            title: '请先登录',
            icon:"none"
        })
        setTimeout(()=>{
            wx.navigateTo({
                url: `/pages/login/login`,
            })
        },1500)
        return
}
let that=this.data;
},
authTap() {
if (!this.data.token) {
wx.showToast({
title: '请先登陆',
icon: 'none'
})
return
}
},
async handleUpdateData() {
let nowTable = wx.getStorageSync("nowTable");
const res = await session(nowTable)
if(res.code==0){
getApp().globalData.userInfo=res?.data;
let userId = res?.data.id;
this.setData({
userId
})
}
// 更新当前页面的数据
var that = this;
getApp().globalData.detailId==null?getApp().globalData.detailId=wx.getStorageSync('detailId'):"";
const id = getApp().globalData.detailId;
if (id) {
const {data} = await detail("supplier",id)
this.setData({
payAuth:utils.isAuthFront('supplier','支付')
})





let objClo={}
objClo['detailList']=data;
this.setData(objClo)
if (!this.data.token) {
return
}

}
this.setData({
    });

},

onUnload: function () {
getApp().globalData.detailList = {}
console.log('页面被卸载，执行销毁操作');
},
async listUpdate(name) {
const predetailList = this.data.predetailList
const detailList = this.data.detailList
predetailList.shangpintupian = this.data.picture[0]
if (name == "thumbsupNumber") {
// 点赞
predetailList.thumbsupNumber = predetailList.thumbsupNumber + 1
detailList.thumbsupNumber = detailList.thumbsupNumber + 1
}
if (name == "cancelthumb") {
// 取消点赞
predetailList.thumbsupNumber = predetailList.thumbsupNumber - 1
detailList.thumbsupNumber = detailList.thumbsupNumber - 1
}
if (name == "crazilyNumber") {
predetailList.crazilyNumber = predetailList.crazilyNumber + 1
detailList.crazilyNumber = detailList.crazilyNumber + 1
}
if (name == "cancelCrazily") {
predetailList.crazilyNumber = predetailList?.crazilyNumber - 1
detailList.crazilyNumber = detailList.crazilyNumber - 1
}
if (name == 'cancelislike') {
predetailList.storeupNumber = predetailList.storeupNumber - 1
detailList.storeupNumber = detailList.storeupNumber - 1

}
if (name == "islike") {
predetailList.storeupNumber = predetailList.storeupNumber + 1
detailList.storeupNumber = detailList.storeupNumber + 1
}
this.setData({
detailList
})
const resUpdate = await update('supplier', predetailList)
if (resUpdate.code == 0) {
this.setData({
predetailList,
"detailList.crazilyNumber": predetailList.crazilyNumber
})

}


},
onPayTap()  {
if(!wx.getStorageSync('token')){
wx.showToast({
    title: '请先登录',
    icon: "none"
})
setTimeout(()=>{
    wx.navigateTo({
        url: `/pages/login/login`
    })
},1000)
return
}

const baseURL=  wx.getStorageSync('baseURL')
let data=this.data.detailList
data['buynumber']=1
wx.setStorageSync('payObject',data);
wx.setStorageSync('paytable','supplier');

wx.navigateTo({
url: "/pages/pay-confirm/pay-confirm?type=1"
})
},
onSHTap() {
this.selectComponent('#bottomFrame').showFrame();
},
canlreply() {
this.selectComponent('#bottomFrame').hideFrame();
},
async reply() {
const detailList = this.data.detailList
const res = await update("supplier", detailList)
if (res.code == 0) {
setTimeout(function () {
wx.showToast({
title: '回复成功',
icon: "none"
})
}, 1000)

this.handleUpdateData()
}
this.selectComponent('#bottomFrame').hideFrame();
},
async onShow() {
    this.handleUpdateData()
},
//免费试读

// 下载
download(e) {
let url = e.currentTarget.dataset.url
url = wx.getStorageSync('baseURL') + '/' + url;
wx.downloadFile({
url: url,
success: (res) => {
if (res.statusCode === 200) {
wx.showToast({
title: '下载成功',
icon: "none"
})

const filePath = res.tempFilePath
wx.openDocument({
filePath: filePath,
showMenu: true,
success: function (res) {
console.log('打开文档成功')
}
})
console.log('点击查看文件', filePath);
}
}
});
},
// 跨表
async onAcrossTap(e){

// (tableName,crossOptAudit,statusColumnName,tips,statusColumnValue)
const info = e.currentTarget.dataset.info;
const crossOptAudit=info.split(",")[1].replace(/'/g, '')

const token = wx.getStorageSync("token")
if (!this.data.token) {
wx.showToast({
title: '请登录后再操作',
icon:"none"
})
return
}

let pagetableName = info.split(",")[0]
const statusColumnName = info.split(",")[2]
const tips = info.split(",")[3]
let statusColumnValue = info.split(",")[3]
let type = info.split(",")[6];
type?type*1:type=1;
statusColumnValue = statusColumnValue.replace(/^'|'$/g, '');
wx.setStorageSync('crossTable','supplier');
wx.setStorageSync(`crossObj`, this.data.detailList);
wx.setStorageSync('statusColumnName', statusColumnName?.substring(1, statusColumnName?.length - 1))
wx.setStorageSync('tips', tips.substring(1, tips?.length - 1))
let keyname=statusColumnValue?.split('[')[0]
let statusColumnValueindex=statusColumnValue?.split('[')[1]?.replace(']','')
keyname=keyname?keyname.replace('CrossSelect',''):''

wx.setStorageSync('statusColumnValue',statusColumnValue)
if (statusColumnName != '' && !statusColumnName.startsWith("[")) {
var obj = this.data.detailList

for (var o in obj) {
if (statusColumnName.includes(o) && statusColumnValue==obj[o]) {
wx.showToast({
title: tips,
icon: "none"
})
return
}
}
}
getApp().globalData.detailId = this.data.detailList.id
getApp().globalData.detailList = this.data.detailList
wx.navigateTo({
url: `/pages/${pagetableName}/update-and-add?cross=true`,
})

},



})