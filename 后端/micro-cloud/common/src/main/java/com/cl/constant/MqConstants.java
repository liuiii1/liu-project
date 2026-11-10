package com.cl.constant;

/**
 * RabbitMQ 交换机 / 队列 / 路由键常量。
 * 所有生产端和消费端共享同一个命名契约，避免硬编码不一致。
 *
 * 本项目使用 TTL + DLX（Dead Letter Exchange）模式实现延迟消息，
 * 不需要 rabbitmq-delayed-message-exchange 插件，任何 RabbitMQ 版本都支持。
 */
public final class MqConstants {

    private MqConstants() {}

    // ========== 订单超时取消（延迟队列） ==========
    // 交换机：死信交换机（接收超时未支付订单的消息）
    public static final String EXCHANGE_ORDER_TIMEOUT_DLX = "order.timeout.dlx.exchange";
    // 队列：死信队列（真正被消费者监听的队列）
    public static final String QUEUE_ORDER_TIMEOUT_DLX = "order.timeout.dlx.queue";
    // 路由键
    public static final String ROUTING_ORDER_TIMEOUT_DLX = "order.timeout.dlx";

    // 交换机：TTL 消息进入的交换机（消息过期后转发到 DLX）
    public static final String EXCHANGE_ORDER_TIMEOUT = "order.timeout.exchange";
    // 队列：TTL 队列（消息在这里停留 15 分钟，过期后转到 DLX 队列）
    public static final String QUEUE_ORDER_TIMEOUT = "order.timeout.queue";
    // 路由键
    public static final String ROUTING_ORDER_TIMEOUT = "order.timeout";



    public static final long ORDER_TIMEOUT_MS = 15L * 60 * 1000;

    // ========== 订单成功异步通知 ==========
    // 交换机（direct 类型，一条订单成功消息可能被多个消费者消费：日志、积分、通知）
    public static final String EXCHANGE_ORDER_SUCCESS = "order.success.exchange";
    // 队列：订单成功日志
    public static final String QUEUE_ORDER_SUCCESS_LOG = "order.success.log.queue";
    // 队列：订单成功通用通知（预留积分/通知接口）
    public static final String QUEUE_ORDER_SUCCESS_NOTIFY = "order.success.notify.queue";
    // 路由键
    public static final String ROUTING_ORDER_SUCCESS_LOG = "order.success.log";
    public static final String ROUTING_ORDER_SUCCESS_NOTIFY = "order.success.notify";
}
