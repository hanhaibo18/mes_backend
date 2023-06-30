package com.kld.mes.plm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling()
@MapperScan("com/kld/mes/plm/mapper")
@SpringBootApplication
public class PlmApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlmApplication.class, args);
    }
}
