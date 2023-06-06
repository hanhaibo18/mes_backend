package com.kld.mes.erp.utils;

import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class PurchaseOrderUtil {

    @Value("${interface.erp.order-purchase}")
    private String URL_PURCHASE;

    public static void main(String[] args) {
        PurchaseOrderUtil purchaseOrderUtil = new PurchaseOrderUtil();
        String url = "http://10.30.47.134:8000/ZBZZ/HTXT/GetPurchaseInfoByTime2/service/PS/GetPurchaseInfoByTime2";
        List<ProducePurchaseOrder> producePurchaseOrders = purchaseOrderUtil.queryPurchaseSynchronization(url, "2023-01-01", "2023-04-20");
    }

    public List<ProducePurchaseOrder> queryPurchaseSynchronization(String url, String startTime, String endTime) {
        //时间参数
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:ZC80_MMIF019>" +
                //拼接查询开始时间
                "<urn:I_DATE_CHANGE>" +
                startTime +
//                .append("2021-10-01")
                "</urn:I_DATE_CHANGE>" +
                //拼接查询结束时间
                "<urn:I_DATE_UNTIL>" +
//                .append("2021-10-07")
                endTime +
                "</urn:I_DATE_UNTIL>" +
                //固定时间参数
                "<urn:I_TIME_CHANGE>00:00:00</urn:I_TIME_CHANGE>" +
                "<urn:I_TIME_UNTIL>23:59:59</urn:I_TIME_UNTIL>" +
                "</urn:ZC80_MMIF019>" +
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
        //获取工厂ID
        return xmlAnalysis(tmpStr);
    }

    private List<ProducePurchaseOrder> xmlAnalysis(String xml) {
        Document doc = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<ProducePurchaseOrder> list = new ArrayList<>();
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            Element body = rootElt.element("Body");
            if (body.hasContent()) {
                Element response = body.element("ZC80_MMIF019.Response");
                if (response.hasContent()) {
                    Element tEKPO = response.element("T_EKPO");
                    Element tEKKO = response.element("T_EKKO");
                    if (tEKKO.hasContent()) {
                        Iterator<Element> itemTEKKON = tEKKO.elementIterator("item");
                        while (itemTEKKON.hasNext()) {
                            Element itemTEKKONNext = itemTEKKON.next();
                            String orderNo = itemTEKKONNext.elementTextTrim("EBELN");
                            String orderType = itemTEKKONNext.elementTextTrim("BSART");
                            //先用String接收数据 以后再处理
                            Date purchaseDate = format.parse(itemTEKKONNext.elementTextTrim("BEDAT"));
                            String lifnr = itemTEKKONNext.elementTextTrim("LIFNR");
                            if (tEKPO.hasContent()) {
                                Iterator<Element> item = tEKPO.elementIterator("item");
                                while (item.hasNext()) {
                                    Element itemNext = item.next();
                                    ProducePurchaseOrder purchase = new ProducePurchaseOrder();
                                    purchase.setOrderNo(orderNo);
                                    purchase.setOrderType(orderType);
                                    purchase.setPurchaseDate(purchaseDate);
                                    purchase.setLifnr(lifnr);
                                    Boolean isLOEKZ = itemNext.elementTextTrim("LOEKZ") != null && itemNext.elementTextTrim("LOEKZ").trim().equals("L");
                                    Boolean isRETPO = itemNext.elementTextTrim("RETPO") != null && !itemNext.elementTextTrim("RETPO").trim().equals("");
                                    if (isLOEKZ || isRETPO) {
                                        continue;
                                    }
                                    char zero = 48;
                                    purchase.setProjectNo(trimStringWith(itemNext.elementTextTrim("EBELP"), zero));
                                    purchase.setMaterialNo(trimStringWith(itemNext.elementTextTrim("MATNR"), zero));
                                    if (purchase.getMaterialNo() == null || purchase.getMaterialNo() == "") {
                                        continue;
                                    }
                                    //上面获取所有列表转换成MAP 用KEY去查询返回在展示到列表上
                                    purchase.setMaterialCode(itemNext.elementTextTrim("MATKL"));
                                    String menge = itemNext.elementTextTrim("MENGE");
                                    if (menge != null && !"".equals(menge)) {
                                        purchase.setNumber((int) Float.parseFloat(menge));
                                    }
                                    //收货数量映射
                                    purchase.setPackagesNumber(itemNext.elementTextTrim("ZMENGE"));
                                    purchase.setUnit(itemNext.elementTextTrim("MEINS"));
                                    purchase.setLgort(itemNext.elementTextTrim("LGORT"));
                                    list.add(purchase);
                                }
                            }
                        }
                    }
                }
            }
        } catch (DocumentException | ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String trimStringWith(String str, char beTrim) {
        int st = 0;
        int len = str.length();
        char[] val = str.toCharArray();
        char sbeTrim = beTrim;
        while ((st < len) && (val[st] <= sbeTrim)) {
            st++;
        }
        return st > 0 ? str.substring(st, len) : str;
    }
}
