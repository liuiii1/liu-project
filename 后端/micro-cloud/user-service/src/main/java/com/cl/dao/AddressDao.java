package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.AddressEntity;
import com.cl.entity.view.AddressView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 地址
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
public interface AddressDao extends BaseMapper<AddressEntity> {

    List<AddressView> selectListView(@Param("ew") Wrapper<AddressEntity> wrapper);

    List<AddressView> selectListView(Pagination page, @Param("ew") Wrapper<AddressEntity> wrapper);

    AddressView selectView(@Param("ew") Wrapper<AddressEntity> wrapper);


}
