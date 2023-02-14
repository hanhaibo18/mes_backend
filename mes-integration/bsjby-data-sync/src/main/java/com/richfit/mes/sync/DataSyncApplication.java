package com.richfit.mes.sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author gaol
 * @date 2023/2/9
 * @apiNote
 */

@SpringBootApplication(scanBasePackages = "com.richfit.mes")
public class DataSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataSyncApplication.class, args);
    }
}
