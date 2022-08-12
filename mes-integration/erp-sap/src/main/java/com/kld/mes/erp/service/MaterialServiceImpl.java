package com.kld.mes.erp.service;


import com.kld.mes.erp.entity.dto.MaterialTypeDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            ZPPFM0004 ZPPFM0004 = new ZPPFM0004();
            WERKS w = new WERKS();
            w.setWERKS(erpCode);
            ZPPFM0004.setZWERKS(w);
            ZPPFM0004.setZDATUM(date);

            //获取调用服务接口类实例
            WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);

            //发起接口调用
            ZPPFM0004Response o = (ZPPFM0004Response) webServiceTemplate
                    .marshalSendAndReceive(URL, ZPPFM0004);

            List<Product> products = new ArrayList<>();
            char zero = 48;
            for (int i = 0; i < o.getTMARA().getItem().size(); i++) {
                Product p = new Product();
                String name = o.getTMARA().getItem().get(i).getMAKTX();
                String[] data = name.split("\\s+");
                if (data.length > 3) {
                    p.setProductName(data[1] + " " + data[2]);
                } else {
                    p.setProductName(data[1]);
                }
                if (data[data.length - 1].matches("[a-zA-Z]+") || "/".equals(data[data.length - 1])) {
                    MaterialTypeDto type = materialType().get(data[data.length - 1]);
                    p.setMaterialType(type.getNewCode());
                    p.setMaterialTypeName(type.getDesc());
                }
                p.setMaterialDesc(name);
                p.setDrawingNo(o.getTMARA().getItem().get(i).getZEINR());
                p.setMaterialNo(trimStringWith(o.getTMARA().getItem().get(i).getMATNR(), zero));
                p.setBranchCode(o.getTMARA().getItem().get(i).getWERKS());
                p.setUnit(o.getTMARA().getItem().get(i).getMEINS());
                products.add(p);
            }
            return products;
        } catch (Exception e) {
            return null;

        }
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


    public static Map<String, MaterialTypeDto> materialType() {
        Map<String, MaterialTypeDto> map = new HashMap<>(4);
        map.put("Z", new MaterialTypeDto("Z", "0", "铸件"));
        map.put("D", new MaterialTypeDto("D", "1", "锻件"));
        map.put("JZ", new MaterialTypeDto("JZ", "2", "精铸件"));
        map.put("/", new MaterialTypeDto("/", "3", "成品/半成品"));
        return map;
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
