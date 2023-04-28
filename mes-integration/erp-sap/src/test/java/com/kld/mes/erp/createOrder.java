package com.kld.mes.erp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kld.mes.erp.entity.order.creat.Zc80Ppif032;
import com.kld.mes.erp.entity.order.creat.Zc80Ppif032SI;
import com.kld.mes.erp.entity.order.creat.Zc80Ppif032SO;
import com.kld.mes.erp.entity.order.creat.Zc80Ppif032TI;
import com.kld.mes.erp.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Test
    public void createOrder(){
        try {
            XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            calendar.setTime(13,38,00);
            Zc80Ppif032 zc80Ppif032 = new Zc80Ppif032();
            Zc80Ppif032TI zc80Ppif032TI = new Zc80Ppif032TI();
            List<Zc80Ppif032SI> zc80Ppif032SOList = new ArrayList<>();
            Zc80Ppif032SI zc80Ppif032SI = new Zc80Ppif032SI();
            zc80Ppif032SI.setDocId("4000001");
            zc80Ppif032SI.setMaterial("909098368");
            zc80Ppif032SI.setPlant("X092");
            zc80Ppif032SI.setOrderType("zp01");
//            zc80Ppif032SI.setBasicStartDate("2023-04-19");
//            zc80Ppif032SI.setBasicEndDate("2023-04-22");
//            zc80Ppif032SI.setBasicStartTime(calendar);
//            zc80Ppif032SI.setBasicEndTime(calendar);
            zc80Ppif032SI.setQuantity(new BigDecimal(10));
            zc80Ppif032SI.setQuantityUom("台");
//            zc80Ppif032SI.setUnloadingPoint("测试地址");
//            zc80Ppif032SI.setZfield1("");
//            zc80Ppif032SI.setZfield2("");
//            zc80Ppif032SI.setZfield3("");
            zc80Ppif032SOList.add(zc80Ppif032SI);
            zc80Ppif032TI.setItem(zc80Ppif032SOList);
            zc80Ppif032.setTIn(zc80Ppif032TI);
            List<Zc80Ppif032SO> creat = orderService.creat(zc80Ppif032);
            System.out.println(JSON.toJSONString(creat));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
