package com.richfit.mes.gateway.web.config;

import com.richfit.mes.gateway.web.handler.ImageCodeCheckHandler;
import com.richfit.mes.gateway.web.handler.ImageCodeCreateHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * @author sun
 * @Description 路由配置信息
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class RouterFunctionConfiguration {

    private final ImageCodeCreateHandler imageCodeCreateHandler;
    private final ImageCodeCheckHandler imageCodeCheckHandler;

    @Bean
    public RouterFunction routerFunction() {
        return RouterFunctions
                .route(RequestPredicates.path("/auth/code").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                        imageCodeCreateHandler)
                .andRoute(RequestPredicates.POST("/auth/code/check").and(RequestPredicates.accept(MediaType.ALL)),
                        imageCodeCheckHandler);

    }

}
