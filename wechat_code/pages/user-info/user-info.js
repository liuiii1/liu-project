const dateUtils = require('../../utils/defautils')
const menuData=require('../../utils/menu.js')
const {
SendverificationCode,
option,
follow,wxbind,wxunbind
} = require('../../api/login.js')
const {
levelOption,
sheng,
update,
session
} = require('../../api/index.js')
const utils = require("../../utils/index.js")
Page({
data: {
code:'',
openid:'',
tableName:"",
    userInfo:'',
ro:{
},
userAccount:'',
userPassword:'',
userName:'',
headportrait:'../../static/upload.png',
tempPathheadportrait:'../../static/upload.png',
genderList:"男,女".split(','),
genderIndex:0,

phoneNumber:'',
money:'',
account:'',
password:'',
name:'',
contactPhone:'',
address:'',

registerContainerClass: "",
role:wx.getStorageSync("nowTable")

},

async onLoad() {

wx.login({
provider: 'weixin',
success: function (res) {
let code = res.code;
if (code) {
this.setData({
code
});
}
}.bind(this)
});
let tableName = wx.getStorageSync("nowTable");
const  userInfo  = getApp().globalData.userInfo
    this.setData({
        tableName,
        userInfo
    })
const   baseURL= wx.getStorageSync('baseURL')+"/"
    let userInfoObj={}
    userInfoObj["userAccount"]= userInfo.userAccount==null?"":userInfo.userAccount
    userInfoObj["userPassword"]= userInfo.userPassword==null?"":userInfo.userPassword
    userInfoObj["userName"]= userInfo.userName==null?"":userInfo.userName
userInfo['headportrait']=userInfo?.headportrait?.replace('upload','file')
this.setData({
headportrait:baseURL+userInfo.headportrait,
tempPathheadportrait:baseURL+userInfo.headportrait,
})
    userInfoObj["gender"]= userInfo.gender==null?"":userInfo.gender
this.setData({
phoneNumber: userInfo.phoneNumber,
})
    userInfoObj["money"]= userInfo.money==null?"":Number(userInfo.money).toFixed(2)
    userInfoObj["account"]= userInfo.account==null?"":userInfo.account
    userInfoObj["password"]= userInfo.password==null?"":userInfo.password
    userInfoObj["name"]= userInfo.name==null?"":userInfo.name
this.setData({
contactPhone: userInfo.contactPhone,
})
    userInfoObj["address"]= userInfo.address==null?"":userInfo.address
    userInfoObj['role']=wx.getStorageSync("nowTable");
    this.setData(
        userInfoObj
    )
//ss读取

},
async onShow() {
},
headportraitTap() {
wx.chooseImage({
count: 1,
sizeType: ['compressed'],
sourceType: ['album', 'camera'],
success: async (res) => {
const tempFilePaths = res.tempFilePaths;
// 本地临时图片的路径
this.setData({
tempPathheadportrait: tempFilePaths[0]
})
// 上传网络图片
const  baseURL=  wx.getStorageSync("baseURL")
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
        headportrait: 'file/' + result.file
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



}
})
},
genderChange(e) {
const selectedIndex = e.detail.value;
this.setData({
genderIndex: selectedIndex,
});
},
async sendCodeHandler() {
if (!this.data.canSendCode) {
return;
}
if (this.data.phoneNumber == "") {
wx.showToast({
title: '请输入电话号码',
icon: 'none'
})
return;
} else {
if (validatePhoneNumber(this.data.phoneNumber) == false) {
wx.showToast({
title: '请输入有效电话号码',
icon: 'none'
})
return;
} else {
const res = await SendverificationCode("${tableName}", 'sendsms','phoneNumber',  this.data.phoneNumber)
this.setData({
smscode: res.data
})
}
}
this.setData({
canSendCode: false,
});
let time = this.data.countdown;
let timer = setInterval(() => {
time--;
this.setData({
countdown: time, // 更新倒计时的时间
});
if (time <= 0) {
clearInterval(timer); // 倒计时结束，清除定时器
this.setData({
buttonText: '发送验证码',
canSendCode: true,
countdown: 60
});
}
}, 1000);

},
async sendCodeHandler() {
if (!this.data.canSendCode) {
return;
}
if (this.data.contactPhone == "") {
wx.showToast({
title: '请输入联系电话',
icon: 'none'
})
return;
} else {
if (validatePhoneNumber(this.data.contactPhone) == false) {
wx.showToast({
title: '请输入有效联系电话',
icon: 'none'
})
return;
} else {
const res = await SendverificationCode("${tableName}", 'sendsms','contactPhone',  this.data.contactPhone)
this.setData({
smscode: res.data
})
}
}
this.setData({
canSendCode: false,
});
let time = this.data.countdown;
let timer = setInterval(() => {
time--;
this.setData({
countdown: time, // 更新倒计时的时间
});
if (time <= 0) {
clearInterval(timer); // 倒计时结束，清除定时器
this.setData({
buttonText: '发送验证码',
canSendCode: true,
countdown: 60
});
}
}, 1000);

},
quitTap(){
let saveBaseURL = wx.getStorageSync('baseURL')

wx.clearStorageSync();
wx.setStorageSync('baseURL', saveBaseURL)
wx.reLaunch({
url: "/pages/login/login"
});
},
async  saveTap(){


const baseURL = wx.getStorageSync('baseURL') + "/"
const regex = new RegExp(baseURL, "g");
const resultObj={
userAccount:this.data.userAccount,
userPassword:this.data.userPassword,
userName:this.data.userName,
headportrait:this.data.headportrait.replace(regex, ""),
gender:this.data.genderIndex ? this.data.genderList[this.data.genderIndex] : "",
phoneNumber:this.data.phoneNumber,
money:this.data.money,
account:this.data.account,
password:this.data.password,
password2:this.data.password2,
name:this.data.name,
contactPhone:this.data.contactPhone,
address:this.data.address,
id:getApp().globalData.userInfo.id

}
const role=wx.getStorageSync('nowTable')
const res = await update(role, resultObj)
if (res.code == 0) {
const userInfoRes = await session(role)
if (userInfoRes.code == 0) {
getApp().globalData.userInfo = userInfoRes.data
wx.reLaunch({
url: '/pages/center/center',
})
}
} else {
wx.showToast({
title: res.msg,
icon: "none"
})
}

},
async   getSession(){
let res = await session(this.data.role)
getApp().globalData.userInfo=res.data
this.setData({
openid:res.data.openid
})
}
});