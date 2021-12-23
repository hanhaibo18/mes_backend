package com.richfit.mes.base.controller;

import com.richfit.mes.base.service.ProductService;
import com.richfit.mes.base.service.ProductionBomService;
import com.richfit.mes.base.util.utilTest;
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


public class ProductionBomControllerTest extends AbstractControllerTest{


   

    @Before
    public void setUp() throws Exception {
             // 获取TOKEN
        token = loginSysAdmin();
         // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        super.setUp();
    }

     public String token = null;

    @Test
    void add() throws Exception {
        String object ="{\"materialNo\":\"100010000\",\"drawingNo\":\"AH10001\",\"status\":\"0\", \"number\":\"1\"}";
        MvcResult result = mockMvc.perform(
                post("/api/base/production_bom/production_bom").content(object).contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }

    @Test
    void update() throws Exception {
        String object ="{\"id\":\"eba85acd7aebb9be11430ab4b62f4858\", \"materialNo\":\"100010000\",\"drawingNo\":\"AH10001\",\"status\":\"0\", \"number\":\"1\"}";
        MvcResult result = mockMvc.perform(
                put("/api/base/production_bom/production_bom").content(object).contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }

    @Test
    void select() throws Exception{
        MvcResult result = mockMvc.perform(
                get("/api/base/production_bom/production_bom").param("drawingNo", "AH10001").contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }

    @Test
    void remove() throws Exception{
        MvcResult result = mockMvc.perform(
                delete("/api/base/production_bom/production_bom").content("[\"AH10001\"]").contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }
}