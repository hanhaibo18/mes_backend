package com.tc.mes.pdm;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling()
@MapperScan("com/tc/mes/pdm/mapper")
@SpringBootApplication
public class PdmtcApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdmtcApplication.class, args);
    }
}
