package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.SupplierDao;
import com.cl.entity.SupplierEntity;
import com.cl.entity.view.SupplierView;
import com.cl.service.SupplierService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("supplierService")
public class SupplierServiceImpl extends ServiceImpl<SupplierDao, SupplierEntity> implements SupplierService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<SupplierEntity> page = this.selectPage(
                new Query<SupplierEntity>(params).getPage(),
                new EntityWrapper<SupplierEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<SupplierEntity> wrapper) {
        Page<SupplierView> page = new Query<SupplierView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<SupplierView> selectListView(Wrapper<SupplierEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public SupplierView selectView(Wrapper<SupplierEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
