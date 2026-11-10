package com.cl.config;

import com.cl.constant.MqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 交换机 / 队列 / 绑定 声明。
 *
 * 用 TTL + Dead Letter Exchange 模式实现订单延迟超时取消，
 * 不依赖 rabbitmq-delayed-message-exchange 插件。
 *
 * 延迟取消流程：
 *   1. 下单成功 → 发消息到 order.timeout.exchange → 进入 order.timeout.queue
 *      （该队列配置了 x-message-ttl=15min + x-dead-letter-exchange=order.timeout.dlx.exchange）
 *   2. 消息在 order.timeout.queue 停留 15 分钟
 *   3. 过期后自动转到 order.timeout.dlx.exchange → 路由到 order.timeout.dlx.queue
 *   4. 消费者监听 order.timeout.dlx.queue，检查订单状态 → 未支付则取消
 *
 * 订单成功通知流程：
 *   1. 下单成功 → 发消息到 order.success.exchange
 *   2. 路由到 log 队列（写日志）+ notify 队列（积分/通知预留）
 *   3. 各消费者独立处理，互不阻塞
 */
@Configuration
public class RabbitMqConfig {

    // ========== 1. 订单超时：死信交换机 + 死信队列（消费者监听这里）==========

    @Bean
    public DirectExchange orderTimeoutDlxExchange() {
        return new DirectExchange(MqConstants.EXCHANGE_ORDER_TIMEOUT_DLX, true, false);
    }

    @Bean
    public Queue orderTimeoutDlxQueue() {
        return new Queue(MqConstants.QUEUE_ORDER_TIMEOUT_DLX, true);
    }

    @Bean
    public Binding orderTimeoutDlxBinding() {
        return BindingBuilder.bind(orderTimeoutDlxQueue())
                .to(orderTimeoutDlxExchange())
                .with(MqConstants.ROUTING_ORDER_TIMEOUT_DLX);
    }

    // ========== 2. 订单超时：TTL 交换机 + TTL 队列（消息在这里停 15 分钟）==========

    @Bean
    public DirectExchange orderTimeoutExchange() {
        return new DirectExchange(MqConstants.EXCHANGE_ORDER_TIMEOUT, true, false);
    }

    /**
     * TTL 队列：
     * - x-message-ttl = 15 分钟（毫秒）
     * - x-dead-letter-exchange = 死信交换机（消息过期后自动转到这里）
     * - x-dead-letter-routing-key = 死信路由键
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(MqConstants.QUEUE_ORDER_TIMEOUT)
                .withArgument("x-message-ttl", MqConstants.ORDER_TIMEOUT_MS)
                .withArgument("x-dead-letter-exchange", MqConstants.EXCHANGE_ORDER_TIMEOUT_DLX)
                .withArgument("x-dead-letter-routing-key", MqConstants.ROUTING_ORDER_TIMEOUT_DLX)
                .withArgument("x-max-length", 10000) // 队列长度保护，防止消息堆积
                .build();
    }

    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutQueue())
                .to(orderTimeoutExchange())
                .with(MqConstants.ROUTING_ORDER_TIMEOUT);
    }

    // ========== 3. 订单成功通知：direct 交换机 + 多个队列 ==========

    @Bean
    public DirectExchange orderSuccessExchange() {
        return new DirectExchange(MqConstants.EXCHANGE_ORDER_SUCCESS, true, false);
    }

    @Bean
    public Queue orderSuccessLogQueue() {
        return new Queue(MqConstants.QUEUE_ORDER_SUCCESS_LOG, true);
    }

    @Bean
    public Queue orderSuccessNotifyQueue() {
        return new Queue(MqConstants.QUEUE_ORDER_SUCCESS_NOTIFY, true);
    }

    @Bean
    public Binding orderSuccessLogBinding() {
        return BindingBuilder.bind(orderSuccessLogQueue())
                .to(orderSuccessExchange())
                .with(MqConstants.ROUTING_ORDER_SUCCESS_LOG);
    }

    @Bean
    public Binding orderSuccessNotifyBinding() {
        return BindingBuilder.bind(orderSuccessNotifyQueue())
                .to(orderSuccessExchange())
                .with(MqConstants.ROUTING_ORDER_SUCCESS_NOTIFY);
    }
}
