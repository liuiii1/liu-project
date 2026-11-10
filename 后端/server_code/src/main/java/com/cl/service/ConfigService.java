package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ConfigEntity;
import com.cl.entity.view.ConfigView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 轮播图
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface ConfigService extends IService<ConfigEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ConfigView> selectListView(Wrapper<ConfigEntity> wrapper);

    ConfigView selectView(@Param("ew") Wrapper<ConfigEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ConfigEntity> wrapper);


}

