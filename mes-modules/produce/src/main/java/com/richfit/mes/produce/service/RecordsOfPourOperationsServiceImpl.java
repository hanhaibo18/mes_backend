package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RecordsOfPourOperationsMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

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
    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private TrackAssignService trackAssignService;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private RecordsOfSteelmakingOperationsService recordsOfSteelmakingOperationsService;

    @Override
    public RecordsOfPourOperations getByPrechargeFurnaceId(Long prechargeFurnaceId) {
        if (prechargeFurnaceId == null) {
            throw new GlobalException("请检查传入的预装炉id！", ResultCode.FAILED);
        }
        QueryWrapper<RecordsOfPourOperations> queryWrapperPour = new QueryWrapper<>();
        queryWrapperPour.eq("precharge_furnace_id", prechargeFurnaceId);
        RecordsOfPourOperations recordsOfPourOperation = this.getOne(queryWrapperPour);
        //根据预装炉号找对应当前工序
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId).eq("is_current", 1);
        List<TrackItem> trackItemList = trackItemService.list(itemQueryWrapper);
        for (TrackItem trackItem : trackItemList) {
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            Router router = baseServiceClient.getRouter(trackHead.getRouterId()).getData();
            trackItem.setPourTemperature(router.getPourTemp());
            trackItem.setPourTime(router.getPourTime());
            trackItem.setWeightMolten(router.getWeightMolten());
            trackItem.setPieceWeight(router.getPieceWeight());
            trackItem.setTestBarTrackNo(trackHead.getTestBarTrackNo());
            trackItem.setTestBarType(trackHead.getTestBarType());
            trackItem.setProductName(trackHead.getProductName());
        }
        recordsOfPourOperation.setItemList(trackItemList);
        return recordsOfPourOperation;
    }

    @Override
    public Boolean init(Long prechargeFurnaceId, String recordNo) {
        QueryWrapper<RecordsOfPourOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_no", recordNo);
        if (!CollectionUtils.isEmpty(this.list(queryWrapper))) {
            throw new GlobalException("该作业记录编号已存在，请尝试重新初始化！", ResultCode.FAILED);
        }
        RecordsOfPourOperations recordsOfPourOperations = new RecordsOfPourOperations();
        recordsOfPourOperations.setPrechargeFurnaceId(prechargeFurnaceId);
        recordsOfPourOperations.setOperator(SecurityUtils.getCurrentUser().getUsername());
        recordsOfPourOperations.setOperatorTime(new Date());
        recordsOfPourOperations.setRecordNo(recordNo);
        //查询炼钢信息
        QueryWrapper<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfSteelmakingOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
        RecordsOfSteelmakingOperations steelmakingOperations = recordsOfSteelmakingOperationsService.getOne(recordsOfSteelmakingOperationsQueryWrapper);
        //查询预装炉信息
        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(prechargeFurnaceId);
        if (prechargeFurnace == null) {
            throw new GlobalException("没有找到预装炉信息！", ResultCode.FAILED);
        }
        recordsOfPourOperations.setTypeOfSteel(prechargeFurnace.getTypeOfSteel());
        recordsOfPourOperations.setFurnaceNo(steelmakingOperations.getFurnaceNo());
        return this.save(recordsOfPourOperations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RecordsOfPourOperations recordsOfPourOperations) {
        //先保存工序信息
        List<TrackItem> itemList = recordsOfPourOperations.getItemList();
        trackItemService.updateBatchById(itemList);
        return this.updateById(recordsOfPourOperations);
    }

    @Override
    public Boolean check(List<String> ids, int state) {
        Date date = new Date();
        String username = SecurityUtils.getCurrentUser().getUsername();
        QueryWrapper<RecordsOfPourOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<RecordsOfPourOperations> recordsOfPourOperations = this.list(queryWrapper);
        for (RecordsOfPourOperations recordsOfPourOperation : recordsOfPourOperations) {
            recordsOfPourOperation.setAssessor(username);
            recordsOfPourOperation.setAssessorTime(date);
            recordsOfPourOperation.setStatus(state);
        }
        return this.updateBatchById(recordsOfPourOperations);
    }
}

