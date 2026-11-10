package com.cl.feign;

import com.cl.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务远程调用（order-service -> user-service）
 * 直连服务名走 Nacos 负载均衡，路径不带网关 /project 前缀。
 * 端点在 user-service 的 RpcUserController 暴露，@IgnoreAuth 放行。
 */
@FeignClient(name = "user-service")
public interface UserFeignClient {

    /** 按主键查用户 */
    @GetMapping("/rpc/user/{id}")
    UserEntity getById(@PathVariable("id") Long id);

    /** 原子扣减余额，返回受影响行数（0=余额不足） */
    @PostMapping("/rpc/user/decreaseMoney")
    Integer decreaseMoney(@RequestParam("userId") Long userId, @RequestParam("total") double total);

    /** 原子回补余额（退款/取消），返回受影响行数 */
    @PostMapping("/rpc/user/increaseMoney")
    Integer increaseMoney(@RequestParam("userId") Long userId, @RequestParam("total") double total);

    /** 用户总数 */
    @GetMapping("/rpc/user/count")
    Integer count();
}
