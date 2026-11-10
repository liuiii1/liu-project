package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.ProductcategoryDao;
import com.cl.entity.ProductcategoryEntity;
import com.cl.entity.view.ProductcategoryView;
import com.cl.service.ProductcategoryService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("productcategoryService")
public class ProductcategoryServiceImpl extends ServiceImpl<ProductcategoryDao, ProductcategoryEntity> implements ProductcategoryService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ProductcategoryEntity> page = this.selectPage(
                new Query<ProductcategoryEntity>(params).getPage(),
                new EntityWrapper<ProductcategoryEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ProductcategoryEntity> wrapper) {
        Page<ProductcategoryView> page = new Query<ProductcategoryView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<ProductcategoryView> selectListView(Wrapper<ProductcategoryEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public ProductcategoryView selectView(Wrapper<ProductcategoryEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
