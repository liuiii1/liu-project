package com.cl.feign;

import com.cl.entity.AgriculturalproductinformationEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商品服务远程调用（order-service -> product-service）
 * 端点在 product-service 的 RpcProductController 暴露，@IgnoreAuth 放行。
 */
@FeignClient(name = "product-service")
public interface ProductFeignClient {

    /** 按主键查商品 */
    @GetMapping("/rpc/product/{id}")
    AgriculturalproductinformationEntity getById(@PathVariable("id") Long id);

    /** 原子扣减库存，返回受影响行数（0=库存不足） */
    @PostMapping("/rpc/product/decreaseStock")
    Integer decreaseStock(@RequestParam("id") Long id, @RequestParam("qty") int qty);

    /** 原子回补库存（退款/取消/小车采收），qty 支持负数，返回受影响行数 */
    @PostMapping("/rpc/product/increaseStock")
    Integer increaseStock(@RequestParam("id") Long id, @RequestParam("qty") int qty);

    /** 商品总数；account 非空时只统计该供应商的商品 */
    @GetMapping("/rpc/product/count")
    Integer countByAccount(@RequestParam(value = "account", required = false) String account);

    /** 按 商品名 + 供应商账号 查商品（小车采收上报按名称定位商品用） */
    @PostMapping("/rpc/product/findByNameAndAccount")
    List<AgriculturalproductinformationEntity> findByNameAndAccount(@RequestParam("productname") String productname,
                                                                    @RequestParam("account") String account);
}
