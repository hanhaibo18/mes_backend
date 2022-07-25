package com.kld.mes.wms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 10:24
 */
@SpringBootApplication(scanBasePackages = "com.kld.mes", exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
public class WmsIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsIntegrationApplication.class, args);
    }
}
