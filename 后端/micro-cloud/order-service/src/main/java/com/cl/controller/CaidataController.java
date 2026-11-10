package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cl.annotation.IgnoreAuth;
import com.cl.dao.CaidataDao;
import com.cl.entity.AgriculturalproductinformationEntity;
import com.cl.entity.CaidataEntity;
import com.cl.entity.TokenEntity;
import com.cl.feign.ProductFeignClient;
import com.cl.service.CaidataService;
import com.cl.service.TokenService;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 采摘小车操作日志
 * 核心接口：
 *   POST /caidata/report  —— 接收小车 JSON 上报，调用 addStock 改库存 + 写日志
 *   POST /caidata/addStock —— 公共库存增量方法（也可直接调用）
 *   GET  /caidata/page     —— 列表查询（供应商按 account 过滤）
 */
@RestController
@RequestMapping("/caidata")
public class CaidataController {

    @Autowired
    private CaidataService caidataService;

    @Autowired
    private CaidataDao caidataDao;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private TokenService tokenService;

    // ============================================================
    // 1. 公共方法：库存增量修改（独立可复用）
    // ============================================================

    /**
     * 公共库存增量方法：给指定商品增加 harvest 数量
     * 用 increaseStock 原子操作（乐观锁），支持负数减少库存
     *
     * @param productId 商品 ID
     * @param harvest   增量（正数=增加，负数=扣减）
     * @return true=成功
     */
    public boolean addStock(Long productId, int harvest) {
        if (productId == null || harvest == 0) {
            return false;
        }
        // increaseStock：Feign 远程商品服务原子增减库存，harvest 为负数即扣减
        int rows = productFeignClient.increaseStock(productId, harvest);
        return rows > 0;
    }

    /**
     * 公共库存增量方法（按商品名称 + account 查找）
     * 先查出商品 id，再调 addStock
     */
    public boolean addStock(String productName, String account, int harvest) {
        if (productName == null || account == null || harvest == 0) {
            return false;
        }
        // Feign 远程商品服务：按商品名 + 供应商账号定位商品
        java.util.List<AgriculturalproductinformationEntity> list =
                productFeignClient.findByNameAndAccount(productName, account);
        if (list == null || list.isEmpty()) {
            return false;
        }
        return addStock(list.get(0).getId(), harvest);
    }

    // ============================================================
    // 2. 独立 HTTP 端点：库存增量（小车硬件也可直接调用，不写日志）
    // ============================================================

    /**
     * 独立库存增量接口（可免登录）
     * 入参 JSON 二选一：
     *   A. { "productId": 5,           "quantity": 2 }
     *   B. { "productname": "苹果", "account": "root", "quantity": 2 }
     */
    @IgnoreAuth
    @RequestMapping("/addStock")
    public R addStockEndpoint(@RequestBody Map<String, Object> body) {
        int quantity = Integer.parseInt(String.valueOf(body.get("quantity")));

        // 方式 A：直接传 productId
        if (body.containsKey("productId")) {
            Long productId = Long.parseLong(String.valueOf(body.get("productId")));
            boolean ok = addStock(productId, quantity);
            return ok ? R.ok("库存已 +" + quantity) : R.error("库存更新失败（商品不存在或 SQL 影响 0 行）");
        }

        // 方式 B：传商品名称 + account 查找
        String productName = String.valueOf(body.getOrDefault("productname", body.getOrDefault("product_name", "")));
        String account     = String.valueOf(body.getOrDefault("account", ""));
        boolean ok = addStock(productName, account, quantity);
        return ok ? R.ok("库存已 +" + quantity) : R.error("库存更新失败：未找到商品 [" + productName + "]");
    }

    // ============================================================
    // 3. 接收小车 JSON 上报（改库存 + 写 caidata 日志）
    // ============================================================

    /**
     * 小车上报接口（可免登录，用 Token 头或 device_token 鉴权）
     * 入参 JSON：
     *   {
     *     "number":    "小车编号",
     *     "productname":"苹果",
     *     "account":   "root",
     *     "harvest":   2
     *   }
     */
    @IgnoreAuth
    @RequestMapping("/report")
    @Transactional
    public R report(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        System.out.println("采集数据"+body);
        try {
            // 1. 提取字段
            String cartId     = String.valueOf(body.getOrDefault("number", body.getOrDefault("cart_id", "")));
            String productName= String.valueOf(body.getOrDefault("productname", body.getOrDefault("product_name", "")));
            String account    = String.valueOf(body.getOrDefault("account", ""));
            int    harvest    = Integer.parseInt(String.valueOf(body.getOrDefault("harvest", body.getOrDefault("quantity", 0))));

            // 2. 基础校验
            if (productName == null || productName.isEmpty()) {
                return R.error("商品名称不能为空");
            }
            if (account == null || account.isEmpty()) {
                return R.error("供应商账号不能为空");
            }
            if (harvest <= 0) {
                return R.error("harvest 必须大于 0");
            }

            // 3. 调公共方法改库存（harvest 不入 caidata 表，只作为 addStock 的入参）
            boolean stockOk = addStock(productName, account, harvest);
            if (!stockOk) {
                return R.error("库存更新失败：未找到商品 [" + productName + "] 或库存不足");
            }

            // 4. 写 caidata 日志（持久化字段不包含 harvest，用 quantity 记录数量）
            CaidataEntity log = new CaidataEntity();
            log.setAccount(account);
            log.setCartId(cartId);
            log.setProductName(productName);
            log.setQuantity(harvest);
            log.setPayload(body.toString());
            log.setAddtime(new Date());

            caidataService.insert(log);

            return R.ok("上报成功，库存已更新 +" + harvest).put("logId", log.getId());

        } catch (NumberFormatException e) {
            return R.error("harvest 必须是数字");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上报异常: " + e.getMessage());
        }
    }

    // ============================================================
    // 4. 管理端列表（供应商按 account 过滤，管理员看全部）
    // ============================================================

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, CaidataEntity caidata, HttpServletRequest request) {
        // 从 token 拿当前角色 + account
        String role = getRole(request);
        String account = getAccount(request);

        EntityWrapper<CaidataEntity> ew = new EntityWrapper<>();

        // 供应商只看自己的（兼容前端 roleName "商家" 和 token.role "供应商"/"supplier"）
        if ("供应商".equals(role) || "supplier".equals(role) || "商家".equals(role)) {
            ew.eq("account", account);
        }

        Page<CaidataEntity> page = new Page<>(
            Integer.parseInt(params.getOrDefault("page", "1").toString()),
            Integer.parseInt(params.getOrDefault("limit", "10").toString())
        );
        page = caidataService.selectPage(page, ew);
        // 用 PageUtils 包装 — 前端读的是 data.list，不是 data.records
        return R.ok().put("data", new PageUtils(page));
    }

    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CaidataEntity caidata = caidataService.selectById(id);
        return R.ok().put("data", caidata);
    }

    // 删除（管理员）
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        caidataService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    // ============================================================
    // 辅助方法
    // ============================================================

    private String getRole(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (token == null) token = request.getParameter("Token");
        if (token == null) return null;
        TokenEntity te = tokenService.selectOne(
            new EntityWrapper<TokenEntity>().eq("token", token));
        return te != null ? te.getRole() : null;
    }

    private String getAccount(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (token == null) token = request.getParameter("Token");
        if (token == null) return null;
        TokenEntity te = tokenService.selectOne(
            new EntityWrapper<TokenEntity>().eq("token", token));
        return te != null ? te.getUsername() : null;
    }
}
