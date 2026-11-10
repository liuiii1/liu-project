package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.UsersEntity;
import com.cl.entity.view.UsersView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 管理员
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface UsersService extends IService<UsersEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<UsersView> selectListView(Wrapper<UsersEntity> wrapper);

    UsersView selectView(@Param("ew") Wrapper<UsersEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<UsersEntity> wrapper);


}

