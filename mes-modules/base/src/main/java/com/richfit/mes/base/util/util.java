/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.richfit.mes.base.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Administrator
 */
public class util {
    // 利用正则表达式校验QQ号
     public  boolean checkQQ(String str) {
         return str.matches("[1-9][0-9]{4,14}");    // matches()方法告知此字符串是否匹配给定的正则表达式。 
    }
     
     // 校验邮箱地址
     public  boolean checkEmail(String email) {
        return email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }
     
     //获取TOKEN
      public static String login() throws Exception  {
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
