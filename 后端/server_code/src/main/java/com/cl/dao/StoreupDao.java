package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.StoreupEntity;
import com.cl.entity.view.StoreupView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 我的收藏
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface StoreupDao extends BaseMapper<StoreupEntity> {

    List<StoreupView> selectListView(@Param("ew") Wrapper<StoreupEntity> wrapper);

    List<StoreupView> selectListView(Pagination page, @Param("ew") Wrapper<StoreupEntity> wrapper);

    StoreupView selectView(@Param("ew") Wrapper<StoreupEntity> wrapper);


}
