package com.richfit.mes.auth.authorization.oauth2.enhancer;

import com.google.common.collect.Maps;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

/**
 * @author sun
 * @Description 自定义token携带内容
 */
public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = Maps.newHashMap();
        //TODO 自定义token内容
        TenantUserDetails tenantUserDetails = (TenantUserDetails) authentication.getUserAuthentication().getPrincipal();

        additionalInfo.put("id",tenantUserDetails.getUserId());
        additionalInfo.put("tenantId",tenantUserDetails.getTenantId());
        additionalInfo.put("orgId",tenantUserDetails.getOrgId());
        additionalInfo.put("belongOrgId",tenantUserDetails.getBelongOrgId());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
