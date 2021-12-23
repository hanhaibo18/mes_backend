package com.richfit.mes.auth.authorization.handler;

import com.richfit.mes.common.core.constant.CommonConstant;
import com.richfit.mes.common.core.utils.WebUtils;
import com.richfit.mes.common.log.provider.LogServiceClient;
import com.richfit.mes.common.log.util.SystemLogUtils;
import com.richfit.mes.common.model.sys.SystemLog;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.handler.AuthenticationFailureHandler;
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sun
 * @Description 自定义认证失败处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class CustomAuthenticationFailureEventHandler implements AuthenticationFailureHandler {

    private final LogServiceClient logServiceClient;

    @Async
    @SneakyThrows
    @Override
    public void handle(AuthenticationException authenticationException, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        String username = authentication.getName();
        SystemLog systemLog = SystemLogUtils.getSystemLog(request,username);

        //TODO 统一tenantId
        systemLog.setTenantId(CommonConstant.SYS_TENANT_ID);
        systemLog.setType("SYS_LOGIN");
        systemLog.setTitle(username + "用户登录");
        systemLog.setParams(username);
        systemLog.setResult(CommonConstant.FAIL);
        systemLog.setException(authenticationException.getLocalizedMessage());
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        systemLog.setServiceId(WebUtils.extractClientId(header).orElse("N/A"));

        logServiceClient.saveLog(systemLog, SecurityConstants.FROM_INNER);
        log.info("user：{} login error", username);
    }
}
