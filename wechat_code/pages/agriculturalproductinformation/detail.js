const {
  deleteData,
  update,
  add,
  page,
  list,
  detail,
  save,
  session,
  mlist,
  info,
  lists,
  query
} = require("../../api/index.js")
const utils = require("../../utils/index.js")
Page({
  data: {
    title: '',
    user: {},
    pageIsAuth: false,
    token: '',
    baseURL: '',
    id: getApp().globalData.detailId,
    userId: getApp().globalData.userInfo.id,
    detailList: {},
    payAuth: "",
    picture: "",
    goodname: "",

    buyNumber: 1,
    isInCart: false,
    cartForm: {
      userid: getApp().globalData.userInfo.id
    },
    islike: false,
    commmentList: [],
  },
  async onLoad(option) {
    let authobj = {}
    authobj['pageIsAuth'] = wx.getStorageSync('isAuth')
    this.setData(authobj)
    let myid = option?.id ? option?.id : getApp().globalData.detailId;
    const user = wx.getStorageSync("userSession");
    this.setData({
      user,
      id: myid,
      token: wx.getStorageSync('token'),
      baseURL: wx.getStorageSync('baseURL') + '/'
    })
    await this.handleUpdateData()
  },
  //立即预订
  orderTap() {
    if (this.data.activeSeat.length <= 0) {
      wx.showToast({
        title: '请选择需要预订的位置',
        icon: 'none'
      })
      return
    }
    const userInfo = getApp().globalData.userInfo
    const activeSeat = this.data.activeSeat.join(',') + ',' + this.data.detailList.selected
    let data = {
      orderid: this.createOrderId(),
      tablename: 'agriculturalproductinformation',
      userid: userInfo.id,
      goodid: this.data.detailList.id,
      goodname: this.data.detailList.productname,
      buynumber: this.data.activeSeat.length,
      total: 0,
      discounttotal: 0,
      address: this.data.activeSeat,
      status: '已支付',
      discountprice: this.data.detailList.vipprice,
      picture: this.data.picture[0],
      account: this.data.detailList.account,
    }
    if (this.data.detailList?.price) {
      data['status'] = '未支付'
      data['price'] = this.data.detailList.price
      data['total'] = parseFloat(data['price'] * activeSeat.length).toFixed(2)
      wx.showModal({
        title: '提示',
        content: '是否预订选中座位',
        complete: async (res) => {
          if (res.confirm) {
            wx.setStorageSync('orderGoods', [data])
            wx.navigateTo({
              url: "/pages/shop-order/orders-confirm?type=1&seat=1"
            })
          }
        }
      })
    }

  },
  chapterClick(e) {
    const item = e.currentTarget.dataset.item;
    const user = this.data.user;

    if (!this.data.userId) {
      wx.showToast({
        title: '请先登录',
        icon: "none"
      })
      setTimeout(() => {
        wx.navigateTo({
          url: `/pages/login/login`,
        })
      }, 1500)
      return
    }
    let that = this.data;
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
    if (nowTable && this.data.token) {
      const res = await session(nowTable)
      if (res.code == 0) {
        getApp().globalData.userInfo = res?.data;
        let userId = res?.data.id;
        this.setData({
          userId
        })
      }
    }
    // 更新当前页面的数据
    var that = this;
    getApp().globalData.detailId == null ? getApp().globalData.detailId = wx.getStorageSync('detailId') : "";
    const id = getApp().globalData.detailId;
    if (id) {
      const {
        data
      } = await detail("agriculturalproductinformation", id)
      this.setData({
        payAuth: utils.isAuthFront('agriculturalproductinformation', '支付')
      })

      if (!data) {
        wx.showToast({ title: '商品数据加载失败', icon: 'none' });
        return;
      }

      data["price"] = data?.price ? data.price.toFixed(2) : "0.00"
      data["vipprice"] = data?.vipprice ? data.vipprice.toFixed(2) : "";

      const predetailList = Object.assign({}, data)
      this.setData({
        predetailList
      })
      let objClo = {}
      objClo['picture'] = data.image ? data.image.split(',') : [];
      data.productdetails = data.productdetails ? utils.addImgWidthStyle(data.productdetails, '100%') : '';
      objClo['detailList'] = data;
      this.setData(objClo)
      if (!this.data.token) {
        return
      }

      this.getInCartList();
      await this.searchList("isStoreup")
    }
    const commentData = {
      page: 1,
      limit: 100,
      refid: getApp().globalData.detailId
    }
    const commentRes = await list("discussagriculturalproductinformation", commentData);
    // 评论数增加
    let detailList = this.data.detailList;
    detailList.discussNumber = commentRes.data.list.length;
    await update('agriculturalproductinformation', detailList)
    const avatarBaseURL = wx.getStorageSync('baseURL') + '/'
    commentRes.data.list = commentRes.data?.list.map((item) => {
      let content = utils.addImgWidthStyle(item.content, '60px')
      if (content) {
        content = content.replace(/src="(?!http)/gi, 'src="' + avatarBaseURL)
      }
      item.content = content
      if (item.reply) {
        let reply = utils.addImgWidthStyle(item.reply, '60px')
        reply = reply.replace(/src="(?!http)/gi, 'src="' + avatarBaseURL)
        item.reply = reply
      }
      return item
    })
    this.setData({
      commmentList: commentRes.data?.list,
      detailList
    })
    this.setData({
      title: this.data.detailList.productname,
    });

  },

  onUnload: function () {
    getApp().globalData.detailList = {}
    console.log('页面被卸载，执行销毁操作');
  },
  async addCommentap() {

    if (!this.data.token) {
      return
    }
    let params = {
      page: 1,
      limit: 100,
      goodid: this.data.detailList.id,
      userid: getApp().globalData.userInfo.id
    }
    let res = await list('orders', params)

    if (res.data.list.length == 0) {
      wx.showToast({
        title: '请先购买该商品后再评价',
        icon: "none"
      })
      return;
    }

    let validOrder = res.data.list.find(item => item.status == '已收货')
    if (!validOrder) {
      let paidOrder = res.data.list.find(item => item.status == '已支付')
      if (paidOrder) {
        wx.showToast({ title: '请先确认收货后再评价', icon: "none" })
      } else {
        wx.showToast({ title: '请完成订单后再评价', icon: "none" })
      }
      return;
    }

    let commentParams = {
      page: 1,
      limit: 10,
      refid: this.data.detailList.id,
      userid: getApp().globalData.userInfo.id
    }
    let commentRes = await list('discussagriculturalproductinformation', commentParams)
    let commentCount = 0
    commentRes.data.list.forEach(c => {
      commentCount += 1
      if (c.reply) commentCount += 1
    })
    if (commentCount >= 2) {
      wx.showToast({ title: '评论已上限，无法再次评论', icon: "none" })
      return;
    }

    getApp().globalData.detailId = this.data.detailList.id
    wx.setStorageSync('tableName', "agriculturalproductinformation")
    wx.navigateTo({
      url: `/pages/discussagriculturalproductinformation/update-and-add`,
    })
    console.log("uusuer", getApp().globalData.userInfo);
  },
  async listAdd(anyType) {
    const userid = getApp().globalData.userInfo.id
    const data = {
      name: this.data.detailList.productname,
      picture: this.data.detailList.image,
      tablename: `agriculturalproductinformation`,
      refid: getApp().globalData.detailId,
      userid,
      // type 收藏是1 关注是41
      type: anyType
    }
    await add("storeup", data)
  },
  async searchList(name) {
    const userid = getApp().globalData.userInfo.id
    const searchData = {
      page: 1,
      limit: 1,
      refid: getApp().globalData.detailId,
      tablename: "agriculturalproductinformation",
      userid,
      type: 1
    }
    if (name == "isStoreup") {

      const storeupRes = userid ? await list("storeup", searchData) : ''
      if (storeupRes?.data?.list?.length > 0) {
        // 收藏
        this.setData({
          islike: true
        })
        const id = [storeupRes?.data?.list[0]?.id]
        return id
      } else {
        this.setData({
          islike: false
        })
      }
    }
  },
  likeTap() {

    if (!this.data.token) {
      return
    }
    wx.showModal({
      title: '提示',
      content: "是" == '是' ? '是否收藏' : "是否是",
      complete: async (res) => {
        if (res.confirm) {
          // 添加收藏
          await this.listAdd(1)
          await this.listUpdate('islike')
          await this.searchList("isStoreup")
        }
      }
    })

  },
  cancelLikeTap() {

    if (!this.data.token) {
      return
    }
    wx.showModal({
      title: '提示',
      content: "是" == '是' ? '是否取消收藏' : "是否取消是",
      complete: async (res) => {
        if (res.cancel) {}
        if (res.confirm) {
          const id = await this.searchList("isStoreup")
          // 删除收藏
          await deleteData('storeup', id)
          await this.listUpdate('cancelislike')
          this.searchList("isStoreup")
        }
      }
    })

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
    const resUpdate = await update('agriculturalproductinformation', predetailList)
    if (resUpdate.code == 0) {
      this.setData({
        predetailList,
        "detailList.crazilyNumber": predetailList.crazilyNumber
      })

    }


  },
  async getInCartList() {
    const params = {
      userid: getApp().globalData.userInfo.id,
      tablename: 'agriculturalproductinformation',
      goodid: this.data.detailList.id
    }
    const res = await list('cart', params)
    if (res?.data?.list?.length) {
      this.setData({
        isInCart: true
      })
    } else {
      this.setData({
        isInCart: false
      })
    }
  },
  // 数量 +1 / -1
  changeBuyNumber(e) {
    const op = parseInt(e.currentTarget.dataset.op, 10)
    let num = this.data.buyNumber + op
    if (num < 1) num = 1

    // 三层上限（取最小）：单限 > 总限 > 库存
    const detail = this.data.detailList || {}
    let max = 99999
    if (detail.onelimittimes > 0) max = Math.min(max, detail.onelimittimes)
    if (detail.alllimittimes > 0) max = Math.min(max, detail.alllimittimes)
    if (detail.number && parseInt(detail.number) > 0) max = Math.min(max, parseInt(detail.number))
    if (num > max) num = max

    this.setData({ buyNumber: num })
  },
  async addCart() {

    if (!this.data.token) {
      return
    }
    //单次购买限制
    if (this.data.detailList.onelimittimes > 0 && this.data.detailList.onelimittimes < this.data.buyNumber) {
      wx.showToast({
        title: `每人只能购买${this.data.detailList.onelimittimes}件商品`,
        icon: "none"
      })
      return
    }
    //库存不足
    if (this.data.detailList.alllimittimes <= 0) {
      wx.showToast({
        title: '商品售罄',
        icon: "none"
      })
      return
    }
    //库存限制
    if (this.data.detailList.alllimittimes > 0 && this.data.detailList.alllimittimes < this.data.buyNumber) {
      wx.showToast({
        title: '库存不足',
        icon: "none"
      })
      return
    }
    if (this.data.isInCart) {
      wx.showToast({
        title: '该商品已在购物车，请前往购买',
        icon: "none"
      })
      return
    }
    const baseURL = wx.getStorageSync('baseURL') + "/"
    this.setData({
      'cartForm.buynumber': this.data.buyNumber,
      'cartForm.goodid': this.data.detailList.id,
      'cartForm.goodname': this.data.detailList.productname,
      'cartForm.tablename': "agriculturalproductinformation",
      'cartForm.picture': this.data.picture[0],
      picture: this.data.picture[0],
    })
    this.setData({
      'cartForm.account': this.data.detailList.account,
    })
    let cartForm = this.data.cartForm

    this.setData({
      'cartForm.price': this.data.detailList?.price
    })
    const res = await save('cart', this.data.cartForm)
    let detailList = this.data.detailList
    if (res.code == 0) {
      wx.showToast({
        title: '添加成功',
        icon: "none"
      })

      this.getInCartList()
      var that = this
      setTimeout(function () {

        that.onLoad(detailList.id)
        return
      }, 1000)

    }

  },
  onPayTap() {
    if (!wx.getStorageSync('token')) {
      wx.showToast({
        title: '请先登录',
        icon: "none"
      })
      setTimeout(() => {
        wx.navigateTo({
          url: `/pages/login/login`
        })
      }, 1000)
      return
    }

    const baseURL = wx.getStorageSync('baseURL')
    let data = this.data.detailList
    data["image"] = data.image
    data['buynumber'] = 1
    wx.setStorageSync('payObject', data);
    wx.setStorageSync('paytable', 'agriculturalproductinformation');

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
    const res = await update("agriculturalproductinformation", detailList)
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
  onBuyTap() {

    if (!this.data.token) {
      return
    }
    const data = {
      tablename: 'agriculturalproductinformation',
      goodid: this.data.detailList.id,
      goodname: this.data.detailList.productname,
      picture: this.data.picture[0],
      account: this.data.detailList.account,
      buynumber: 1,
      price: this.data.detailList?.price,
      discountprice: this.data.detailList.vipprice
    }

    wx.setStorageSync('orderGoods', [data])
    getApp().globalData.total = this.data.detailList?.price
    wx.navigateTo({
      url: "/pages/shop-order/orders-confirm?type=1"
    })
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
  async onAcrossTap(e) {

    // (tableName,crossOptAudit,statusColumnName,tips,statusColumnValue)
    const info = e.currentTarget.dataset.info;
    const crossOptAudit = info.split(",")[1].replace(/'/g, '')

    const token = wx.getStorageSync("token")
    if (!this.data.token) {
      wx.showToast({
        title: '请登录后再操作',
        icon: "none"
      })
      return
    }

    let pagetableName = info.split(",")[0]
    const statusColumnName = info.split(",")[2]
    const tips = info.split(",")[3]
    let statusColumnValue = info.split(",")[3]
    let type = info.split(",")[6];
    type ? type * 1 : type = 1;
    statusColumnValue = statusColumnValue.replace(/^'|'$/g, '');
    wx.setStorageSync('crossTable', 'agriculturalproductinformation');
    wx.setStorageSync(`crossObj`, this.data.detailList);
    wx.setStorageSync('statusColumnName', statusColumnName?.substring(1, statusColumnName?.length - 1))
    wx.setStorageSync('tips', tips.substring(1, tips?.length - 1))
    let keyname = statusColumnValue?.split('[')[0]
    let statusColumnValueindex = statusColumnValue?.split('[')[1]?.replace(']', '')
    keyname = keyname ? keyname.replace('CrossSelect', '') : ''

    wx.setStorageSync('statusColumnValue', statusColumnValue)
    if (statusColumnName != '' && !statusColumnName.startsWith("[")) {
      var obj = this.data.detailList

      for (var o in obj) {
        if (statusColumnName.includes(o) && statusColumnValue == obj[o]) {
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