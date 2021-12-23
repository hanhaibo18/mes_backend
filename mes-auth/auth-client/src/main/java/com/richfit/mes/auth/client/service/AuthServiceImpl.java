package com.richfit.mes.auth.client.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 鉴权服务
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    /**
     * Authorization认证开头是"bearer "
     */
    private static final String BEARER = "Bearer ";

    @Override
    public boolean hasPermission(String authentication, String url, String method) {
        // 如果请求未携带token信息, 直接权限
        if (StringUtils.isBlank(authentication) || !authentication.startsWith(BEARER)) {
            log.error("user token is null");
            return Boolean.FALSE;
        }
        //token是否有效
        if (invalidJwtAccessToken(authentication)) {
            return Boolean.FALSE;
        }
        //TODO 从认证服务获取是否有权限,远程调用
        return Boolean.TRUE;
    }

    @Override
    public boolean invalidJwtAccessToken(String authentication) {
        // 是否无效true表示无效
        boolean invalid = Boolean.TRUE;
        try {
            getJwt(authentication);
            invalid = Boolean.FALSE;
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException ex) {
            log.error("user token error :{}", ex.getMessage());
        }
        return invalid;
    }

    @Override
    public Jws<Claims> getJwt(String jwtToken) {
        if (jwtToken.startsWith(BEARER)) {
            jwtToken = StringUtils.substring(jwtToken, BEARER.length());
        }
        //TODO 非对称
        return Jwts.parser()
                .setSigningKey("test".getBytes())
                .parseClaimsJws(jwtToken);
    }
}
