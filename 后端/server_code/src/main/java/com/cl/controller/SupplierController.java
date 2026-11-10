package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.SupplierEntity;
import com.cl.entity.view.SupplierView;
import com.cl.service.SupplierService;
import com.cl.service.TokenService;
import com.cl.utils.MD5Util;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 供应商
 * 后端接口
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@RestController
@RequestMapping("/supplier")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;


    @Autowired
    private TokenService tokenService;

    /**
     * 登录
     */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        SupplierEntity u = supplierService.selectOne(new EntityWrapper<SupplierEntity>().eq("account", username));
        if (u == null || !MD5Util.verifyPassword(password, u.getPassword())) {
            return R.error("账号或密码不正确");
        }
        String token = tokenService.generateToken(u.getId(), username, "supplier", "供应商");
        return R.ok().put("token", token);
    }


    /**
     * 注册
     */
    @IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody SupplierEntity supplier) {
        //ValidatorUtils.validateEntity(supplier);
        SupplierEntity u = supplierService.selectOne(new EntityWrapper<SupplierEntity>().eq("account", supplier.getAccount()));
        if (u != null) {
            return R.error("注册用户已存在");
        }
        Long uId = new Date().getTime();
        supplier.setId(uId);
        // 密码加盐 MD5 加密
        supplier.setPassword(MD5Util.md5WithSalt(supplier.getPassword()));
        supplierService.insert(supplier);
        return R.ok();
    }


    /**
     * 退出
     */
    @RequestMapping("/logout")
    public R logout(HttpServletRequest request) {
        // 销毁数据库中的 Token 记录
        String token = request.getHeader("Token");
        if (token != null) {
            tokenService.delete(new com.baomidou.mybatisplus.mapper.EntityWrapper<com.cl.entity.TokenEntity>().eq("token", token));
        }
        request.getSession().invalidate();
        return R.ok("退出成功");
    }

    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request) {
        Long id = (Long) request.getSession().getAttribute("userId");
        return R.ok().put("data", supplierService.selectView(new EntityWrapper<SupplierEntity>().eq("id", id)));
    }

    /**
     * 密码重置
     */
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, String oldPassword, String newPassword, HttpServletRequest request) {
        // 先校验旧密码
        SupplierEntity u = supplierService.selectOne(new EntityWrapper<SupplierEntity>().eq("account", username));
        if (u == null) {
            return R.error("账号不存在");
        }
        if (!MD5Util.verifyPassword(oldPassword, u.getPassword())) {
            return R.error("旧密码不正确");
        }
        u.setPassword(MD5Util.md5WithSalt(newPassword));
        supplierService.updateById(u);
        return R.ok("密码重置成功");
    }


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, SupplierEntity supplier,
                  HttpServletRequest request) {
        EntityWrapper<SupplierEntity> ew = new EntityWrapper<SupplierEntity>();


        PageUtils page = supplierService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, supplier), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, SupplierEntity supplier,
                  HttpServletRequest request) {
        EntityWrapper<SupplierEntity> ew = new EntityWrapper<SupplierEntity>();

        PageUtils page = supplierService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, supplier), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(SupplierEntity supplier) {
        EntityWrapper<SupplierEntity> ew = new EntityWrapper<SupplierEntity>();
        ew.allEq(MPUtil.allEQMapPre(supplier, "supplier"));
        return R.ok().put("data", supplierService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(SupplierEntity supplier) {
        EntityWrapper<SupplierEntity> ew = new EntityWrapper<SupplierEntity>();
        ew.allEq(MPUtil.allEQMapPre(supplier, "supplier"));
        SupplierView supplierView = supplierService.selectView(ew);
        return R.ok("查询供应商成功").put("data", supplierView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SupplierEntity supplier = supplierService.selectById(id);
        supplier = supplierService.selectView(new EntityWrapper<SupplierEntity>().eq("id", id));
        return R.ok().put("data", supplier);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        SupplierEntity supplier = supplierService.selectById(id);
        supplier = supplierService.selectView(new EntityWrapper<SupplierEntity>().eq("id", id));
        return R.ok().put("data", supplier);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SupplierEntity supplier, HttpServletRequest request) {
        if (supplierService.selectCount(new EntityWrapper<SupplierEntity>().eq("account", supplier.getAccount())) > 0) {
            return R.error("账号已存在");
        }
        supplier.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        //ValidatorUtils.validateEntity(supplier);
        SupplierEntity u = supplierService.selectOne(new EntityWrapper<SupplierEntity>().eq("account", supplier.getAccount()));
        if (u != null) {
            return R.error("用户已存在");
        }
        supplier.setId(new Date().getTime());
        // 密码加盐 MD5 加密
        supplier.setPassword(MD5Util.md5WithSalt(supplier.getPassword()));
        supplierService.insert(supplier);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody SupplierEntity supplier, HttpServletRequest request) {
        if (supplierService.selectCount(new EntityWrapper<SupplierEntity>().eq("account", supplier.getAccount())) > 0) {
            return R.error("账号已存在");
        }
        supplier.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        //ValidatorUtils.validateEntity(supplier);
        SupplierEntity u = supplierService.selectOne(new EntityWrapper<SupplierEntity>().eq("account", supplier.getAccount()));
        if (u != null) {
            return R.error("用户已存在");
        }
        supplier.setId(new Date().getTime());
        // 密码加盐 MD5 加密
        supplier.setPassword(MD5Util.md5WithSalt(supplier.getPassword()));
        supplierService.insert(supplier);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody SupplierEntity supplier, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(supplier);
        supplierService.updateById(supplier);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        supplierService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
