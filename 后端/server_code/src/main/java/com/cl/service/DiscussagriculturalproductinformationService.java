package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.DiscussagriculturalproductinformationEntity;
import com.cl.entity.view.DiscussagriculturalproductinformationView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * agriculturalproductinformation评论表
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
public interface DiscussagriculturalproductinformationService extends IService<DiscussagriculturalproductinformationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<DiscussagriculturalproductinformationView> selectListView(Wrapper<DiscussagriculturalproductinformationEntity> wrapper);

    DiscussagriculturalproductinformationView selectView(@Param("ew") Wrapper<DiscussagriculturalproductinformationEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussagriculturalproductinformationEntity> wrapper);


}

