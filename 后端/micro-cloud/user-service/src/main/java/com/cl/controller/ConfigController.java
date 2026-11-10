package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.ConfigEntity;
import com.cl.entity.view.ConfigView;
import com.cl.service.ConfigService;
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
 * 轮播图
 * 后端接口
 */
@RestController
@RequestMapping("/config")
public class ConfigController {
    @Autowired
    private ConfigService configService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ConfigEntity config,
                  HttpServletRequest request) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();


        PageUtils page = configService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, config), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ConfigEntity config,
                  HttpServletRequest request) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();

        PageUtils page = configService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, config), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(ConfigEntity config) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
        ew.allEq(MPUtil.allEQMapPre(config, "config"));
        return R.ok().put("data", configService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ConfigEntity config) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
        ew.allEq(MPUtil.allEQMapPre(config, "config"));
        ConfigView configView = configService.selectView(ew);
        return R.ok("查询轮播图成功").put("data", configView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ConfigEntity config = configService.selectById(id);
        config = configService.selectView(new EntityWrapper<ConfigEntity>().eq("id", id));
        return R.ok().put("data", config);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        ConfigEntity config = configService.selectById(id);
        config = configService.selectView(new EntityWrapper<ConfigEntity>().eq("id", id));
        return R.ok().put("data", config);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ConfigEntity config, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(config);
        configService.insert(config);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ConfigEntity config, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(config);
        configService.insert(config);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ConfigEntity config, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(config);
        configService.updateById(config);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        configService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
