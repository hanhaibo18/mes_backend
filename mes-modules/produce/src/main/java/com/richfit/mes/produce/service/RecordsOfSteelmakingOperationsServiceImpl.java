package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.RecordsOfSteelmakingOperations;
import com.richfit.mes.common.model.produce.ResultsOfSteelmaking;
import com.richfit.mes.produce.dao.RecordsOfSteelmakingOperationsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 炼钢作业记录表(RecordsOfSteelmakingOperations)表服务实现类
 *
 * @author makejava
 * @since 2023-05-12 15:54:38
 */
@Service
public class RecordsOfSteelmakingOperationsServiceImpl extends ServiceImpl<RecordsOfSteelmakingOperationsMapper, RecordsOfSteelmakingOperations> implements RecordsOfSteelmakingOperationsService {

    @Autowired
    private ResultsOfSteelmakingService resultsOfSteelmakingService;
    @Override
    public RecordsOfSteelmakingOperations getByPrechargeFurnaceId(Long prechargeFurnaceId) {
        if (prechargeFurnaceId == null){
            throw new GlobalException("请检查传入的预装炉id！", ResultCode.FAILED);
        }
        QueryWrapper<RecordsOfSteelmakingOperations> queryWrapperSteelmaking = new QueryWrapper<>();
        queryWrapperSteelmaking.eq("precharge_furnace_id",prechargeFurnaceId);
        RecordsOfSteelmakingOperations recordsOfSteelmakingOperation = this.getOne(queryWrapperSteelmaking);
        if (ObjectUtil.isEmpty(recordsOfSteelmakingOperation)){
            throw new GlobalException("没有查询到炼钢记录！",ResultCode.FAILED);
        }
        QueryWrapper<ResultsOfSteelmaking> queryWrapperResult = new QueryWrapper<>();
        queryWrapperResult.eq("steelmaking_id",recordsOfSteelmakingOperation.getId());
        List<ResultsOfSteelmaking> resultsOfSteelmakings = resultsOfSteelmakingService.list(queryWrapperResult);
        recordsOfSteelmakingOperation.setResultsOfSteelmaking(resultsOfSteelmakings);
        return recordsOfSteelmakingOperation;
    }
}

