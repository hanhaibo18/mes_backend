package com.kld.mes.plm.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.kld.mes.plm.entity.PdmResult;
import com.kld.mes.plm.entity.dto.ProductionSchedulingDto;
import com.kld.mes.plm.entity.request.ProductionSchedulingRequest;
import com.kld.mes.plm.entity.vo.ProduceNoticeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kld.mes.plm.constant.PdmConstant.*;


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
    public PdmResult login() {
        Map<String, String> params = map();
        String s = HttpUtil.createPost(url + "/system/login").contentType("application/json").body(JSONUtil.toJsonStr(params)).execute().body();
        PdmResult result = JSONUtil.toBean(s, PdmResult.class);
        return result;
    }

    /**
     * 生产排产单同步到 pdm
     * @param produceNoticeDtoList
     * @return
     */
    public PdmResult productionSchedulingSync(List<ProduceNoticeVo> produceNoticeDtoList) {
        if (StringUtils.isEmpty(getCookieValue())) {
            return null;
        }
        List<ProductionSchedulingDto> productionSchedulingDtoList = convertDto(produceNoticeDtoList);
        List<ProductionSchedulingRequest> productionSchedulingRequestList = convertRequest(productionSchedulingDtoList);
        //发送请求
        String s = HttpUtil.createPost(url + "/produce/sync").cookie(new HttpCookie(J_SESSION_ID, getCookieValue())).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").body(String.valueOf(productionSchedulingRequestList)).execute().body();
        PdmResult result = JSONUtil.toBean(s, PdmResult.class);
        return result;
    }

    private List<ProductionSchedulingDto> convertDto(List<ProduceNoticeVo> produceNoticeDtoList) {
        List<ProductionSchedulingDto> productionSchedulingDtoList = new ArrayList<>();
        for (ProduceNoticeVo produceNotice : produceNoticeDtoList) {
            ProductionSchedulingDto productionSchedulingDto = new ProductionSchedulingDto();
            productionSchedulingDto.setSchedulingNo(produceNotice.getProductionOrder());
            productionSchedulingDto.setNoticSouce(produceNotice.getNotificationType());
            productionSchedulingDto.setTechPlanTime(String.valueOf(produceNotice.getTechnicalCompletionTime()));
            productionSchedulingDto.setDeliveryDate(produceNotice.getDeliveryDate());
            productionSchedulingDto.setExecuOrganization(produceNotice.getUnit());
            productionSchedulingDto.setWorkNo(produceNotice.getWorkNo());
            productionSchedulingDto.setSchedulingGroup(produceNotice.getIssuingUnit());
            productionSchedulingDto.setSchedulingDate(String.valueOf(produceNotice.getProductionScheduleDate()));
            productionSchedulingDto.setCustomerName(produceNotice.getUserUnit());
            productionSchedulingDto.setSchedulingType(produceNotice.getProductionType());
            productionSchedulingDto.setProductName(produceNotice.getProduceName());
            productionSchedulingDto.setPreviewUrl(produceNotice.getPreviewUrl());
            productionSchedulingDtoList.add(productionSchedulingDto);
        }
        return productionSchedulingDtoList;
    }

    private List<ProductionSchedulingRequest> convertRequest(List<ProductionSchedulingDto> productionSchedulingDtoList) {
        List<ProductionSchedulingRequest> productionSchedulingRequestList = new ArrayList<>();
        productionSchedulingDtoList.forEach(e -> {
            ProductionSchedulingRequest schedulingRequest = new ProductionSchedulingRequest();
            schedulingRequest.setScheduling_no(e.getSchedulingNo());
            schedulingRequest.setNotic_souce(e.getNoticSouce());
            schedulingRequest.setTech_plan_time(e.getTechPlanTime());
            schedulingRequest.setDelivery_date(e.getDeliveryDate());
            schedulingRequest.setExecu_organization(e.getExecuOrganization());
            schedulingRequest.setWork_no(e.getWorkNo());
            schedulingRequest.setScheduling_group(e.getSchedulingGroup());
            schedulingRequest.setScheduling_date(e.getSchedulingDate());
            schedulingRequest.setCustomer_name(e.getCustomerName());
            schedulingRequest.setScheduling_type(e.getSchedulingType());
            schedulingRequest.setProduct_name(e.getProductName());
            schedulingRequest.setPreview_url(e.getPreviewUrl());
            productionSchedulingRequestList.add(schedulingRequest);
        });
        return productionSchedulingRequestList;
    }

    private Map<String, String> map() {
        //构造访问参数
        Map<String, String> params = new HashMap<>(2);
        params.put(PASSWORD, password);
        params.put(USER_ID, user_id);
        return params;
    }

}
