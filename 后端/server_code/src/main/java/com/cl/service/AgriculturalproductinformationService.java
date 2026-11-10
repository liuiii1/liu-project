package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.AgriculturalproductinformationEntity;
import com.cl.entity.view.AgriculturalproductinformationView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 农产品信息
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface AgriculturalproductinformationService extends IService<AgriculturalproductinformationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<AgriculturalproductinformationView> selectListView(Wrapper<AgriculturalproductinformationEntity> wrapper);

    AgriculturalproductinformationView selectView(@Param("ew") Wrapper<AgriculturalproductinformationEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<AgriculturalproductinformationEntity> wrapper);


}

