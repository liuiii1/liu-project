package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 采摘小车操作日志
 * harvest 只在接口入参使用，不落库；库存修改通过 increaseStock 公共方法实现
 */
@TableName("caidata")
public class CaidataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String account;      // 供应商账号

    private String cartId;       // 小车编号（对应数据库 cart_id）

    private String productName;  // 商品名称（对应数据库 product_name）

    private Integer quantity;    // 数量（harvest 语义，落库到此字段）

    private String payload;      // 原始 JSON 报文

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public CaidataEntity() {}

    // ---- getter / setter ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
