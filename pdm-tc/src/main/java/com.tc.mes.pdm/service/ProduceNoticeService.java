package com.tc.mes.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tc.mes.pdm.entity.SaleProductionSchedulingDto;
import com.tc.mes.pdm.entity.domain.ProduceNotice;

import java.util.List;


public interface ProduceNoticeService extends IService<ProduceNotice> {

    boolean saveBatchNotice(List<SaleProductionSchedulingDto> schedulingDtoList);
}
