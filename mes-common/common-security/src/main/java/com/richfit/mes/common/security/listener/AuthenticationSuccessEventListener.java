package com.richfit.mes.common.security.listener;

import cn.hutool.core.collection.CollUtil;
import com.richfit.mes.common.security.handler.AuthenticationSuccessHandler;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sun
 * @Description 认证成功事件监听器
 */
@Slf4j
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired(required = false)
    private AuthenticationSuccessHandler successHandler;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        log.debug("Authentication Success Event Listener........");
        Authentication authentication = (Authentication) event.getSource();
        if (successHandler != null && isUserAuthentication(authentication)) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            HttpServletResponse response = requestAttributes.getResponse();

            successHandler.handle(authentication, request, response);
        }
    }

    private boolean isUserAuthentication(Authentication authentication) {
        return authentication.getPrincipal() instanceof TenantUserDetails
                || CollUtil.isNotEmpty(authentication.getAuthorities());
    }
}
