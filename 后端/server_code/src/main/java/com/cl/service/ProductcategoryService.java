package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ProductcategoryEntity;
import com.cl.entity.view.ProductcategoryView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 产品分类
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface ProductcategoryService extends IService<ProductcategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductcategoryView> selectListView(Wrapper<ProductcategoryEntity> wrapper);

    ProductcategoryView selectView(@Param("ew") Wrapper<ProductcategoryEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ProductcategoryEntity> wrapper);


}

