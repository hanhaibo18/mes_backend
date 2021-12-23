package com.richfit.mes.gateway.web.service;

import com.richfit.mes.auth.client.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 签权服务
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    /**
     * auth-client模块提供
     */
    @Autowired
    private AuthService authService;

    @Override
    public boolean permission(String authentication, String url, String method) {
        return authService.hasPermission(authentication, url, method);
    }
}
