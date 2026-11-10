package com.cl.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.TokenDao;
import com.cl.entity.TokenEntity;
import com.cl.service.TokenService;
import com.cl.utils.CommonUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * token
 */
@Service("tokenService")
public class TokenServiceImpl extends ServiceImpl<TokenDao, TokenEntity> implements TokenService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TokenEntity> page = this.selectPage(
                new Query<TokenEntity>(params).getPage(),
                new EntityWrapper<TokenEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<TokenEntity> selectListView(Wrapper<TokenEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params,
                               Wrapper<TokenEntity> wrapper) {
        Page<TokenEntity> page = new Query<TokenEntity>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public String generateToken(Long userid, String username, String tableName, String role) {
        TokenEntity tokenEntity = this.selectOne(new EntityWrapper<TokenEntity>().eq("userid", userid).eq("role", role));
        String token = CommonUtil.getRandomString(32);
        // Token 有效期：2 天
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 2);
        cal.set(Calendar.MILLISECOND, 0);
        Date expiratedtime = cal.getTime();
        if (tokenEntity != null) {
            tokenEntity.setToken(token);
            tokenEntity.setExpiratedtime(expiratedtime);
            this.updateById(tokenEntity);
        } else {
            this.insert(new TokenEntity(userid, username, tableName, role, token, expiratedtime));
        }
        return token;
    }

    @Override
    public TokenEntity getTokenEntity(String token) {
        TokenEntity tokenEntity = this.selectOne(new EntityWrapper<TokenEntity>().eq("token", token));
        if (tokenEntity == null || tokenEntity.getExpiratedtime().getTime() < new Date().getTime()) {
            return null;
        }
        return tokenEntity;
    }
}
