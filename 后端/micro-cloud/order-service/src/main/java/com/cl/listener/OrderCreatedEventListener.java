package com.cl.listener;

import com.alibaba.fastjson.JSONObject;
import com.cl.constant.MqConstants;
import com.cl.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * OrderCreatedEvent 监听器 —— 事务提交后发 RabbitMQ 消息。
 *
 * phase=AFTER_COMMIT 保证：
 *   1. 只有 DB 事务提交成功才发 MQ
 *   2. 如果事务回滚（createOrder 里任何 Feign/DB 操作失败），MQ 消息不会发
 *
 * 发什么消息：
 *   - paidNow=true  → 发 order.success 两条消息（日志 + 通知）
 *   - paidNow=false → 发 order.timeout 延迟消息（15 分钟后过期 → 死信队列 → 超时取消消费者）
 */
@Component
public class OrderCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventListener.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        JSONObject msg = new JSONObject();
        msg.put("orderId", event.getOrderId());
        msg.put("userId", event.getUserId());
        msg.put("total", event.getTotal());
        msg.put("itemCount", event.getItemCount());
        msg.put("timestamp", System.currentTimeMillis());

        try {
            if (event.isPaidNow()) {
                // 即时支付 → 发订单成功通知（两条，不同路由键 → 两个不同消费者）
                rabbitTemplate.convertAndSend(
                        MqConstants.EXCHANGE_ORDER_SUCCESS,
                        MqConstants.ROUTING_ORDER_SUCCESS_LOG,
                        msg.toJSONString());
                rabbitTemplate.convertAndSend(
                        MqConstants.EXCHANGE_ORDER_SUCCESS,
                        MqConstants.ROUTING_ORDER_SUCCESS_NOTIFY,
                        msg.toJSONString());
                log.info("【MQ】订单已支付，发成功通知消息，orderId={}", event.getOrderId());

            } else {
                // 待支付 → 发延迟消息（TTL 15 分钟，过期后自动转到死信队列）
                rabbitTemplate.convertAndSend(
                        MqConstants.EXCHANGE_ORDER_TIMEOUT,
                        MqConstants.ROUTING_ORDER_TIMEOUT,
                        msg.toJSONString());
                log.info("【MQ】待支付订单，发 15 分钟延迟消息，orderId={}", event.getOrderId());
            }
        } catch (Exception e) {
            // MQ 发送失败不回滚已提交的 DB 事务（数据已入库）
            // 后续可以用定时任务兜底扫未支付超时订单
            log.error("【MQ】订单消息发送失败（DB 事务已提交，不回滚），orderId={}: {}",
                    event.getOrderId(), e.getMessage());
        }
    }
}
