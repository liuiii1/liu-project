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

productname:"",
productcategoryList:"${column.customize}".split(','),
productcategoryIndex:0,
image:"",
specification:"",
placeofproduction:"",
account:"",
name:"",
storeupNumber:0,
discussNumber:0,
price:0,
onelimittimes:0,
alllimittimes:0,
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
const productcategoryres = await option('productcategory/productcategory')
productcategoryres.data.unshift('请选择产品分类')
this.setData({
productcategoryList: productcategoryres.data
})
let  ro=this.data.ro
if(options?.cross){
var obj = wx.getStorageSync('crossObj');
let refobjempty={}
for (var o in obj){
if(o=='productname'){
    refobjempty["productname"]=obj[o]
ro.productname = true;
continue;
}else{
}

if(o=='productcategory'){
    refobjempty["productcategory"]=obj[o]
ro.productcategory = true;
continue;
}else{
}

if(o=='image'){
 refobjempty['image']=obj[o];
 refobjempty['imagePath']=baseURL+ obj[o].split(",")[0];
ro.image = true;
continue;
}else{
}

if(o=='specification'){
    refobjempty["specification"]=obj[o]
ro.specification = true;
continue;
}else{
}

if(o=='placeofproduction'){
    refobjempty["placeofproduction"]=obj[o]
ro.placeofproduction = true;
continue;
}else{
}

if(o=='productdetails'){
 getApp().globalData.editorContent=obj[o]
ro.productdetails = true;
continue;
}else{
}

if(o=='account'){
    refobjempty["account"]=obj[o]
ro.account = true;
continue;
}else{
}

if(o=='name'){
    refobjempty["name"]=obj[o]
ro.name = true;
continue;
}else{
}

if(o=='storeupNumber'){
    refobjempty["storeupNumber"]=obj[o]
ro.storeupNumber = true;
continue;
}else{
}

if(o=='discussNumber'){
    refobjempty["discussNumber"]=obj[o]
ro.discussNumber = true;
continue;
}else{
}

if(o=='price'){
    refobjempty["price"]=obj[o]
ro.price = true;
continue;
}else{
}

if(o=='onelimittimes'){
    refobjempty["onelimittimes"]=obj[o]
ro.onelimittimes = true;
continue;
}else{
}

if(o=='alllimittimes'){
    refobjempty["alllimittimes"]=obj[o]
ro.alllimittimes = true;
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
productcategoryres.data.map((item, index) => {
if (item == data.productcategory) {
 this.setData({
productcategoryIndex: index,
productcategory: item
 })
}else if (this.data.productcategoryList.length == 1) {
this.setData({
productcategoryIndex: 0,
productcategory: v
 })
}
})
const url = wx.getStorageSync("baseURL") + "/"
const detailList = data
let  objtemp= {
detailList,
 productname: data.productname,
 productcategory:this.data.productcategoryList[this.data.productcategoryIndex],
 image:data?.image?.split(',')[0],
 imagePath:baseURL+data?.image?.split(',')[0],
 specification: data.specification,
 placeofproduction: data.placeofproduction,
 productdetails: data.productdetails,
 account: data.account,
 name: data.name,
 storeupNumber: data.storeupNumber,
 discussNumber: data.discussNumber,
 price: data.price,
 onelimittimes: data.onelimittimes,
 alllimittimes: data.alllimittimes,
}
this.data.editStatus? getApp().globalData.editorContent=data.productdetails:""
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
if(getApp().globalData.userInfo.account){
let account= getApp().globalData.userInfo.account
ro.account=true
this.setData({
  account,
})
sessionReadArr.push('account')

}
if(getApp().globalData.userInfo.name){
let name= getApp().globalData.userInfo.name
ro.name=true
this.setData({
  name,
})
sessionReadArr.push('name')

}
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






//productcategory, 0
// 下拉变化
productcategoryChange(e) {
this.setData({
 productcategoryIndex:   e.detail.value,
 productcategory :this.data.productcategoryList[e.detail.value]
})
},





















uploadimage() {
wx.chooseImage({
count: 1,
sizeType: ['compressed'],
sourceType: ['album', 'camera'],
success: async (res) => {
const tempFilePaths = res.tempFilePaths;
// 本地临时图片的路径
this.setData({
 imagePath: tempFilePaths[0]
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
    image: 'file/' + result.file
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














































async submit() {
let that = this
var query = wx.createSelectorQuery();

const baseURL = wx.getStorageSync('baseURL') + "/"
const regex = new RegExp(baseURL, "g");
const obj={
productname: this.data. productname,
productcategory:this.data.productcategory?this.data.productcategory:this.data.productcategoryList[this.data.productcategoryIndex],
image:this.data.image?.replace(regex, ""),
specification: this.data. specification,
placeofproduction: this.data. placeofproduction,
productdetails: getApp().globalData.editorContent,
account: this.data. account,
name: this.data. name,
storeupNumber: this.data. storeupNumber,
discussNumber: this.data. discussNumber,
price: this.data. price,
onelimittimes: this.data. onelimittimes,
alllimittimes: this.data. alllimittimes,
}
const detailId= getApp().globalData.detailId
const tableName= `agriculturalproductinformation`
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








if(this.data.productcategoryList[this.data.productcategoryIndex]==undefined && !this.data.productcategory ){
wx.showToast({
icon: "none",
title: `产品分类不能为空`,
})
return
}
















let productdetails=  getApp().globalData.editorContent
ruleForm['productdetails']=getApp().globalData.editorContent
if(productdetails==='' ){
wx.showToast({
icon: "none",
title: `产品详情不能为空`,
})
return
}









const valuestoreupNumber = this.data.storeupNumber;
const isIntegerstoreupNumber = /^[-+]?\d+$/.test(valuestoreupNumber);
if(!isIntegerstoreupNumber && this.data.storeupNumber){
wx.showToast({
icon: "none",
title: `收藏数应输入整数`,
})
return
}





const valuediscussNumber = this.data.discussNumber;
const isIntegerdiscussNumber = /^[-+]?\d+$/.test(valuediscussNumber);
if(!isIntegerdiscussNumber && this.data.discussNumber){
wx.showToast({
icon: "none",
title: `评论数应输入整数`,
})
return
}





const valueprice = this.data.price;
if(isNaN(valueprice)  && this.data.price){
wx.showToast({
icon: "none",
title: `价格应输入数字`,
})
return
}
 if(this.data.price==0){
  wx.showToast({
   icon: "none",
   title: `价格不能为零`,
  })
  return
 }




const valueonelimittimes = this.data.onelimittimes;
const isIntegeronelimittimes = /^[-+]?\d+$/.test(valueonelimittimes);
if(!isIntegeronelimittimes && this.data.onelimittimes){
wx.showToast({
icon: "none",
title: `单限应输入整数`,
})
return
}





const valuealllimittimes = this.data.alllimittimes;
const isIntegeralllimittimes = /^[-+]?\d+$/.test(valuealllimittimes);
if(!isIntegeralllimittimes && this.data.alllimittimes){
wx.showToast({
icon: "none",
title: `库存应输入整数`,
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
let corssRes = await list(`agriculturalproductinformation`, params)
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
await update(`agriculturalproductinformation`, ruleForm)
}
else {
 this.data?.commentScore?ruleForm['score']= this.data.commentScore:'';
 await add(`agriculturalproductinformation`, ruleForm)
}
}
}
else {
//跨表计算
if (ruleForm.id || this.data.editStatus) {
this.data.editStatus?ruleForm['id']= getApp().globalData.detailId:""
await update(`agriculturalproductinformation`, ruleForm)
}
else {
 this.data?.commentScore?ruleForm['score']= this.data.commentScore:'';
await add(`agriculturalproductinformation`, ruleForm)
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