package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.OrdersEntity;
import com.cl.entity.view.OrdersView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 商品订单
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
public interface OrdersService extends IService<OrdersEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<OrdersView> selectListView(Wrapper<OrdersEntity> wrapper);

    OrdersView selectView(@Param("ew") Wrapper<OrdersEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<OrdersEntity> wrapper);


}

