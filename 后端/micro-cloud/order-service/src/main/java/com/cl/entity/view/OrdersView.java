package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.OrdersEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 商品订单
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:51
 */
@TableName("orders")
public class OrdersView extends OrdersEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public OrdersView() {
    }

    public OrdersView(OrdersEntity ordersEntity) {
        try {
            BeanUtils.copyProperties(this, ordersEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
