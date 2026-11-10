package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.ProductcategoryEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 产品分类
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@TableName("productcategory")
public class ProductcategoryView extends ProductcategoryEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public ProductcategoryView() {
    }

    public ProductcategoryView(ProductcategoryEntity productcategoryEntity) {
        try {
            BeanUtils.copyProperties(this, productcategoryEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
