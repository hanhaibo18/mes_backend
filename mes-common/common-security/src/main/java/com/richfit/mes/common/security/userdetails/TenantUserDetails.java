package com.richfit.mes.common.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author sun
 * @Description 扩展userDetails.User
 */
public class TenantUserDetails extends User {
    private static final long serialVersionUID = 1630579217770988519L;
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

    /**
     * 所在租户ERP code
     */
    private String tenantErpCode;

    /**
     * 所在租户公司 code
     */
    private String companyCode;

    public TenantUserDetails(String id, String tenantId, String orgId, String belongOrgId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String tenantErpCode, String companyCode) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = id;
        this.tenantId = tenantId;
        this.orgId = orgId;
        this.belongOrgId = belongOrgId;
        this.tenantErpCode = tenantErpCode;
        this.companyCode = companyCode;
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

    public String getTenantErpCode() {
        return tenantErpCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public boolean isSysAdmin() {
        List<GrantedAuthority> authorities = new ArrayList<>(this.getAuthorities());
        boolean isAdmin = false;
        for (GrantedAuthority authority : authorities) {
            //超级管理员 ROLE_12345678901234567890000000000000
            if ("ROLE_12345678901234567890000000000000".equals(authority.getAuthority())) {
                isAdmin = true;
                break;
            }
        }

        return isAdmin;
    }

}
