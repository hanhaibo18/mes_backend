package com.richfit.mes.common.core.api;

/**
 * @author sun
 * @Description 封装API的错误码
 */
public interface IErrorCode {

    /**
     * Error Code
     * @return code
     */
    long getCode();
    /**
     * Error message
     * @return message
     */
    String getMessage();
}
