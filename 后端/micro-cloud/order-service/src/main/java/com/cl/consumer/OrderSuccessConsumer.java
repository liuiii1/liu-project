package com.cl.consumer;

import com.alibaba.fastjson.JSONObject;
import com.cl.constant.MqConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单成功异步通知消费者。
 *
 * 下单成功后主链路立即返回给前端，同时 RabbitMQ 发送两条消息：
 *   - order.success.log.queue   → 本消费者：记录业务日志（解耦主链路，不阻塞下单接口响应）
 *   - order.success.notify.queue → 另一个消费者：预留积分增加/站内通知/短信推送
 *
 * 为什么拆两条？
 *   日志是必须的、低延迟的；通知可能慢（调别的服务、发短信），拆开后互不影响。
 *   未来要加更多通知渠道（短信、邮件、微信推送），只需要新增队列+消费者，不改下单代码。
 */
@Component
public class OrderSuccessConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderSuccessConsumer.class);

    /**
     * 订单成功 → 写业务日志（核心日志：谁、什么时候、买了什么、花了多少钱）。
     * 失败不阻塞主链路，重试交给 MQ 重投递。
     */
    @RabbitListener(queues = MqConstants.QUEUE_ORDER_SUCCESS_LOG)
    public void onOrderSuccessLog(String message) {
        try {
            JSONObject json = JSONObject.parseObject(message);
            String orderId = json.getString("orderId");
            Long userId = json.getLong("userId");
            double total = json.getDoubleValue("total");
            int itemCount = json.getIntValue("itemCount");

            // 业务日志（未来可以落到独立表、ES、或推到别的日志系统）
            log.info("【订单成功日志】orderId={} userId={} 金额={} 商品数={}",
                    orderId, userId, total, itemCount);

        } catch (Exception e) {
            log.error("【订单成功日志】处理失败: {} | 原消息: {}", e.getMessage(), message, e);
            // 不 rethrow：自动 ack，避免无限重试打爆日志
            // 需要可靠投递的话改成手动 ack + 失败时 nack/requeue + 死信队列
        }
    }

    /**
     * 订单成功 → 预留积分/通知接口。
     * 当前仅打日志占位；未来积分系统/消息中心接入后在这里扩展即可。
     */
    @RabbitListener(queues = MqConstants.QUEUE_ORDER_SUCCESS_NOTIFY)
    public void onOrderSuccessNotify(String message) {
        try {
            JSONObject json = JSONObject.parseObject(message);
            String orderId = json.getString("orderId");
            Long userId = json.getLong("userId");

            log.info("【订单成功通知】orderId={} userId={} — 预留积分/通知入口", orderId, userId);

            // ==== 未来接入点 ====
            // 1. 积分：userFeignClient.increasePoints(userId, itemCount)
            // 2. 站内通知：pushService.notify(userId, "您的订单已下单...")
            // 3. 短信：smsClient.send(userIdPhone, "订单号" + orderId)
            // 4. 邮件 / 微信模板消息 等
            // =====================

        } catch (Exception e) {
            log.error("【订单成功通知】处理失败: {} | 原消息: {}", e.getMessage(), message, e);
        }
    }
}
