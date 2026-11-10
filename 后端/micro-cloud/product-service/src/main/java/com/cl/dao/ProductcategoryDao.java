package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.ProductcategoryEntity;
import com.cl.entity.view.ProductcategoryView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 产品分类
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface ProductcategoryDao extends BaseMapper<ProductcategoryEntity> {

    List<ProductcategoryView> selectListView(@Param("ew") Wrapper<ProductcategoryEntity> wrapper);

    List<ProductcategoryView> selectListView(Pagination page, @Param("ew") Wrapper<ProductcategoryEntity> wrapper);

    ProductcategoryView selectView(@Param("ew") Wrapper<ProductcategoryEntity> wrapper);


}
