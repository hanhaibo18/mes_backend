package com.richfit.plm.common;

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
