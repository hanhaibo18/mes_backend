package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.RecordsOfPourOperations;
import com.richfit.mes.produce.dao.RecordsOfPourOperationsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (RecordsOfPourOperations)表服务实现类
 *
 * @author makejava
 * @since 2023-05-12 15:53:49
 */
@Service
public class RecordsOfPourOperationsServiceImpl extends ServiceImpl<RecordsOfPourOperationsMapper, RecordsOfPourOperations> implements RecordsOfPourOperationsService {

    @Autowired
    private TrackItemService trackItemService;

    @Override
    public RecordsOfPourOperations getByPrechargeFurnaceId(Long prechargeFurnaceId) {
        if (prechargeFurnaceId == null) {
            throw new GlobalException("请检查传入的预装炉id！", ResultCode.FAILED);
        }
        QueryWrapper<RecordsOfPourOperations> queryWrapperPour = new QueryWrapper<>();
        queryWrapperPour.eq("precharge_furnace_id", prechargeFurnaceId);
        RecordsOfPourOperations recordsOfPourOperation = this.getOne(queryWrapperPour);

        return null;
    }
}

