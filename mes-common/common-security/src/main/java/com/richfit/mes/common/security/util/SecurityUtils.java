package com.richfit.mes.common.security.util;

import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author sun
 * @Description 系统工具类
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户
     */
    public static TenantUserDetails getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof TenantUserDetails) {
            return (TenantUserDetails) principal;
        }

        return null;
    }

    /**
     * 获取用户
     */
    public static TenantUserDetails getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return getCurrentUser(authentication);
    }

    /**
     * 获取用户角色信息
     *
     * @return 角色集合
     */
    public static List<String> getRoles() {
        Authentication authentication = getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roleIds = new ArrayList<>();
        authorities.stream().filter(granted -> StringUtils.startsWith(granted.getAuthority(), SecurityConstants.ROLE))
                .forEach(granted -> {
                    String id = StringUtils.removeStart(granted.getAuthority(), SecurityConstants.ROLE);
                    roleIds.add(id);
                });
        return roleIds;
    }
}
