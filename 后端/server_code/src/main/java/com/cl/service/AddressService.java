package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.AddressEntity;
import com.cl.entity.view.AddressView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 地址
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
public interface AddressService extends IService<AddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<AddressView> selectListView(Wrapper<AddressEntity> wrapper);

    AddressView selectView(@Param("ew") Wrapper<AddressEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<AddressEntity> wrapper);


}

