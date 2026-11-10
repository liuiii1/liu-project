// pages/edit/edit.js

const {
detail,
option,
update,
add,
list,
follow,
faceMatch,
session,
rubbish,
levelOption,
baiduIdentify
} = require("../../api/index.js")

const des = require('../../utils/des.js')
const utils = require("../../utils/index.js")

Page({

/**
* 页面的初始数据
*/
data: {
imgIcon: "../../static/upload.png",
editStatus: false,
baseURL:'',
sessionReadArr:[],

detailList: null,
id: "",
cross:"",
ruleForm:{},
userid:getApp().globalData.userInfo.id,
userInfo:getApp().globalData.userInfo,
ro:{
},

refid:"",
content:"",
commentLimitReached: false, // 是否已达评价上限
isAppendReview: false, // 是否为追加评价
},


/**
* 生命周期函数--监听页面加载
*/
async onLoad(options) {
let userid
if (options?.id) {
this.setData({
editStatus: true
})

}
let nowTable = wx.getStorageSync("nowTable");
const res = await session(nowTable)
if(res.data.code==0){
getApp().globalData.userInfo=res?.data
userid = res?.data.id
this.setData({
userid
})

}
let baseURL =wx.getStorageSync('baseURL') + '/'
const id = getApp().globalData.detailId
this.setData({
refid:id,
baseURL
})
// 检查评论次数上限（每订单最多 2 次：首次 + 追加）
const orderId = getApp().globalData.commentOrderId
if (id && orderId && !options?.cross && !options?.id) {
    try {
        const checkRes = await list('discussagriculturalproductinformation', {
            page: 1,
            limit: 100,
            orderid: orderId
        })
        const reviewList = checkRes?.data?.list || []
        let reviewCount = reviewList.length
        if (reviewCount === 1 && reviewList[0].reply && String(reviewList[0].reply).trim().length > 0) {
            reviewCount = 2
        }
        if (reviewCount >= 2) {
            this.setData({ commentLimitReached: true })
            wx.showModal({
                title: '提示',
                content: '该订单已评价 2 次（首次评价 + 追加评价），已达评价上限',
                showCancel: false,
                confirmText: '知道了'
            })
        } else if (reviewCount === 1) {
            this.setData({ isAppendReview: true })
        }
    } catch (e) {}
}
//人脸识别
let  ro=this.data.ro
if(options?.cross){
var obj = wx.getStorageSync('crossObj');
let refobjempty={}
for (var o in obj){
if(o=='refid'){
    refobjempty["refid"]=obj[o]
ro.refid = true;
continue;
}else{
}

if(o=='userid'){
    refobjempty["userid"]=obj[o]
ro.userid = true;
continue;
}else{
}

if(o=='avatarurl'){
 refobjempty['avatarurl']=obj[o];
 refobjempty['avatarurlPath']=baseURL+ obj[o].split(",")[0];
ro.avatarurl = true;
continue;
}else{
}

if(o=='nickname'){
    refobjempty["nickname"]=obj[o]
ro.nickname = true;
continue;
}else{
}

if(o=='content'){
    refobjempty["content"]=obj[o]
ro.content = true;
continue;
}else{
}

if(o=='reply'){
    refobjempty["reply"]=obj[o]
ro.reply = true;
continue;
}else{
}

}

let  statusColumnName=wx.getStorageSync('statusColumnName');
statusColumnName=statusColumnName.replace('[',"").replace(']',"");
this.setData({
ro,
cross:options?.cross,
statusColumnName
})
this.setData(refobjempty)
}
if(id && !options?.cross){
// 如果上一级页面传递了id，获取改id数据信息
const   data=getApp().globalData.detailList
}else {
this.setData({
})
}
// ss读取
let sessionReadArr=[]
this.setData({
cross:options?.cross,
ro,
id,
})

if (wx.getStorageSync('raffleType') ) {
 wx.removeStorageSync('raffleType')
 setTimeout(() => {
  this.submit()
 }, 300)
}






},
getUUID () {
return new Date().getTime();
},
onUnload: function () {
getApp().globalData.editorContent = ''
},
onShow() {
},
 commentScore(e){
 if(this.data?.commentScore){
  this.setData({
   commentScore:e.detail
  })
 }
 },


























uploadavatarurl() {
wx.chooseImage({
count: 1,
sizeType: ['compressed'],
sourceType: ['album', 'camera'],
success: async (res) => {
const tempFilePaths = res.tempFilePaths;
// 本地临时图片的路径
this.setData({
 avatarurlPath: tempFilePaths[0]
})
let _this = this;
// 上传网络图片
const baseURL = wx.getStorageSync('baseURL')
wx.uploadFile({
url: `${baseURL}/file/upload`,
filePath: res.tempFilePaths[0],
name: 'file',
header: {
 'Token': wx.getStorageSync('token')
},
success: (uploadFileRes) => {
 let result = JSON.parse(uploadFileRes.data);
// result.file是上传成功为网络图片的名称
 if (result.code == 0) {
  this.setData({
    avatarurl: 'file/' + result.file
  })
 } else {
  wx.showToast({
   title: result.msg,
   icon: 'none',
   duration: 2000
  });
 }
}
})

this.setData({
face: tempFilePaths[0]
});

}
})
},








contentInput(e){
this.setData({
content: e.detail.value // 每次输入变化时更新文本框的值
});},




contentInput(e){
this.setData({
content: e.detail.value // 每次输入变化时更新文本框的值
});

},
replyInput(e){
this.setData({
reply: e.detail.value // 每次输入变化时更新文本框的值
});},




replyInput(e){
this.setData({
reply: e.detail.value // 每次输入变化时更新文本框的值
});

},


async submit() {
if (this.data.commentLimitReached) {
    wx.showToast({ title: '已达评价上限（最多2次）', icon: 'none' })
    return
}
let that = this
var query = wx.createSelectorQuery();
// 在 Page 中的某个方法中调用
query.select('#content').boundingClientRect();
query.exec(function (res) {
//res就是 所有标签为v1的元素的信息 的数组
that.setData({
content: res[0]?.dataset?.info
})
})

const baseURL = wx.getStorageSync('baseURL') + "/"
const regex = new RegExp(baseURL, "g");
const obj={
refid: this.data. refid,
userid: this.data. userid,
orderid: getApp().globalData.commentOrderId || '',
avatarurl:getApp().globalData.userInfo.headportrait,
nickname: wx.getStorageSync('nickname'),
content: this.data. content,
reply: this.data. reply,
 content:getApp().globalData.editorContent,
}
const detailId= getApp().globalData.detailId
const tableName= `discussagriculturalproductinformation`
//跨表计算判断
var obj2;
var  ruleForm=obj
obj2 = ruleForm
this.data.refid==""? ruleForm['refid']= getApp().globalData.detailId:""
ruleForm['userid']=getApp().globalData.userInfo.id;
var userInfo=getApp().globalData.userInfo
const sessionReadArr=this.data.sessionReadArr
const phonePattern = /^\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}$/;
const mobilePattern = /^(?:\+?86)?1[3-9]\d{9}$/;
const emailPattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
const idPattern = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2]\d|3[0-1])\d{3}[\dxX]$/;
const urlPattern = /^(http|https):\/\/[\w\-]+(\.[\w\-]+)+[/#?]?.*$/;
















if(!getApp().globalData.editorContent){
wx.showToast({
 icon: "none",
 title: `评论内容不能为空`,
})
return
}








//更新跨表属性
var crossuserid;
var crossrefid;
var crossoptnum;
if(this.data.cross) {
wx.setStorageSync('crossCleanType', true);
var statusColumnName = wx.getStorageSync('statusColumnName');
var statusColumnValue = wx.getStorageSync('statusColumnValue');
if (statusColumnName != '') {
obj2 = wx.getStorageSync('crossObj');
if (!statusColumnName.startsWith("[")) {
for (var o in obj2) {
if (statusColumnName==o){
obj2[o] = statusColumnValue;
}

}
var table = wx.getStorageSync('crossTable');

await update(table, obj2)
} else {

crossuserid =getApp().globalData.userInfo.id
crossrefid =  this.data.id
crossoptnum = wx.getStorageSync('statusColumnName');
crossoptnum = crossoptnum.replace(/\[/, "").replace(/\]/, "");
}
}
}
this.data.cross ? (crossrefid = obj2.id, crossuserid =getApp().globalData.userInfo.id) : ""
ruleForm?.crossrefid==undefined? ( ruleForm["crossrefid"] = obj2.id, ruleForm["crossuserid"] =getApp().globalData.userInfo.id ): "";
ruleForm?.shhf?ruleForm.shhf=this.data.shhf:''
if(crossrefid && crossuserid) {
ruleForm['crossuserid'] =getApp().globalData.userInfo.id;
ruleForm['crossrefid'] =obj2.id;
this.setData({
ruleForm
})
let params = {
page: 1,
limit: 10,
crossuserid: crossuserid,
crossrefid: crossrefid,
}
const tips = wx.getStorageSync('tips')
let corssRes = await list(`discussagriculturalproductinformation`, params)
crossoptnum = wx.getStorageSync('statusColumnName');
crossoptnum = crossoptnum.match(/\d+/g);
if (corssRes?.data?.total >= parseInt(crossoptnum)) {
wx.showToast({
icon: "none",
title: tips,
})
wx.removeStorageSync('crossCleanType');
return ;
}
else {
//跨表计算
if (ruleForm.id ) {
await update(`discussagriculturalproductinformation`, ruleForm)
}
else {
 this.data?.commentScore?ruleForm['score']= this.data.commentScore:'';
 let addRes = await add(`discussagriculturalproductinformation`, ruleForm)
 if (addRes.code != 0) {
     wx.showToast({ title: addRes.msg || '评论失败', icon: 'none' })
     return
 }
}
}
}
else {
//跨表计算
if (ruleForm.id || this.data.editStatus) {
this.data.editStatus?ruleForm['id']= getApp().globalData.detailId:""
await update(`discussagriculturalproductinformation`, ruleForm)
}
else {
 this.data?.commentScore?ruleForm['score']= this.data.commentScore:'';
 let addRes = await add(`discussagriculturalproductinformation`, ruleForm)
 if (addRes.code != 0) {
     wx.showToast({ title: addRes.msg || '评论失败', icon: 'none' })
     return
 }
}
}
wx.setStorageSync('discussagriculturalproductinformationCleanType', true);
getApp().globalData.editorContent=''
wx.showToast({
title: '提交成功',
icon: "none"
})
const preId = getApp().globalData.detailId

if(table){
let res = await detail(table, preId)
if(res.code==0){
getApp().globalData.detailList = res.data
}

}
wx.navigateBack({
delta: 1,
complete: () => {
// 触发事件通知，传递需要更新的数据
const pages = getCurrentPages();
if (pages.length >= 2) {
const prePage = pages[pages.length - 2];
prePage.onLoad(); //
}
}
})

},
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