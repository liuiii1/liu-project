package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.StoreupEntity;
import com.cl.entity.view.StoreupView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 我的收藏
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface StoreupService extends IService<StoreupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<StoreupView> selectListView(Wrapper<StoreupEntity> wrapper);

    StoreupView selectView(@Param("ew") Wrapper<StoreupEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<StoreupEntity> wrapper);


}

