package com.richfit.mes.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author sun
 * @Description Base Service Application
 */
@SpringBootApplication(scanBasePackages="com.richfit.mes")
public class BaseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseServiceApplication.class, args);
    }
}
