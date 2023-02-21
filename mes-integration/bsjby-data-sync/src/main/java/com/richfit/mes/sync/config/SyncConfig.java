package com.richfit.mes.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaol
 * @date 2023/2/10
 * @apiNote
 */
@Configuration
@ConfigurationProperties(prefix = "sync")
@Data
public class SyncConfig {

    String fileName;

}
