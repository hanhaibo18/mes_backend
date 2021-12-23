package com.richfit.mes.auth.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

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
}
