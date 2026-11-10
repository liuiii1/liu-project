package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;


/**
 * 购物车
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@TableName("cart")
public class CartEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 商品表名
     */

    private String tablename;
    /**
     * 商品id
     */

    private Long goodid;
    /**
     * 商品名称
     */

    private String goodname;
    /**
     * 图片
     */

    private String picture;
    /**
     * 购买数量
     */

    private Integer buynumber;
    /**
     * 单价
     */

    private Double price;
    /**
     * 折扣价
     */

    private Double discountprice;
    /**
     * 用户id
     */

    private Long userid;
    /**
     * 商户名称
     */

    private String account;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public CartEntity() {

    }


    public CartEntity(T t) {
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
     * 获取：商品表名
     */
    public String getTablename() {
        return tablename;
    }

    /**
     * 设置：商品表名
     */
    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    /**
     * 获取：商品id
     */
    public Long getGoodid() {
        return goodid;
    }

    /**
     * 设置：商品id
     */
    public void setGoodid(Long goodid) {
        this.goodid = goodid;
    }

    /**
     * 获取：商品名称
     */
    public String getGoodname() {
        return goodname;
    }

    /**
     * 设置：商品名称
     */
    public void setGoodname(String goodname) {
        this.goodname = goodname;
    }

    /**
     * 获取：图片
     */
    public String getPicture() {
        return picture;
    }

    /**
     * 设置：图片
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * 获取：购买数量
     */
    public Integer getBuynumber() {
        return buynumber;
    }

    /**
     * 设置：购买数量
     */
    public void setBuynumber(Integer buynumber) {
        this.buynumber = buynumber;
    }

    /**
     * 获取：单价
     */
    @JsonSerialize(using = MoneySerializer.class)
    public Double getPrice() {
        return price;
    }

    /**
     * 设置：单价
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 获取：折扣价
     */
    @JsonSerialize(using = MoneySerializer.class)
    public Double getDiscountprice() {
        return discountprice;
    }

    /**
     * 设置：折扣价
     */
    public void setDiscountprice(Double discountprice) {
        this.discountprice = discountprice;
    }

    /**
     * 获取：用户id
     */
    public Long getUserid() {
        return userid;
    }

    /**
     * 设置：用户id
     */
    public void setUserid(Long userid) {
        this.userid = userid;
    }

    /**
     * 获取：商户名称
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置：商户名称
     */
    public void setAccount(String account) {
        this.account = account;
    }

}
