package com.kld.mes.wechat.service;

import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HanHaiBo
 * @date 2023/2/27 9:55
 */

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${message.appkey}")
    private String appkey;
    @Value("${message.appsecret}")
    private String appsecret;
    @Value("${message.tokenUrl}")
    private String tokenUrl;
    @Value("${message.sendMessageUrl}")
    private String sendMessageUrl;

    @Override
    public String sentGzhMessage(MessageDto messageInfo) {
        String token = getToken(appkey, appsecret);
        //创建正则表达式匹配规则
        Pattern pattern = Pattern.compile("^\\r\\n");
        Matcher matcher = pattern.matcher(token);
        //将匹配字符串替换
        token = matcher.replaceAll("");
        if (StringUtils.isNullOrEmpty(token)) {
            return "获取token失败";
        }
        String url = sendMessageUrl + "Token=" + token;
        String result = restTemplate.postForObject(url, messageInfo, String.class);
        matcher = pattern.matcher(result);
        result = matcher.replaceAll("");
        return result;
    }

    private String getToken(String appkey, String appsecret) {
        String url = tokenUrl + "act=login&appkey=" + appkey + "&appsecret=" + appsecret;
        return restTemplate.getForEntity(url, String.class).getBody();
    }
}
