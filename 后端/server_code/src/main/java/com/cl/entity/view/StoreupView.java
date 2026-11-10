package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.StoreupEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 我的收藏
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@TableName("storeup")
public class StoreupView extends StoreupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public StoreupView() {
    }

    public StoreupView(StoreupEntity storeupEntity) {
        try {
            BeanUtils.copyProperties(this, storeupEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
