package com.richfit.mes.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import lombok.Getter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author sun
 * @Description 自定义OauthException
 */
@Getter
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {
    private final CommonResult result;
    public CustomOauthException(OAuth2Exception oAuth2Exception) {
        super(oAuth2Exception.getSummary(), oAuth2Exception);
        this.result = CommonResult.failed(ResultCode.FAILED,oAuth2Exception.getMessage());
    }
}
