package com.kld.mes.erp.service;

import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 从ERP查询物料
 *
 * @Author: fengxy
 * @Date: 2022年9月13日11:13:19
 */
public interface MaterialService {

    public List<Product> getMaterial(@ApiParam(value = "日期") @RequestBody String dat,
                                     @ApiParam(value = "erp代号") @RequestParam String erpCode);

}
