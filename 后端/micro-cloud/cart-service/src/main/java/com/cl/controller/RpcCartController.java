package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.dao.CartDao;
import com.cl.entity.CartEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车服务内部 RPC 接口（仅供 order-service 经 OpenFeign 调用）
 * 全部 @IgnoreAuth 放行。
 */
@RestController
@RequestMapping("/rpc/cart")
public class RpcCartController {

    @Autowired
    private CartDao cartDao;

    @IgnoreAuth
    @PostMapping("/deleteBatch")
    public void deleteBatch(@RequestParam("ids") List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            cartDao.deleteBatchIds(ids);
        }
    }

    @IgnoreAuth
    @PostMapping("/deleteByUserAndGood")
    public void deleteByUserAndGood(@RequestParam("userid") Long userid, @RequestParam("goodid") Long goodid) {
        cartDao.delete(new EntityWrapper<CartEntity>()
                .eq("userid", userid)
                .eq("goodid", goodid));
    }
}
