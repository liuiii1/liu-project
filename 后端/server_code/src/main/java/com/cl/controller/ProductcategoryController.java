package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.ProductcategoryEntity;
import com.cl.entity.view.ProductcategoryView;
import com.cl.service.ProductcategoryService;
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
 * 产品分类
 * 后端接口
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@RestController
@RequestMapping("/productcategory")
public class ProductcategoryController {
    @Autowired
    private ProductcategoryService productcategoryService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ProductcategoryEntity productcategory,
                  HttpServletRequest request) {
        EntityWrapper<ProductcategoryEntity> ew = new EntityWrapper<ProductcategoryEntity>();


        PageUtils page = productcategoryService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, productcategory), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ProductcategoryEntity productcategory,
                  HttpServletRequest request) {
        EntityWrapper<ProductcategoryEntity> ew = new EntityWrapper<ProductcategoryEntity>();

        PageUtils page = productcategoryService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, productcategory), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(ProductcategoryEntity productcategory) {
        EntityWrapper<ProductcategoryEntity> ew = new EntityWrapper<ProductcategoryEntity>();
        ew.allEq(MPUtil.allEQMapPre(productcategory, "productcategory"));
        return R.ok().put("data", productcategoryService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ProductcategoryEntity productcategory) {
        EntityWrapper<ProductcategoryEntity> ew = new EntityWrapper<ProductcategoryEntity>();
        ew.allEq(MPUtil.allEQMapPre(productcategory, "productcategory"));
        ProductcategoryView productcategoryView = productcategoryService.selectView(ew);
        return R.ok("查询产品分类成功").put("data", productcategoryView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ProductcategoryEntity productcategory = productcategoryService.selectById(id);
        productcategory = productcategoryService.selectView(new EntityWrapper<ProductcategoryEntity>().eq("id", id));
        return R.ok().put("data", productcategory);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        ProductcategoryEntity productcategory = productcategoryService.selectById(id);
        productcategory = productcategoryService.selectView(new EntityWrapper<ProductcategoryEntity>().eq("id", id));
        return R.ok().put("data", productcategory);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ProductcategoryEntity productcategory, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(productcategory);
        productcategoryService.insert(productcategory);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ProductcategoryEntity productcategory, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(productcategory);
        productcategoryService.insert(productcategory);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ProductcategoryEntity productcategory, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(productcategory);
        productcategoryService.updateById(productcategory);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        productcategoryService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
