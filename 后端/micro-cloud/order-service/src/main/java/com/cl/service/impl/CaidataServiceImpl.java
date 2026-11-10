package com.cl.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.CaidataDao;
import com.cl.entity.CaidataEntity;
import com.cl.service.CaidataService;
import org.springframework.stereotype.Service;

/**
 * 采摘小车日志 Service 实现
 */
@Service
public class CaidataServiceImpl extends ServiceImpl<CaidataDao, CaidataEntity> implements CaidataService {
}
