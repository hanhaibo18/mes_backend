package com.richfit.mes.base.controller;

import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.junit.jupiter.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class SequenceControllerTest extends AbstractControllerTest {    
    //验证TOKEN
    public String token = "";
    @Before
    public void setUp() throws Exception {
      
       
          // 获取TOKEN
        token = loginSysAdmin();
         // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        super.setUp();
    }

    @Test
    public void testPage() throws Exception {
        
        MvcResult result2 = mockMvc.perform(get("/api/base/sequence/page").header("Authorization", token)
                .param("routerId", "c40ec9890c18e3b20c814eb4f44f2d3e").param("page", "1").param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        //获取返回结果
        String content2 = result2.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200, result2.getResponse().getStatus());
    }
}
