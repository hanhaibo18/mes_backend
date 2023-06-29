package com.tc.mes.plm.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.richfit.mes.common.model.produce.Notice;
import com.tc.mes.plm.common.Result;
import com.tc.mes.plm.entity.PdmResult;
import com.tc.mes.plm.entity.domain.MesPdmAttachment;
import com.tc.mes.plm.entity.domain.NoticeTenant;
import com.tc.mes.plm.entity.request.ProductionSchedulingRequest;
import com.tc.mes.plm.mapper.MesPdmAttachmentMapper;
import com.tc.mes.plm.mapper.NoticeTenantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tc.mes.plm.constant.PdmConstant.*;


@Slf4j
@Service
public class ProductToPdmServiceImpl {
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

    @Resource
    private NoticeTenantMapper noticeTenantMapper;

    @Resource
    private MesPdmAttachmentMapper mesPdmAttachmentMapper;


    /**
     * 获取cookie
     * @return
     */
    public String getCookieValue() {
        Map<String, String> params = map();
        // 发送请求
        HttpResponse execute = HttpUtil.createPost(url + "/system/login")
                .contentType("application/json")
                .body(JSONUtil.toJsonStr(params))
                .execute();
        String cookieValue = execute.getCookieValue(J_SESSION_ID);
        return cookieValue;
    }

    /**
     * 用户登录
     * @return
     */
    public Result login() {
        Map<String, String> params = map();
        String s = HttpUtil.createPost(url + "/system/login").contentType("application/json").body(JSONUtil.toJsonStr(params)).execute().body();
        PdmResult result = JSONUtil.toBean(s, PdmResult.class);
        return result.getCode() == 200 ? Result.success() : Result.error(String.valueOf(result.getCode()), result.getMsg());
    }

    /**
     * 生产排产单同步到 pdm
     * @param notice
     * @return
     */
    public Result productionSchedulingSync(Notice notice) {
        // 排产单租户
        LambdaQueryWrapper<NoticeTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeTenant::getNoticeId, notice.getId());
        NoticeTenant noticeTenant = noticeTenantMapper.selectOne(wrapper);
        // 附件
        LambdaQueryWrapper<MesPdmAttachment> attachmentWrapper  = new LambdaQueryWrapper<>();
        attachmentWrapper.eq(MesPdmAttachment::getTableId, notice.getId());
        List<MesPdmAttachment> attachments = mesPdmAttachmentMapper.selectList(attachmentWrapper);
        ProductionSchedulingRequest productionSchedulingRequest = new ProductionSchedulingRequest(notice, noticeTenant, attachments);
        //发送请求
        String s = HttpUtil.createPost(url + "/produce/sync").cookie(new HttpCookie(J_SESSION_ID, getCookieValue())).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").body(String.valueOf(productionSchedulingRequest)).execute().body();
        PdmResult result = JSONUtil.toBean(s, PdmResult.class);
        return result.getCode() == 200 ? Result.success() : Result.error(String.valueOf(result.getCode()), result.getMsg());
    }


    private Map<String, String> map() {
        //构造访问参数
        Map<String, String> params = new HashMap<>(2);
        params.put(PASSWORD, password);
        params.put(USER_ID, user_id);
        return params;
    }

}
