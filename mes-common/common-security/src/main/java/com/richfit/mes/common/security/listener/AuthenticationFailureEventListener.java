package com.richfit.mes.common.security.listener;

import com.richfit.mes.common.security.handler.AuthenticationFailureHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sun
 * @Description 认证失败事件监听器
 */
@Slf4j
public class AuthenticationFailureEventListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    @Autowired(required = false)
    private AuthenticationFailureHandler failureHandler;

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        log.debug("AuthenticationFailureListener........");
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();

        AuthenticationException authenticationException = event.getException();
        Authentication authentication = (Authentication) event.getSource();

        // 调用自定义业务实现
        if (failureHandler != null) {
            failureHandler.handle(authenticationException, authentication, request, response);
        }
    }
}
