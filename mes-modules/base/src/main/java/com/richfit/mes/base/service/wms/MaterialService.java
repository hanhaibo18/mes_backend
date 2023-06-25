package com.richfit.mes.base.service.wms;

import com.richfit.mes.common.model.base.Product;

import java.util.List;

/**
 * 功能描述:物料管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
public interface MaterialService {

    /**
     * 功能描述:同步选中物料数据物料到wms
     *
     * @param products 物料信息
     * @Author: zhiqiang.lu
     * @Date: 2023/06/15 10:51
     **/
    void sync(List<Product> products);

    /**
     * 功能描述:同步选中物料数据物料到wms
     *
     * @param products 物料信息
     * @param erpCode
     * @Author: zhiqiang.lu
     * @Date: 2023/06/15 10:51
     **/
    void sync(List<Product> products, String erpCode);
}
