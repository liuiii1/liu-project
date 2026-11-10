package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 轮播图
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@TableName("config")
public class ConfigEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 名称
     */

    private String name;
    /**
     * 值
     */

    private String value;
    public ConfigEntity() {

    }

    public ConfigEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置：名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取：值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置：值
     */
    public void setValue(String value) {
        this.value = value;
    }

}
