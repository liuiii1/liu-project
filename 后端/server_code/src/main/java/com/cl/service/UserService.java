package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.UserEntity;
import com.cl.entity.view.UserView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 用户
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<UserView> selectListView(Wrapper<UserEntity> wrapper);

    UserView selectView(@Param("ew") Wrapper<UserEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<UserEntity> wrapper);


}

