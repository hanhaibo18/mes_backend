package com.richfit.mes.auth.authorization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author sun
 * @Description 授权服务
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.richfit.mes")
@EnableAsync
public class Oauth2AuthorizationApplication  {
    public static void main(String[] args) {
        SpringApplication.run(Oauth2AuthorizationApplication.class, args);
    }

    /**
     * 全局返回json格式化
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(tz);
        return builder -> builder
                .deserializerByType(String.class, new StdScalarDeserializer<String>(String.class) {
                    @Override
                    public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
                            throws IOException {
                        // 去除前后空格
                        return org.springframework.util.StringUtils.trimWhitespace(jsonParser.getValueAsString());
                    }
                }).dateFormat(df);
    }
}
