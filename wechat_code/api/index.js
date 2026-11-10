const request = require('./request.js')

export function encrypt(tableName, data) {
    return request({
        url: `/encrypt/${tableName}?text=` + data,
        method: 'get'
    });
}

export function query (tableName, data)  {
return request({
  url: `/${tableName}/query`,
  method: 'get',
  data
  })
}
export function baiduIdentify(tableName, type, fileName) {
    return request({
        url: `/${tableName}/baidu/${type}?fileName=` + fileName,
        method: 'get'
    })
}
export function rubbish(tableName, fileName) {
    return request({
        url: `/${tableName}/aliyun/rubbish?fileName=` + fileName,
        method: 'get'
    });
}


export function autoSort(tableName, data,autoSorttype) {
    return request({
        url: `/${tableName}/${autoSorttype}`,
        method: 'get',
        data
    })
}
export function follow(path, columnValue) {
    return request({
        url: `/follow/${path}?columnValue=${columnValue}`,
        method: 'get',
    })
}
export function groupby() {
    return request({
        url: `/examrecord/groupby`,
        method: 'get'
    })
}
export function faceMatch(params) {
    return request({
        url: `/matchFace`,
        method: 'get',
        data: params
    })
}
export function list(tableName, obj) {
    return request({
        url: `/${tableName}/list`,
        method: 'get',
        data: obj
    })
}
export function lists(tableName, obj) {
  return request({
      url: `/${tableName}/lists`,
      method: 'get',
      data: obj
  })
}
export function mlist(tableName, obj) {
  return request({
      url: `/${tableName}/mlist`,
      method: 'get',
      data: obj
  })
}
export function page2 (tableName, data)  {
  return request({
      url: `${tableName}/page2`,
      method: 'GET',
      data
  });
}
export function page(tableName, data) {
    return request({
        url: `/${tableName}/page`,
        method: 'get',
        data
    })
}
export function session(roleName) {
    return request({
        url: `/${roleName}/session`,
        method: 'get'
    })
}

export function detail(tableName, id) {
    return request({
        url: `/${tableName}/detail/${id}`,
        method: 'get'
    })
}


export function deleteData(tableName, obj) {
    return request({
        url: `/${tableName}/delete`,
        method: 'post',
        data: obj
    })
}
export function deleteRecords(userid,paperid) {
    // // 将数组转换为字符串
    return request({
        url: `/examrecord/deleteRecords?userid=${userid}&&paperid=${paperid}`,
        method: 'post',
    })
}
export function option(path) {
    return request({
        url: `/option/${path}`,
        method: 'get',
    })
}
export function info(tableName, id) {
    return request({
        url: `/${tableName}/info/${id}`,
        method: 'get',
    })
}

export function forumList(data) {
    return request({
        url: '/forum/flist',
        method: 'get',
        data

    })
}

export function forumDetail(tableName, id) {
    return request({
        url: `/${tableName}/list/${id}`,
        method: 'get',
    })
}
export function myForum(tableName, data) {
    return request({
        url: `/${tableName}/page`,
        method: 'get',
        data
    })
}
export function messagesList(tableName, data) {
    return request({
        url: `/${tableName}/list`,
        method: 'get',
        data
    })
}
export function save(tableName, data) {
    return request({
        url: `/${tableName}/save`,
        method: 'post',
        data
    })
}
export function add(tableName, obj) {
    return request({
        url: `/${tableName}/add`,
        method: 'post',
        data: obj
    })
}
export function chatAdd(data) {
    return request({
        url: '/chat/add',
        method: 'post',
        data
    })
}
export function chatSave(data) {
    return request({
        url: '/chat/save',
        method: 'post',
        data
    })
}
export function addtime(sort, order, limit) {
    return request({
        url: `/chat/page?sort=${sort}&order=${order}&limit=${limit}`,
        method: 'get'
    })
}

// 智能聊天 
export function chathelper(limit, ask) {
    return request({
        url: `/chathelper/page?limit=${limit}&ask=${ask}`,
        method: 'get'
    })
}
// 入库查询
export function wareHouseSelectList(page, limit, crossuserid, crossrefid) {
    return request({
        url: `/ruku/list?page=${page}&limit=${limit}&crossuserid=${crossuserid}&crossrefid=${crossrefid}`,
        method: 'get'
    })
}
// 商品信息更改
export function update(tableName, data) {
    return request({
        url: `/${tableName}/update`,
        method: 'post',
        data
    })
}
// 入库添加
export function wareHouseAdd(data) {
    return request({
        url: '/ruku/add',
        method: 'post',
        data
    })
}

// http://localhost:8080/ssm88ifk5/config/page?page=1&limit=5
// 轮播图
export function swiperData(page, limit) {
    return request({
        url: `/config/list?page=${page}&limit=${limit}`,
        method: 'get'
    })
}
// 考试列表
export function exampaperlist(tableName, data) {
    return request({
        url: `/${tableName}/page`,
        method: 'get',
        data
    })
}
// 新闻资讯
export function newsData(page, limit) {
    return request({
        url: `/news/list?page=${page}&limit=${limit}`,
        method: 'get'
    })
}
export function sheng(path) {
    return request({
        url: `/${path}`,
        method: 'get',
    })
}
// 市区
export function levelOption(path, conditionColumn, conditionValue) {
    return request({
        url: `/${path}?conditionColumn=${conditionColumn}&conditionValue=${conditionValue}`,
        method: 'get',
    })
}

/**
 * 统一事务下单接口（后端保障原子性）
 * 一次性完成：扣余额 → 扣库存 → 建订单 → 删购物车
 * 请求体：{ address, tel, consignee, remark, type, goods: [{tablename, goodid, buynumber, cartId}] }
 */
export function createOrder(data) {
    return request({
        url: `/orders/createOrder`,
        method: 'post',
        data
    })
}

export function confirmOrder(data) {
    return request({
        url: `/orders/confirm`,
        method: 'post',
        data
    })
}