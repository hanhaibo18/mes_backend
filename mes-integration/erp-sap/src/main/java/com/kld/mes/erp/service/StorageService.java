package com.kld.mes.erp.service;

import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 向ERP推送工艺
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
public interface StorageService {

    public List<Product> getStorage(@ApiParam(value = "物料号") @RequestBody String[] materialNos,
                                    @ApiParam(value = "erp代号") @RequestParam String erpCode);

}
