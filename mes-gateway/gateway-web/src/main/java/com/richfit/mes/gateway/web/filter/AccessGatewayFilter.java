package com.richfit.mes.gateway.web.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.auth.client.service.AuthService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.gateway.web.config.FilterIgnorePropertiesConfig;
import com.richfit.mes.gateway.web.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author sun
 * @Description 请求url权限校验
 */
@Configuration
@Slf4j
public class AccessGatewayFilter implements GlobalFilter {

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    /**
     * auth-client模块提供
     */
    @Autowired
    private AuthService authService;

    @Autowired
    private PermissionService permissionService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authentication = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String method = request.getMethodValue();
        String url = request.getPath().value();
        log.debug("url:{},method:{},headers:{}", url, method, request.getHeaders());
        //不需要网关签权的url
        if (ignoreAuthentication(url)) {
            return chain.filter(exchange);
        }
        //调用签权服务
        if (permissionService.permission(authentication, url, method)) {
            ServerHttpRequest.Builder builder = request.mutate();
            //TODO 转发的请求加上其他token

            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        return unauthorized(exchange);

    }

    /**
     * 判断不做拦截的url
     *
     * @param url
     */
    private boolean ignoreAuthentication(String url) {
        List<String> ignoreUrls = filterIgnorePropertiesConfig.getUrls();
        for (String ignoreUrl:ignoreUrls){
            if(pathMatcher.match(ignoreUrl, url)){
                return true;
            }
        }
        return false;
    }

    /**
     * Unauthorized,401
     *
     * @param serverWebExchange
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        try {
            byte[] responseByte = new ObjectMapper().writeValueAsString(CommonResult.unauthorized(null)).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(responseByte);
            return response.writeWith(Flux.just(buffer));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBuffer buffer = response
                .bufferFactory().wrap(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
        return response.writeWith(Flux.just(buffer));
    }
}
