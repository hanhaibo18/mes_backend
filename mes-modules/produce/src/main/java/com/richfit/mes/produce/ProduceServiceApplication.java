package com.richfit.mes.produce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author sun
 * @Description Produce Service Application
 */
@SpringBootApplication(scanBasePackages="com.richfit.mes")
@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class ProduceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProduceServiceApplication.class, args);
    }
}
