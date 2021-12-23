package com.richfit.mes.base.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


/**
 * @author 马峰
 * @Description 通用类单元测试 。
 */
public class utilTest {
    com.richfit.mes.base.util.util util = new util();

    @Test
    public void testCheckQQ() throws Exception {
        boolean result = util.checkQQ("121290895");
        Assert.assertEquals(true, result);
        result = util.checkQQ("qq121290895");
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCheckEmail() throws Exception {
        boolean result = util.checkEmail("mafeng0@cnpc.com.cn");
        Assert.assertEquals(true, result);
        result = util.checkEmail("mafeng02");
        Assert.assertEquals(false, result);
    }

   
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme