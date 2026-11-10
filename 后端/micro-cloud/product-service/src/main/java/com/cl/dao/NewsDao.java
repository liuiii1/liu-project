package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.NewsEntity;
import com.cl.entity.view.NewsView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 公告资讯
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
public interface NewsDao extends BaseMapper<NewsEntity> {

    List<NewsView> selectListView(@Param("ew") Wrapper<NewsEntity> wrapper);

    List<NewsView> selectListView(Pagination page, @Param("ew") Wrapper<NewsEntity> wrapper);

    NewsView selectView(@Param("ew") Wrapper<NewsEntity> wrapper);


}
