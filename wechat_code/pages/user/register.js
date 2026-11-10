
const dateUtils = require('../../utils/defautils')
const utils = require("../../utils/index.js")
const {
SendverificationCode,
register,
option,
    smscode,
follow
} = require('../../api/login.js')
const {
    levelOption,
    sheng,
} = require('../../api/index.js')
Page({
data: {
     userAccount:'',
     userPassword:'',
     userName:'',
    headportrait:'',
    tempPathheadportrait:'../../static/upload.png',
genderList:"男,女".split(','),
genderIndex:0,
     phoneNumber:'',
     money:'',

    registerContainerClass: "",

},

async onLoad() {










},
onUnload() {
},
async onShow() {





    this.setData({
            genderList:  "男,女".split(',')
    })





},







userAccountInput(e) {
this.setData({
    userAccount: e.detail.value
})
},

userPasswordInput(e) {
this.setData({
    userPassword: e.detail.value
})
},

userNameInput(e) {
this.setData({
    userName: e.detail.value
})
},


headportraitTap() {
wx.chooseImage({
count: 1,
sizeType: ['compressed'],
sourceType: ['album', 'camera'],
success: async (res) => {
const tempFilePaths = res.tempFilePaths;
let tempPathheadportrait= tempFilePaths[0]
// 本地临时图片的路径
this.setData({
    tempPathheadportrait,
})
// 上传网络图片
const  baseURL= wx.getStorageSync('baseURL')
    if(baseURL){
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
if (!utils.validata("phoneNumber",this.data.phoneNumber)) {
    wx.showToast({
        title: '请输入有效电话号码',
        icon: 'none'
    })
    return;
} else {
    const res = await SendverificationCode("user", 'sendsms','phoneNumber',  this.data.phoneNumber)
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

moneyInput(e) {
this.setData({
    money: e.detail.value
})
},

async  register(){
if (this.data.userAccount == "") {
wx.showToast({
title: '请输入用户账号',
icon: "none"
})
return;
}
if (this.data.userPassword == "") {
wx.showToast({
title: '请输入用户密码',
icon: "none"
})
return;
}
if (this.data.userPassword2=="") {
wx.showToast({
title: '请输入确认用户密码',
icon: "none"
})
return;
}
if (this.data.userPassword !== this.data.userPassword2) {
wx.showToast({
title: '用户密码与确认用户密码不一致!!',
icon: "none"
})
return;
}
if (this.data.phoneNumber == "") {
wx.showToast({
title: '请输入电话号码',
icon: "none"
})
return;
}
        if (!utils.validata("phoneNumber",this.data.phoneNumber)) {
        wx.showToast({
            title: '请输入有效电话号码',
            icon: 'none'
        })
        return;
    }










    const regex = new RegExp(wx.getStorageSync("baseURL"), "g");
  const resultObj={
        userAccount:this.data.userAccount,
        userPassword:this.data.userPassword,
        userName:this.data.userName,
        headportrait:this.data.headportrait.replace(regex, ""),
        gender: this.data.genderList?.length ? this.data.genderList[this.data.genderIndex] : "",
        phoneNumber:this.data.phoneNumber,
        money:this.data.money,
  }
    const name="userAccount"
    const password="userPassword"
    const res = await register("user", name, this.data[name],password , this.data[password], resultObj)
if (res.code == 0) {
wx.navigateTo({
url: '../login/login',
})
} else {
wx.showToast({
title: res.msg,
icon: "none"
})
}

}



});