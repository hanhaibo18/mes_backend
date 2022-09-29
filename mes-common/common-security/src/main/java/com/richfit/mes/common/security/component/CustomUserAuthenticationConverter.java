package com.richfit.mes.common.security.component;

import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sun
 * @Description 根据checktoken 的结果转化用户信息
 */
public class CustomUserAuthenticationConverter implements UserAuthenticationConverter {
    private static final String N_A = "N/A";

    /**
     * Extract information about the user to be used in an access token (i.e. for resource
     * servers).
     *
     * @param authentication an authentication representing a user
     * @return a map of key values representing the unique information about the user
     */
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    /**
     * Inverse of {@link #convertUserAuthentication(Authentication)}. Extracts an
     * Authentication from a map.
     *
     * @param map a map of user information
     * @return an Authentication representing the user or null if there is none
     */
    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);

            String username = (String) map.get("user_name");
            String id = (String) map.get("id");
            String tenantId = (String) map.get("tenantId");
            String orgId = (String) map.get("orgId");
            String belongOrgId = (String) map.get("belongOrgId");
            String tenantErpCode = (String) map.get("tenantErpCode");
            String companyCode = (String) map.get("companyCode");
            TenantUserDetails user = new
                    TenantUserDetails(
                    id,
                    tenantId,
                    orgId,
                    belongOrgId,
                    username,
                    N_A,
                    true,
                    true,
                    true,
                    true,
                    authorities, tenantErpCode, companyCode);
            return new UsernamePasswordAuthenticationToken(user, N_A, authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(
                    StringUtils.collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        return AuthorityUtils.NO_AUTHORITIES;
    }
}
