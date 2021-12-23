package com.richfit.mes.common.security.config;

import com.richfit.mes.common.security.component.CustomUserAuthenticationConverter;
import com.richfit.mes.common.security.exception.AuthExceptionEntryPoint;
import com.richfit.mes.common.security.handler.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author sun
 * @Description 资源服务器配置-获取用户全部信息
 *
 */
@Configuration
@EnableResourceServer
public class CustomResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired(required = false)
    protected RemoteTokenServices remoteTokenServices;

    /**
     * 开放权限的URL
     */
    private final FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    @Autowired
    public CustomResourceServerConfig(FilterIgnorePropertiesConfig filterIgnorePropertiesConfig) {
        this.filterIgnorePropertiesConfig = filterIgnorePropertiesConfig;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] ignores = new String[filterIgnorePropertiesConfig.getUrls().size()];
        http.csrf().disable()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers(filterIgnorePropertiesConfig.getUrls().toArray(ignores)).permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());

    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        UserAuthenticationConverter userTokenConverter = new CustomUserAuthenticationConverter();
        accessTokenConverter.setUserTokenConverter(userTokenConverter);
        //TODO jwt
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);
        //TODO 转化用户信息
        resources.authenticationEntryPoint(new AuthExceptionEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .tokenServices(remoteTokenServices);
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
