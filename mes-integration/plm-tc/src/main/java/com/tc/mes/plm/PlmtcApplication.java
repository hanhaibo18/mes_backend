package com.tc.mes.plm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling()
@MapperScan("com/tc/mes/plm/mapper")
@SpringBootApplication
public class PlmtcApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlmtcApplication.class, args);
    }
}
