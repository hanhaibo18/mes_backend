package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.PrechargeFurnaceAssign;
import com.richfit.mes.common.model.produce.RecordsOfSteelmakingOperations;
import com.richfit.mes.common.model.produce.ResultsOfSteelmaking;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RecordsOfSteelmakingOperationsMapper;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;

    @Override
    public RecordsOfSteelmakingOperations getByPrechargeFurnaceId(Long prechargeFurnaceId) {
        if (prechargeFurnaceId == null) {
            throw new GlobalException("请检查传入的预装炉id！", ResultCode.FAILED);
        }
        QueryWrapper<RecordsOfSteelmakingOperations> queryWrapperSteelmaking = new QueryWrapper<>();
        queryWrapperSteelmaking.eq("precharge_furnace_id", prechargeFurnaceId);
        RecordsOfSteelmakingOperations recordsOfSteelmakingOperation = this.getOne(queryWrapperSteelmaking);
        if (ObjectUtil.isEmpty(recordsOfSteelmakingOperation)) {
            throw new GlobalException("没有查询到炼钢记录！", ResultCode.FAILED);
        }
        QueryWrapper<ResultsOfSteelmaking> queryWrapperResult = new QueryWrapper<>();
        queryWrapperResult.eq("steelmaking_id", recordsOfSteelmakingOperation.getId());
        List<ResultsOfSteelmaking> resultsOfSteelmakings = resultsOfSteelmakingService.list(queryWrapperResult);
        recordsOfSteelmakingOperation.setResultsOfSteelmaking(resultsOfSteelmakings);
        return recordsOfSteelmakingOperation;
    }

    @Override
    public  Boolean init(Long prechargeFurnaceId, String recordNo) {
        QueryWrapper<RecordsOfSteelmakingOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_no", recordNo);
        if (!CollectionUtils.isEmpty(this.list(queryWrapper))) {
            throw new GlobalException("该作业记录编号已存在，请尝试重新初始化！", ResultCode.FAILED);
        }
        RecordsOfSteelmakingOperations recordsOfSteelmakingOperations = new RecordsOfSteelmakingOperations();
        recordsOfSteelmakingOperations.setPrechargeFurnaceId(prechargeFurnaceId);
        recordsOfSteelmakingOperations.setOperator(SecurityUtils.getCurrentUser().getUsername());
        recordsOfSteelmakingOperations.setOperatorTime(new Date());
        recordsOfSteelmakingOperations.setRecordNo(recordNo);
        //查询预装炉信息
        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(prechargeFurnaceId);
        if (prechargeFurnace == null) {
            throw new GlobalException("没有找到预装炉信息！", ResultCode.FAILED);
        }
        recordsOfSteelmakingOperations.setTypeOfSteel(prechargeFurnace.getTypeOfSteel());
        recordsOfSteelmakingOperations.setSmeltingEquipment(prechargeFurnace.getSmeltingEquipment());
        return this.save(recordsOfSteelmakingOperations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RecordsOfSteelmakingOperations recordsOfSteelmakingOperations) {
        //先保存分析结果信息
        QueryWrapper<ResultsOfSteelmaking> resultsOfSteelmakingQueryWrapper = new QueryWrapper<>();
        resultsOfSteelmakingQueryWrapper.eq("steelmaking_id", recordsOfSteelmakingOperations.getId());
        resultsOfSteelmakingService.remove(resultsOfSteelmakingQueryWrapper);
        resultsOfSteelmakingService.saveBatch(recordsOfSteelmakingOperations.getResultsOfSteelmaking());
        return this.updateById(recordsOfSteelmakingOperations);
    }

    @Override
    public Boolean check(List<String> ids, int state) {
        Date date = new Date();
        String username = SecurityUtils.getCurrentUser().getUsername();
        QueryWrapper<RecordsOfSteelmakingOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperations = this.list(queryWrapper);
        for (RecordsOfSteelmakingOperations recordsOfSteelmakingOperation : recordsOfSteelmakingOperations) {
            recordsOfSteelmakingOperation.setAssessor(username);
            recordsOfSteelmakingOperation.setAssessorTime(date);
        }
        return this.updateBatchById(recordsOfSteelmakingOperations);
    }

    @Override
    public IPage<RecordsOfSteelmakingOperations> bzzcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit) {
        //班组长查询同班员工号
        List<TenantUser> tenantUserList = systemServiceClient.queryClass(SecurityUtils.getCurrentUser().getUsername());
        List<String> userIdList = tenantUserList.stream().map(TenantUser::getUserAccount).collect(Collectors.toList());
        //根据员工号查询派炉信息
        QueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceAssignQueryWrapper.in("user_id", userIdList);
        List<PrechargeFurnaceAssign> prechargeFurnaceAssignList = prechargeFurnaceAssignService.list(prechargeFurnaceAssignQueryWrapper);
        if (CollectionUtils.isEmpty(prechargeFurnaceAssignList)) {
            return null;
        }
        //获取派送预装炉id
        Set<Long> prechargeFurnaceIdSet = prechargeFurnaceAssignList.stream().map(PrechargeFurnaceAssign::getPrechargeFurnaceId).collect(Collectors.toSet());
        //根据预装炉id获取炼钢记录
        QueryWrapper<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfSteelmakingOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
        if (!StringUtils.isNullOrEmpty(recordNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("record_no", recordNo);
        }
        if (prechargeFurnaceId != null) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("type_of_steel", typeOfSteel);
        }
        if (!StringUtils.isNullOrEmpty(smeltingEquipment)) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("smelting_equipment", smeltingEquipment);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            recordsOfSteelmakingOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            recordsOfSteelmakingOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
        }
        if (status != null) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("status", status);
        }

        return this.page(new Page<>(page, limit), recordsOfSteelmakingOperationsQueryWrapper);
    }

    @Override
    public IPage<RecordsOfSteelmakingOperations> czgcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit) {
        //根据员工号查询派炉信息
        QueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceAssignQueryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        List<PrechargeFurnaceAssign> prechargeFurnaceAssignList = prechargeFurnaceAssignService.list(prechargeFurnaceAssignQueryWrapper);
        if (CollectionUtils.isEmpty(prechargeFurnaceAssignList)) {
            return null;
        }
        //获取派送预装炉id
        Set<Long> prechargeFurnaceIdSet = prechargeFurnaceAssignList.stream().map(PrechargeFurnaceAssign::getPrechargeFurnaceId).collect(Collectors.toSet());
        //根据预装炉id获取炼钢记录
        QueryWrapper<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfSteelmakingOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
        if (!StringUtils.isNullOrEmpty(recordNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("record_no", recordNo);
        }
        if (prechargeFurnaceId != null) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("type_of_steel", typeOfSteel);
        }
        if (!StringUtils.isNullOrEmpty(smeltingEquipment)) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("smelting_equipment", smeltingEquipment);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            recordsOfSteelmakingOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            recordsOfSteelmakingOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
        }
        if (status != null) {
            recordsOfSteelmakingOperationsQueryWrapper.eq("status", status);
        }

        return this.page(new Page<>(page, limit), recordsOfSteelmakingOperationsQueryWrapper);
    }
}

