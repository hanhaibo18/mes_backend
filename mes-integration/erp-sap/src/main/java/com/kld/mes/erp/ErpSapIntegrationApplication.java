package com.kld.mes.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/21 14:32
 */
@SpringBootApplication(scanBasePackages = "com.kld.mes,com.richfit.mes", exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
public class ErpSapIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpSapIntegrationApplication.class, args);
    }
}
