package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.dao.OrderMapper;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import io.netty.util.internal.StringUtil;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName: OrderSyncServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年01月19日 16:18:00
 */
@Slf4j
@Service
public class OrderSyncServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderSyncService{

    @Resource
    private OrderSyncService orderSyncService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Value("${synchronization.orders-synchronization}")
    private String url;

    @Override
    public List<Order> queryOrderSynchronization(OrdersSynchronizationDto orderSynchronizationDto) {
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <urn:ZC80_PPIF009>" +
                "         <!--You may enter the following 3 items in any order-->" +
                "         <urn:ZDATUM>"+orderSynchronizationDto.getDate()+"</urn:ZDATUM>" +
                "         <urn:ZWERKS>" +
                "            <urn:WERKS>"+orderSynchronizationDto.getCode()+"</urn:WERKS>" +
                "         </urn:ZWERKS>" +
                "      </urn:ZC80_PPIF009>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
        //构造http请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("text/xml;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<>(soapRequestData, headers);
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();
        //返回结果
        String resultStr = restTemplate.postForObject(url, formEntity, String.class);
        //转换返回结果中的特殊字符，返回的结果中会将xml转义，此处需要反转移
        String tmpStr = StringEscapeUtils.unescapeXml(resultStr);
        //获取工厂ID
        return xmlAnalysis(tmpStr, orderSynchronizationDto);
    }

    /**
     * 功能描述: 保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022年1月18日14:19:44
     * @param orderList
     * @return: CommonResult<Boolean>
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveOrderSync(List<Order> orderList) {
        //TODO:没有物料编号 不同步  status状态=0
        for (Order order : orderList) {
            if (order.getMaterialCode() == null){
                continue;
            }
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            if (order.getOrderSn() != null) {
                queryWrapper.eq("order_sn",order.getOrderSn());
            }
            order.setStatus(0);
            orderSyncService.remove(queryWrapper);
            orderSyncService.save(order);
        }
        return CommonResult.success(true,"操作成功!");
    }

    /**
     * 功能描述: 定时保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022年1月18日14:19:44
     * @return: CommonResult<Boolean>
     **/
    @Override
//    @Scheduled(cron = "0 30 23 * * ? ")
    //"*/10 * * * * ? "
//    @Scheduled(cron = "${time.order}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingOrderSync() {
        //拿到今天的同步数据
        OrdersSynchronizationDto ordersSynchronization = new OrdersSynchronizationDto();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        ordersSynchronization.setDate(format.format(date));
        //获取工厂列表
        Boolean saveData = false;
        try {
            CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
            for (ItemParam itemParam : listCommonResult.getData()){
                ordersSynchronization.setCode(itemParam.getCode());
                List<Order> orderList = orderSyncService.queryOrderSynchronization(ordersSynchronization);
                for (Order order : orderList){
                    order.setBranchCode(itemParam.getLabel());
                    order.setTenantId(itemParam.getTenantId());
                    order.setCreateBy("system");
                    order.setModifyBy("system");
                    order.setCreateTime(date);
                    order.setModifyTime(date);
                    order.setStatus(0);
                    if (order.getMaterialCode() == null) {
                        continue;
                    }
                    QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_sn", order.getOrderSn());
                    orderSyncService.remove(queryWrapper);
                    saveData = orderSyncService.save(order);
                }
            }
        }catch (Exception e) {
            saveData = false;
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return CommonResult.success(saveData);
    }


    private List<Order> xmlAnalysis(String xml,OrdersSynchronizationDto orderSynchronizationDto ){
        Document doc = null;
        int size = 0;
        List<Order> list = new ArrayList<>();
        char zero = 48;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            log.info(rootElt.getName());
            Iterator<Element> body = rootElt.elementIterator("Body");
            while (body.hasNext()) {
                Element bodyNext =  body.next();
                Iterator<Element> response = bodyNext.elementIterator("ZC80_PPIF009.Response");
                while (response.hasNext()) {
                    Element responseNext = response.next();
                    Iterator<Element> tAUFK = responseNext.elementIterator("T_AUFK");
                    while (tAUFK.hasNext()){
                        Element tAUFKNext = tAUFK.next();
                        Iterator<Element> item = tAUFKNext.elementIterator("item");
                        while (item.hasNext()) {
                            Element itemNext = item.next();
                            Order order = new Order();
                            order.setOrderSn(trimStringWith(itemNext.elementTextTrim("AUFNR"),zero));
                            order.setMaterialCode(trimStringWith(itemNext.elementTextTrim("MATNR"),zero));
                            order.setMaterialDesc(itemNext.elementTextTrim("MAKTX"));
                            //TODO: 从xml获取的参数还需再去查询在存储
                            order.setBranchCode(itemNext.elementTextTrim("WERKS"));
                            order.setOrderNum((int) Float.parseFloat(itemNext.elementTextTrim("GAMNG").trim()));
                            order.setStartTime(format.parse(itemNext.elementTextTrim("GSTRP")));
                            order.setEndTime(format.parse(itemNext.elementTextTrim("GLTRP")));
                            order.setInChargeOrg(itemNext.elementTextTrim("DISPO"));
                            boolean orderJudge = StringUtil.isNullOrEmpty(orderSynchronizationDto.getOrderSn());
                            boolean inChargeOrgJudge = StringUtil.isNullOrEmpty(orderSynchronizationDto.getInChargeOrg());
                            if ( !orderJudge || !inChargeOrgJudge) {
                                boolean orderSnData = !orderJudge && orderSynchronizationDto.getOrderSn().equals(order.getOrderSn());
                                boolean inChargeOrgData = !inChargeOrgJudge && orderSynchronizationDto.getInChargeOrg().equals(order.getInChargeOrg());
                                if (orderSnData && inChargeOrgData){
                                    list.add(order);
                                    continue;
                                }
                                if (orderSnData && inChargeOrgJudge){
                                    list.add(order);
                                    continue;
                                }
                                if (inChargeOrgData && orderJudge){
                                    list.add(order);
                                }
                            }else {
                                list.add(order);
                            }
                        }
                    }
                }
            }
            log.info(Integer.toString(size));
        } catch (DocumentException | ParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        log.info("orderServiceImplEnd");
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
        return st > 0 ? str.substring(st, len) : str;
    }
}
