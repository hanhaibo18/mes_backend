package com.richfit.mes.common.security.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sun
 * @Description 不做权限拦截的URL
 */
@Configuration
@RefreshScope
@Data
@ConditionalOnExpression("!'${secure.ignore}'.isEmpty()")
@ConfigurationProperties(prefix = "secure.ignore")
public class FilterIgnorePropertiesConfig {
    private List<String> urls = new ArrayList<>();
}
