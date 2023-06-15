package com.richfit.mes.produce;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.service.ActionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author sun
 * @Description Produce Service Application
 */
@SpringBootApplication(scanBasePackages = "com.richfit.mes")
@EnableAutoConfiguration(exclude = {FreeMarkerAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class ProduceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProduceServiceApplication.class, args);
    }

    /**
     * 全局返回json格式化
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                .deserializerByType(String.class, new StdScalarDeserializer<String>(String.class) {
                    @Override
                    public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
                            throws IOException {
                        // 去除前后空格
                        return org.springframework.util.StringUtils.trimWhitespace(jsonParser.getValueAsString());
                    }
                });
    }
}
