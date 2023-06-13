package com.pdm.mes.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com/pdm/mes/schedule/mapper")
@SpringBootApplication
public class ScheduleNoticeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleNoticeApplication.class, args);
    }
}

