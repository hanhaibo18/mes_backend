package com.richfit.plm.config;

/**
 * @author HanHaiBo
 * @date 2023/6/13 15:24
 */

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ftp配置信息
 */
@Component
@ConfigurationProperties(prefix = "ftp.server")
@Data
public class FtpPropertiesConfig {
    /**
     * IP地址
     */
    private String host;

    /**
     * 端口
     */
    private String port;

    /**
     * 登陆用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;
}
