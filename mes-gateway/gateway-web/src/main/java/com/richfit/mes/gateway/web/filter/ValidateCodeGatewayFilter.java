package com.richfit.mes.gateway.web.filter;

import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.richfit.mes.gateway.web.constants.GatewayConstant;
import com.richfit.mes.gateway.web.exception.ValidateCodeException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * @author sun
 * @Description 验证码生成逻辑处理类
 */
@Slf4j
@Component

public class ValidateCodeGatewayFilter extends AbstractGatewayFilterFactory {

    @Autowired
    @Lazy
    private CaptchaService captchaService;
    
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 不是登录请求，直接向下执行
            if (!StringUtils.containsIgnoreCase(request.getURI().getPath(), GatewayConstant.OAUTH_TOKEN_URL)) {
                return chain.filter(exchange);
            }

            // 刷新token，直接向下执行
            String grantType = request.getQueryParams().getFirst("grant_type");
            if (StringUtils.equals(GatewayConstant.REFRESH_TOKEN, grantType)) {
                return chain.filter(exchange);
            }

            // 校验验证码
            checkCode(request);
            return chain.filter(exchange);
        };
    }

    /**
     * 检查code
     *
     * @param request
     */
    @SneakyThrows
    private void checkCode(ServerHttpRequest request) {
        String randomStr = request.getQueryParams().getFirst("randomStr");

        // 若是滑块登录
        if (GatewayConstant.IMAGE_CODE_TYPE.equalsIgnoreCase(randomStr)) {
            String code = request.getQueryParams().getFirst("code");
            if (StringUtils.isBlank(code)) {
                throw new ValidateCodeException("验证码不能为空");
            }
            CaptchaVO vo = new CaptchaVO();
            vo.setCaptchaVerification(code);
            if (!captchaService.verification(vo).isSuccess()) {
                throw new ValidateCodeException("验证码不合法");
            }
        }else{
            throw new ValidateCodeException("验证码不合法");
        }

    }

}
