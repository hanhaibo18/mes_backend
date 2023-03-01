package com.kld.mes.wechat.service;

import com.richfit.mes.common.model.produce.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public String sentGzhMessage(MessageDto messageInfo) {
        String token = getToken(appkey, appsecret);
        if (token.startsWith("\r\n")){
            token = token.substring(2);
        }
        if (token.isEmpty()) {
            return "获取token失败";
        }
        String url = "http://10.134.100.222:909/sendmsg.php?Token=" + token;
        String result = restTemplate.postForObject(url, messageInfo, String.class);
        if (result.startsWith("\r\n")){
            result = result.substring(2);
        }
        return result;
    }

    private String getToken(String appkey, String appsecret) {
        String url = "http://10.134.100.222:909/get_token.php?act=login&appkey=" + appkey + "&appsecret=" + appsecret;
        return restTemplate.getForEntity(url, String.class).getBody();
    }
}
