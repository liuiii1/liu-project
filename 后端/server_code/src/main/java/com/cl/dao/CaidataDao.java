package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.CaidataEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 采摘小车日志 DAO
 */
public interface CaidataDao extends BaseMapper<CaidataEntity> {

    /** 按供应商 account 查日志列表 */
    @Select("<script>" +
            "SELECT * FROM caidata " +
            "<where>" +
            "<if test='account != null and account != \"\"'>AND account = #{account}</if>" +
            "</where>" +
            "ORDER BY addtime DESC" +
            "</script>")
    List<Map<String, Object>> listByAccount(@Param("account") String account);
}
