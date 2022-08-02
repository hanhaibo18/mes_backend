package com.kld.mes.erp.service;


import com.kld.mes.erp.utils.WsTemplateFactory;
import com.kld.mes.erp.entity.material.*;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 从ERP查询物料
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
@Slf4j
@Service
public class MaterialServiceImpl implements MaterialService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${interface.erp.material}")
    private String URL;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    private final String packageName = "com.kld.mes.erp.entity.material";

    @Override
    public List<Product> getMaterial(@ApiParam(value = "日期") @RequestBody String date,
                                     @ApiParam(value = "erp代号") @RequestParam String erpCode) {

        try {
            //生成报文主体
            Z_PPFM0004 z_PPFM0004 = new Z_PPFM0004();
            WERKS w = new WERKS();
            w.setWERKS("X092");

            z_PPFM0004.setZWERKS(w);
            z_PPFM0004.setZDATUM("2022-07-10");

            //获取调用服务接口类实例
            WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);

            //发起接口调用
            Object o = webServiceTemplate
                    .marshalSendAndReceive(URL, z_PPFM0004);

            List<Product> products = new ArrayList<>();

            return products;
        } catch (Exception e) {
            return null;

        }
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
