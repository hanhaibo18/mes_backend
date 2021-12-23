package com.richfit.mes.common.security.feign;

import cn.hutool.core.collection.CollUtil;
import com.richfit.mes.common.security.constant.SecurityConstants;
import feign.RequestTemplate;
import org.springframework.cloud.security.oauth2.client.AccessTokenContextRelay;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;


import java.util.Collection;

/**
 * @author sun
 * @Description 扩展OAuth2FeignRequestInterceptor
 */
public class CustomFeignClientInterceptor extends OAuth2FeignRequestInterceptor {

    private final OAuth2ClientContext oAuth2ClientContext;
    private final AccessTokenContextRelay accessTokenContextRelay;

    /**
     * Default constructor which uses the provided OAuth2ClientContext and Bearer tokens
     * within Authorization header
     * @param oAuth2ClientContext provided context
     * @param resource type of resource to be accessed
     * @param accessTokenContextRelay
     */
    public CustomFeignClientInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource,
                                     AccessTokenContextRelay accessTokenContextRelay) {
        super(oAuth2ClientContext, resource);
        this.oAuth2ClientContext = oAuth2ClientContext;
        this.accessTokenContextRelay = accessTokenContextRelay;
    }

    /**
     * Create a template with the header of provided name and extracted extract
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        //区分服务间
        Collection<String> fromHeader = template.headers().get(SecurityConstants.FROM);
        if (CollUtil.isNotEmpty(fromHeader) && fromHeader.contains(SecurityConstants.FROM_INNER)) {
            return;
        }
        accessTokenContextRelay.copyToken();
        if (oAuth2ClientContext != null && oAuth2ClientContext.getAccessToken() != null) {
            super.apply(template);
        }
    }
}
