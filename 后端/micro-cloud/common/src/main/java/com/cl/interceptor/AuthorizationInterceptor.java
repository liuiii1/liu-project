package com.cl.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.TokenEntity;
import com.cl.service.TokenService;
import com.cl.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限(Token)验证
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    public static final String LOGIN_TOKEN_KEY = "Token";

    /**
     * 允许跨域的 Origin 白名单
     * - 小程序端不受 CORS 限制，由微信校验
     * - 管理端前端默认运行在 http://localhost:8081
     * - 生产环境部署时，请把实际前端域名加进来
     * - 本地开发的 localhost:* 用通配匹配
     */
    private static final Set<String> ALLOWED_ORIGINS = new HashSet<>(Arrays.asList(
            "http://localhost:8081",
            "http://localhost:3000",
            "http://127.0.0.1:8081",
            "http://127.0.0.1:3000"
    ));

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 跨域配置（已收敛）
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,request-source,Token, Origin,imgType, Content-Type, cache-control,postman-token,Cookie, Accept,authorization");

        // 仅对白名单 Origin 设置 Allow-Origin，防止任意站点跨域窃取 Cookie
        String origin = request.getHeader("Origin");
        if (origin != null && isAllowedOrigin(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return false;
        }

        IgnoreAuth annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IgnoreAuth.class);
        } else {
            return true;
        }

        //从header中获取token
        String token = request.getHeader(LOGIN_TOKEN_KEY);

        /**
         * 不需要验证权限的方法直接放过
         */
        if (annotation != null) {
            return true;
        }

        TokenEntity tokenEntity = null;
        if (StringUtils.isNotBlank(token)) {
            tokenEntity = tokenService.getTokenEntity(token);
        }

        if (tokenEntity != null) {
            request.getSession().setAttribute("userId", tokenEntity.getUserid());
            request.getSession().setAttribute("role", tokenEntity.getRole());
            request.getSession().setAttribute("tableName", tokenEntity.getTablename());
            request.getSession().setAttribute("username", tokenEntity.getUsername());
            return true;
        }

        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JSONObject.toJSONString(R.error(401, "请先登录")));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
//				throw new EIException("请先登录", 401);
        return false;
    }

    /**
     * 检查 Origin 是否在白名单内（支持 localhost 通配）
     */
    private boolean isAllowedOrigin(String origin) {
        // localhost / 127.0.0.1 任意端口
        if (origin.startsWith("http://localhost:") || origin.startsWith("http://127.0.0.1:")) {
            return true;
        }
        return ALLOWED_ORIGINS.contains(origin);
    }
}
