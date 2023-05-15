package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.feeding.FeedingResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 生产订单投料
 *
 * @author wcy
 * @date 2023/5/15 16:07
 */

@Slf4j
@Service
public class FeedingServiceImpl implements FeedingService {

    @Value("${interface.erp.feeding}")
    private String url;

    @Override
    public FeedingResult sendFeeding(String erpCode, String orderCode, String materialNo, String drawingNo,
                                     String prodQty, String unit, String lgort, String date) {
        //时间参数
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:ZC80_PPIF022>" +
                "<urn:T_ITEM>" +
                "<urn:item>" +
                "<urn:WERKS>" +
                erpCode +
                "</urn:WERKS>" +
                "<urn:AUFNR>" +
                orderCode +
                "</urn:AUFNR>" +
                "<urn:MATNR>" +
                materialNo +
                "</urn:MATNR>" +
                "<urn:CHARG>" +
                drawingNo +
                "</urn:CHARG>" +
                "<urn:ERFMG>" +
                prodQty +
                "</urn:ERFMG>" +
                "<urn:ERFME>" +
                unit +
                "</urn:ERFME>" +
                "<urn:LGORT>" +
                lgort +
                "</urn:LGORT>" +
                "<urn:BUDAT>" +
                date +
                "</urn:BUDAT>" +
                "<urn:ZCANCELF>" +
                "N" +
                "</urn:ZCANCELF>" +
                "</urn:item>" +
                "</urn:T_ITEM>" +
                "</urn:ZC80_PPIF022>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        //构造http请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("text/xml;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<>(soapRequestData, headers);
        RestTemplateBuilder builder = new RestTemplateBuilder();
        //设置链接超时时间
        builder.setConnectTimeout(Duration.ofMinutes(1));
        builder.setReadTimeout(Duration.ofMinutes(1));
        RestTemplate restTemplate = builder.basicAuthentication("OSB_USER", "welcome1").build();
        //返回结果
        String resultStr = restTemplate.postForObject(url, formEntity, String.class);
        //转换返回结果中的特殊字符，返回的结果中会将xml转义，此处需要反转移
        String tmpStr = StringEscapeUtils.unescapeXml(resultStr);
        //结果集封装
        FeedingResult feedingResult = this.convertReturn(tmpStr);
        return feedingResult;
    }

    private FeedingResult convertReturn(String tmpStr) {
        FeedingResult feedingResult = new FeedingResult();
        int codeStart = tmpStr.indexOf("<E_RETURN_TYPE>");
        int codeEnd = tmpStr.indexOf("</E_RETURN_TYPE>");
        String code = tmpStr.substring(codeStart + "<E_RETURN_TYPE>".length(), codeEnd);
        int msgStart = tmpStr.indexOf("<E_RETURN_MSG>");
        int msgEnd = tmpStr.indexOf("</E_RETURN_MSG>");
        String msg = tmpStr.substring(msgStart + "<E_RETURN_TYPE>".length(), msgEnd);
        feedingResult.setCode(code);
        feedingResult.setMsg(msg);
        return feedingResult;
    }
}
