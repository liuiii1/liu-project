package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.UsersEntity;
import com.cl.entity.view.UsersView;
import com.cl.service.TokenService;
import com.cl.service.UsersService;
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
 * 管理员
 * 后端接口
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;


    @Autowired
    private TokenService tokenService;

    /**
     * 登录
     */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        UsersEntity u = usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
        if (u == null || !MD5Util.verifyPassword(password, u.getPassword())) {
            return R.error("账号或密码不正确");
        }
        String token = tokenService.generateToken(u.getId(), username, "users", "管理员");
        return R.ok().put("token", token);
    }


    /**
     * 注册
     */
    @IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody UsersEntity users) {
        //ValidatorUtils.validateEntity(users);
        UsersEntity u = usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", users.getUsername()));
        if (u != null) {
            return R.error("注册用户已存在");
        }
        Long uId = new Date().getTime();
        users.setId(uId);
        // 密码加盐 MD5 加密
        users.setPassword(MD5Util.md5WithSalt(users.getPassword()));
        usersService.insert(users);
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
        return R.ok().put("data", usersService.selectView(new EntityWrapper<UsersEntity>().eq("id", id)));
    }

    /**
     * 密码重置
     */
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, String oldPassword, String newPassword, HttpServletRequest request) {
        // 先校验旧密码
        UsersEntity u = usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
        if (u == null) {
            return R.error("账号不存在");
        }
        if (!MD5Util.verifyPassword(oldPassword, u.getPassword())) {
            return R.error("旧密码不正确");
        }
        u.setPassword(MD5Util.md5WithSalt(newPassword));
        usersService.updateById(u);
        return R.ok("密码重置成功");
    }


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, UsersEntity users,
                  HttpServletRequest request) {
        EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();


        PageUtils page = usersService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, users), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, UsersEntity users,
                  HttpServletRequest request) {
        EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();

        PageUtils page = usersService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, users), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(UsersEntity users) {
        EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
        ew.allEq(MPUtil.allEQMapPre(users, "users"));
        return R.ok().put("data", usersService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(UsersEntity users) {
        EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
        ew.allEq(MPUtil.allEQMapPre(users, "users"));
        UsersView usersView = usersService.selectView(ew);
        return R.ok("查询管理员成功").put("data", usersView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        UsersEntity users = usersService.selectById(id);
        users = usersService.selectView(new EntityWrapper<UsersEntity>().eq("id", id));
        return R.ok().put("data", users);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        UsersEntity users = usersService.selectById(id);
        users = usersService.selectView(new EntityWrapper<UsersEntity>().eq("id", id));
        return R.ok().put("data", users);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UsersEntity users, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(users);
        UsersEntity u = usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", users.getUsername()));
        if (u != null) {
            return R.error("用户已存在");
        }
        users.setId(new Date().getTime());
        // 密码加盐 MD5 加密
        users.setPassword(MD5Util.md5WithSalt(users.getPassword()));
        usersService.insert(users);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody UsersEntity users, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(users);
        UsersEntity u = usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", users.getUsername()));
        if (u != null) {
            return R.error("用户已存在");
        }
        users.setId(new Date().getTime());
        // 密码加盐 MD5 加密
        users.setPassword(MD5Util.md5WithSalt(users.getPassword()));
        usersService.insert(users);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody UsersEntity users, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(users);
        usersService.updateById(users);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        usersService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
