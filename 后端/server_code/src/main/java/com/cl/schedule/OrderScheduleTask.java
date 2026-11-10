package com.cl.schedule;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.dao.AgriculturalproductinformationDao;
import com.cl.dao.OrdersDao;
import com.cl.dao.UserDao;
import com.cl.entity.OrdersEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 订单定时任务
 * 1. 每小时扫描一次：未支付超过 24 小时的订单 → 自动取消 + 释放库存（已支付的才退余额）
 */
@Component
public class OrderScheduleTask {

    private static final Logger log = LoggerFactory.getLogger(OrderScheduleTask.class);

    /** 订单超时时间（毫秒）— 24 小时 */
    private static final long ORDER_TIMEOUT_MS = 24L * 60 * 60 * 1000;

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private AgriculturalproductinformationDao productDao;

    @Autowired
    private UserDao userDao;

    /**
     * 每小时第 0 分钟执行
     * 扫描 status = '未支付' 且 addtime < (now - 24h) 的订单
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void cancelExpiredOrders() {
        Date deadline = new Date(System.currentTimeMillis() - ORDER_TIMEOUT_MS);

        // 找未支付且超时的订单
        EntityWrapper<OrdersEntity> ew = new EntityWrapper<>();
        ew.eq("status", "未支付");
        ew.lt("addtime", deadline);
        List<OrdersEntity> expiredList = ordersDao.selectList(ew);

        if (expiredList.isEmpty()) {
            return;
        }

        log.info("【定时任务】扫描到 {} 笔超时未支付订单，开始自动取消", expiredList.size());

        for (OrdersEntity order : expiredList) {
            try {
                // 回补库存（无库存限制的商品 alllimittimes=-1，increaseStock 里已处理）
                productDao.increaseStock(order.getGoodid(), order.getBuynumber());

                // 只有已支付类型的订单需要退余额
                // 未支付类型（type=2）的订单本来就没扣钱，不用退
                String type = order.getType();
                if ("1".equals(type)) {
                    userDao.increaseMoney(order.getUserid(), order.getTotal());
                    log.info("【定时任务】订单 {} 退款给用户 {} 元", order.getOrderid(), order.getTotal());
                }

                // 改状态
                OrdersEntity update = new OrdersEntity();
                update.setId(order.getId());
                update.setStatus("已取消");
                update.setRemark((order.getRemark() == null ? "" : order.getRemark()) + " [系统自动取消：超时未支付]");
                ordersDao.updateById(update);

                log.info("【定时任务】订单 {} 自动取消成功", order.getOrderid());
            } catch (Exception e) {
                log.error("【定时任务】订单 {} 自动取消失败: {}", order.getOrderid(), e.getMessage(), e);
                // 单个订单失败不影响其他
            }
        }
    }
}
