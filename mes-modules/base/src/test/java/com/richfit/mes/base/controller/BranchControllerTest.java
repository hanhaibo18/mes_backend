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




public class BranchControllerTest extends AbstractControllerTest{

  
    public String token = null;

   
    @Before
    public void setUp() throws Exception {
         // 获取TOKEN
        token = loginSysAdmin();
         // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        super.setUp();
    }

    

    @Test
    public void addBranch() throws Exception {
        String object ="{\"branchCode\":\"testCode\",\"branchName\":\"测试机构\",\"branchLevel\":\"1\",\"branchType\":\"1\"}";
        MvcResult result = mockMvc.perform(
                post("/api/base/branch/branch").content(object).contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }

    @Test
    public void updateBranch() throws Exception {
        String object ="{\"id\":\"5a9fac832172dae2f8b6c0ad7562ddf8\", \"branchCode\":\"testCode\",\"branchName\":\"测试机构\",\"branchLevel\":\"1\",\"branchType\":\"1\"}";
        MvcResult result = mockMvc.perform(
                put("/api/base/branch/branch").content(object).contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }

    @Test
    public void selectBranchByCode() throws Exception{
        MvcResult result = mockMvc.perform(
                get("/api/base/branch/select_branches_by_code").param("branchCode", "testCode").contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }

    @Test
    public void deleteBranchById() throws Exception{
       
        MvcResult result = mockMvc.perform(
                delete("/api/base/branch/branch/5a9fac832172dae2f8b6c0ad7562ddf8").contentType(MediaType.APPLICATION_JSON).header("Authorization",token)).andReturn();
        //获取返回结果
        String content = result.getResponse().getContentAsString();
        //断言 判断返回结果状态
        Assertions.assertEquals(200,  result.getResponse().getStatus());
    }
}