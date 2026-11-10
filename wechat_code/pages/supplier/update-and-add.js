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

account:"",
name:"",
contactPhone:"",
address:"",
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
//人脸识别
let  ro=this.data.ro
if(options?.cross){
var obj = wx.getStorageSync('crossObj');
let refobjempty={}
for (var o in obj){
if(o=='account'){
    refobjempty["account"]=obj[o]
ro.account = true;
continue;
}else{
}

if(o=='password'){
    refobjempty["password"]=obj[o]
ro.password = true;
continue;
}else{
}

if(o=='name'){
    refobjempty["name"]=obj[o]
ro.name = true;
continue;
}else{
}

if(o=='contactPhone'){
    refobjempty["contactPhone"]=obj[o]
ro.contactPhone = true;
continue;
}else{
}

if(o=='address'){
    refobjempty["address"]=obj[o]
ro.address = true;
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
const url = wx.getStorageSync("baseURL") + "/"
const detailList = data
let  objtemp= {
detailList,
 account: data.account,
 password: data.password,
 name: data.name,
 contactPhone: data.contactPhone,
 address: data.address,
}
this.setData(objtemp);

//ss读取
let h = this.data
let c = this.data
let d1 = this.data
let d2 = this.data
this.setData({
});
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








































async submit() {
let that = this
var query = wx.createSelectorQuery();

const baseURL = wx.getStorageSync('baseURL') + "/"
const regex = new RegExp(baseURL, "g");
const obj={
account: this.data. account,
name: this.data. name,
contactPhone: this.data. contactPhone,
address: this.data. address,
}
const detailId= getApp().globalData.detailId
const tableName= `supplier`
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
if(!this.data.account ){
wx.showToast({
icon: "none",
title: `账号不能为空`,
})
return
}




if(!this.data.password ){
wx.showToast({
icon: "none",
title: `密码不能为空`,
})
return
}




if(!this.data.name ){
wx.showToast({
icon: "none",
title: `名称不能为空`,
})
return
}





const valuecontactPhone = this.data.contactPhone;

if (!mobilePattern.test(valuecontactPhone)) {
wx.showToast({
icon: "none",
title: `联系电话应输入手机格式`,
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
let corssRes = await list(`supplier`, params)
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
await update(`supplier`, ruleForm)
}
else {
 this.data?.commentScore?ruleForm['score']= this.data.commentScore:'';
 await add(`supplier`, ruleForm)
}
}
}
else {
//跨表计算
if (ruleForm.id || this.data.editStatus) {
this.data.editStatus?ruleForm['id']= getApp().globalData.detailId:""
await update(`supplier`, ruleForm)
}
else {
 this.data?.commentScore?ruleForm['score']= this.data.commentScore:'';
await add(`supplier`, ruleForm)
}
}
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
if (pages.length >= 1) {
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