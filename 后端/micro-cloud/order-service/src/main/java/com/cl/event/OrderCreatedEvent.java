package com.cl.event;

/**
 * 订单创建事件。
 *
 * 用 Spring Event 机制把"发 MQ 消息"从 createOrder 主链路解耦：
 *   - 在 createOrder 的 @Transactional 方法内 publish 事件
 *   - @TransactionalEventListener(phase=AFTER_COMMIT) 监听器在事务提交后才真正发消息
 *
 * 为什么不在 createOrder 里直接发 MQ？
 *   因为如果 DB 事务回滚（比如后面的 Feign 扣库存失败），MQ 消息已经发出去了，
 *   消费者会收到一条指向不存在订单的幽灵消息。
 *
 * 只传业务主键和最小必要字段，避免传递大对象。
 */
public class OrderCreatedEvent {

    /** 订单 ID（orderid，不是数据库自增 id） */
    private final String orderId;

    /** 用户 ID */
    private final Long userId;

    /** 订单总金额（可能多订单条目共享同一个 orderId） */
    private final double total;

    /** 商品条目数 */
    private final int itemCount;

    /** true=即时支付（status=已支付），false=待支付（status=未支付，15 分钟超时自动取消） */
    private final boolean paidNow;

    public OrderCreatedEvent(String orderId, Long userId, double total, int itemCount, boolean paidNow) {
        this.orderId = orderId;
        this.userId = userId;
        this.total = total;
        this.itemCount = itemCount;
        this.paidNow = paidNow;
    }

    public String getOrderId() { return orderId; }
    public Long getUserId()     { return userId; }
    public double getTotal()    { return total; }
    public int getItemCount()   { return itemCount; }
    public boolean isPaidNow()  { return paidNow; }
}
