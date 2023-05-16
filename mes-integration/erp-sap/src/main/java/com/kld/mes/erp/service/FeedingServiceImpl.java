package com.kld.mes.erp.service;

import cn.hutool.core.date.DateUtil;
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
import java.util.Date;

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
    private String URL;
    @Value("${interface.erp.username}")
    private String USERNAME;
    @Value("${interface.erp.password}")
    private String PASSWORD;

    @Override
    public FeedingResult sendFeeding(String erpCode, String orderCode, String materialNo, String drawingNo,
                                     String prodQty, String unit, String lgort, Date date) throws Exception {
        FeedingResult feedingResult = new FeedingResult();
        switch (lgort) {
            case "BOMCO_BF_JM":
                lgort = "2001";
                break;
            case "BOMCO_BF_JG":
                lgort = "2000";
                break;
            case "BOMCO_ZS_JJCJ":
                lgort = "3000";
                break;
            case "BOMCO_ZC_JJ":
                lgort = "1006";
                break;
            case "BOMCO_HY_JJ":
                lgort = "2001";
                break;
            default:
                lgort = "";
                break;
        }
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
                DateUtil.format(date, "yyyy-MM-dd") +
                "</urn:BUDAT>" +
                "<urn:ZCANCELF>" +
                "N" +
                "</urn:ZCANCELF>" +
                "</urn:item>" +
                "</urn:T_ITEM>" +
                "</urn:ZC80_PPIF022>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        System.out.println("---------------------");
        System.out.println(soapRequestData);
        //构造http请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("text/xml;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<>(soapRequestData, headers);
        RestTemplateBuilder builder = new RestTemplateBuilder();
        //设置链接超时时间
        builder.setConnectTimeout(Duration.ofMinutes(1));
        builder.setReadTimeout(Duration.ofMinutes(1));
        RestTemplate restTemplate = builder.basicAuthentication(USERNAME, PASSWORD).build();
        System.out.println(URL);
        //返回结果
        String resultStr = restTemplate.postForObject(URL, formEntity, String.class);
        //转换返回结果中的特殊字符，返回的结果中会将xml转义，此处需要反转移
        String tmpStr = StringEscapeUtils.unescapeXml(resultStr);
        //结果集封装
        feedingResult = this.convertReturn(tmpStr);
        return feedingResult;
    }

    //处理结果集
    private FeedingResult convertReturn(String tmpStr) {
        FeedingResult feedingResult = new FeedingResult();
        String feedingCode = "";
        int codeStart = tmpStr.indexOf("<E_RETURN_TYPE>");
        int codeEnd = tmpStr.indexOf("</E_RETURN_TYPE>");
        String code = tmpStr.substring(codeStart + "<E_RETURN_TYPE>".length(), codeEnd);
        int msgStart = tmpStr.indexOf("<E_RETURN_MSG>");
        int msgEnd = tmpStr.indexOf("</E_RETURN_MSG>");
        String msg = tmpStr.substring(msgStart + "<E_RETURN_TYPE>".length(), msgEnd);
        if (msg.contains("成功")) {
            int feedingCodeStart = tmpStr.indexOf("<E_MBLNR>");
            int feedingCodeEnd = tmpStr.indexOf("</E_MBLNR>");
            feedingCode = tmpStr.substring(feedingCodeStart + "<E_MBLNR>".length(), feedingCodeEnd);
        }
        feedingResult.setCode(code);
        feedingResult.setMsg(msg);
        feedingResult.setFeedingCode(feedingCode);
        return feedingResult;
    }
}
