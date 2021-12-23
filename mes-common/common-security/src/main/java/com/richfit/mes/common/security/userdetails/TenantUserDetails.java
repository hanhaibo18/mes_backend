package com.richfit.mes.common.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author sun
 * @Description 扩展userDetails.User
 */
public class TenantUserDetails extends User {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 部门ID
     */
    private String orgId;

    /**
     * 所在机构ID
     */
    private String belongOrgId;

    public TenantUserDetails(String id, String tenantId, String orgId, String belongOrgId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = id;
        this.tenantId = tenantId;
        this.orgId = orgId;
        this.belongOrgId = belongOrgId;
    }
    public String getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getBelongOrgId() {
        return belongOrgId;
    }

}
