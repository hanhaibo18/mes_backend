package com.richfit.plm.controller;

import com.richfit.plm.Application;
import com.richfit.plm.config.FtpPropertiesConfig;
import com.richfit.plm.util.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author HanHaiBo
 * @date 2023/6/13 17:09
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    private FtpUtils ftpUtils;
    @Autowired
    private FtpPropertiesConfig ftpPropertiesConfig;

    @Test
    public void test(){
        try {
            FTPClient login = FtpUtils.login(ftpPropertiesConfig.getHost(), ftpPropertiesConfig.getPort(), ftpPropertiesConfig.getUserName(), ftpPropertiesConfig.getPassword());
            System.out.println(login);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
