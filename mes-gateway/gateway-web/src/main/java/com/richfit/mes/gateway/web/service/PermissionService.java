package com.richfit.mes.gateway.web.service;

/**
 * @author sun
 * @Description 签权服务
 */
public interface PermissionService {
    /**
     * @param authentication
     * @param method
     * @param url
     * @return
     */
    boolean permission(String authentication, String url, String method);
}
