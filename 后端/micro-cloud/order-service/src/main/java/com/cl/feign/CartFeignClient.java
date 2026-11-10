package com.cl.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 购物车服务远程调用（order-service -> cart-service）
 * 端点在 cart-service 的 RpcCartController 暴露，@IgnoreAuth 放行。
 */
@FeignClient(name = "cart-service")
public interface CartFeignClient {

    /** 按主键批量删除购物车条目（下单成功后清理） */
    @PostMapping("/rpc/cart/deleteBatch")
    void deleteBatch(@RequestParam("ids") List<Long> ids);

    /** 按 用户ID + 商品ID 删除（未传 cartId 时的兜底清理） */
    @PostMapping("/rpc/cart/deleteByUserAndGood")
    void deleteByUserAndGood(@RequestParam("userid") Long userid, @RequestParam("goodid") Long goodid);
}
