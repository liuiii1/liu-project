package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.SupplierEntity;
import com.cl.entity.view.SupplierView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 供应商
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface SupplierService extends IService<SupplierEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SupplierView> selectListView(Wrapper<SupplierEntity> wrapper);

    SupplierView selectView(@Param("ew") Wrapper<SupplierEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<SupplierEntity> wrapper);


}

