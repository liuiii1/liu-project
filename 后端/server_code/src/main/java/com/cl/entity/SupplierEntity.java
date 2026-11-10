package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;


/**
 * 供应商
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@TableName("supplier")
public class SupplierEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    private Long id;
    /**
     * 账号
     */

    private String account;
    /**
     * 密码
     */

    private String password;
    /**
     * 名称
     */

    private String name;
    /**
     * 联系电话
     */

    private String contactPhone;
    /**
     * 地址
     */

    private String address;

    /** 采摘小车启用标志（1=启用，0=未启用） */
    private Integer useCart;

    /** 绑定的采摘小车编号 */
    private String deviceToken;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public SupplierEntity() {

    }


    public SupplierEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：账号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置：账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取：密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置：密码
     */
    public void setPassword(String password) {
        this.password = password;
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
     * 获取：联系电话
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * 设置：联系电话
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * 获取：地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置：地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getUseCart() {
        return useCart;
    }

    public void setUseCart(Integer useCart) {
        this.useCart = useCart;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

}
