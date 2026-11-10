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
 * 农产品信息
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@TableName("agriculturalproductinformation")
public class AgriculturalproductinformationEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 产品名称
     */

    private String productname;
    /**
     * 产品分类
     */

    private String productcategory;
    /**
     * 图片
     */

    private String image;
    /**
     * 规格
     */

    private String specification;
    /**
     * 出产地
     */

    private String placeofproduction;
    /**
     * 产品详情
     */

    private String productdetails;
    /**
     * 账号
     */

    private String account;
    /**
     * 名称
     */

    private String name;
    /**
     * 收藏数
     */

    private Integer storeupNumber;
    /**
     * 评论数
     */

    private Integer discussNumber;
    /**
     * 价格
     */

    private Double price;
    /**
     * VIP价格
     */

    private Double vipprice;
    /**
     * 单限
     */

    private Integer onelimittimes;
    /**
     * 库存
     */

    private Integer alllimittimes;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public AgriculturalproductinformationEntity() {

    }


    public AgriculturalproductinformationEntity(T t) {
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
     * 获取：产品名称
     */
    public String getProductname() {
        return productname;
    }

    /**
     * 设置：产品名称
     */
    public void setProductname(String productname) {
        this.productname = productname;
    }

    /**
     * 获取：产品分类
     */
    public String getProductcategory() {
        return productcategory;
    }

    /**
     * 设置：产品分类
     */
    public void setProductcategory(String productcategory) {
        this.productcategory = productcategory;
    }

    /**
     * 获取：图片
     */
    public String getImage() {
        return image;
    }

    /**
     * 设置：图片
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 获取：规格
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * 设置：规格
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

    /**
     * 获取：出产地
     */
    public String getPlaceofproduction() {
        return placeofproduction;
    }

    /**
     * 设置：出产地
     */
    public void setPlaceofproduction(String placeofproduction) {
        this.placeofproduction = placeofproduction;
    }

    /**
     * 获取：产品详情
     */
    public String getProductdetails() {
        return productdetails;
    }

    /**
     * 设置：产品详情
     */
    public void setProductdetails(String productdetails) {
        this.productdetails = productdetails;
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
     * 获取：收藏数
     */
    public Integer getStoreupNumber() {
        return storeupNumber;
    }

    /**
     * 设置：收藏数
     */
    public void setStoreupNumber(Integer storeupNumber) {
        this.storeupNumber = storeupNumber;
    }

    /**
     * 获取：评论数
     */
    public Integer getDiscussNumber() {
        return discussNumber;
    }

    /**
     * 设置：评论数
     */
    public void setDiscussNumber(Integer discussNumber) {
        this.discussNumber = discussNumber;
    }

    /**
     * 获取：价格
     */
    @JsonSerialize(using = MoneySerializer.class)
    public Double getPrice() {
        return price;
    }

    /**
     * 设置：价格
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 获取：VIP价格
     */
    @JsonSerialize(using = MoneySerializer.class)
    public Double getVipprice() {
        return vipprice;
    }

    /**
     * 设置：VIP价格
     */
    public void setVipprice(Double vipprice) {
        this.vipprice = vipprice;
    }

    /**
     * 获取：单限
     */
    public Integer getOnelimittimes() {
        return onelimittimes;
    }

    /**
     * 设置：单限
     */
    public void setOnelimittimes(Integer onelimittimes) {
        this.onelimittimes = onelimittimes;
    }

    /**
     * 获取：库存
     */
    public Integer getAlllimittimes() {
        return alllimittimes;
    }

    /**
     * 设置：库存
     */
    public void setAlllimittimes(Integer alllimittimes) {
        this.alllimittimes = alllimittimes;
    }

}
