
const {
deleteData,
page,
list,
newsData,
option,
} = require("../../api/index.js")
const utils = require("../../utils/index.js")
Page({
/**
* 页面的初始数据
*/
data: {
        questList: [],
    pageNum: 1,
pageSize: 12,
    total: 0,
    flag: true, // 防抖开关 防止用户不停的下拉


showToTopButton: true,
onPageScrollTop: 0, // 存储滚动距离的变量
goodsListData: [],
activeIndex: 0,
allData: [],
deleteShow: false,
className:"",
name:"",
addAuth:"",
delAuth: "",
editAuth:"" ,
userid:"",
baseURL: wx.getStorageSync('baseURL') + "/",
isAuthStatus:false
},

/**
* 生命周期函数--监听页面加载
*/
async onLoad(options) {
this.setData({
        isAuthStatus: wx.getStorageSync('isAuth'),
        userid:wx.getStorageSync('userid')
})
if(options?.userid) {
this.setData({
    userid:options.userid
})

}
const currentPageUrl = this.getCurrentPageUrl();
if(getApp().globalData.name!=null){
this.setData({
    name: getApp().globalData.name
})
getApp().globalData.name=null
this.searhandler()
}

},

onShow() {
// 获取当前页面栈
const pages = getCurrentPages();
// 获取当前页面对象
const currentPage = pages[pages.length - 1];
// 获取当前页面的显示报告对象
let displayReporter = currentPage.__displayReporter;
if (getApp().globalData.name == null) {
    this.setData({
        name: null
    })
    this.setData({
        isAuthStatus: wx.getStorageSync('isAuth')
    })
    }
const currentPageUrl = this.getCurrentPageUrl();
if(getApp().globalData.name){
    this.setData({
        name: getApp().globalData.name
    })
    getApp().globalData.name=null
    this.searhandler()
}
this.setData({pageNum:1,goodsListData:[]})
this.getData()
},
/**
* 生命周期函数--监听页面初次渲染完成
*/
onPageShow() {
// 页面显示时执行的操作
},



screenReset(){
    let obj={}

    this.setData(obj)

},



async   search(){
const nowTable=wx.getStorageSync('nowTable')
let searchForm = {
page: 1,
limit: 999
}


if(this.data.account){
    searchForm['account'] = '%' + this.data.account + '%'
}

let user = wx.getStorageSync("userid")?wx.getStorageSync('userSession'):{};
let res = {};
if( this.data.isAuthStatus) {
   
res = await page(`supplier`, searchForm);
} else {
res = await list(`supplier`, searchForm);
}


let goodsListData
// 如果是第一页数据置空
 if ( this.data.pageNum == 1) {
     goodsListData = []
 };
    goodsListData=res.data.list.map(item=>{
        return item
    })


this.setData({
goodsListData,
popopShow:false
})

},
/**
* 生命周期函数--监听页面显示
*/
getCurrentPageUrl() {
const pages = getCurrentPages();
const currentPage = pages[pages.length - 1];
const currentPageUrl = `/${currentPage.route}`;
return currentPageUrl;
},
async  handleTabClick(e) {
        let params={
        }
        const nowTable=  wx.getStorageSync('nowTable')

this.setData({
activeIndex: index,
});
},
    async searhandler(){
        let token = wx.getStorageSync('token')
        if (!token) {
            return
        }
        const allData=this.data.allData

        let goodsListData

        if(this.data.name==''){
            goodsListData=allData
        }else{
            goodsListData = allData.filter(item => item[targetName].includes(this.data.name));

        }
        this.setData({
            goodsListData
        })



    },
addTap() {
getApp().globalData.detailId=null
wx.navigateTo({
url: `/pages/supplier/update-and-add`
})
if(this.data.isAuthStatus) {
setTimeout(function () {
    wx.setStorageSync('isAuth', true)
}, 1000)
}
},
searchListHandler(e) {
this.setData({
goodsListData: e.detail.data
})
},
onPageScroll(e) {
if (e.scrollTop >= 225) {
this.setData({
    showToTopButton: true
});
}

},
backToTop() {
wx.pageScrollTo({
scrollTop: 0, // 返回顶部的位置
duration: 1000, // 滚动动画的时长，单位为 ms
});
// 返回顶部时隐藏按钮

},
deleteBtn(e) {
wx.showModal({
title: '提示',
content: '确认删除？',
complete: async (res) => {
    if (res.cancel) {}
    if (res.confirm) {
        const id = e.currentTarget.dataset.id;
        const res = await deleteData("supplier",[id])
        console.log(res);
        if (res.code == 0) {
            this.getData()
        }
    }
}
})
},
editBtn(e) {
const id = e.currentTarget.dataset.id;
getApp().globalData.detailId=id
wx.navigateTo({
url: `/pages/supplier/update-and-add?id=${id}&isAuth=${this.data.isAuthStatus}`
})
},
async detailBtn(e) {
const item = e.currentTarget.dataset.item;
getApp().globalData.detailId = item?.id
getApp().globalData.detailList =item
wx.navigateTo({
    url: `/pages/supplier/detail?isAuth=${this.data.isAuthStatus}`
})
if(this.data.isAuthStatus) {
setTimeout(function () {
    wx.setStorageSync('isAuth', true)
}, 1000)
}
},
     /**
      * 页面上拉触底事件的处理函数
      */
onReachBottom() {
         if (this.data.flag) {
             this.setData({
                 flag: false
             })
             this.getData(); // 疯狂的请求的方法
         }
     },
async getData() {
    const obj={
        page: this.data.pageNum,
        limit: this.data.pageSize,
            }
const nowTable=wx.getStorageSync('nowTable')
const userId= getApp().globalData.userInfo.id
let isAuthObj={}
if(this.data.isAuthStatus){
//111 supplier Supplier supplier ${pathname}
console.log('Supplier  supplier')
isAuthObj['addAuth']   = utils.isAuth("supplier", "新增")
isAuthObj['delAuth']  = utils.isAuth("supplier", "删除")
isAuthObj['editAuth']   =utils.isAuth("supplier", "修改")
}else{
isAuthObj['addAuth']   = utils.isAuthFront("supplier", "新增")
isAuthObj['delAuth']  = utils.isAuthFront("supplier", "删除")
isAuthObj['editAuth']   =utils.isAuthFront("supplier", "修改")
}
//前后台权限判断
this.setData(isAuthObj)



let resList
var that = this
obj['page']=this.data.pageNum;
obj['limit']=this.data.pageSize;
let user = wx.getStorageSync("userid")?wx.getStorageSync('userSession'):{};
    if(this.data.isAuthStatus){

        resList = await page("supplier",obj)
    }else{
         resList= await list("supplier",obj)
    }

    if(resList.code==0){
    let mylist = this.data.goodsListData;

        //先处理成你想要的数据 下面再去赋值

          let    resData =  resList.data.list.map(item => {
return item;
});
        if (that.data.pageNum == 1) {
            mylist = []
        }

        // 新旧数据合并到一起
        let resultdata
        if(resList.data.list.length>0){
      resultdata = this.data.goodsListData.concat(resData)
        }
        if (this.data.pageNum<resList.data.totalPage ){
            that.setData({
                pageNum:obj.page+ 1,
                flag:true
            })
        } else {
            that.setData({
                flag:false
            })
        }
        that.setData({
            goodsListData: resultdata,
            questList: mylist,
            total: resList.total,
            allData: mylist
        })
        
    }






// 商品名称
if (getApp().globalData.name != null) {
this.setData({
goodsListData: getApp().globalData.goodsList,
})
getApp().globalData.name = null
console.log('页面重新加载');
}

},
/**
* 生命周期函数--监听页面隐藏
*/
onHide() {
    wx.removeStorageSync('isAuth');
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
* 用户点击右上角分享
*/
onShareAppMessage() {

}
})