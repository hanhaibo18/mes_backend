package com.kld.mes.attachment;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com/kld/mes/attachment/mapper")
@SpringBootApplication
public class AttachmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttachmentApplication.class, args);
    }
}
