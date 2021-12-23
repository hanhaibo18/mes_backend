package com.richfit.mes.auth.authorization.handler;

import com.richfit.mes.common.core.utils.WebUtils;
import com.richfit.mes.common.log.provider.LogServiceClient;
import com.richfit.mes.common.log.util.SystemLogUtils;
import com.richfit.mes.common.model.sys.SystemLog;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.handler.AuthenticationSuccessHandler;
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sun
 * @Description 自定义认证成功处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class CustomAuthenticationSuccessEventHandler implements AuthenticationSuccessHandler {

    private final LogServiceClient logServiceClient;

    @Async
    @Override
    public void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        String username = authentication.getName();
        SystemLog systemLog = SystemLogUtils.getSystemLog(request,username);
        //TODO 统一tenantId
        systemLog.setTenantId(SecurityUtils.getCurrentUser(authentication).getTenantId());
        systemLog.setType("SYS_LOGIN");
        systemLog.setTitle(username + "用户登录");
        systemLog.setParams(username);
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        systemLog.setServiceId(WebUtils.extractClientId(header).orElse("N/A"));

        logServiceClient.saveLog(systemLog, SecurityConstants.FROM_INNER);
        log.info("user：{} login successful", username);
    }
}
