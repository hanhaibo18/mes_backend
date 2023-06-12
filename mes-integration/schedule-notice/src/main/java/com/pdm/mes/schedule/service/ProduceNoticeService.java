package com.pdm.mes.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pdm.mes.schedule.entity.domain.ProduceNotice;
import com.pdm.mes.schedule.entity.request.SaleProductionSchedulingRequest;

import java.util.List;


/**
* @author llh
* @description 针对表【produce_notice】的数据库操作Service
* @createDate 2023-06-12 14:01:16
*/
public interface ProduceNoticeService extends IService<ProduceNotice> {
    /**
     * 批量保存排产单
     * @param schedulingList
     * @return
     */
    boolean saveBatchNotice(List<SaleProductionSchedulingRequest> schedulingList);
}
