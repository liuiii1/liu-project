package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;


/**
 * 用户
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@TableName("user")
public class UserEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    private Long id;
    /**
     * 用户账号
     */

    private String userAccount;
    /**
     * 用户密码
     */

    private String userPassword;
    /**
     * 用户姓名
     */

    private String userName;
    /**
     * 头像
     */

    private String headportrait;
    /**
     * 性别
     */

    private String gender;
    /**
     * 电话号码
     */

    private String phoneNumber;
    /**
     * 余额
     */

    private Double money;
    /**
     * VIP标识（是/否）
     */

    private String vip;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public UserEntity() {

    }


    public UserEntity(T t) {
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
     * 获取：用户账号
     */
    public String getUserAccount() {
        return userAccount;
    }

    /**
     * 设置：用户账号
     */
    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * 获取：用户密码
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * 设置：用户密码
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * 获取：用户姓名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置：用户姓名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取：头像
     */
    public String getHeadportrait() {
        return headportrait;
    }

    /**
     * 设置：头像
     */
    public void setHeadportrait(String headportrait) {
        this.headportrait = headportrait;
    }

    /**
     * 获取：性别
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置：性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 获取：电话号码
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置：电话号码
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 获取：余额
     */
    @JsonSerialize(using = MoneySerializer.class)
    public Double getMoney() {
        return money;
    }

    /**
     * 设置：余额
     */
    public void setMoney(Double money) {
        this.money = money;
    }

    /**
     * 获取：VIP标识
     */
    public String getVip() {
        return vip;
    }

    /**
     * 设置：VIP标识
     */
    public void setVip(String vip) {
        this.vip = vip;
    }

}
