package com.tc.mes.pdm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling()
@SpringBootApplication(scanBasePackages = "com.tc.mes.pdm", exclude = {DataSourceAutoConfiguration.class})
public class PdmtcApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdmtcApplication.class, args);
    }
}
