package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.dao.OrdersDao;
import com.cl.entity.DiscussagriculturalproductinformationEntity;
import com.cl.entity.OrdersEntity;
import com.cl.entity.view.DiscussagriculturalproductinformationView;
import com.cl.service.DiscussagriculturalproductinformationService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * agriculturalproductinformation评论表
 * 后端接口
 */
@RestController
@RequestMapping("/discussagriculturalproductinformation")
public class DiscussagriculturalproductinformationController {
    @Autowired
    private DiscussagriculturalproductinformationService discussagriculturalproductinformationService;
    @Autowired
    private OrdersDao ordersDao;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, DiscussagriculturalproductinformationEntity discussagriculturalproductinformation,
                  HttpServletRequest request) {
        EntityWrapper<DiscussagriculturalproductinformationEntity> ew = new EntityWrapper<DiscussagriculturalproductinformationEntity>();


        PageUtils page = discussagriculturalproductinformationService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussagriculturalproductinformation), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, DiscussagriculturalproductinformationEntity discussagriculturalproductinformation,
                  HttpServletRequest request) {
        EntityWrapper<DiscussagriculturalproductinformationEntity> ew = new EntityWrapper<DiscussagriculturalproductinformationEntity>();

        PageUtils page = discussagriculturalproductinformationService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussagriculturalproductinformation), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(DiscussagriculturalproductinformationEntity discussagriculturalproductinformation) {
        EntityWrapper<DiscussagriculturalproductinformationEntity> ew = new EntityWrapper<DiscussagriculturalproductinformationEntity>();
        ew.allEq(MPUtil.allEQMapPre(discussagriculturalproductinformation, "discussagriculturalproductinformation"));
        return R.ok().put("data", discussagriculturalproductinformationService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DiscussagriculturalproductinformationEntity discussagriculturalproductinformation) {
        EntityWrapper<DiscussagriculturalproductinformationEntity> ew = new EntityWrapper<DiscussagriculturalproductinformationEntity>();
        ew.allEq(MPUtil.allEQMapPre(discussagriculturalproductinformation, "discussagriculturalproductinformation"));
        DiscussagriculturalproductinformationView discussagriculturalproductinformationView = discussagriculturalproductinformationService.selectView(ew);
        return R.ok("查询agriculturalproductinformation评论表成功").put("data", discussagriculturalproductinformationView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        DiscussagriculturalproductinformationEntity discussagriculturalproductinformation = discussagriculturalproductinformationService.selectById(id);
        discussagriculturalproductinformation = discussagriculturalproductinformationService.selectView(new EntityWrapper<DiscussagriculturalproductinformationEntity>().eq("id", id));
        return R.ok().put("data", discussagriculturalproductinformation);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        DiscussagriculturalproductinformationEntity discussagriculturalproductinformation = discussagriculturalproductinformationService.selectById(id);
        discussagriculturalproductinformation = discussagriculturalproductinformationService.selectView(new EntityWrapper<DiscussagriculturalproductinformationEntity>().eq("id", id));
        return R.ok().put("data", discussagriculturalproductinformation);
    }


    /**
     * 后端保存
     * 评论上限：每用户 + 每商品最多 2 条（content + reply）
     * - 第 1 次：insert 新记录
     * - 第 2 次（追加评论）：update 原记录的 reply 字段
     * - 第 3 次：拒绝
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscussagriculturalproductinformationEntity discuss, HttpServletRequest request) {
        return doComment(discuss, request);
    }

    /**
     * 前端保存（评价流程）
     *
     * 订单状态流转：已支付 →（确认收货）→ 已收货
     *
     * 规则：按订单维度评价，每个订单最多评价 2 次（首次评价 + 追加评价）
     * 1. 校验订单存在且属于当前用户
     * 2. 第1次评价：订单必须"已收货"
     * 3. 第2次评价：追加评价（订单"已收货"时允许）
     * 4. 每订单最多评价 2 次（首次评价 + 追加评价）
     */
    @RequestMapping("/add")
    @Transactional
    public R add(@RequestBody DiscussagriculturalproductinformationEntity discussagriculturalproductinformation, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }
        // 安全：用 session 中的 userId 覆盖前端传入值
        discussagriculturalproductinformation.setUserid(userId);
        Long orderid = discussagriculturalproductinformation.getOrderid();
        if (orderid == null) {
            return R.error("缺少订单信息");
        }
        Long refid = discussagriculturalproductinformation.getRefid();

        // 1. 查询订单，校验归属
        OrdersEntity order = ordersDao.selectById(orderid);
        if (order == null) {
            return R.error("订单不存在");
        }
        if (!userId.equals(order.getUserid())) {
            return R.error("无权操作此订单");
        }
        if (!"agriculturalproductinformation".equals(order.getTablename())) {
            return R.error("订单类型不支持评价");
        }

        // 2. 查询该订单下的已有评价数（按 orderid 统计，每订单最多 2 次）
        List<DiscussagriculturalproductinformationEntity> existingReviews =
                discussagriculturalproductinformationService.selectList(
                        new EntityWrapper<DiscussagriculturalproductinformationEntity>()
                                .eq("orderid", orderid)
                                .eq("userid", userId)
                );
        int reviewCount = existingReviews.size();
        // 追加评论存在 reply 字段中，检查 reply 是否有值来判断实际评论数
        if (reviewCount == 1 && existingReviews.get(0).getReply() != null
                && !existingReviews.get(0).getReply().trim().isEmpty()) {
            reviewCount = 2;
        }

        // 3. 最多 2 条评价（首次 + 追加）
        if (reviewCount >= 2) {
            return R.error("该订单已评价 2 次（首次评价 + 追加评价），已达评价上限");
        }

        // 4. 校验订单状态
        String orderStatus = order.getStatus();
        if ("已支付".equals(orderStatus)) {
            return R.error("请先确认收货后再评价");
        }
        if (!"已收货".equals(orderStatus)) {
            return R.error("订单当前状态（" + orderStatus + "）不允许评价");
        }

        if (reviewCount == 0) {
            // 首次评价
            discussagriculturalproductinformationService.insert(discussagriculturalproductinformation);
            return R.ok("评价成功");
        } else {
            // 追加评价：作为 reply 追加到首次评价
            DiscussagriculturalproductinformationEntity firstReview = existingReviews.get(0);
            firstReview.setReply(discussagriculturalproductinformation.getContent());
            discussagriculturalproductinformationService.updateById(firstReview);
            return R.ok("追加评价成功");
        }
    }

    /**
     * 评论公共逻辑（后端保存用，不校验订单）
     */
    private R doComment(DiscussagriculturalproductinformationEntity discuss, HttpServletRequest request) {
        Long refid = discuss.getRefid();
        Long userid = discuss.getUserid();
        if (refid == null || userid == null) {
            return R.error("参数不完整");
        }

        // 查询该用户对该商品的已有评论记录
        DiscussagriculturalproductinformationEntity existing = discussagriculturalproductinformationService.selectOne(
                new EntityWrapper<DiscussagriculturalproductinformationEntity>()
                        .eq("refid", refid)
                        .eq("userid", userid)
        );

        if (existing == null) {
            // 第 1 次：insert 新评论
            discussagriculturalproductinformationService.insert(discuss);
            return R.ok();
        }

        // 已有记录，计算已占用槽位：content 有值算 1，reply 有值算 1
        int usedSlots = 0;
        if (existing.getContent() != null && !existing.getContent().isEmpty()) usedSlots++;
        if (existing.getReply() != null && !existing.getReply().isEmpty()) usedSlots++;

        if (usedSlots >= 2) {
            // 两个槽位都占了 → 阻止
            return R.error("评论已达上限，不能再发了");
        }

        // 追加到 reply 槽位
        String newContent = discuss.getContent();
        if (newContent != null && !newContent.isEmpty()) {
            existing.setReply(newContent);
        }
        discussagriculturalproductinformationService.updateById(existing);
        return R.ok("追加评论成功");
    }

    /**
     * 获取用户密保
     */
    @RequestMapping("/security")
    @IgnoreAuth
    public R security(@RequestParam String username) {
        DiscussagriculturalproductinformationEntity discussagriculturalproductinformation = discussagriculturalproductinformationService.selectOne(new EntityWrapper<DiscussagriculturalproductinformationEntity>().eq("", username));
        return R.ok().put("data", discussagriculturalproductinformation);
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @IgnoreAuth
    public R update(@RequestBody DiscussagriculturalproductinformationEntity discussagriculturalproductinformation, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(discussagriculturalproductinformation);
        discussagriculturalproductinformationService.updateById(discussagriculturalproductinformation);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        discussagriculturalproductinformationService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 前端智能排序
     */
    @IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params, DiscussagriculturalproductinformationEntity discussagriculturalproductinformation, HttpServletRequest request, String pre) {
        EntityWrapper<DiscussagriculturalproductinformationEntity> ew = new EntityWrapper<DiscussagriculturalproductinformationEntity>();
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> param = new HashMap<String, Object>();
        Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            String newKey = entry.getKey();
            if (pre.endsWith(".")) {
                newMap.put(pre + newKey, entry.getValue());
            } else if (StringUtils.isEmpty(pre)) {
                newMap.put(newKey, entry.getValue());
            } else {
                newMap.put(pre + "." + newKey, entry.getValue());
            }
        }
        params.put("sort", "clicktime");
        params.put("order", "desc");
        PageUtils page = discussagriculturalproductinformationService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussagriculturalproductinformation), params), params));
        return R.ok().put("data", page);
    }


}
