package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.storage.*;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ws.client.core.WebServiceTemplate;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 向ERP查询库存
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${interface.erp.storage}")
    private String URL;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    private final String packageName = "com.kld.mes.erp.entity.storage";

    @Override
    public List<Product> getStorage(@ApiParam(value = "物料号") @RequestBody String[] materialNos,
                                    @ApiParam(value = "erp代号") @RequestParam String erpCode) {

        try {
            //生成报文主体
            Zc80Mmif015 zc80Mmif015 = generateRequstBody(materialNos, erpCode);

            //获取调用服务接口类实例
            WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);

            //发起接口调用
            Zc80Mmif015Response zc80Mmif015Response = (Zc80Mmif015Response) webServiceTemplate
                    .marshalSendAndReceive(URL, zc80Mmif015);

            TableOfZc80Mmif015S2 tableOfZc80Mmif015S2 = zc80Mmif015Response.getTTable();
            List<Zc80Mmif015S2> list = tableOfZc80Mmif015S2.getItem();
            List<Product> products = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Product p = new Product();
                p.setMaterialNo(list.get(i).getMatnr());
                p.setQty(list.get(i).getLabst());
                p.setUnit(list.get(i).getMeins());
                p.setBranchCode(list.get(i).getWerks());
                products.add(p);
            }
            return products;
        } catch (Exception e) {
            return null;

        }
    }


    private Zc80Mmif015 generateRequstBody(@ApiParam(value = "物料号") @RequestBody String[] materialNos,
                                           @ApiParam(value = "erp代号") @RequestParam String erpCode) {

        Zc80Mmif015 zc80Mmif015 = new Zc80Mmif015();
        zc80Mmif015.setIWerks(erpCode);

        TableOfZc80Mmif015S1 t = new TableOfZc80Mmif015S1();

        List<Zc80Mmif015S1> list = new ArrayList<>();
        for (int i = 0; i < materialNos.length; i++) {
            Zc80Mmif015S1 zc80Mmif015S1 = new Zc80Mmif015S1();
            zc80Mmif015S1.setMatnr(materialNos[i]);
        }
        t.setItem(list);
        zc80Mmif015.setTMg(t);
        TableOfZc80Mmif015S2 tableOfZc80Mmif015S2 = new TableOfZc80Mmif015S2();
        zc80Mmif015.setTTable(tableOfZc80Mmif015S2);


        return zc80Mmif015;
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
