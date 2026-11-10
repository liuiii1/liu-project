package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.SupplierEntity;
import com.cl.entity.view.SupplierView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 供应商
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface SupplierDao extends BaseMapper<SupplierEntity> {

    List<SupplierView> selectListView(@Param("ew") Wrapper<SupplierEntity> wrapper);

    List<SupplierView> selectListView(Pagination page, @Param("ew") Wrapper<SupplierEntity> wrapper);

    SupplierView selectView(@Param("ew") Wrapper<SupplierEntity> wrapper);


}
