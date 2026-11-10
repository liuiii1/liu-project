package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.AgriculturalproductinformationDao;
import com.cl.entity.AgriculturalproductinformationEntity;
import com.cl.entity.view.AgriculturalproductinformationView;
import com.cl.service.AgriculturalproductinformationService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("agriculturalproductinformationService")
public class AgriculturalproductinformationServiceImpl extends ServiceImpl<AgriculturalproductinformationDao, AgriculturalproductinformationEntity> implements AgriculturalproductinformationService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<AgriculturalproductinformationEntity> page = this.selectPage(
                new Query<AgriculturalproductinformationEntity>(params).getPage(),
                new EntityWrapper<AgriculturalproductinformationEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<AgriculturalproductinformationEntity> wrapper) {
        Page<AgriculturalproductinformationView> page = new Query<AgriculturalproductinformationView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<AgriculturalproductinformationView> selectListView(Wrapper<AgriculturalproductinformationEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public AgriculturalproductinformationView selectView(Wrapper<AgriculturalproductinformationEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
