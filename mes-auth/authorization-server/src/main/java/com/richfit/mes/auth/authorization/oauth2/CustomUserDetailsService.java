package com.richfit.mes.auth.authorization.oauth2;

import com.richfit.mes.auth.authorization.service.RoleService;
import com.richfit.mes.auth.authorization.service.UserService;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author sun
 * @Description 自定义用户信息接口
 */
@Slf4j
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String uniqueId) {

        TenantUser user = userService.getByUniqueId(uniqueId);
        log.debug("load user by username :{}", user.toString());

        return new TenantUserDetails(
                user.getId(),
                user.getTenantId(),
                user.getOrgId(),
                user.getBelongOrgId(),
                user.getUserAccount(),
                user.getPasswd(),
                user.getStatus(),
                true,
                true,
                true,
                this.obtainGrantedAuthorities(user), user.getTenantErpCode());
    }

    /**
     * 获得登录者所有角色的权限集合.
     *
     * @param user
     * @return
     */
    protected List<GrantedAuthority> obtainGrantedAuthorities(TenantUser user) {
        log.debug("user:{},roles:{}", user.getUserAccount(), user.getRoleIds());
        Set<String> authsSet = new HashSet<>();
        // 获取角色
        user.getRoleIds().forEach((roleId -> authsSet.add(SecurityConstants.ROLE + roleId)));
        //TODO 获取资源

        return AuthorityUtils.createAuthorityList(authsSet.toArray(new String[0]));
    }
}
