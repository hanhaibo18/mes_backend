package com.richfit.mes.common.core.exception;


import com.richfit.mes.common.core.api.ResultCode;

/**
 * @author sun
 * @Description 自定义异常
 */
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private ResultCode errorCode;

    public GlobalException() {
        super();
    }

    public GlobalException(ResultCode errorCode) {
        this.errorCode = errorCode;
    }

    public GlobalException(String message, ResultCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GlobalException(String message, Throwable cause, ResultCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GlobalException(Throwable cause, ResultCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ResultCode getErrorCode() {
        return errorCode;
    }
}

