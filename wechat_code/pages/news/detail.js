// pages/newsDetail/newsDetail.js
const {
    detail,
    info,
    list,
    deleteData,
    add,update
} = require("../../api/index.js")
Page({

    /**
     * 页面的初始数据
     */
    data: {
        detail: {},
        id: '',
        likeType: false,
        likeForm: {},
        collectType: false,
        collectForm: {},
        baseUrl:'',
allList: [],
currentIndex: 0,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    async onLoad(options) {
        const id = options?.id?options.id:this.data.detail.id;
        if(id){
            this.setData({id, baseUrl: wx.getStorageSync('baseURL'),userid:wx.getStorageSync('userid')})
            this.getInfo()
        }

    },
onReachBottom() {

},
async getInfo(){
let res = await detail('news', this.data.id)
res.data.content = res.data.content.replace(/<img/g,'<img style="width: 100%;"');
this.setData({detail:res.data})
let currentIndex=this.data.currentIndex;
res = await list('news',{page:1,limit:100,sort:'addtime',order:'desc'})
for(let x in res.data.list){
    if(res.data.list[x].id == this.id){
       currentIndex = Number(x)
        break
    }
}
this.setData({allList:res.data.list,currentIndex})

},
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

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
        // console.log("销毁");

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