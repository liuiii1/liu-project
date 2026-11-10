package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.dao.AgriculturalproductinformationDao;
import com.cl.entity.AgriculturalproductinformationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品服务内部 RPC 接口（仅供 order-service 经 OpenFeign 调用）
 * 全部 @IgnoreAuth 放行；直接返回业务对象，不包统一 R。
 */
@RestController
@RequestMapping("/rpc/product")
public class RpcProductController {

    @Autowired
    private AgriculturalproductinformationDao productDao;

    @IgnoreAuth
    @GetMapping("/{id}")
    public AgriculturalproductinformationEntity getById(@PathVariable("id") Long id) {
        return productDao.selectById(id);
    }

    @IgnoreAuth
    @PostMapping("/decreaseStock")
    public Integer decreaseStock(@RequestParam("id") Long id, @RequestParam("qty") int qty) {
        return productDao.decreaseStock(id, qty);
    }

    @IgnoreAuth
    @PostMapping("/increaseStock")
    public Integer increaseStock(@RequestParam("id") Long id, @RequestParam("qty") int qty) {
        return productDao.increaseStock(id, qty);
    }

    @IgnoreAuth
    @GetMapping("/count")
    public Integer countByAccount(@RequestParam(value = "account", required = false) String account) {
        if (account == null || account.isEmpty()) {
            return productDao.selectCount(null);
        }
        return productDao.selectCount(
                new EntityWrapper<AgriculturalproductinformationEntity>().eq("account", account));
    }

    @IgnoreAuth
    @PostMapping("/findByNameAndAccount")
    public List<AgriculturalproductinformationEntity> findByNameAndAccount(@RequestParam("productname") String productname,
                                                                           @RequestParam("account") String account) {
        EntityWrapper<AgriculturalproductinformationEntity> ew = new EntityWrapper<>();
        ew.eq("productname", productname).eq("account", account);
        return productDao.selectList(ew);
    }
}
