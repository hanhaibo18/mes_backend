package com.richfit.mes.base.controller;
import com.richfit.mes.base.util.util;
import com.richfit.mes.base.service.RouterService;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


/**
 * @author 马峰
 * @Description 工艺单元测试  模拟前端请求
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
public class RouterControllerTest {

    @Autowired 
    private MockMvc mockMvc;
    
    @Mock   //模拟service
    public RouterService routerService;
    
    @Autowired //负责找到controller
    @InjectMocks  //负责把所有有@Mock注解的对象注入到controller中
    RouterController routerController;
    

    
    String token = null;
     @Before
    public void setUp()  throws Exception {
         
         
        // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        MockitoAnnotations.initMocks(this);
        // 创建模拟的当前Controller
        this.mockMvc = MockMvcBuilders.standaloneSetup(routerController).build();
    }

    @Test
    public void testPage() throws Exception {
          token = util.login();
        MvcResult result = mockMvc.perform(get("/api/base/router/page").param("page", "1").param("limit", "10").contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
              
        //获取返回结果
         String content = result.getResponse().getContentAsString();
         //断言 判断返回结果状态
         Assertions.assertEquals(200,  result.getResponse().getStatus()); 
    }

    
    @Test
    public void testAddRouter() throws Exception  {
        token = util.login();
        String object ="{\"id\":\"1\",\"routerName\":\"测试工艺\",\"status\":\"1\",\"routerNo\":\"test001\"}";
        MvcResult result = mockMvc.perform(post("/api/base/router/add").content(object).contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
         String content = result.getResponse().getContentAsString();
         //断言 判断返回结果状态
         Assertions.assertEquals(200,  result.getResponse().getStatus()); 
    }
    
    @Test
    public void testDelete() throws Exception  {
          token = util.login();
        String object ="[\"1\"]";
        MvcResult result = mockMvc.perform(post("/api/base/router/delete").header("Authorization",token).content(object).contentType(MediaType.APPLICATION_JSON)).andReturn();
        //获取返回结果
         String content = result.getResponse().getContentAsString();
         //断言 判断返回结果状态
         Assertions.assertEquals(200,  result.getResponse().getStatus()); 
    }
}

