package com.kld.mes.pdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wcy
 * @date 2023/3/31 13:55
 */
@SpringBootApplication(scanBasePackages = "com.kld.mes,com.richfit.mes", exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class PdmbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdmbsApplication.class, args);
    }
}
