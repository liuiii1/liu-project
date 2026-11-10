package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.UserEntity;
import com.cl.entity.view.UserView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 用户
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface UserDao extends BaseMapper<UserEntity> {

    List<UserView> selectListView(@Param("ew") Wrapper<UserEntity> wrapper);

    List<UserView> selectListView(Pagination page, @Param("ew") Wrapper<UserEntity> wrapper);

    UserView selectView(@Param("ew") Wrapper<UserEntity> wrapper);

    /**
     * 原子扣减余额（WHERE money >= total 防止透支）
     *
     * @param userId 用户ID
     * @param total  扣减金额
     * @return 受影响行数，0 表示余额不足
     */
    int decreaseMoney(@Param("userId") Long userId, @Param("total") double total);

    /**
     * 原子退回余额（退款/取消订单时调用）
     */
    int increaseMoney(@Param("userId") Long userId, @Param("total") double total);

}
