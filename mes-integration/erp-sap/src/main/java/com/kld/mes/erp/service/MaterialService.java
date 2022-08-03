package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.material.ZPPS0007;
import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 从ERP查询物料
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
public interface MaterialService {

    public List<Product> getMaterial(@ApiParam(value = "日期") @RequestBody String dat,
                                     @ApiParam(value = "erp代号") @RequestParam String erpCode);

}
