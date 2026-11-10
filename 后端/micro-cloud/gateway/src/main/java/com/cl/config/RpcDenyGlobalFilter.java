package com.cl.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关全局过滤器：拒绝所有包含 /rpc/ 路径段的请求。
 *
 * 各业务服务的 /rpc/** 端点（RpcUserController / RpcProductController / RpcCartController）
 * 是 order-service 通过 OpenFeign 远程调用的内部接口，标注了 @IgnoreAuth 无鉴权。
 * 必须禁止外部客户端经网关直接访问，防止伪造请求篡改用户余额/库存/购物车。
 *
 * 过滤器在 gateway 最早阶段（Order=-1）执行，比路由定位更早。
 */
@Component
public class RpcDenyGlobalFilter implements GlobalFilter, Ordered {

    private static final String RPC_MARKER = "/rpc/";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (path != null && path.contains(RPC_MARKER)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            String body = "{\"code\":403,\"msg\":\"Forbidden: /rpc/** 为内部端点，禁止外部访问\"}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // 在所有常规过滤器之前执行
    }
}
