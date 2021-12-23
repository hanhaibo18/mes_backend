package com.richfit.mes.gateway.web.exception;

/**
 * @author sun
 * @Description Validate Code Exception
 */
public class  ValidateCodeException extends RuntimeException {

    private static final long serialVersionUID = -7285211528095468156L;

    public ValidateCodeException() {
    }

    public ValidateCodeException(String msg) {
        super(msg);
    }

}
