package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.RecordsOfPourOperations;

/**
 * (RecordsOfPourOperations)表服务接口
 *
 * @author makejava
 * @since 2023-05-12 15:53:49
 */
public interface RecordsOfPourOperationsService extends IService<RecordsOfPourOperations> {

    RecordsOfPourOperations getByPrechargeFurnaceId(Long prechargeFurnaceId);
}

