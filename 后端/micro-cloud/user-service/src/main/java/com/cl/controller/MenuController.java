package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.MenuEntity;
import com.cl.entity.SupplierEntity;
import com.cl.entity.TokenEntity;
import com.cl.entity.view.MenuView;
import com.cl.service.MenuService;
import com.cl.service.SupplierService;
import com.cl.service.TokenService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 菜单
 * 后端接口
 *
 * @author
 * @email
 * @date 2024-12-09 09:01:50
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SupplierService supplierService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, MenuEntity menu,
                  HttpServletRequest request) {
        EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();


        PageUtils page = menuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, menu), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, MenuEntity menu,
                  HttpServletRequest request) {
        EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();

        PageUtils page = menuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, menu), params), params));

        // use_cart 权限过滤：supplier.use_cart=0 时，隐藏采摘小车相关菜单
        filterCaidataMenuByUseCart(request, page);

        return R.ok().put("data", page);
    }

    /**
     * 根据 supplier.use_cart 过滤采摘数据菜单
     * 仅当请求方是 supplier 且 use_cart=0 时，移除 caidata 菜单项
     */
    private void filterCaidataMenuByUseCart(HttpServletRequest request, PageUtils page) {
        try {
            // 1. 拿 token
            String token = request.getHeader("Token");
            if (token == null || token.isEmpty()) {
                token = request.getParameter("Token");
            }
            if (token == null || token.isEmpty()) {
                return;  // 无 token 不过滤
            }

            // 2. 查 token
            TokenEntity te = tokenService.selectOne(
                    new EntityWrapper<TokenEntity>().eq("token", token));
            if (te == null) {
                return;
            }

            // 3. 只处理 supplier 角色
            String tableName = te.getTablename();
            if (!"supplier".equals(tableName)) {
                return;
            }

            // 4. 查 supplier 的 use_cart
            SupplierEntity supplier = supplierService.selectOne(
                    new EntityWrapper<SupplierEntity>().eq("account", te.getUsername()));
            if (supplier == null || supplier.getUseCart() == null || supplier.getUseCart() != 0) {
                return;  // use_cart=1 或 NULL 不过滤
            }

            // 5. 过滤 menujson 里 caidata 项
            java.util.List<?> records = page.getList();
            for (Object rec : records) {
                if (rec instanceof MenuEntity) {
                    MenuEntity me = (MenuEntity) rec;
                    String json = me.getMenujson();
                    if (json == null) continue;
                    me.setMenujson(removeTableFromMenu(json, "caidata"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 过滤失败不影响主流程
        }
    }

    /**
     * 从菜单 JSON 数组中移除指定 tableName 的菜单项
     */
    private String removeTableFromMenu(String menujson, String targetTable) {
        try {
            ArrayNode root = (ArrayNode) objectMapper.readTree(menujson);
            for (JsonNode roleNode : root) {
                JsonNode backMenu = roleNode.get("backMenu");
                if (backMenu == null || !backMenu.isArray()) continue;
                Iterator<JsonNode> groupIt = backMenu.iterator();
                while (groupIt.hasNext()) {
                    JsonNode group = groupIt.next();
                    JsonNode child = group.get("child");
                    if (child == null || !child.isArray()) continue;
                    Iterator<JsonNode> childIt = child.iterator();
                    while (childIt.hasNext()) {
                        JsonNode item = childIt.next();
                        if (targetTable.equals(item.get("tableName") != null ? item.get("tableName").asText() : "")) {
                            childIt.remove();
                        }
                    }
                    // 如果分组下没子菜单了，移除整个分组
                    if (!child.iterator().hasNext()) {
                        groupIt.remove();
                    }
                }
            }
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            e.printStackTrace();
            return menujson;  // 解析失败返回原值
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(MenuEntity menu) {
        EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();
        ew.allEq(MPUtil.allEQMapPre(menu, "menu"));
        return R.ok().put("data", menuService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(MenuEntity menu) {
        EntityWrapper<MenuEntity> ew = new EntityWrapper<MenuEntity>();
        ew.allEq(MPUtil.allEQMapPre(menu, "menu"));
        MenuView menuView = menuService.selectView(ew);
        return R.ok("查询菜单成功").put("data", menuView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MenuEntity menu = menuService.selectById(id);
        menu = menuService.selectView(new EntityWrapper<MenuEntity>().eq("id", id));
        return R.ok().put("data", menu);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        MenuEntity menu = menuService.selectById(id);
        menu = menuService.selectView(new EntityWrapper<MenuEntity>().eq("id", id));
        return R.ok().put("data", menu);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MenuEntity menu, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(menu);
        menuService.insert(menu);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody MenuEntity menu, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(menu);
        menuService.insert(menu);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody MenuEntity menu, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(menu);
        menuService.updateById(menu);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        menuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
