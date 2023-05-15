package com.kld.mes.erp.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class FeedingUtil {

    @Value("${interface.erp.order-purchase}")
    private String URL_PURCHASE;

    public static void main(String[] args) {
        FeedingUtil feedingUtil = new FeedingUtil();
        String url = "http://10.30.47.134:8000/ZBZZ/MES/ZC80_PPIF022/service/PS/PS_ZC80_PPIF022";
        //参数
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String date = simpleDateFormat.format(new Date());
        String WERKS = "X091";
        String AUFNR = "1000568032";
        String MATNR = "909091621";
        String CHARG = "AF700303";
        String ERFMG = "1";
        String ERFME = "Z09";
        String LGORT = "2000";
        String BUDAT = date;
        String ZCANCELF = "N";
        String message = feedingUtil.creatFeeding(url, WERKS, AUFNR, MATNR, CHARG, ERFMG, ERFME, LGORT, BUDAT, ZCANCELF);
        System.out.print(message);
    }

    public String creatFeeding(String url, String WERKS, String AUFNR, String MATNR, String CHARG, String ERFMG, String ERFME,
                               String LGORT, String BUDAT, String ZCANCELF) {
        //时间参数
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:ZC80_PPIF022>" +
                "<urn:T_ITEM>" +
                "<urn:item>" +
                "<urn:WERKS>" +
                WERKS +
                "</urn:WERKS>" +
                "<urn:AUFNR>" +
                AUFNR +
                "</urn:AUFNR>" +
                "<urn:MATNR>" +
                MATNR +
                "</urn:MATNR>" +
                "<urn:CHARG>" +
                CHARG +
                "</urn:CHARG>" +
                "<urn:ERFMG>" +
                ERFMG +
                "</urn:ERFMG>" +
                "<urn:ERFME>" +
                ERFME +
                "</urn:ERFME>" +
                "<urn:LGORT>" +
                LGORT +
                "</urn:LGORT>" +
                "<urn:BUDAT>" +
                BUDAT +
                "</urn:BUDAT>" +
                "<urn:ZCANCELF>" +
                ZCANCELF +
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
        return tmpStr;
    }

}
