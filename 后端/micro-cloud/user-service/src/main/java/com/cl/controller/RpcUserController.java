package com.cl.controller;

import com.cl.annotation.IgnoreAuth;
import com.cl.dao.UserDao;
import com.cl.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务内部 RPC 接口（仅供 order-service 经 OpenFeign 调用）
 * 全部 @IgnoreAuth 放行：Feign 内部调用不带用户 Token。
 * 直接返回业务对象，不包统一 R，避免 Feign 反序列化后 data 变 LinkedHashMap。
 */
@RestController
@RequestMapping("/rpc/user")
public class RpcUserController {

    @Autowired
    private UserDao userDao;

    @IgnoreAuth
    @GetMapping("/{id}")
    public UserEntity getById(@PathVariable("id") Long id) {
        return userDao.selectById(id);
    }

    @IgnoreAuth
    @PostMapping("/decreaseMoney")
    public Integer decreaseMoney(@RequestParam("userId") Long userId, @RequestParam("total") double total) {
        return userDao.decreaseMoney(userId, total);
    }

    @IgnoreAuth
    @PostMapping("/increaseMoney")
    public Integer increaseMoney(@RequestParam("userId") Long userId, @RequestParam("total") double total) {
        return userDao.increaseMoney(userId, total);
    }

    @IgnoreAuth
    @GetMapping("/count")
    public Integer count() {
        return userDao.selectCount(null);
    }
}
