package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.AgriculturalproductinformationEntity;
import com.cl.entity.view.AgriculturalproductinformationView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 农产品信息
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface AgriculturalproductinformationDao extends BaseMapper<AgriculturalproductinformationEntity> {

    List<AgriculturalproductinformationView> selectListView(@Param("ew") Wrapper<AgriculturalproductinformationEntity> wrapper);

    List<AgriculturalproductinformationView> selectListView(Pagination page, @Param("ew") Wrapper<AgriculturalproductinformationEntity> wrapper);

    AgriculturalproductinformationView selectView(@Param("ew") Wrapper<AgriculturalproductinformationEntity> wrapper);

    /**
     * 原子扣减库存（WHERE alllimittimes >= qty 防止超卖）
     *
     * @param id  商品ID
     * @param qty 购买数量
     * @return 受影响行数，0 表示库存不足
     */
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);

    /**
     * 原子回补库存（退款/取消订单时调用）
     */
    int increaseStock(@Param("id") Long id, @Param("qty") int qty);

}
