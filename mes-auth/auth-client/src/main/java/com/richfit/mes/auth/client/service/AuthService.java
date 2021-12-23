package com.richfit.mes.auth.client.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * @author sun
 * @Description 鉴权服务
 */
public interface AuthService {

    /**
     * 判断用户是否有权限
     *
     * @param authentication
     * @param url
     * @param method
     * @return true/false
     */
    boolean hasPermission(String authentication, String url, String method);
    /**
     * 是否无效authentication
     *
     * @param authentication
     * @return
     */
    boolean invalidJwtAccessToken(String authentication);

    /**
     * 从认证信息中提取jwt token 对象
     *
     * @param jwtToken toke信息 header.payload.signature
     * @return Jws对象
     */
    Jws<Claims> getJwt(String jwtToken);
}
