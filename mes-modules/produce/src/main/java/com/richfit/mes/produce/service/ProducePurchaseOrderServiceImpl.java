package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.produce.dao.ProducePurchaseOrderMapper;
import com.richfit.mes.produce.entity.PurchaseOrderSynchronizationDto;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName: ProducePurchaseOrderServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年01月10日 14:25:00
 */
@Service
@Slf4j
public class ProducePurchaseOrderServiceImpl extends ServiceImpl<ProducePurchaseOrderMapper,ProducePurchaseOrder> implements ProducePurchaseOrderService{

    @Value("${synchronization.purchase-order-synchronization}")
    private String url;

    @Override
    public List<ProducePurchaseOrder> queryPurchaseSynchronization(PurchaseOrderSynchronizationDto purchaseOrderSynchronizationDto) {
        //时间参数
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:Z_MMFM0003>" +
                //拼接查询开始时间
                "<urn:I_DATE_CHANGE>" +
                purchaseOrderSynchronizationDto.getStartTime() +
//                .append("2021-10-01")
                "</urn:I_DATE_CHANGE>" +
                //拼接查询结束时间
                "<urn:I_DATE_UNTIL>" +
//                .append("2021-10-07")
                purchaseOrderSynchronizationDto.getEndTime() +
                "</urn:I_DATE_UNTIL>" +
                //固定时间参数
                "<urn:I_TIME_CHANGE>00:00:00</urn:I_TIME_CHANGE>" +
                "<urn:I_TIME_UNTIL>23:59:59</urn:I_TIME_UNTIL>" +
                "</urn:Z_MMFM0003>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        //构造http请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("text/xml;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<>(soapRequestData, headers);
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.basicAuthentication("zbzz_esb", "ZBZZOSBinterface1").build();
        //返回结果
        String resultStr = restTemplate.postForObject(url, formEntity, String.class);
        //转换返回结果中的特殊字符，返回的结果中会将xml转义，此处需要反转移
        String tmpStr = StringEscapeUtils.unescapeXml(resultStr);
        //获取工厂ID
        return xmlAnalysis(tmpStr, purchaseOrderSynchronizationDto.getCode());
    }

    /**
     * 功能描述: 字符串解析
     * @Author: xinYu.hou
     * @Date: 2022/1/13 13:57
     * @param xml
     * @param factoryId
     * @return: List<ProducePurchaseSynchronization>
     **/
    private List<ProducePurchaseOrder> xmlAnalysis(String xml,String factoryId){
        Document doc = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<ProducePurchaseOrder> list = new ArrayList<>();
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            log.info(rootElt.getName());
            Iterator<Element> body = rootElt.elementIterator("Body");
            while (body.hasNext()) {
                Element bodyNext =  body.next();
                Iterator<Element> response = bodyNext.elementIterator("Z_MMFM0003.Response");
                while (response.hasNext()) {
                    Element responseNext = response.next();
                    Iterator<Element> tEKPO = responseNext.elementIterator("T_EKPO");
                    Iterator<Element> tEKKO = responseNext.elementIterator("T_EKKO");
                    while (tEKKO.hasNext()) {
                        Element tEKKONext = tEKKO.next();
                        Iterator<Element> itemTEKKON = tEKKONext.elementIterator("item");
                        while (itemTEKKON.hasNext()) {
                            Element itemTEKKONNext = itemTEKKON.next();
                            String BUKRS = itemTEKKONNext.elementTextTrim("BUKRS");
                            String EKORG = itemTEKKONNext.elementTextTrim("EKORG");
                            String FRGKE = itemTEKKONNext.elementTextTrim("FRGKE");
                            Boolean is_BUKRS = itemTEKKONNext.elementTextTrim("BUKRS") !=null && "K923".equals(BUKRS);
                            Boolean is_EKORG = itemTEKKONNext.elementTextTrim("EKORG") != null && ("X092".equals(EKORG) || "X070".equals(EKORG));
                            Boolean is_FRGKE = itemTEKKONNext.elementTextTrim("FRGKE") != null && "T".equals(FRGKE);
                            if (is_BUKRS && is_EKORG && is_FRGKE){
                                String orderNo = itemTEKKONNext.elementTextTrim("EBELN");
                                String orderType = itemTEKKONNext.elementTextTrim("BSART");
                                //先用String接收数据 以后再处理
                                Date purchaseDate = format.parse(itemTEKKONNext.elementTextTrim("BEDAT"));
                                String lifnr = itemTEKKONNext.elementTextTrim("LIFNR");
                                while (tEKPO.hasNext()) {
                                    Element tEKPONext = tEKPO.next();
                                    Iterator<Element> item = tEKPONext.elementIterator("item");
                                    while (item.hasNext()) {
                                        Element itemNext = item.next();
                                        if(itemNext.elementTextTrim("EBELN").equals(itemTEKKONNext.elementTextTrim("EBELN"))
                                                && itemNext.elementTextTrim("WERKS").equals(factoryId)){
                                            ProducePurchaseOrder purchase = new ProducePurchaseOrder();
                                            purchase.setOrderNo(orderNo);
                                            purchase.setOrderType(orderType);
                                            purchase.setPurchaseDate(purchaseDate);
                                            purchase.setLifnr(lifnr);
                                            Boolean isLOEKZ = itemNext.elementTextTrim("LOEKZ") != null && itemNext.elementTextTrim("LOEKZ").trim().equals("L");
                                            Boolean isRETPO = itemNext.elementTextTrim("RETPO") != null && !itemNext.elementTextTrim("RETPO").trim().equals("");
                                            if (isLOEKZ || isRETPO){
                                                continue;
                                            }
                                            char zero = 48;
                                            purchase.setProjectNo(trimStringWith(itemNext.elementTextTrim("EBELP"), zero));
                                            purchase.setMaterialNo(trimStringWith(itemNext.elementTextTrim("MATNR"), zero));
                                            if(purchase.getMaterialNo() == null || purchase.getMaterialNo() == ""){
                                                continue;
                                            }
                                            purchase.setWerks(itemNext.elementTextTrim("WERKS"));
                                            //TODO: 从xml获取的参数还需再去查询在存储
                                            purchase.setBranchCode(itemNext.elementTextTrim("WERKS"));
                                            purchase.setMaterialCode(itemNext.elementTextTrim("MATKL"));
                                            String menge = itemNext.elementTextTrim("MENGE");
                                            if (menge != null && menge != ""){
                                                purchase.setNumber((int) Float.parseFloat(menge));
                                            }
                                            purchase.setUnit(itemNext.elementTextTrim("MEINS"));
                                            //TODO 后续完善
//                                            DataTable dt = bll.GetDescByUnit(purchase.Unit).Tables[0];
//                                            if(dt.Rows.Count > 0)
//                                            {
//                                                purchase.Desc = dt.Rows[0]["Desc"].ToString();
//                                            }
                                            purchase.setLgort(itemNext.elementTextTrim("LGORT"));
                                            //TODO 后续完善
//                                            SaintSoft.ProdMaterial.Model.C_Production production = bll.GetModel(purchase.Material_No);
//                                            if (production != null)
//                                            {
//                                                purchase.Material_Remark = production.Prod_Desc;
//                                                purchase.Drawing_No = production.Drawing_No;
//                                            }
                                            boolean isHave = false;
                                            for (ProducePurchaseOrder purchaseSynchronization : list) {
                                                if (purchaseSynchronization.getOrderNo().equals(purchase.getOrderNo())
                                                        && purchaseSynchronization.getMaterialNo().equals(purchase.getMaterialNo())){
                                                    log.error(purchase.getOrderNo()+","+purchase.getMaterialNo());
                                                    isHave = true;
                                                    break;
                                                }
                                            }
                                            if (!isHave){
                                                list.add(purchase);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        log.info("serviceImplEnd");
        return list;
    }

    /**
     * 功能描述:字符串截取
     * @Author: xinYu.hou
     * @Date: 2022/1/13 9:31
     * @param str
     * @param beTrim
     * @return: String
     **/
    private String trimStringWith(String str, char beTrim) {
        int st = 0;
        int len = str.length();
        char[] val = str.toCharArray();
        char sbeTrim = beTrim;
        while ((st < len) && (val[st] <= sbeTrim)) {
            st++;
        }
//        while ((st < len) && (val[len - 1] <= sbeTrim)) {
//            len--;
//        }
        return st > 0 ? str.substring(st, len) : str;
    }
}
