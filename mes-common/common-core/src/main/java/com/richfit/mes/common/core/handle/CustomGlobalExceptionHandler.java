package com.richfit.mes.common.core.handle;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 异常处理器
 */

@ControllerAdvice
@Slf4j
public class CustomGlobalExceptionHandler {
    /**
     * 处理参数校验异常
     *
     * @param ex ex
     * @return ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validationBodyException(MethodArgumentNotValidException ex) {
        // 获取所有异常信息
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        CommonResult<List<String>> responseBean = new CommonResult(ResultCode.INVALID_ARGUMENTS.getCode(), errors, null);
        return new ResponseEntity<>(responseBean, HttpStatus.BAD_REQUEST);
    }


    /**
     * 处理CommonException
     *
     * @param globalException e
     * @return ResponseEntity
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity handleGlobalException(GlobalException globalException) {
        ResultCode errorCode = globalException.getErrorCode();
        HttpStatus status;

        switch (errorCode) {
            case UNAUTHORIZED:
                status = HttpStatus.UNAUTHORIZED;
                break;
            case FORBIDDEN:
                status = HttpStatus.FORBIDDEN;
                break;
            case INVALID_ARGUMENTS:
                status = HttpStatus.BAD_REQUEST;
                break;
            case ITEM_NOT_FOUND:
                status = HttpStatus.NOT_FOUND;
                break;
            case FAILED:
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }

        return new ResponseEntity(CommonResult.failed(errorCode, globalException.getMessage()), new HttpHeaders(), status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity handleException(Exception e) {
        e.printStackTrace();
        CommonResult<List<String>> responseBean = new CommonResult<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        return new ResponseEntity<>(responseBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
