package com.richfit.mes.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author sun
 * @Description Base Service Application
 */
@SpringBootApplication(scanBasePackages="com.richfit.mes")
@EnableDiscoveryClient
@EnableFeignClients
public class BaseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseServiceApplication.class, args);
    }
}
