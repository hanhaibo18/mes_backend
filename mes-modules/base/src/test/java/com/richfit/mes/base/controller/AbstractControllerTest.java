package com.richfit.mes.base.controller;



import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.boot.test.context.SpringBootContextLoader;

import org.springframework.test.context.ContextConfiguration;


/**
 * 单元测试抽象基类
 * @author mafeng02
 */

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AbstractControllerTest.class, loader = SpringBootContextLoader.class)
@SpringBootTest(classes = com.richfit.mes.base.BaseServiceApplication.class)
@WebAppConfiguration
@ComponentScan({"com.richfit.mes.base"})
public class  AbstractControllerTest {
    public MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private javax.servlet.Filter springSecurityFilterChain;
    
    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
       
    }
     
     @Test
      public void init() throws Exception {
     }
     
     
     public String loginSysAdmin() throws Exception {
    
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        params.add("grant_type", "password");
        params.add("username", "admin");
        params.add("password", "admin@mes");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        headers.add(HttpHeaders.AUTHORIZATION, "Basic d2ViX2FwcDptZXMtd2ViLXNlY3JldA==");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        String content = restTemplate.exchange("http://11.11.62.2/oauth/token", HttpMethod.POST, entity, String.class).getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(content);
        String token = "Bearer " + jsonNode.get("access_token").asText();
        return token;
     }


}


