package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.DiscussagriculturalproductinformationEntity;
import com.cl.entity.view.DiscussagriculturalproductinformationView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * agriculturalproductinformation评论表
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
public interface DiscussagriculturalproductinformationDao extends BaseMapper<DiscussagriculturalproductinformationEntity> {

    List<DiscussagriculturalproductinformationView> selectListView(@Param("ew") Wrapper<DiscussagriculturalproductinformationEntity> wrapper);

    List<DiscussagriculturalproductinformationView> selectListView(Pagination page, @Param("ew") Wrapper<DiscussagriculturalproductinformationEntity> wrapper);

    DiscussagriculturalproductinformationView selectView(@Param("ew") Wrapper<DiscussagriculturalproductinformationEntity> wrapper);


}
