package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.produce.dao.OrderMapper;
import com.richfit.mes.produce.entity.OrderDto;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/11 9:09
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements OrderService{

    final int ORDER_NEW  = 0;
    final int ORDER_START = 1;
    final int ORDER_CLOSE = 2;

    @Value("${synchronization.orders-synchronization}")
    private String url;

    @Autowired
    OrderMapper orderMapper;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Override
    public IPage<Order> queryPage(Page<Order> orderPage, OrderDto orderDto) {

        IPage<Order> planList =  orderMapper.queryOrder(orderPage,orderDto);

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();

        for(Order order :planList.getRecords()){
            findBranchName(order,branchList);
        }

        return planList;
    }



    @Override
    public void findBranchName(Order order) {
        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();
        findBranchName(order,branchList);
    }

    @Override
    public void setOrderStatusStart(String id) {
        setOrderStatus(id,ORDER_START);
    }

    @Override
    public void setOrderStatusNew(String id) {
        setOrderStatus(id,ORDER_NEW);
    }

    @Override
    public void setOrderStatusClose(String id) {
        setOrderStatus(id,ORDER_CLOSE);
    }

    @Override
    public Order findByOrderCode(String orderCode, String tenantId) {

        Page<Order> orderPage = new Page<>(1,10);

        OrderDto orderDto = new OrderDto();

        orderDto.setOrderSn(orderCode);
        orderDto.setTenantId(tenantId);

        IPage<Order> planList =  orderMapper.queryOrder(orderPage,orderDto);

        return planList.getRecords().size()>0?planList.getRecords().get(0):null;
    }


    private void setOrderStatus(String id ,int status){
        Order order = this.getById(id);
        order.setStatus(status);
        this.updateById(order);
    }

    private void findBranchName(Order order, List<Branch> branchList) {

        for(Branch b : branchList){
            if(b.getBranchCode().equals(order.getBranchCode())){
                order.setBranchName(b.getBranchName());
            }
            if(b.getBranchCode().equals(order.getInChargeOrg())){
                order.setInchargeOrgName(b.getBranchName());
            }
        }
    }


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


    private List<Order> xmlAnalysis(String xml,OrdersSynchronizationDto orderSynchronizationDto ){
        Document doc = null;
        Integer size = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Order> list = new ArrayList<>();
        char zero = 48;
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
            log.info(size.toString());
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
