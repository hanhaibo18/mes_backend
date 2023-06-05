package com.tc.mes.pdm.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.tc.mes.pdm.entity.PdmResult;
import com.tc.mes.pdm.entity.ProductionSchedulingDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

import static com.tc.mes.pdm.constant.PdmConstant.J_SESSION_ID;

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
    public String getCookieValue() {
        //构造访问参数
        Map<String, String> params = new HashMap<>(2);
        params.put("password", password);
        params.put("user_id", user_id);
        // 发送请求
        HttpResponse execute = HttpUtil.createPost(url + "/system/login")
                .contentType("application/json")
                .body(JSONUtil.toJsonStr(params))
                .execute();
        String cookieValue = execute.getCookieValue(J_SESSION_ID);
        return cookieValue;
    }

    /**
     * 生产排产单同步到 pdm
     * @param productionSchedulingDto
     * @return
     */
    public PdmResult ProductionSchedulingSync(ProductionSchedulingDto productionSchedulingDto) {
        if (StringUtils.isEmpty(getCookieValue())) {
            return null;
        }
        //发送请求
        String s = HttpUtil.createPost(url + "/produce/sync").cookie(new HttpCookie(J_SESSION_ID, getCookieValue())).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").body(String.valueOf(productionSchedulingDto)).execute().body();
        PdmResult result = JSONUtil.toBean(s, PdmResult.class);
        return result;
    }

}
