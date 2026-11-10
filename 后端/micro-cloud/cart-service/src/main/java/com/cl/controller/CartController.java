package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.CartEntity;
import com.cl.entity.view.CartView;
import com.cl.service.CartService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * 购物车
 * 后端接口
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, CartEntity cart,
                  HttpServletRequest request) {
        if (!request.getSession().getAttribute("role").toString().equals("管理员")) {
            cart.setUserid((Long) request.getSession().getAttribute("userId"));
        }
        EntityWrapper<CartEntity> ew = new EntityWrapper<CartEntity>();


        PageUtils page = cartService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, cart), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, CartEntity cart,
                  HttpServletRequest request) {
        // 按角色隔离数据
        if (!request.getSession().getAttribute("role").toString().equals("管理员")) {
            cart.setUserid((Long) request.getSession().getAttribute("userId"));
        }
        EntityWrapper<CartEntity> ew = new EntityWrapper<CartEntity>();

        PageUtils page = cartService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, cart), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(CartEntity cart) {
        EntityWrapper<CartEntity> ew = new EntityWrapper<CartEntity>();
        ew.allEq(MPUtil.allEQMapPre(cart, "cart"));
        return R.ok().put("data", cartService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(CartEntity cart) {
        EntityWrapper<CartEntity> ew = new EntityWrapper<CartEntity>();
        ew.allEq(MPUtil.allEQMapPre(cart, "cart"));
        CartView cartView = cartService.selectView(ew);
        return R.ok("查询购物车成功").put("data", cartView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CartEntity cart = cartService.selectById(id);
        cart = cartService.selectView(new EntityWrapper<CartEntity>().eq("id", id));
        return R.ok().put("data", cart);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        // 归属校验：非管理员只能看自己的购物车
        CartEntity db = cartService.selectById(id);
        if (db != null) {
            String role = (String) request.getSession().getAttribute("role");
            Long userId = (Long) request.getSession().getAttribute("userId");
            if (!"管理员".equals(role) && (userId == null || !userId.equals(db.getUserid()))) {
                return R.error("无权查看此购物车");
            }
        }
        CartEntity cart = cartService.selectById(id);
        cart = cartService.selectView(new EntityWrapper<CartEntity>().eq("id", id));
        return R.ok().put("data", cart);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CartEntity cart, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(cart);
        cart.setUserid((Long) request.getSession().getAttribute("userId"));
        cartService.insert(cart);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody CartEntity cart, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(cart);
        cartService.insert(cart);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody CartEntity cart, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(cart);
        cartService.updateById(cart);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        cartService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
