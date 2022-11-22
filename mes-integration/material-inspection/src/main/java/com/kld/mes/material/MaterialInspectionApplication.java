package com.kld.mes.material;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/21 14:32
 */
@SpringBootApplication(scanBasePackages="com.kld.mes")
@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class MaterialInspectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaterialInspectionApplication.class, args);
    }
}
