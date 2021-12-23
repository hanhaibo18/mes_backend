package com.richfit.mes.auth.authorization.controller;

import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sun
 * @Description 测试
 */
@RestController
public class TestController {
    @GetMapping("/api/test")
    public String sayHi() {
        return "Hello"+SecurityUtils.getCurrentUser().getUsername();
    }

}
