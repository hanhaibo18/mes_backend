package com.kld.mes.plm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.kld.mes.plm", exclude = {DataSourceAutoConfiguration.class})
public class PlmtcApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlmtcApplication.class, args);
    }
}
