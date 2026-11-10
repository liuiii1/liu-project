package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.OrdersEntity;
import com.cl.entity.view.OrdersView;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


/**
 * 商品订单
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
public interface OrdersDao extends BaseMapper<OrdersEntity> {

    List<OrdersView> selectListView(@Param("ew") Wrapper<OrdersEntity> wrapper);

    List<OrdersView> selectListView(Pagination page, @Param("ew") Wrapper<OrdersEntity> wrapper);

    OrdersView selectView(@Param("ew") Wrapper<OrdersEntity> wrapper);

    /** 今日订单数（供应商可按 account 过滤） */
    @Select("<script>" +
            "SELECT COUNT(*) FROM orders " +
            "WHERE DATE(addtime) = CURDATE() " +
            "<if test='account != null and account != \"\"'>AND account = #{account}</if>" +
            "</script>")
    int countTodayOrders(@Param("account") String account);

    /** 今日已支付/已发货/已完成/已退款订单营收（供应商可按 account 过滤） */
    @Select("<script>" +
            "SELECT IFNULL(SUM(total), 0) FROM orders " +
            "WHERE DATE(addtime) = CURDATE() AND status IN ('已支付','已发货','已收货','已退款') " +
            "<if test='account != null and account != \"\"'>AND account = #{account}</if>" +
            "</script>")
    double sumTodayRevenue(@Param("account") String account);

    /** 按状态分组统计数量（供应商可按 account 过滤） */
    @Select("<script>" +
            "SELECT status, COUNT(*) AS cnt FROM orders " +
            "<where>" +
            "<if test='account != null and account != \"\"'>account = #{account}</if>" +
            "</where>" +
            "GROUP BY status" +
            "</script>")
    List<Map<String, Object>> countByStatus(@Param("account") String account);

    /** 近 N 天每日订单数 + 营收（供应商可按 account 过滤） */
    @Select("<script>" +
            "SELECT DATE_FORMAT(addtime,'%m-%d') AS d, COUNT(*) AS cnt, IFNULL(SUM(total),0) AS revenue " +
            "FROM orders " +
            "WHERE addtime >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "<if test='account != null and account != \"\"'>AND account = #{account}</if>" +
            "GROUP BY DATE_FORMAT(addtime,'%m-%d') ORDER BY MIN(addtime) ASC" +
            "</script>")
    List<Map<String, Object>> trendByDays(@Param("days") int days, @Param("account") String account);

    /** 热销商品 Top N（按销量，供应商可按 account 过滤） */
    @Select("<script>" +
            "SELECT p.productname AS name, SUM(o.buynumber) AS qty, IFNULL(SUM(o.total),0) AS revenue " +
            "FROM orders o JOIN agriculturalproductinformation p ON o.goodid = p.id " +
            "WHERE o.status != '已取消' " +
            "<if test='account != null and account != \"\"'>AND o.account = #{account}</if>" +
            "GROUP BY o.goodid ORDER BY qty DESC LIMIT #{limit}" +
            "</script>")
    List<Map<String, Object>> topProducts(@Param("limit") int limit, @Param("account") String account);

    /** 按商品分类统计销量（供应商可按 account 过滤） */
    @Select("<script>" +
            "SELECT p.productcategory AS category, SUM(o.buynumber) AS qty " +
            "FROM orders o JOIN agriculturalproductinformation p ON o.goodid = p.id " +
            "WHERE o.status != '已取消' " +
            "<if test='account != null and account != \"\"'>AND o.account = #{account}</if>" +
            "GROUP BY p.productcategory ORDER BY qty DESC" +
            "</script>")
    List<Map<String, Object>> categorySales(@Param("account") String account);

    /** 供应商累计独立购买用户数（用于替换管理员的"注册用户"指标） */
    @Select("SELECT COUNT(DISTINCT userid) FROM orders WHERE account = #{account}")
    int countDistinctBuyers(@Param("account") String account);

    /** 供应商待处理订单 = 待发货(status='已支付') + 退款待审核(sfsh='待审核') */
    @Select("<script>" +
            "SELECT COUNT(*) FROM orders " +
            "WHERE account = #{account} " +
            "AND (status = '已支付' OR sfsh = '待审核') " +
            "</script>")
    int countPendingOrders(@Param("account") String account);

}
