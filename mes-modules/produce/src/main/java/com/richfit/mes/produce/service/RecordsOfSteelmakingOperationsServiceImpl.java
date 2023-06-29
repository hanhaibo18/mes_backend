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
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RecordsOfSteelmakingOperationsMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.richfit.mes.produce.utils.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private PrechargeFurnaceAssignPersonService prechargeFurnaceAssignPersonService;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private RecordsOfPourOperationsService recordsOfPourOperationsService;
    @Autowired
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;

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
        queryWrapperResult.eq("steelmaking_id", recordsOfSteelmakingOperation.getId())
                .orderByAsc("remark");
        List<ResultsOfSteelmaking> resultsOfSteelmakings = resultsOfSteelmakingService.list(queryWrapperResult);
        recordsOfSteelmakingOperation.setResultsOfSteelmaking(resultsOfSteelmakings);
        return recordsOfSteelmakingOperation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean init(Long prechargeFurnaceId, String branchCode) {
        String recordNo = null;
        try {
            recordNo = Code.valueOnUpdate("steelmaking_no", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        Branch branch = baseServiceClient.selectBranchByCodeAndTenantId(SecurityUtils.getCurrentUser().getBelongOrgId(), SecurityUtils.getCurrentUser().getTenantId()).getData();
        QueryWrapper<RecordsOfSteelmakingOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_no", recordNo);
        if (!CollectionUtils.isEmpty(this.list(queryWrapper))) {
            throw new GlobalException("该作业记录编号已存在，请尝试重新初始化！", ResultCode.FAILED);
        }
        QueryWrapper<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfSteelmakingOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
        //判断预装炉是否已存在炼钢记录
        if (!CollectionUtils.isEmpty(this.list(recordsOfSteelmakingOperationsQueryWrapper))) {
            return true;
        }
        RecordsOfSteelmakingOperations recordsOfSteelmakingOperations = new RecordsOfSteelmakingOperations();
        recordsOfSteelmakingOperations.setPrechargeFurnaceId(prechargeFurnaceId);
        recordsOfSteelmakingOperations.setOperator(SecurityUtils.getCurrentUser().getUsername());
        recordsOfSteelmakingOperations.setOperatorTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordsOfSteelmakingOperations.setRecordNo(recordNo);
        recordsOfSteelmakingOperations.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        recordsOfSteelmakingOperations.setClassGroup(branch.getBranchName());
        //查询预装炉信息
        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(prechargeFurnaceId);
        if (prechargeFurnace == null) {
            throw new GlobalException("没有找到预装炉信息！", ResultCode.FAILED);
        }
        //根据预装炉信息查找炼钢工序派工信息
        QueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceAssignQueryWrapper.eq("furnace_id", prechargeFurnaceId).eq("opt_type", "15").last("limit 1");
        PrechargeFurnaceAssign prechargeFurnaceAssign = prechargeFurnaceAssignService.getOne(prechargeFurnaceAssignQueryWrapper);
        recordsOfSteelmakingOperations.setTypeOfSteel(prechargeFurnace.getTypeOfSteel());
        recordsOfSteelmakingOperations.setSmeltingEquipment(prechargeFurnaceAssign.getDeviceName());
        UpdateWrapper<PrechargeFurnaceAssign> assignUpdateWrapper = new UpdateWrapper<>();
        assignUpdateWrapper.eq("furnace_id", prechargeFurnaceId).eq("opt_type", "15").set("record_status", 2);
        prechargeFurnaceAssignService.update(assignUpdateWrapper);
        return this.save(recordsOfSteelmakingOperations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RecordsOfSteelmakingOperations recordsOfSteelmakingOperations) {
        //先保存分析结果信息
        QueryWrapper<ResultsOfSteelmaking> resultsOfSteelmakingQueryWrapper = new QueryWrapper<>();
        resultsOfSteelmakingQueryWrapper.eq("steelmaking_id", recordsOfSteelmakingOperations.getId());
        resultsOfSteelmakingService.remove(resultsOfSteelmakingQueryWrapper);
        for (ResultsOfSteelmaking resultsOfSteelmaking : recordsOfSteelmakingOperations.getResultsOfSteelmaking()) {
            resultsOfSteelmaking.setSteelmakingId(recordsOfSteelmakingOperations.getId());
        }
        resultsOfSteelmakingService.saveBatch(recordsOfSteelmakingOperations.getResultsOfSteelmaking());
        return this.updateById(recordsOfSteelmakingOperations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean check(List<String> ids, int state) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String username = SecurityUtils.getCurrentUser().getUsername();
        QueryWrapper<RecordsOfSteelmakingOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperations = this.list(queryWrapper);
        Set<Long> furnaceId = recordsOfSteelmakingOperations.stream().map(RecordsOfSteelmakingOperations::getPrechargeFurnaceId).collect(Collectors.toSet());
        UpdateWrapper<PrechargeFurnaceAssign> assignUpdateWrapper = new UpdateWrapper<>();
        assignUpdateWrapper.in("furnace_id", furnaceId).eq("opt_type", "15").set("record_status", state);
        prechargeFurnaceAssignService.update(assignUpdateWrapper);
        for (RecordsOfSteelmakingOperations recordsOfSteelmakingOperation : recordsOfSteelmakingOperations) {
            recordsOfSteelmakingOperation.setAssessor(username);
            recordsOfSteelmakingOperation.setAssessorTime(date);
            recordsOfSteelmakingOperation.setStatus(state);
        }
        return this.updateBatchById(recordsOfSteelmakingOperations);
    }

    @Override
    public IPage<RecordsOfSteelmakingOperations> bzzcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit, String order, String orderCol) {
        //班组长查询同班员工号
        List<TenantUser> tenantUserList = systemServiceClient.queryClass(SecurityUtils.getCurrentUser().getUsername());
        List<String> userIdList = tenantUserList.stream().map(TenantUser::getUserAccount).collect(Collectors.toList());
        //根据员工号查询派炉信息
        QueryWrapper<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceAssignQueryWrapper.in("user_id", userIdList);
        List<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignPersonList = prechargeFurnaceAssignPersonService.list(prechargeFurnaceAssignQueryWrapper);
        if (CollectionUtils.isEmpty(prechargeFurnaceAssignPersonList)) {
            return null;
        }
        //获取派送预装炉id
        Set<Long> prechargeFurnaceIdSet = prechargeFurnaceAssignPersonList.stream().map(PrechargeFurnaceAssignPerson::getPrechargeFurnaceId).collect(Collectors.toSet());
        //根据预装炉id获取炼钢记录
        QueryWrapper<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfSteelmakingOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
        if (!StringUtils.isNullOrEmpty(recordNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("record_no", recordNo);
        }
        if (prechargeFurnaceId != null) {
            recordsOfSteelmakingOperationsQueryWrapper.like("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("type_of_steel", typeOfSteel);
        }
        if (!StringUtils.isNullOrEmpty(smeltingEquipment)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("smelting_equipment", smeltingEquipment);
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
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(recordsOfSteelmakingOperationsQueryWrapper, orderCol, order);
        } else {
            recordsOfSteelmakingOperationsQueryWrapper.orderByDesc("modify_time");
        }

        return this.page(new Page<>(page, limit), recordsOfSteelmakingOperationsQueryWrapper);
    }

    @Override
    public IPage<RecordsOfSteelmakingOperations> czgcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit, String order, String orderCol) {
        //根据员工号查询派炉信息
        QueryWrapper<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceAssignQueryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        List<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignPersonList = prechargeFurnaceAssignPersonService.list(prechargeFurnaceAssignQueryWrapper);
        if (CollectionUtils.isEmpty(prechargeFurnaceAssignPersonList)) {
            return null;
        }
        //获取派送预装炉id
        Set<Long> prechargeFurnaceIdSet = prechargeFurnaceAssignPersonList.stream().map(PrechargeFurnaceAssignPerson::getPrechargeFurnaceId).collect(Collectors.toSet());
        //根据预装炉id获取炼钢记录
        QueryWrapper<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfSteelmakingOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
        if (!StringUtils.isNullOrEmpty(recordNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("record_no", recordNo);
        }
        if (prechargeFurnaceId != null) {
            recordsOfSteelmakingOperationsQueryWrapper.like("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("type_of_steel", typeOfSteel);
        }
        if (!StringUtils.isNullOrEmpty(smeltingEquipment)) {
            recordsOfSteelmakingOperationsQueryWrapper.like("smelting_equipment", smeltingEquipment);
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
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(recordsOfSteelmakingOperationsQueryWrapper, orderCol, order);
        } else {
            recordsOfSteelmakingOperationsQueryWrapper.orderByDesc("modify_time");
        }

        return this.page(new Page<>(page, limit), recordsOfSteelmakingOperationsQueryWrapper);
    }

    @Override
    public void export(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, HttpServletResponse response) {
        boolean isBzz = recordsOfPourOperationsService.isBzz();
        List<RecordsOfSteelmakingOperations> list = new ArrayList<>();
        if (isBzz) {
            //班组长查询同班员工号
            List<TenantUser> tenantUserList = systemServiceClient.queryClass(SecurityUtils.getCurrentUser().getUsername());
            List<String> userIdList = tenantUserList.stream().map(TenantUser::getUserAccount).collect(Collectors.toList());
            //根据员工号查询派炉信息
            QueryWrapper<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
            prechargeFurnaceAssignQueryWrapper.in("user_id", userIdList);
            List<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignPersonList = prechargeFurnaceAssignPersonService.list(prechargeFurnaceAssignQueryWrapper);
            if (CollectionUtils.isEmpty(prechargeFurnaceAssignPersonList)) {
                throw new GlobalException("没有派工信息！", ResultCode.FAILED);
            }
            //获取派送预装炉id
            Set<Long> prechargeFurnaceIdSet = prechargeFurnaceAssignPersonList.stream().map(PrechargeFurnaceAssignPerson::getPrechargeFurnaceId).collect(Collectors.toSet());
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
            list = this.list(recordsOfSteelmakingOperationsQueryWrapper);
        } else {
            //根据员工号查询派炉信息
            QueryWrapper<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
            prechargeFurnaceAssignQueryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
            List<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignPersonList = prechargeFurnaceAssignPersonService.list(prechargeFurnaceAssignQueryWrapper);
            if (CollectionUtils.isEmpty(prechargeFurnaceAssignPersonList)) {
                throw new GlobalException("没有派工信息！", ResultCode.FAILED);
            }
            //获取派送预装炉id
            Set<Long> prechargeFurnaceIdSet = prechargeFurnaceAssignPersonList.stream().map(PrechargeFurnaceAssignPerson::getPrechargeFurnaceId).collect(Collectors.toSet());
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
            list = this.list(recordsOfSteelmakingOperationsQueryWrapper);
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException("没有找到炼钢记录信息！", ResultCode.FAILED);
        }
        String fileName = "炼钢记录.xlsx";
        String[] columnHeaders = {"审核状态", "记录编号", "配炉编号", "炉号", "钢种", "冶炼设备", "冶炼班组", "班长", "记录人", "记录时间", "审核人", "审核时间"};
        String[] fieldNames = {"status", "recordNo", "prechargeFurnaceId", "furnaceNo", "typeOfSteel", "smeltingEquipment", "classGroup", "leader", "operator", "operatorTime", "assessor", "assessorTime", "remark"};
        try {
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(List<String> ids) {
        List<RecordsOfSteelmakingOperations> steelmakingOperationsList = this.listByIds(ids);
        Set<Long> furnaceId = steelmakingOperationsList.stream().map(RecordsOfSteelmakingOperations::getPrechargeFurnaceId).collect(Collectors.toSet());
        UpdateWrapper<PrechargeFurnaceAssign> assignUpdateWrapper = new UpdateWrapper<>();
        assignUpdateWrapper.in("furnace_id", furnaceId).eq("opt_type", "15").set("record_status", null);
        prechargeFurnaceAssignService.update(assignUpdateWrapper);
        return this.removeByIds(ids);
    }
}

