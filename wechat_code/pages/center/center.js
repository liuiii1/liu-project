const utils = require('../../utils/index.js')
const menu = require('../../utils/menu.js')
const {
    session,encrypt,update,
    page,page2,add,deleteData,mlist
} = require("../../api/index")
Page({


    /**
     * 页面的初始数据
     */
    data: {
avatarurl:"",
recordListIndex:'i0',
// 聊天
recordList: [],
nowfid: 0,
nowfpic: '',
nowname: '',
nowtablename: '',
chatList: [],
chatForm: {
content: ''
},
chatTimer: null,
hasChatList: [
],
friendList: [],
mypic:wx.getStorageSync('avatarurl'),
passWorded:"",
newPassword:'',
confirmPassword:'',
nickname:'',
token: '',
backMenuList: "",
userInfo:{},
secondIndex: false,
baseURL: wx.getStorageSync('baseURL') + '/',
    },

    /**
     * 生命周期函数--监听页面加载
     */

onLoad(options) {
this.setData({
    nickname:  wx.getStorageSync('nickname'),
    avatarurl:wx.getStorageSync('avatarurl')
})
        this.getData()

},
    onShow() {
        this.onLoad()
        this.setData({
            nickname:  wx.getStorageSync('nickname'),
            avatarurl:wx.getStorageSync('avatarurl'),
    mypic:wx.getStorageSync('avatarurl')
        })
    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {
    },

    async getFriendList() {
        let res = await page('chatfriend',{
            uid: getApp().globalData.userInfo.id
        })
        res.data.list.sort(function(a, b) {
            return (a.type - b.type)
        });
        let arr = []
        for (let x in res.data.list) {
            if (res.data.list[x].type != 2) {
                arr.push(res.data.list[x])
            }
        }
        this.setData({
            friendList: arr
        })
        this.selectComponent('#firendFrame').showFrame();
    },
    async confirmFriend(e) {
        const item = e.currentTarget.dataset.item;
        let _this = this.data;
        let _that = this;
        wx.showModal({
            title: '提示',
            content: '是否同意好友申请？',
            success: async function (obj) {
                if (obj.confirm) {
                    await add('chatfriend', {
                        uid: item.fid,
                        fid: _this.userInfo.id,
                        type: 1,
                        tablename: wx.getStorageSync("nowTable"),
                        name: wx.getStorageSync("nickname"),
                        picture: _this.mypic,
                    })
                    await add('chatfriend', {
                        fid: item.fid,
                        uid:  _this.userInfo.id,
                        type: 1,
                        tablename: item.tablename,
                        name: item.name,
                        picture: item.picture
                    })
                    await deleteData('chatfriend', [item.id])
                    _that.getFriendList()

                    wx.showToast({
                        title: '添加好友成功',
                        icon: 'none'
                    })
                }
            }
        })

    },
    cancelFirendFrame() {
        this.selectComponent('#firendFrame').hideFrame()
    },
    changeHasChat(){
        let table = wx.getStorageSync("nowTable")
        for(let x in this.data.hasChatList){
            if(this.data.hasChatList[x]==table){
                return true
            }
        }
        return false
    },
    async getRecordList(type=1) {
        let res = await page2('/chatfriend',{uid: this.data.userInfo.id, type: 2})
      let recordList=  res?.data?.list.map(v=>{
            if(v.content?.includes('file/')){
                v['isImg']=true
            }
            return v
        })
        this.setData({
            recordList
        })
        this.selectComponent('#recordListFrame').showFrame();
    },
    cancelMyChatShow(){
        this.getRecordList()
        clearTimeout(this.data.chatTimer)
        this.selectComponent('#myChatFrame').hideFrame();
    },
    async uploadSuccess() {
        let userInfo=getApp().globalData.userInfo
        clearTimeout(this.data.chatTimer)
        wx.chooseImage({
            count: 1,
            sizeType: ['compressed'],
            sourceType: ['album', 'camera'],
            success: async (res) => {
                let _this = this.data;
// 上传网络图片
                wx.uploadFile({
                    url: `${_this.baseURL}/file/upload`,
                    filePath: res.tempFilePaths[0],
                    name: 'file',
                    header: {
                        'Token': wx.getStorageSync('token')
                    },
                    success: async(uploadFileRes) => {
                        let _this=this.data;
                        let result = JSON.parse(uploadFileRes.data);
// result.file是上传成功为网络图片的名称
                        let res = await page('chatfriend', {
                            uid: userInfo.id,
                            fid: this.data.nowfid,
                        })
                        if(!res.data.list.length){
                            await page('chatfriend', {
                                uid: userInfo.id,
                                fid: this.data.nowfid,
                                name: wx.getStorageSync("nickname"),
                                picture: _this.mypic,
                                type: 2,
                                tablename:   wx.getStorageSync("nowTable")
                            }
                            )
                            await page('chatfriend', {
                                fid: userInfo.id,
                                uid: _this.fid,
                                name: wx.getStorageSync("nickname"),
                                picture: _this.mpic,
                                type: 2,
                                tablename: wx.getStorageSync("nowTable")
                            })

                        }
                        await add('chatmessage', {
                            uid: userInfo.id,
                            fid: _this.nowfid,
                            content: 'file/' + result.file,
                            format: 2
                        })
                        _this.chatForm = {
                            content: ''
                        }
                        this.getChatList()
                    }
                })


            }
        })


    },
    async getChatList() {
        let res=     await mlist('chatmessage',{
            page: 1,
            limit: 1000,
            uid: this.data.userInfo.id,
            fid: this.data.nowfid
        })
        this.setData({chatList: res?.data?.list.map(v=>{
                if(v.format==2){
                    v.content=  v.content.replace('upload','file')
                }
                return v
            })})
        setTimeout(() => {
            this.setData({
                recordListIndex:'i'+(this.data.chatList.length-1)
            })
        }, 0)
        this.setData({
            chatTimer:setTimeout(() => {
                this.getChatList()
            }, 5000)
        })


    },
    async chatClick(e) {
        const row = e.currentTarget.dataset.item;
        if (row.type == 0) {
            return
        }
        let obj={}
        obj['nowfid'] = row.fid;
        obj['nowfpic'] = row.picture;
        obj['nowname'] = row.name;
        obj['nowtablename'] = row.tablename;
        this.setData(obj)
        await this.getChatList()
        this.selectComponent('#myChatFrame').showFrame();
    },
    cancelrecordListFrame(){
        this.selectComponent('#recordListFrame').hideFrame();
    },

contentTap(e){
let chatForm=Object.assign(this.data.chatForm,{})
chatForm['content']=e.detail.value;
this.setData(chatForm)
},
    async addChat() {
        let userInfo=getApp().globalData.userInfo;
        clearTimeout(this.data.chatTimer)
        if(!this.data.chatForm.content){
            return
        }
        let res = await page('chatfriend', {
            uid: userInfo.id,
            fid: this.data.nowfid,
            type: 2
        })
        if(!res.data.list.length){
            await add('chatfriend', {
                uid: userInfo.id,
                fid: this.data.nowfid,
                name: this.data.nowname,
                picture: this.data.nowfpic,
                type: 2,
                tablename: 'yonghu'
            })
            await add('chatfriend', {
                fid: userInfo.id,
                uid:this.data.nowfid,
                name: wx.getStorageSync("nickname"),
                picture: this.data.mypic,
                type: 2,
                tablename: wx.getStorageSync("nowTable")
            })
        }
        await add('chatmessage', {
            fid: this.data.nowfid,
            uid:userInfo.id,
            content: this.data.chatForm.content,
            format: 1
        })

        this.setData({
            chatForm:{
                content: ''
            }
        })
        this.getChatList()
    },
    toDetail() {
        wx.navigateTo({
            url: '/pages/user-info/user-info',
        })
    },
    orderTap(e) {
        const name = e.currentTarget.dataset.name;
        wx.navigateTo({
            url: `/pages/shop-order/orders-list?name=${name}`,
        })
    },
 async   getData() {
     this.setData({
         token: wx.getStorageSync('token'),
     })
     if (!this.data.token) {
         return
     }
        let table = wx.getStorageSync("nowTable");
        const res = await session(table)
        const menus = menu.default.list()
        // 图标映射：菜单名称 → 本地图片路径
        const IMAGE_MAP = {
            '我的收藏管理': '/images/收藏.png',
            '地址管理': '/images/地址.png',
            '订单管理': '/images/_订单.png'
        };
        // 合并跳转：菜单名称 → 小程序页面路径
        const DIRECT_JUMP = {
            '我的收藏管理': '/pages/storeup/list',
            '地址管理': '/pages/address/list',
            '订单管理': '/pages/shop-order/orders-list'
        };
        // 这三个已经在上面 grid 固定显示了，从 backMenuList 里过滤掉避免重复
        const FILTER_KEYS = ['我的收藏管理', '地址管理', '订单管理'];
        for (let x in menus) {
            if (menus[x].tableName == table) {
                let backMenulist = (menus[x].backMenu || [])
                    .filter(item => !FILTER_KEYS.includes(item.menu))
                    .map(item => {
                        item.image = IMAGE_MAP[item.menu] || '';
                        item.directJump = DIRECT_JUMP[item.menu] || '';
                        // 兜底：从 child[0] 取 tableName，防止 optionsTap 里 item.tableName 为空
                        if (!item.tableName && item.child && item.child.length > 0) {
                            item.tableName = item.child[0].tableName;
                        }
                        return item;
                    });
                this.setData({
                    backMenuList: backMenulist
                })
                break
            }
        }
        // 修复：用正确的字段 headportrait（不是 touxiang），并确保路径正确
        let headportrait = res?.data?.headportrait || '';
        // 把 upload 替换为 file（兼容老数据）
        headportrait = headportrait.replace('upload', 'file');
        res.data["headportrait"] = headportrait;

        // 关键修复：同步更新 data.avatarurl 和 storage.avatarurl
        // 这样 wxml 里的 {{baseURL+avatarurl}} 就能立即拿到最新头像
        wx.setStorageSync("avatarurl", headportrait);
        wx.setStorageSync("nickname", res?.data?.userName || res?.data?.username || wx.getStorageSync('nickname'));
        wx.setStorageSync("userId", res?.data?.id);  // 兜底存 userId
        // 余额保留两位小数
        const userData = res.data || {}
        if (userData.money !== undefined && userData.money !== null) {
            userData.money = Number(userData.money).toFixed(2)
        }
        this.setData({
            userInfo: userData,
            avatarurl: headportrait,       // 回写 data.avatarurl，确保 wxml 立即渲染
            mypic: headportrait
        });
        getApp().globalData.userInfo = userData;
    },
    userRecharge() {
        wx.removeStorageSync('payObject');
        wx.navigateTo({
            url: "/pages/shop-recharge/pay-confirm"
        })
    },
    menuTap(e) {
        const item = e.currentTarget.dataset.item;
        const index = e.currentTarget.dataset.index;
        const tableName = item.child[0].tableName
        if (item.menu == "论坛管理") {
            if (this.data.secondIndex == index) {
                this.setData({
                    secondIndex: -1
                })
                return false
            } else {
                this.setData({
                    secondIndex: index
                })
                
            }
        } else if (item.menu.includes("我的收藏")) {
                wx.navigateTo({
                    url: `/pages/${tableName}/${tableName}-list`,
                })
        }

        else {
            utils.menuTap(tableName)
        }
    },
    tologin(){
        wx.navigateTo({
            url: '/pages/login/login',
        })
    },
    optionsTap(e) {
        // 论坛管理
        const item = e.currentTarget.dataset.item;
        const tableName = item.tableName
        if (item.menu.includes("论坛交流")) {
            wx.navigateTo({
                url: `/pages/${tableName}/${tableName}-my`,
            })
        } else if (item.menu.includes("我的发布")) {
            wx.navigateTo({
                url: `/pages/${tableName}/${tableName}-add-or-update`,
            })

        }

    },
    // 我的订单
    orderPage() {
        wx.navigateTo({ url: '/pages/shop-order/orders-list' });
    },
    // 我的收藏
    storeUp() {
        wx.navigateTo({ url: '/pages/storeup/list' });
    },
    // 收货地址
    address() {
        wx.navigateTo({ url: '/pages/address/list' });
    },
    cancelShow(){
        this.selectComponent('#bottomFrame').hideFrame();
    },
    uppdatePassword() {
        this.selectComponent('#bottomFrame').showFrame();
    },
   async  resetpasswordBtn(){

if (!this.data.passWorded){
    wx.showToast({
        title: '原密码不能为空',
        icon: 'none'
    })
return
}
if (!this.data.newPassword){
    wx.showToast({
        title: '新密码不能为空',
        icon: 'none'
    })
    return
}
if (!this.data.confirmPassword){
    wx.showToast({
        title: '确认密码不能为空',
        icon: 'none'
    })
    return
}
let password = ''
 let table=   wx.getStorageSync("nowTable")

if (table=='user') {
password = getApp().globalData.userInfo.userPassword;
}
let newpassword = this.data.passWorded
if(password != newpassword){
    wx.showToast({
        title: '原密码不正确',
        icon: 'none'
    })
    return
}
if (this.data.newPassword != this.data.confirmPassword){
    wx.showToast({
        title: '两次密码不一致',
        icon: 'none'
    })
    return
}
if (table == 'user') {
}
if (table=='user') {
getApp().globalData.userInfo.userPassword = this.data.newPassword
}
       let userInfo=getApp().globalData.userInfo;
       await update(table,userInfo)
       wx.showToast({
           title: `修改密码成功,下次登录系统生效`,
           icon: 'none'
       })
       this.selectComponent('#bottomFrame').hideFrame();
     },
    onPageTap() {
        wx.navigateTo({
            url: "/pages/vip-payconfirm/vip-payconfirm"
        })
    },
    /**
     * 生命周期函数--监听页面隐藏
     */
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