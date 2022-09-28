package com.kld.mes.erp.service;


import com.kld.mes.erp.entity.dto.MaterialTypeDto;
import com.kld.mes.erp.utils.ColumnsConfig;
import com.kld.mes.erp.utils.SqlServerConnect;
import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从ERP查询物料
 *
 * @Author: fengxy
 * @Date: 2022年9月9日16:28:32
 */
@Slf4j
@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private SqlServerConnect sqlServerConnect;

    @Override
    public List<Product> getMaterial(@ApiParam(value = "日期") @RequestBody String date,
                                     @ApiParam(value = "erp代号") @RequestParam String erpCode) {

        try {
            List<Product> products = new ArrayList<>();
            //查询SQL
            String sql="select "+ ColumnsConfig.materialSync+" from ERPINVMB where MODI_DATE  = '"+date+"'";
            //sqlserver结果返回
            List<Map<String, Object>> maps = sqlServerConnect.executeQuery(sql, ColumnsConfig.materialSync);

            maps.forEach(materialMap->{
                Product p = new Product();

                //物料类型（毛坯（锻件、铸件、精铸件...）、成品/半成品）、制造类型（自制件、外购件、外协件），同步物料若同步该信息需要用户提供规则。
                /*if (data[data.length - 1].matches("[a-zA-Z]+") || "/".equals(data[data.length - 1])) {
                    MaterialTypeDto type = materialType().get(data[data.length - 1]);
                    p.setMaterialType(type.getNewCode());
                    p.setMaterialTypeName(type.getDesc());
                }*/
                //公司
                String company = materialMap.get("COMPANY")!=null?materialMap.get("COMPANY").toString():"";
                //商品描述
                String mb009 = materialMap.get("MB009")!=null?materialMap.get("MB009").toString():"";
                //规格
                String mb003 = materialMap.get("MB003")!=null?materialMap.get("MB003").toString():"";
                //品号
                String mb001 = materialMap.get("MB001")!=null?materialMap.get("MB001").toString():"";
                //品名
                String mb002 = materialMap.get("MB002")!=null?materialMap.get("MB002").toString():"";
                //库存单位
                String mb004 = materialMap.get("MB004")!=null?materialMap.get("MB004").toString():"";

                p.setProductName(mb002);
                p.setMaterialDesc(mb003);
                p.setDrawingNo(mb001);
                p.setMaterialNo(mb001);
                p.setBranchCode(company);
                p.setUnit(mb004);
                products.add(p);
            });
            log.info("ERP物料查询成功------");
            return products;
        } catch (Exception e) {
            log.error("物料接口异常: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public static Map<String, MaterialTypeDto> materialType() {
        Map<String, MaterialTypeDto> map = new HashMap<>(4);
        map.put("Z", new MaterialTypeDto("Z", "0", "铸件"));
        map.put("D", new MaterialTypeDto("D", "1", "锻件"));
        map.put("JZ", new MaterialTypeDto("JZ", "2", "精铸件"));
        map.put("/", new MaterialTypeDto("/", "3", "成品/半成品"));
        return map;
    }

}
