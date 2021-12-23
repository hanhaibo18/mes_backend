package com.richfit.mes.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author sun
 * @Description 监控中心
 */
@EnableAdminServer
@SpringBootApplication
@EnableDiscoveryClient
public class MesMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(MesMonitorApplication.class, args);
    }
}
