package com.richfit.mes.gateway.web.handler;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.gateway.web.constants.GatewayConstant;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author sun
 * @Description 验证码生成逻辑处理类
 */
@Slf4j
@Component
public class ImageCodeCreateHandler implements HandlerFunction<ServerResponse> {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Lazy
    private CaptchaService captchaService;

    @Override
    @SneakyThrows
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaType(GatewayConstant.IMAGE_CODE_TYPE);
        ResponseModel responseModel = captchaService.get(vo);

        return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(objectMapper.writeValueAsString(CommonResult.success(responseModel))));
    }

}