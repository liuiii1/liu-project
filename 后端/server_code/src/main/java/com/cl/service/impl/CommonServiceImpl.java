package com.cl.service.impl;


import com.cl.dao.CommonDao;
import com.cl.entity.EIException;
import com.cl.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;


/**
 * 通用业务服务层 - 已增加表名/列名白名单校验，防止 SQL 注入
 */
@Service("commonService")
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonDao commonDao;

    /**
     * 允许访问的表名白名单（全部小写）
     */
    private static final Set<String> ALLOWED_TABLES = new HashSet<>(Arrays.asList(
            "address",
            "agriculturalproductinformation",
            "cart",
            "config",
            "discussagriculturalproductinformation",
            "menu",
            "news",
            "orders",
            "productcategory",
            "storeup",
            "supplier",
            "token",
            "user",
            "users"
    ));

    /**
     * 列名合法字符正则（只允许字母、数字、下划线）
     * 不允许任何注释符、分号、空格等 SQL 注入常用符号
     */
    private static final Pattern COLUMN_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    /**
     * 校验表名是否在白名单中
     */
    private void validateTable(Map<String, Object> params) {
        Object tableObj = params.get("table");
        if (tableObj == null) {
            throw new EIException("table 参数不能为空");
        }
        String table = tableObj.toString().toLowerCase();
        if (!ALLOWED_TABLES.contains(table)) {
            throw new EIException("非法的表名: " + table);
        }
    }

    /**
     * 校验列名是否合法（只允许字母数字下划线）
     */
    private void validateColumn(String columnName) {
        if (StringUtils.isBlank(columnName)) {
            throw new EIException("column 参数不能为空");
        }
        if (!COLUMN_PATTERN.matcher(columnName).matches()) {
            throw new EIException("非法的列名: " + columnName);
        }
    }

    @Override
    public List<String> getOption(Map<String, Object> params) {
        validateTable(params);
        validateColumn(params.get("column") != null ? params.get("column").toString() : null);
        if (params.get("conditionColumn") != null) {
            validateColumn(params.get("conditionColumn").toString());
        }
        return commonDao.getOption(params);
    }

    @Override
    public Map<String, Object> getFollowByOption(Map<String, Object> params) {
        validateTable(params);
        validateColumn(params.get("column") != null ? params.get("column").toString() : null);
        return commonDao.getFollowByOption(params);
    }

    @Override
    public void sh(Map<String, Object> params) {
        validateTable(params);
        commonDao.sh(params);
    }

    @Override
    public int remindCount(Map<String, Object> params) {
        validateTable(params);
        validateColumn(params.get("column") != null ? params.get("column").toString() : null);
        return commonDao.remindCount(params);
    }

    @Override
    public Map<String, Object> selectCal(Map<String, Object> params) {
        validateTable(params);
        validateColumn(params.get("column") != null ? params.get("column").toString() : null);
        return commonDao.selectCal(params);
    }

    @Override
    public List<Map<String, Object>> selectGroup(Map<String, Object> params) {
        validateTable(params);
        validateColumn(params.get("column") != null ? params.get("column").toString() : null);
        return commonDao.selectGroup(params);
    }

    @Override
    public List<Map<String, Object>> selectValue(Map<String, Object> params) {
        validateTable(params);
        validateColumn(params.get("xColumn") != null ? params.get("xColumn").toString() : null);
        validateColumn(params.get("yColumn") != null ? params.get("yColumn").toString() : null);
        return commonDao.selectValue(params);
    }

    @Override
    public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params) {
        validateTable(params);
        validateColumn(params.get("xColumn") != null ? params.get("xColumn").toString() : null);
        validateColumn(params.get("yColumn") != null ? params.get("yColumn").toString() : null);
        return commonDao.selectTimeStatValue(params);
    }

}
