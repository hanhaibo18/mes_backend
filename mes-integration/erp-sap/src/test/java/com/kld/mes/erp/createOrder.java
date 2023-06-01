package com.kld.mes.erp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024Response;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024S1;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024T1;
import com.kld.mes.erp.entity.order.creat.*;
import com.kld.mes.erp.service.CertWorkHourServiceImpl;
import com.kld.mes.erp.service.OrderService;
import com.kld.mes.erp.utils.WsTemplateFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wcy
 * @date 2023/4/26 10:45
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class createOrder {

    @Autowired
    OrderService orderService;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    @Test
    public void createOrder() {
        try {
            XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            calendar.setTime(13, 38, 00);
            ObjectFactory objectFactory = new ObjectFactory();
            Zc80Ppif032 zc80Ppif032 = objectFactory.createZc80Ppif032();
            Zc80Ppif032TI zc80Ppif032TI = objectFactory.createZc80Ppif032TI();

            List<Zc80Ppif032SI> zc80Ppif032SOList = new ArrayList<>();
            Zc80Ppif032SI zc80Ppif032SI = objectFactory.createZc80Ppif032SI();
            zc80Ppif032SI.setDocId("4000001");
            zc80Ppif032SI.setMaterial("909098368");
            zc80Ppif032SI.setPlant("X092");
            zc80Ppif032SI.setOrderType("ZP01");
            zc80Ppif032SI.setBasicStartDate("2023-04-19");
            zc80Ppif032SI.setBasicEndDate("2023-04-22");
            zc80Ppif032SI.setBasicStartTime(calendar);
            zc80Ppif032SI.setBasicEndTime(calendar);
            zc80Ppif032SI.setQuantity(new BigDecimal(10));
            zc80Ppif032SI.setQuantityUom("TAI");
            zc80Ppif032SOList.add(zc80Ppif032SI);
            zc80Ppif032TI.setItem(zc80Ppif032SOList);
            zc80Ppif032.setTIn(zc80Ppif032TI);
            WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate("com.kld.mes.erp.entity.order.creat");
            List<Zc80Ppif032SO> list = new ArrayList<>();
            Zc80Ppif032Response o = (Zc80Ppif032Response) webServiceTemplate.marshalSendAndReceive("http://10.30.47.134:8001/ZBZZ/HTXT/XS/ZC80_PPIF032/service/PS/PS_ZC80_PPIF032", zc80Ppif032);
            list = o.getTOut().getItem();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void creat() {

        CertWorkHourServiceImpl test = new CertWorkHourServiceImpl();

        com.kld.mes.erp.entity.certWorkHour.ObjectFactory objectFactory = new com.kld.mes.erp.entity.certWorkHour.ObjectFactory();

        Zc80Ppif024 zc80Ppif024 = objectFactory.createZc80Ppif024();
        Zc80Ppif024T1 zc80Ppif024T1 = objectFactory.createZc80Ppif024T1();

        Zc80Ppif024S1 zc80Ppif024S1 = new Zc80Ppif024S1();
        zc80Ppif024S1.setWerks("X092");
        zc80Ppif024S1.setAufnr("order");
        zc80Ppif024S1.setVornr("0010");
        zc80Ppif024S1.setLmnga(new BigDecimal(1));
        zc80Ppif024S1.setMeinh("件");
        zc80Ppif024S1.setIle01("MIN");

        zc80Ppif024S1.setIsm01(new BigDecimal(60));
        zc80Ppif024S1.setFinConf("P");
        zc80Ppif024S1.setBudat("2022-07-22");
        zc80Ppif024S1.setZflag("Y");


        List<Zc80Ppif024S1> item = new ArrayList<>();
        item.add(zc80Ppif024S1);

        zc80Ppif024T1.setItem(item);
        zc80Ppif024.setIInput(zc80Ppif024T1);


//        try {
//            marshaller.createMarshaller().marshal(zc80Ppif024, System.out);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
        WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate("com.kld.mes.erp.entity.certWorkHour");

        Zc80Ppif024Response zc80Ppif024Response = (Zc80Ppif024Response) webServiceTemplate
                .marshalSendAndReceive("http://emaip.erp.cnpc:80/ZBZZ/MES/ZC80_PPIF024/service/PS/PS_ZC80_PPIF024", zc80Ppif024);


    }

    @Test
    public void creatFeeding() {
        String url = "http://10.30.47.134:8000/ZBZZ/MES/ZC80_PPIF022/service/PS/PS_ZC80_PPIF022";
        //参数
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String date = simpleDateFormat.format(new Date());
        String WERKS = "X091";
        String AUFNR = "1000568032";
        String MATNR = "909153594";
        String CHARG = "AF700303";
        String ERFMG = "1";
        String ERFME = "Z09";
        String LGORT = "2000";
        String BUDAT = date;
        String ZCANCELF = "N";
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
        int codeStart = tmpStr.indexOf("<E_RETURN_TYPE>");
        int codeEnd = tmpStr.indexOf("</E_RETURN_TYPE>");
        String code = tmpStr.substring(codeStart + "<E_RETURN_TYPE>".length(), codeEnd);
        int msgStart = tmpStr.indexOf("<E_RETURN_MSG>");
        int msgEnd = tmpStr.indexOf("</E_RETURN_MSG>");
        String msg = tmpStr.substring(msgStart + "<E_RETURN_TYPE>".length(), msgEnd);
    }
}
