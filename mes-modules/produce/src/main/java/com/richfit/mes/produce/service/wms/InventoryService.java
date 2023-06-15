package com.richfit.mes.produce.service.wms;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackFlow;

import java.util.List;

/**
 * 功能描述:库存管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
public interface InventoryService extends IService<Certificate> {
    /**
     * 功能描述:上交库存
     *
     * @param certificate 合格证信息
     * @return 通用返回信息
     * @Author: zhiqiang.lu
     * @Date: 2023/05/26 16:27
     **/
    void handOver(List<Certificate> certificate);
}
