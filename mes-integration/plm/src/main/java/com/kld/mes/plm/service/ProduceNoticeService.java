package com.kld.mes.plm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kld.mes.plm.entity.domain.ProduceNotice;
import com.kld.mes.plm.entity.request.SaleProductionSchedulingRequest;


import java.util.List;


public interface ProduceNoticeService extends IService<ProduceNotice> {

    /**
     * 批量保存排产单
     * @param schedulingList
     * @return
     */
    boolean saveBatchNotice(List<SaleProductionSchedulingRequest> schedulingList);
}
