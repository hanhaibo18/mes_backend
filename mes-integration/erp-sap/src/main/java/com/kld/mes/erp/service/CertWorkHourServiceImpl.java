package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024Response;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024S1;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024T1;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.model.produce.TrackItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/21 14:59
 */
@Slf4j
@Service
public class CertWorkHourServiceImpl implements CertWorkHourService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${interface.erp.work-hour-sync}")
    private String URL;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    private final String packageName = "com.kld.mes.erp.entity.certWorkHour";

    @Override
    public boolean sendWorkHour(List<TrackItem> trackItemList, String erpCode, String orderNo, int qty, String unit) {

        Zc80Ppif024 zc80Ppif024 = generateRequstBody(trackItemList, erpCode, orderNo, qty, unit);

        WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);

        Zc80Ppif024Response zc80Ppif024Response = (Zc80Ppif024Response) webServiceTemplate
                .marshalSendAndReceive(URL, zc80Ppif024);

        return zc80Ppif024Response.getEMes().contains("完成");
    }


    private Zc80Ppif024 generateRequstBody(List<TrackItem> trackItemList, String erpCode, String orderNo, int qty, String unit) {

        List<Zc80Ppif024S1> zc80Ppif024S1List = new ArrayList<>();
        for (int i = 0; i < trackItemList.size(); i++) {
            Zc80Ppif024S1 zc80Ppif024S1 = getZc80Ppif024S1(trackItemList.get(i), erpCode, orderNo, qty, unit, i);

            zc80Ppif024S1List.add(zc80Ppif024S1);

        }

        Zc80Ppif024 zc80Ppif024 = new Zc80Ppif024();
        Zc80Ppif024T1 zc80Ppif024T1 = new Zc80Ppif024T1();

        zc80Ppif024T1.setItem(zc80Ppif024S1List);
        zc80Ppif024.setIInput(zc80Ppif024T1);

        return zc80Ppif024;
    }

    private Zc80Ppif024S1 getZc80Ppif024S1(TrackItem trackItem, String erpCode, String orderNo, int qty, String unit, int i) {
        Zc80Ppif024S1 zc80Ppif024S1 = new Zc80Ppif024S1();

        zc80Ppif024S1.setWerks(erpCode);
        zc80Ppif024S1.setAufnr(orderNo);
        zc80Ppif024S1.setVornr(getVornr(trackItem, i));
        zc80Ppif024S1.setLmnga(new BigDecimal(qty));
        zc80Ppif024S1.setMeinh(unit);
        zc80Ppif024S1.setIle01("MIN");

        zc80Ppif024S1.setIsm01(new BigDecimal(getHourToMinutes(trackItem)));
        zc80Ppif024S1.setFinConf("P");
        zc80Ppif024S1.setBudat(sdf.format(new Date()));
        zc80Ppif024S1.setZflag("Y");
        return zc80Ppif024S1;
    }

    /**
     * 生成4位的工序号
     *
     * @param item
     * @param i
     * @return
     */
    private String getVornr(TrackItem item, int i) {

        Assert.notNull(item, "item 不应该为null");

        String s = "";

        int optSequence = item.getOptSequence();

        if (optSequence > 0) {
            s = "000" + optSequence;
            s = s.substring(s.length() - 4, 4);
        } else {
            if (i < 9) {
                s = "00" + (i + 1) + "0";
            } else {
                s = "0" + (i + 1) + "0";
            }
        }

        return s;
    }

    /**
     * 返回填报工时转换后的分钟
     *
     * @param item
     * @return
     */
    private Double getHourToMinutes(TrackItem item) {
        Double singlePieceHours = item.getSinglePieceHours() == null ? 0.0 : item.getSinglePieceHours();
        Double prepareEndHours = item.getPrepareEndHours() == null ? 0.0 : item.getPrepareEndHours();

        return (singlePieceHours + prepareEndHours) * 60;
    }


}
