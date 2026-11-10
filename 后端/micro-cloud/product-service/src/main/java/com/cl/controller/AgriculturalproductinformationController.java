package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.AgriculturalproductinformationEntity;
import com.cl.entity.view.AgriculturalproductinformationView;
import com.cl.service.AgriculturalproductinformationService;
import com.cl.service.StoreupService;
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
 * 农产品信息
 * 后端接口
 */
@RestController
@RequestMapping("/agriculturalproductinformation")
public class AgriculturalproductinformationController {

    @Autowired
    private AgriculturalproductinformationService agriculturalproductinformationService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, AgriculturalproductinformationEntity agriculturalproductinformation,
                  HttpServletRequest request) {
        String tableName = request.getSession().getAttribute("tableName").toString();
        if (tableName.equals("supplier")) {
            agriculturalproductinformation.setAccount((String) request.getSession().getAttribute("username"));
        }
        EntityWrapper<AgriculturalproductinformationEntity> ew = new EntityWrapper<AgriculturalproductinformationEntity>();


        PageUtils page = agriculturalproductinformationService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, agriculturalproductinformation), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, AgriculturalproductinformationEntity agriculturalproductinformation,
                  HttpServletRequest request) {
        EntityWrapper<AgriculturalproductinformationEntity> ew = new EntityWrapper<AgriculturalproductinformationEntity>();

        PageUtils page = agriculturalproductinformationService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, agriculturalproductinformation), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(AgriculturalproductinformationEntity agriculturalproductinformation) {
        EntityWrapper<AgriculturalproductinformationEntity> ew = new EntityWrapper<AgriculturalproductinformationEntity>();
        ew.allEq(MPUtil.allEQMapPre(agriculturalproductinformation, "agriculturalproductinformation"));
        return R.ok().put("data", agriculturalproductinformationService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(AgriculturalproductinformationEntity agriculturalproductinformation) {
        EntityWrapper<AgriculturalproductinformationEntity> ew = new EntityWrapper<AgriculturalproductinformationEntity>();
        ew.allEq(MPUtil.allEQMapPre(agriculturalproductinformation, "agriculturalproductinformation"));
        AgriculturalproductinformationView agriculturalproductinformationView = agriculturalproductinformationService.selectView(ew);
        return R.ok("查询农产品信息成功").put("data", agriculturalproductinformationView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        AgriculturalproductinformationEntity agriculturalproductinformation = agriculturalproductinformationService.selectById(id);
        agriculturalproductinformation = agriculturalproductinformationService.selectView(new EntityWrapper<AgriculturalproductinformationEntity>().eq("id", id));
        return R.ok().put("data", agriculturalproductinformation);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        AgriculturalproductinformationEntity agriculturalproductinformation = agriculturalproductinformationService.selectById(id);
        agriculturalproductinformation = agriculturalproductinformationService.selectView(new EntityWrapper<AgriculturalproductinformationEntity>().eq("id", id));
        return R.ok().put("data", agriculturalproductinformation);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AgriculturalproductinformationEntity agriculturalproductinformation, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(agriculturalproductinformation);
        agriculturalproductinformationService.insert(agriculturalproductinformation);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody AgriculturalproductinformationEntity agriculturalproductinformation, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(agriculturalproductinformation);
        agriculturalproductinformationService.insert(agriculturalproductinformation);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody AgriculturalproductinformationEntity agriculturalproductinformation, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(agriculturalproductinformation);
        agriculturalproductinformationService.updateById(agriculturalproductinformation);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        agriculturalproductinformationService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    @RequestMapping("/ceshi")
    public void ceshi() {

        System.out.println("111111111111111111");
    }

}
