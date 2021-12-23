package com.richfit.mes.common.log;

import com.richfit.mes.common.log.aspect.SystemLogAspect;
import com.richfit.mes.common.log.event.SystemLogListener;
import com.richfit.mes.common.log.provider.LogServiceClient;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author sun
 * @Description 日志自动配置
 */
@EnableAsync
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
public class LogAutoConfiguration {
    private final LogServiceClient logServiceClient;

    @Bean
    public SystemLogListener systemLogListener() {
        return new SystemLogListener(logServiceClient);
    }

    @Bean
    public SystemLogAspect systemLogAspect(ApplicationEventPublisher publisher) {
        return new SystemLogAspect(publisher);
    }
}
