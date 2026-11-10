package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.DiscussagriculturalproductinformationDao;
import com.cl.entity.DiscussagriculturalproductinformationEntity;
import com.cl.entity.view.DiscussagriculturalproductinformationView;
import com.cl.service.DiscussagriculturalproductinformationService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("discussagriculturalproductinformationService")
public class DiscussagriculturalproductinformationServiceImpl extends ServiceImpl<DiscussagriculturalproductinformationDao, DiscussagriculturalproductinformationEntity> implements DiscussagriculturalproductinformationService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DiscussagriculturalproductinformationEntity> page = this.selectPage(
                new Query<DiscussagriculturalproductinformationEntity>(params).getPage(),
                new EntityWrapper<DiscussagriculturalproductinformationEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussagriculturalproductinformationEntity> wrapper) {
        Page<DiscussagriculturalproductinformationView> page = new Query<DiscussagriculturalproductinformationView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<DiscussagriculturalproductinformationView> selectListView(Wrapper<DiscussagriculturalproductinformationEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public DiscussagriculturalproductinformationView selectView(Wrapper<DiscussagriculturalproductinformationEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
