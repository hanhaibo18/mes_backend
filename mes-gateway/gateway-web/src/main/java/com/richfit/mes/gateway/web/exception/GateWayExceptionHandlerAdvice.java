package com.richfit.mes.gateway.web.exception;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author sun
 * @Description TODO
 */
@Slf4j
@Component
public class GateWayExceptionHandlerAdvice {

    @ExceptionHandler(value = {ResponseStatusException.class})
    public CommonResult handle(ResponseStatusException ex) {
        log.error("exception:{}", ex.getMessage());
        return CommonResult.failed();
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult handle(Exception ex) {
        log.error("exception:{}", ex.getMessage());
        return CommonResult.failed(ex.getMessage());
    }

    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult handle(Throwable throwable) {
        CommonResult result = CommonResult.failed();
        if (throwable instanceof ResponseStatusException) {
            result = handle((ResponseStatusException) throwable);
        } else if (throwable instanceof ConnectTimeoutException) {
            result = handle((ConnectTimeoutException) throwable);
        } else if (throwable instanceof NotFoundException) {
            result = handle((NotFoundException) throwable);
        } else if (throwable instanceof RuntimeException) {
            result = handle((RuntimeException) throwable);
        } else if (throwable instanceof Exception) {
            result = handle((Exception) throwable);
        }
        return result;
    }
}
