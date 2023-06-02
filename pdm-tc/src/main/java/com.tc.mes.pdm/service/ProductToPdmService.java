package com.tc.mes.pdm.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.tc.mes.pdm.entity.PlmResult;
import com.tc.mes.pdm.entity.ProductionSchedulingDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ProductToPdmService {
    /**
     * 用户名
     */
    @Value("${pdm.user_id}")
    private String user_id;

    /**
     * 密码
     */
    @Value("${pdm.password}")
    private String password;

    /**
     * url
     */
    @Value("${pdm.url}")
    private String url;

    /**
     * 获取cookie
     * @return
     */
    public String getCookie() {
        //构造访问参数
        Map<String, String> params = new HashMap<>(2);
        params.put("password", password);
        params.put("user_id", user_id);
        //获取响应
        HttpResponse execute = HttpUtil.createPost(url + "/system/login")
                .contentType("application/json")
                .body(JSONUtil.toJsonStr(params))
                .execute();
        String jsessionid = execute.getCookieValue("JSESSIONID");
        return  jsessionid;
    }

    /**
     * 生产排产单同步到 pdm
     * @param productionSchedulingDto
     * @return
     */
    public PlmResult ProductionSchedulingSync(ProductionSchedulingDto productionSchedulingDto) {
        String cookie = getCookie();
        if (StringUtils.isEmpty(cookie)) {
            return null;
        }
        //调用上传接口
        String s = HttpRequest.post(url + "/produce/sync").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").body(String.valueOf(productionSchedulingDto)).execute().body();
        PlmResult result = JSONUtil.toBean(s, PlmResult.class);
        return result;
    }

}
