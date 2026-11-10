
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
     account:'',
     password:'',
     name:'',
     contactPhone:'',
     address:'',

    registerContainerClass: "",

},

async onLoad() {








},
onUnload() {
},
async onShow() {






},







async  register(){
if (this.data.account == "") {
wx.showToast({
title: '请输入账号',
icon: "none"
})
return;
}
if (this.data.password == "") {
wx.showToast({
title: '请输入密码',
icon: "none"
})
return;
}
if (this.data.password2=="") {
wx.showToast({
title: '请输入确认密码',
icon: "none"
})
return;
}
if (this.data.password !== this.data.password2) {
wx.showToast({
title: '密码与确认密码不一致!!',
icon: "none"
})
return;
}
if (this.data.contactPhone == "") {
wx.showToast({
title: '请输入联系电话',
icon: "none"
})
return;
}
        if (!utils.validata("contactPhone",this.data.contactPhone)) {
        wx.showToast({
            title: '请输入有效联系电话',
            icon: 'none'
        })
        return;
    }








    const regex = new RegExp(wx.getStorageSync("baseURL"), "g");
  const resultObj={
        account:this.data.account,
        password:this.data.password,
        password2:this.data.password2,
        name:this.data.name,
        contactPhone:this.data.contactPhone,
        address:this.data.address,
  }
    const name="account"
    const password="password"
    const res = await register("supplier", name, this.data[name],password , this.data[password], resultObj)
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