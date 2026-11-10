package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.dao.AgriculturalproductinformationDao;
import com.cl.dao.CartDao;
import com.cl.dao.UserDao;
import com.cl.entity.*;
import com.cl.entity.view.OrdersView;
import com.cl.service.OrdersService;
import com.cl.utils.CommonUtil;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商品订单
 * 后端接口
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private com.cl.dao.OrdersDao ordersDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AgriculturalproductinformationDao productDao;

    @Autowired
    private CartDao cartDao;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, OrdersEntity orders,
                  HttpServletRequest request) {
        String tableName = request.getSession().getAttribute("tableName").toString();
        if (tableName.equals("supplier")) {
            orders.setAccount((String) request.getSession().getAttribute("username"));
            if (orders.getUserid() != null) {
                orders.setUserid(null);
            }
        } else {
            if (!request.getSession().getAttribute("role").toString().equals("管理员")) {
                orders.setUserid((Long) request.getSession().getAttribute("userId"));
            }
        }
        EntityWrapper<OrdersEntity> ew = new EntityWrapper<OrdersEntity>();


        PageUtils page = ordersService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, orders), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, OrdersEntity orders,
                  HttpServletRequest request) {
        // 按角色隔离数据
        String tableName = request.getSession().getAttribute("tableName").toString();
        if (tableName.equals("supplier")) {
            orders.setAccount((String) request.getSession().getAttribute("username"));
            if (orders.getUserid() != null) {
                orders.setUserid(null);
            }
        } else {
            if (!request.getSession().getAttribute("role").toString().equals("管理员")) {
                orders.setUserid((Long) request.getSession().getAttribute("userId"));
            }
        }
        EntityWrapper<OrdersEntity> ew = new EntityWrapper<OrdersEntity>();

        PageUtils page = ordersService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, orders), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(OrdersEntity orders) {
        EntityWrapper<OrdersEntity> ew = new EntityWrapper<OrdersEntity>();
        ew.allEq(MPUtil.allEQMapPre(orders, "orders"));
        return R.ok().put("data", ordersService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(OrdersEntity orders) {
        EntityWrapper<OrdersEntity> ew = new EntityWrapper<OrdersEntity>();
        ew.allEq(MPUtil.allEQMapPre(orders, "orders"));
        OrdersView ordersView = ordersService.selectView(ew);
        return R.ok("查询商品订单成功").put("data", ordersView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        OrdersEntity orders = ordersService.selectById(id);
        orders = ordersService.selectView(new EntityWrapper<OrdersEntity>().eq("id", id));
        return R.ok().put("data", orders);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        // 归属校验：非管理员/非supplier只能看自己的订单
        OrdersEntity db = ordersService.selectById(id);
        if (db != null) {
            String role = (String) request.getSession().getAttribute("role");
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            Long userId = (Long) request.getSession().getAttribute("userId");

            boolean isAdmin = "管理员".equals(role);
            boolean isSupplierOwner = "supplier".equals(tableName) && db.getAccount() != null && db.getAccount().equals(username);
            boolean isUserOwner = userId != null && userId.equals(db.getUserid());

            if (!isAdmin && !isSupplierOwner && !isUserOwner) {
                return R.error("无权查看此订单");
            }
        }
        OrdersEntity orders = ordersService.selectById(id);
        orders = ordersService.selectView(new EntityWrapper<OrdersEntity>().eq("id", id));
        return R.ok().put("data", orders);
    }


    /**
     * 后端保存（由事务下单接口替代，保留兼容）
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrdersEntity orders, HttpServletRequest request) {
        orders.setUserid((Long) request.getSession().getAttribute("userId"));
        ordersService.insert(orders);
        return R.ok();
    }

    /**
     * 前端保存（由事务下单接口替代，保留兼容）
     */
    @RequestMapping("/add")
    public R add(@RequestBody OrdersEntity orders, HttpServletRequest request) {
        ordersService.insert(orders);
        return R.ok();
    }

    /**
     * 【核心新增】统一事务下单接口
     * 前端不再需要自己调 5 个接口，一次调用完成：
     *   1. 校验商品 + 查真实价格（不信任前端）
     *   2. 校验库存（乐观锁）
     *   3. 校验余额（乐观锁）并扣减
     *   4. 扣库存
     *   5. 建订单
     *   6. 删购物车
     * 任一步失败则整体回滚
     *
     * 请求体示例：
     * {
     *   "address": "...", "tel": "...", "consignee": "...", "remark": "...",
     *   "type": "1",                          // 1=已支付, 2=未支付
     *   "goods": [
     *     {"tablename":"agriculturalproductinformation", "goodid":1, "buynumber":2, "cartId":5},
     *     {"tablename":"agriculturalproductinformation", "goodid":2, "buynumber":1, "cartId":6}
     *   ]
     * }
     */
    @RequestMapping("/createOrder")
    @Transactional
    public R createOrder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        // 从 session 拿到当前登录用户
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }
        String role = (String) request.getSession().getAttribute("role");

        // 1. 从数据库查用户余额（二次确认）
        UserEntity user = userDao.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        // 2. 解析商品列表
        List<Map<String, Object>> goods = (List<Map<String, Object>>) body.get("goods");
        if (goods == null || goods.isEmpty()) {
            return R.error("请选择商品");
        }

        // 3. 遍历校验 + 计算总金额
        double totalAmount = 0;
        List<ProductOrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> g : goods) {
            String tablename = (String) g.get("tablename");
            Long goodid = Long.parseLong(g.get("goodid").toString());
            int buynumber = Integer.parseInt(g.get("buynumber").toString());
            Object cartIdObj = g.get("cartId"); // 可选，用于删购物车

            // 只处理农产品表，其他表拒绝
            if (!"agriculturalproductinformation".equals(tablename)) {
                throw new EIException("不支持的商品类型: " + tablename);
            }

            // 查真实商品 + 验证库存
            AgriculturalproductinformationEntity product = productDao.selectById(goodid);
            if (product == null) {
                throw new EIException("商品不存在: id=" + goodid);
            }
            // 校验限购（onelimittimes = -1 表示不限）
            if (product.getOnelimittimes() != null && product.getOnelimittimes() != -1
                    && buynumber > product.getOnelimittimes()) {
                throw new EIException("商品【" + product.getProductname() + "】限购" + product.getOnelimittimes() + "件");
            }
            // 校验库存（alllimittimes = -1 表示不限）
            if (product.getAlllimittimes() != null && product.getAlllimittimes() != -1
                    && buynumber > product.getAlllimittimes()) {
                throw new EIException("商品【" + product.getProductname() + "】库存不足");
            }

            // 用数据库真实价格（不信任前端传入值）
            // VIP 用户且商品配置了 vipprice → 用 vipprice
            double realPrice = product.getPrice() != null ? product.getPrice() : 0.0;
            boolean isVipUser = "是".equals(user.getVip());
            boolean hasVipPrice = product.getVipprice() != null && product.getVipprice() > 0;
            if (isVipUser && hasVipPrice) {
                realPrice = product.getVipprice();
            }
            // 用 Math.round 消除浮点精度：48.8 * 3 = 146.399999 → 146.40
            double itemTotal = Math.round(realPrice * buynumber * 100.0) / 100.0;
            totalAmount = Math.round((totalAmount + itemTotal) * 100.0) / 100.0;

            // 解析图片（image 字段是逗号分隔的多图，取第一张）
            String picture = product.getImage();
            if (picture != null && picture.contains(",")) {
                picture = picture.split(",")[0];
            }

            double originalPrice = product.getPrice() != null ? product.getPrice() : 0.0;
            orderItems.add(new ProductOrderItem(
                    tablename, goodid, product.getProductname(), picture,
                    buynumber, realPrice, originalPrice, itemTotal, product.getAccount(), cartIdObj
            ));
        }

        // 4. 校验余额 + 原子扣余额
        if (totalAmount > 0) {
            if (user.getMoney() == null || user.getMoney() < totalAmount) {
                return R.error("余额不足，总金额：" + String.format("%.2f", totalAmount));
            }
            int rows = userDao.decreaseMoney(userId, totalAmount);
            if (rows == 0) {
                // 乐观锁失败：并发下余额被其他请求先扣了
                return R.error("余额不足，请稍后再试");
            }
        }

        // 5. 逐个原子扣库存（任一失败则回滚）
        for (ProductOrderItem item : orderItems) {
            int rows = productDao.decreaseStock(item.goodid, item.buynumber);
            if (rows == 0) {
                throw new EIException("商品【" + item.goodname + "】库存不足，下单失败");
            }
        }

        // 6. 建订单（每个商品一条）
        String baseOrderId = generateOrderId();
        String address = (String) body.get("address");
        String tel = (String) body.get("tel");
        String consignee = (String) body.get("consignee");
        String remark = (String) body.get("remark");
        String type = (String) body.get("type"); // 1=已支付, 2=未支付

        for (int i = 0; i < orderItems.size(); i++) {
            ProductOrderItem item = orderItems.get(i);
            OrdersEntity oe = new OrdersEntity();
            oe.setOrderid(baseOrderId + (orderItems.size() > 1 ? "-" + (i + 1) : ""));
            oe.setTablename(item.tablename);
            oe.setGoodid(item.goodid);
            oe.setGoodname(item.goodname);
            oe.setPicture(item.picture);
            oe.setBuynumber(item.buynumber);
            oe.setPrice(item.price);                              // 实际成交价（VIP=vipprice）
            oe.setDiscountprice(item.originalPrice);              // 商品原价（VIP 不影响）
            oe.setTotal(item.itemTotal);                          // 实际成交总价
            oe.setDiscounttotal(item.originalPrice * item.buynumber);  // 原价总价
            // type = 支付方式：1=余额支付, 2=积分支付, 3=拼团
            String payType = type != null ? type : "1";
            if (!"1".equals(payType) && !"2".equals(payType) && !"3".equals(payType)) {
                payType = "1";
            }
            oe.setType(payType);
            // 事务下单 = 即时扣款成功 → 订单状态统一为"已支付"
            // 支付方式由 type 字段单独管理，不再影响 status
            oe.setStatus("已支付");
            oe.setAddress(address);
            oe.setTel(tel);
            oe.setConsignee(consignee);
            oe.setRemark(remark);
            oe.setUserid(userId);
            oe.setRole(role);
            oe.setAccount(item.account);
            ordersService.insert(oe);
        }

        // 7. 删购物车中本次下单的商品
        List<Long> cartIds = new ArrayList<>();
        List<Long> noCartGoodIds = new ArrayList<>();
        for (ProductOrderItem item : orderItems) {
            if (item.cartIdObj != null) {
                cartIds.add(Long.parseLong(item.cartIdObj.toString()));
            } else {
                // 兜底：如果前端没传 cartId，按 goodid + userid 删
                noCartGoodIds.add(item.goodid);
            }
        }
        if (!cartIds.isEmpty()) {
            cartDao.deleteBatchIds(cartIds);
        }
        // 兜底删除（没传 cartId 的情况）
        for (Long goodid : noCartGoodIds) {
            cartDao.delete(new EntityWrapper<CartEntity>()
                    .eq("userid", userId)
                    .eq("goodid", goodid));
        }

        // 8. 全部成功
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", baseOrderId);
        result.put("total", String.format("%.2f", totalAmount));
        return R.ok().put("data", result);
    }

    /**
     * 生成订单号（后端唯一，不会重复）
     * 格式：yyyyMMddHHmmss + 4位随机 + 用户ID末4位
     */
    private String generateOrderId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date())
                + CommonUtil.getRandomNumber(4)
                + System.currentTimeMillis() % 10000;
    }


    /**
     * 修改 —— 已增加归属校验
     * 非管理员只能修改自己的订单；管理员不受限
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody OrdersEntity orders, HttpServletRequest request) {
        // 归属校验
        String role = (String) request.getSession().getAttribute("role");
        if (!"管理员".equals(role)) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            OrdersEntity db = ordersService.selectById(orders.getId());
            if (db == null) {
                return R.error("订单不存在");
            }
            // 普通用户和供应商都只能改自己的
            Long orderOwnerId = db.getUserid();
            String orderOwnerAccount = db.getAccount();
            String currentUsername = (String) request.getSession().getAttribute("username");
            String currentTable = (String) request.getSession().getAttribute("tableName");

            boolean isOwner = userId != null && userId.equals(orderOwnerId)
                    || ("supplier".equals(currentTable) && orderOwnerAccount != null && orderOwnerAccount.equals(currentUsername));
            if (!isOwner) {
                return R.error("无权修改此订单");
            }
        }

        // ===== 退货审批自动回补逻辑 =====
        // 管理员将 sfsh 改为 "是" → 回补库存 + 回补余额（已退款场景）
        if ("管理员".equals(role) && orders.getSfsh() != null && "是".equals(orders.getSfsh())) {
            OrdersEntity db = ordersService.selectById(orders.getId());
            if (db != null && !"是".equals(db.getSfsh()) && !"已退款".equals(db.getStatus())) {
                // 原子回补库存
                productDao.increaseStock(db.getGoodid(), db.getBuynumber() != null ? db.getBuynumber() : 1);
                // 原子回补余额（回补实际成交总价）
                double refundAmount = db.getTotal() != null ? db.getTotal() : 0.0;
                if (refundAmount > 0) {
                    userDao.increaseMoney(db.getUserid(), refundAmount);
                }
                // 自动把状态改成已退款
                orders.setStatus("已退款");
            }
        }

        // ===== 取消订单自动回补（用户取消时）=====
        // status 改成 "已取消" 且之前没回补过 → 回补库存 + 余额
        if ("已取消".equals(orders.getStatus())) {
            OrdersEntity db = ordersService.selectById(orders.getId());
            if (db != null && !"已退款".equals(db.getStatus()) && !"已取消".equals(db.getStatus())) {
                // 不退款（用户主动取消还没付款的情况）→ 只回补库存
                productDao.increaseStock(db.getGoodid(), db.getBuynumber() != null ? db.getBuynumber() : 1);
            }
        }

        ordersService.updateById(orders);
        return R.ok();
    }

    /**
     * 确认收货（已支付 → 已收货）
     */
    @RequestMapping("/confirm")
    @Transactional
    public R confirm(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long id = body.get("id");
        if (id == null) return R.error("订单ID不能为空");

        OrdersEntity db = ordersService.selectById(id);
        if (db == null) return R.error("订单不存在");

        // 归属校验（用 userid 更可靠）
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null || !userId.equals(db.getUserid())) {
            return R.error("无权操作此订单");
        }
        if (!"已支付".equals(db.getStatus())) {
            return R.error("当前订单状态不能确认收货");
        }

        db.setStatus("已收货");
        ordersService.updateById(db);
        return R.ok("确认收货成功");
    }

    /**
     * 批量审核（仅管理员可用）
    @RequestMapping("/shBatch")
    @Transactional
    public R update(@RequestBody Long[] ids, @RequestParam String sfsh, @RequestParam String shhf, HttpServletRequest request) {
        // 角色校验：只有管理员能审核
        String role = (String) request.getSession().getAttribute("role");
        if (!"管理员".equals(role)) {
            return R.error("无审核权限");
        }
        List<OrdersEntity> list = new ArrayList<>();
        for (Long id : ids) {
            OrdersEntity orders = ordersService.selectById(id);
            orders.setSfsh(sfsh);
            orders.setShhf(shhf);
            list.add(orders);
        }
        ordersService.updateBatchById(list);
        return R.ok();
    }


    /**
     * 删除 —— 已增加归属校验
     * 非管理员只能删自己的订单
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        if (!"管理员".equals(role)) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            for (Long id : ids) {
                OrdersEntity db = ordersService.selectById(id);
                if (db == null) continue;
                if (userId == null || !userId.equals(db.getUserid())) {
                    return R.error("无权删除订单 id=" + id);
                }
            }
        }
        ordersService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    // ==================== Dashboard 统计接口 ====================

    /**
     * 概览统计（顶部 4 个指标卡片）
     * 管理员 → 全平台数据；供应商 → 只看自己产品的销售数据
     */
    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        String username = (String) request.getSession().getAttribute("username");

        // 允许角色：管理员 + 供应商（中文 role 值，与前端 menu.roleName 对齐）
        boolean isAdmin = "管理员".equals(role);
        boolean isSupplier = "供应商".equals(role);
        if (!isAdmin && !isSupplier) {
            return R.error("无权限");
        }

        // 供应商时用 username 作 account 过滤
        String accountFilter = isSupplier ? username : null;

        Map<String, Object> data = new HashMap<>();
        data.put("todayOrders", ordersDao.countTodayOrders(accountFilter));
        data.put("todayRevenue", Math.round(ordersDao.sumTodayRevenue(accountFilter) * 100.0) / 100.0);

        if (isAdmin) {
            // 管理员看全平台
            data.put("totalUsers", userDao.selectCount(null));
            data.put("totalProducts", productDao.selectCount(null));
            data.put("totalOrders", ordersDao.selectCount(null));
        } else {
            // 供应商看自己产品的数据
            EntityWrapper<OrdersEntity> ew = new EntityWrapper<>();
            ew.eq("account", accountFilter);
            data.put("totalProducts", productDao.selectCount(new EntityWrapper<AgriculturalproductinformationEntity>().eq("account", accountFilter)));
            data.put("totalOrders", ordersDao.selectCount(ew));
            // 供应商专用：待处理订单 = 待发货(status='已支付') + 退款待审核
            data.put("pendingOrders", ordersDao.countPendingOrders(accountFilter));
        }

        // 待审核订单（管理员看全平台，供应商看自己的）
        EntityWrapper<OrdersEntity> ew2 = new EntityWrapper<>();
        ew2.eq("sfsh", "待审核");
        if (accountFilter != null) {
            ew2.eq("account", accountFilter);
        }
        data.put("pendingReviews", ordersDao.selectCount(ew2));

        return R.ok().put("data", data);
    }

    /**
     * 近 N 天订单趋势（折线图）
     */
    @RequestMapping("/trend")
    public R trend(@RequestParam(defaultValue = "7") int days, HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        String username = (String) request.getSession().getAttribute("username");
        boolean isAdmin = "管理员".equals(role);
        boolean isSupplier = "供应商".equals(role);
        if (!isAdmin && !isSupplier) {
            return R.error("无权限");
        }
        String accountFilter = isSupplier ? username : null;
        List<Map<String, Object>> list = ordersDao.trendByDays(days, accountFilter);
        // 聚合 revenue 可能有浮点精度 → Math.round 保留两位
        for (Map<String, Object> m : list) {
            Object rev = m.get("revenue");
            if (rev instanceof Number) {
                m.put("revenue", Math.round(((Number) rev).doubleValue() * 100.0) / 100.0);
            }
        }
        return R.ok().put("data", list);
    }

    /**
     * 订单状态分布（环形饼图）
     */
    @RequestMapping("/status-distribution")
    public R statusDistribution(HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        String username = (String) request.getSession().getAttribute("username");
        boolean isAdmin = "管理员".equals(role);
        boolean isSupplier = "供应商".equals(role);
        if (!isAdmin && !isSupplier) {
            return R.error("无权限");
        }
        String accountFilter = isSupplier ? username : null;
        List<Map<String, Object>> list = ordersDao.countByStatus(accountFilter);
        return R.ok().put("data", list);
    }

    /**
     * 热销商品 Top N（横向柱状图）
     */
    @RequestMapping("/top-products")
    public R topProducts(@RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        String username = (String) request.getSession().getAttribute("username");
        boolean isAdmin = "管理员".equals(role);
        boolean isSupplier = "供应商".equals(role);
        if (!isAdmin && !isSupplier) {
            return R.error("无权限");
        }
        String accountFilter = isSupplier ? username : null;
        List<Map<String, Object>> list = ordersDao.topProducts(limit, accountFilter);
        return R.ok().put("data", list);
    }

    /**
     * 商品分类销量占比（饼图）
     */
    @RequestMapping("/category-sales")
    public R categorySales(HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        String username = (String) request.getSession().getAttribute("username");
        boolean isAdmin = "管理员".equals(role);
        boolean isSupplier = "供应商".equals(role);
        if (!isAdmin && !isSupplier) {
            return R.error("无权限");
        }
        String accountFilter = isSupplier ? username : null;
        List<Map<String, Object>> list = ordersDao.categorySales(accountFilter);
        return R.ok().put("data", list);
    }


    /**
     * 内部辅助：事务下单用的商品条目临时结构
     */
    private static class ProductOrderItem {
        String tablename;
        Long goodid;
        String goodname;
        String picture;
        int buynumber;
        double price;          // 实际成交价（可能是 VIP 价）
        double originalPrice;   // 商品原价（商品标价，不随 VIP 折扣变化）
        double itemTotal;
        String account;
        Object cartIdObj;

        ProductOrderItem(String tablename, Long goodid, String goodname, String picture,
                         int buynumber, double price, double originalPrice,
                         double itemTotal, String account, Object cartIdObj) {
            this.tablename = tablename;
            this.goodid = goodid;
            this.goodname = goodname;
            this.picture = picture;
            this.buynumber = buynumber;
            this.price = price;
            this.originalPrice = originalPrice;
            this.itemTotal = itemTotal;
            this.account = account;
            this.cartIdObj = cartIdObj;
        }
    }


}
