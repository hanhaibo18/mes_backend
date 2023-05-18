package com.kld.mes.erp.service;

import com.alibaba.fastjson.JSON;
import com.kld.mes.erp.entity.order.WERKS;
import com.kld.mes.erp.entity.order.ZC80PPIF009;
import com.kld.mes.erp.entity.order.ZC80PPIF009Response;
import com.kld.mes.erp.entity.order.ZPPS0008;
import com.kld.mes.erp.entity.order.creat.*;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: mes-backend
 * @description: ERP订单服务实现类
 * @author: 王瑞
 * @create: 2022-08-01 15:04
 **/
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${interface.erp.order-search}")
    private String URL_SEARCH;

    @Value("${interface.erp.order-creat}")
    private String URL_CREAT;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    private final String packageName = "com.kld.mes.erp.entity.order";

    @Override
    public List<Order> getErpCode(String erpCode, String selectDate, String controller, String orderNo) throws Exception {
        ZC80PPIF009 zc80PPIF009 = generateRequestBody(erpCode, selectDate);

        //获取调用服务接口类实例
        WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);
        List<ZPPS0008> list = new ArrayList<>();
        try {
            ZC80PPIF009Response o = (ZC80PPIF009Response) webServiceTemplate.marshalSendAndReceive(URL_SEARCH, zc80PPIF009);
            list = o.getTAUFK().getItem();
            System.out.println("-----------------");
            System.out.println(JSON.toJSONString(list));
        } catch (Exception e) {
            throw new GlobalException("ERP接口异常：" + URL_SEARCH, ResultCode.FAILED);
        }
        List<Order> orders = new ArrayList<>();
        char zero = 48;
        for (int i = 0; i < list.size(); i++) {
            Order p = new Order();
            p.setInChargeOrg(list.get(i).getWERKS());
            p.setMaterialCode(trimStringWith(list.get(i).getMATNR(), zero));
            p.setOrderSn(trimStringWith(list.get(i).getAUFNR(), zero));
            p.setOrderNum((int) Double.parseDouble(list.get(i).getGAMNG().trim()));
            p.setController(list.get(i).getDISPO());
            p.setStartTime(sdf.parse(list.get(i).getGSTRP()));
            p.setEndTime(sdf.parse(list.get(i).getGLTRP()));
            p.setMaterialDesc(list.get(i).getMAKTX());
            p.setCreateBy(list.get(i).getZYL4());
            orders.add(p);
        }
        return orders;
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


    private ZC80PPIF009 generateRequestBody(String erpCode, String selectDate) {
        WERKS werks = new WERKS();
        werks.setWERKS(erpCode);
        ZC80PPIF009 zc80PPIF009 = new ZC80PPIF009();
        zc80PPIF009.setZWERKS(werks);
        zc80PPIF009.setZDATUM(selectDate);
        return zc80PPIF009;
    }


    public List<Zc80Ppif032SO> creat(Zc80Ppif032 zc80Ppif032) throws Exception {
        //获取调用服务接口类实例
        WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate("com.kld.mes.erp.entity.order.creat");
        List<Zc80Ppif032SO> list = new ArrayList<>();
        try {
            Zc80Ppif032Response o = (Zc80Ppif032Response) webServiceTemplate.marshalSendAndReceive(URL_CREAT, zc80Ppif032);
            list = o.getTOut().getItem();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("ERP接口异常：" + URL_CREAT, ResultCode.FAILED);
        }
        return list;
    }
}
