package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024;
import com.kld.mes.erp.entity.order.WERKS;
import com.kld.mes.erp.entity.order.ZC80PPIF009;
import com.kld.mes.erp.entity.order.ZC80PPIF009Response;
import com.kld.mes.erp.entity.order.ZPPS0008;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.text.SimpleDateFormat;
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
    private String URL;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    private final String packageName = "com.kld.mes.erp.entity.order";

    @Override
    public List<Order> getErpCode(String erpCode, String selectDate, String controller, String orderNo) throws Exception {
        ZC80PPIF009 zc80PPIF009 = generateRequestBody(erpCode, selectDate);

        //获取调用服务接口类实例
        WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);
        ZC80PPIF009Response o = (ZC80PPIF009Response) webServiceTemplate.marshalSendAndReceive(URL, zc80PPIF009);
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < o.getTAUFK().getItem().size(); i++) {
            Order p = new Order();
            p.setMaterialDesc(o.getTAUFK().getItem().get(i).getMAKTX());
            p.setMaterialCode(o.getTAUFK().getItem().get(i).getMATNR());
            p.setBranchCode(o.getTAUFK().getItem().get(i).getWERKS());
            p.setOrderSn(o.getTAUFK().getItem().get(i).getAUFNR());
            p.setOrderNum((int) Double.parseDouble(o.getTAUFK().getItem().get(i).getGAMNG().trim()));
            p.setController(o.getTAUFK().getItem().get(i).getDISPO());
            p.setStartTime(sdf.parse(o.getTAUFK().getItem().get(i).getGSTRP()));
            p.setEndTime(sdf.parse(o.getTAUFK().getItem().get(i).getGLTRP()));
            p.setMaterialDesc(o.getTAUFK().getItem().get(i).getMAKTX());
            orders.add(p);
        }
        return orders;
    }


    private ZC80PPIF009 generateRequestBody(String erpCode, String selectDate) {
        WERKS werks = new WERKS();
        werks.setWERKS(erpCode);
        ZC80PPIF009 zc80PPIF009 = new ZC80PPIF009();
        zc80PPIF009.setZWERKS(werks);
        zc80PPIF009.setZDATUM(selectDate);
        return zc80PPIF009;
    }
}
