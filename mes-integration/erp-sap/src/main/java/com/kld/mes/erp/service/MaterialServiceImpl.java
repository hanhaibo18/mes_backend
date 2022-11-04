package com.kld.mes.erp.service;


import com.kld.mes.erp.entity.dto.MaterialTypeDto;
import com.kld.mes.erp.entity.material.WERKS;
import com.kld.mes.erp.entity.material.ZPPFM0004;
import com.kld.mes.erp.entity.material.ZPPFM0004Response;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
        log.debug("begin sync erp material,date [{}],erpCode [{}]", date, erpCode);
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
            log.debug("receive erp return materialDate,size [{}]", o.getTMARA().getItem().size());

            for (int i = 0; i < o.getTMARA().getItem().size(); i++) {
                Product p = new Product();
                String name = o.getTMARA().getItem().get(i).getMAKTX();

                /*
                 *   ERP返回的物料名称多为  图号+空格+物料名称+空格+D/JZ格式 或 物料名称+空格+D/JZ  或直接中文名称格式，
                 *   以下逻辑为对物料名称根据空格分割，然后封装名称，转换为系统内的物料类型编码，
                 *   \\S+ 表示空格的正则表达式。
                 *   如果返回物料描述不规范，则只能手动处理，代码无法处理全面形式
                 *
                 */

                if (isChinese(name.charAt(0))) {
                    p.setProductName(name);
                    p.setMaterialType("3");
                    p.setMaterialTypeName("成品");
                } else {
                    String[] data = name.split("\\s+");
                    
                    p.setProductName(data[1]);

                    if (data[data.length - 1].matches("[a-zA-Z]+") || "/".equals(data[data.length - 1])) {
                        MaterialTypeDto type = materialType().get(data[data.length - 1]);
                        if (type != null) {
                            p.setMaterialType(type.getNewCode());
                            p.setMaterialTypeName(type.getDesc());
                        }
                        //如果不是标准格式或者没有结尾字段  默认为成品
                    } else {
                        p.setMaterialType("3");
                        p.setMaterialTypeName("成品");
                    }

                    //描述结尾去掉D Z JZ /信息
                    if ("DZJZ/".contains(data[data.length - 1])) {
                        name = "";
                        for (int n = 0; n < data.length - 1; n++) {
                            name += " " + data[n];
                        }
                        name = name.replaceFirst(" ", "");
                    }
                }


                p.setMaterialDesc(name);
                p.setDrawingNo(o.getTMARA().getItem().get(i).getZEINR());
                p.setMaterialNo(trimStringWith(o.getTMARA().getItem().get(i).getMATNR(), zero));
                p.setBranchCode(o.getTMARA().getItem().get(i).getWERKS());
                //单位取错了
                p.setUnit(o.getTMARA().getItem().get(i).getZYL1());
                products.add(p);
            }
            return products;
        } catch (Exception e) {
            log.error("sync material error:[{}]", e);
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


    /**
     * 成品、半成品（半）、下料件（X），锻件（D）、铸件（Z）、精铸件（JZ）、模型件（MX）这七种物料类型
     *
     * @return
     */
    public static Map<String, MaterialTypeDto> materialType() {
        Map<String, MaterialTypeDto> map = new HashMap<>(4);
        map.put("D", new MaterialTypeDto("D", "0", "锻件"));
        map.put("Z", new MaterialTypeDto("Z", "1", "铸件"));
        map.put("JZ", new MaterialTypeDto("JZ", "2", "精铸件"));
        map.put("/", new MaterialTypeDto("/", "3", "成品"));
        map.put("X", new MaterialTypeDto("X", "4", "下料件"));
        map.put("MX", new MaterialTypeDto("MX", "5", "模型件"));
        map.put("半", new MaterialTypeDto("半", "6", "半成品"));
        return map;
    }

    private static boolean isChinese(char c) {
        // 根据字节码判断
        return c >= 0x4E00 && c <= 0x9FA5;
    }

}
