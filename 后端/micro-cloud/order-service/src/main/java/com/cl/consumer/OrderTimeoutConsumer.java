package com.cl.consumer;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.constant.MqConstants;
import com.cl.dao.OrdersDao;
import com.cl.entity.OrdersEntity;
import com.cl.feign.ProductFeignClient;
import com.cl.feign.UserFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单超时取消消费者（监听死信队列）。
 *
 * 当 createOrder 以 payNow=false 创建未支付订单后，
 * 会发送一条消息到 TTL 队列（order.timeout.queue），
 * 15 分钟未被处理后自动过期 → 转到死信队列（order.timeout.dlx.queue）。
 *
 * 本消费者监听死信队列，收到消息后：
 * 1. 以 DB 状态为准做最终裁决（幂等）：先查订单状态
 * 2. 如果仍是 "未支付" → 执行取消（回补库存）
 * 3. 如果已支付/已取消 → 直接忽略（正常，前端支付或手动取消已处理）
 *
 * 注意：本类只做幂等取消，不负责补偿已支付类型订单的余额（未支付订单本来就没扣钱）。
 */
@Component
public class OrderTimeoutConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutConsumer.class);

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 监听死信队列：收到消息说明订单创建已满 15 分钟未被处理。
     */
    @RabbitListener(queues = MqConstants.QUEUE_ORDER_TIMEOUT_DLX)
    @Transactional
    public void onOrderTimeout(String message) {
        try {
            JSONObject json = JSONObject.parseObject(message);
            String orderId = json.getString("orderId");
            if (orderId == null) {
                log.warn("【超时取消】消息缺少 orderId: {}", message);
                return;
            }

            log.info("【超时取消】收到超时消息，orderId={}", orderId);

            // 1. 查订单（幂等裁决：以 DB 状态为准）
            EntityWrapper<OrdersEntity> ew = new EntityWrapper<>();
            ew.eq("orderid", orderId);
            List<OrdersEntity> orders = ordersDao.selectList(ew);

            if (orders.isEmpty()) {
                log.warn("【超时取消】订单不存在，orderId={}", orderId);
                return;
            }

            // 2. 只取消 "未支付" 的订单；已支付/已取消/已退款/已收货 → 忽略
            boolean cancelled = false;
            for (OrdersEntity order : orders) {
                if (!"未支付".equals(order.getStatus())) {
                    log.info("【超时取消】订单 {} 状态为 {}，跳过", orderId, order.getStatus());
                    continue;
                }

                // 3. 执行取消：回补库存 + 改状态
                productFeignClient.increaseStock(order.getGoodid(), order.getBuynumber());

                OrdersEntity update = new OrdersEntity();
                update.setId(order.getId());
                update.setStatus("已取消");
                update.setRemark((order.getRemark() == null ? "" : order.getRemark())
                        + " [系统自动取消：15分钟内未支付]");
                ordersDao.updateById(update);
                cancelled = true;
                log.info("【超时取消】订单 {} 已自动取消，商品 {} 库存回补 {} 件",
                        orderId, order.getGoodid(), order.getBuynumber());
            }

            if (!cancelled) {
                log.info("【超时取消】订单 {} 无需要取消的未支付条目", orderId);
            }

        } catch (Exception e) {
            log.error("【超时取消】处理超时订单失败: {}", e.getMessage(), e);
            // 不 rethrow：自动 ack，依赖定时任务兜底
        }
    }
}
