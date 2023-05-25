package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RecordsOfPourOperationsMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * (RecordsOfPourOperations)表服务实现类
 *
 * @author makejava
 * @since 2023-05-12 15:53:49
 */
@Service
@Slf4j
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
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private PrechargeFurnaceAssignPersonService prechargeFurnaceAssignPersonService;
    @Autowired
    private CodeRuleService codeRuleService;

    @Override
    public RecordsOfPourOperations getByPrechargeFurnaceId(Long prechargeFurnaceId) {
        if (prechargeFurnaceId == null) {
            throw new GlobalException("请检查传入的预装炉id！", ResultCode.FAILED);
        }
        QueryWrapper<RecordsOfPourOperations> queryWrapperPour = new QueryWrapper<>();
        queryWrapperPour.eq("precharge_furnace_id", prechargeFurnaceId);
        RecordsOfPourOperations recordsOfPourOperation = this.getOne(queryWrapperPour);
        if (recordsOfPourOperation == null) {
            throw new GlobalException("没有找到预装炉浇注信息！", ResultCode.FAILED);
        }
        //根据预装炉号找对应当前工序
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId).eq("is_current", 1);
        List<TrackItem> trackItemList = trackItemService.list(itemQueryWrapper);
        for (TrackItem trackItem : trackItemList) {
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            Router router = baseServiceClient.getRouter(trackHead.getRouterId()).getData();
            trackItem.setPourTemperatureRouter(router.getPourTemp());
            trackItem.setPourTimeRouter(router.getPourTime());
            trackItem.setWeightMolten(router.getWeightMolten());
            trackItem.setPieceWeight(String.valueOf(router.getWeight()));
            trackItem.setTestBarNo(trackHead.getTestBarNo());
            trackItem.setTestBarType(router.getTestBar());
            trackItem.setProductName(trackHead.getProductName());
            trackItem.setTrackNo(trackHead.getTrackNo());
        }
        recordsOfPourOperation.setItemList(trackItemList);
        return recordsOfPourOperation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean init(Long prechargeFurnaceId, String branchCode) {
        String recordNo = null;
        try {
            recordNo = Code.valueOnUpdate("pour_no", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        QueryWrapper<RecordsOfPourOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_no", recordNo);
        if (!CollectionUtils.isEmpty(this.list(queryWrapper))) {
            throw new GlobalException("该作业记录编号已存在，请尝试重新初始化！", ResultCode.FAILED);
        }
        QueryWrapper<RecordsOfPourOperations> recordsOfPourOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfPourOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
        //判断预装炉是否已存在浇注记录
        if (!CollectionUtils.isEmpty(this.list(recordsOfPourOperationsQueryWrapper))) {
            return true;
        }
        Branch branch = baseServiceClient.selectBranchByCodeAndTenantId(SecurityUtils.getCurrentUser().getBelongOrgId(), SecurityUtils.getCurrentUser().getTenantId()).getData();
        RecordsOfPourOperations recordsOfPourOperations = new RecordsOfPourOperations();
        recordsOfPourOperations.setPrechargeFurnaceId(prechargeFurnaceId);
        recordsOfPourOperations.setOperator(SecurityUtils.getCurrentUser().getUsername());
        recordsOfPourOperations.setOperatorTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordsOfPourOperations.setRecordNo(recordNo);
        recordsOfPourOperations.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        recordsOfPourOperations.setClassGroup(branch.getBranchName());
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
        recordsOfPourOperations.setFurnaceNo(steelmakingOperations == null ? "" : steelmakingOperations.getFurnaceNo());
        recordsOfPourOperations.setIngotCase(prechargeFurnace.getIngotCase());
        return this.save(recordsOfPourOperations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RecordsOfPourOperations recordsOfPourOperations) {
        //先保存工序信息
        List<TrackItem> itemList = recordsOfPourOperations.getItemList();
        //计算保温完成时间
        for (TrackItem trackItem : itemList) {
            String pourTime = recordsOfPourOperations.getPourTime();
            String holdTime = trackItem.getHoldTime();
            countHoldFinishedTime(trackItem, pourTime, holdTime);

        }



        trackItemService.updateBatchById(itemList);
        return this.updateById(recordsOfPourOperations);
    }


    /**
     * 计算并设置保温结束时间
     * @param trackItem
     * @param pourTime
     * @param holdTime
     */
    private void countHoldFinishedTime(TrackItem trackItem, String pourTime, String holdTime) {
        if(!StringUtils.isNullOrEmpty(pourTime)&&!StringUtils.isNullOrEmpty(holdTime)){
            Date date = DateUtils.addDateForHour(DateUtils.stringToDate(pourTime), Integer.valueOf(holdTime));
            trackItem.setHoldFinishedTime(date);
        }
    }

    @Override
    public Boolean check(List<String> ids, int state) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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

    @Override
    public IPage<RecordsOfPourOperations> bzzcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, int page, int limit) {
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
        //根据预装炉id获取浇注信息
        QueryWrapper<RecordsOfPourOperations> recordsOfPourOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfPourOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
        if (!StringUtils.isNullOrEmpty(recordNo)) {
            recordsOfPourOperationsQueryWrapper.like("record_no", recordNo);
        }
        if (prechargeFurnaceId != null) {
            recordsOfPourOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfPourOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfPourOperationsQueryWrapper.eq("type_of_steel", typeOfSteel);
        }
        buildQueryWrapper(ingotCase, startTime, endTime, status, recordsOfPourOperationsQueryWrapper);
        return this.page(new Page<>(page, limit), recordsOfPourOperationsQueryWrapper);
    }

    @Override
    public IPage<RecordsOfPourOperations> czgcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, int page, int limit) {
        //根据员工号查询派炉信息
        QueryWrapper<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceAssignQueryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        List<PrechargeFurnaceAssignPerson> prechargeFurnaceAssignPersonList = prechargeFurnaceAssignPersonService.list(prechargeFurnaceAssignQueryWrapper);
        if (CollectionUtils.isEmpty(prechargeFurnaceAssignPersonList)) {
            return null;
        }
        //获取派送预装炉id
        Set<Long> prechargeFurnaceIdSet = prechargeFurnaceAssignPersonList.stream().map(PrechargeFurnaceAssignPerson::getPrechargeFurnaceId).collect(Collectors.toSet());
        //根据预装炉id获取浇注信息
        QueryWrapper<RecordsOfPourOperations> recordsOfPourOperationsQueryWrapper = new QueryWrapper<>();
        recordsOfPourOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
        if (!StringUtils.isNullOrEmpty(recordNo)) {
            recordsOfPourOperationsQueryWrapper.like("record_no", recordNo);
        }
        if (prechargeFurnaceId != null) {
            recordsOfPourOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfPourOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfPourOperationsQueryWrapper.eq("type_of_steel", typeOfSteel);
        }
        if (!StringUtils.isNullOrEmpty(ingotCase)) {
            recordsOfPourOperationsQueryWrapper.eq("smelting_equipment", ingotCase);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            recordsOfPourOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            recordsOfPourOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
        }
        if (status != null) {
            recordsOfPourOperationsQueryWrapper.eq("status", status);
        }
        return this.page(new Page<>(page, limit), recordsOfPourOperationsQueryWrapper);
    }

    @Override
    public void export(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, HttpServletResponse response) {
        boolean isBzz = this.isBzz();
        List<RecordsOfPourOperations> list = new ArrayList<>();
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
            //根据预装炉id获取浇注信息
            QueryWrapper<RecordsOfPourOperations> recordsOfPourOperationsQueryWrapper = new QueryWrapper<>();
            recordsOfPourOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
            if (!StringUtils.isNullOrEmpty(recordNo)) {
                recordsOfPourOperationsQueryWrapper.like("record_no", recordNo);
            }
            if (prechargeFurnaceId != null) {
                recordsOfPourOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
            }
            if (!StringUtils.isNullOrEmpty(furnaceNo)) {
                recordsOfPourOperationsQueryWrapper.like("furnace_no", furnaceNo);
            }
            if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
                recordsOfPourOperationsQueryWrapper.eq("type_of_steel", typeOfSteel);
            }
            buildQueryWrapper(ingotCase, startTime, endTime, status, recordsOfPourOperationsQueryWrapper);
            list = this.list(recordsOfPourOperationsQueryWrapper);
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
            //根据预装炉id获取浇注信息
            QueryWrapper<RecordsOfPourOperations> recordsOfPourOperationsQueryWrapper = new QueryWrapper<>();
            recordsOfPourOperationsQueryWrapper.in("precharge_furnace_id", prechargeFurnaceIdSet);
            if (!StringUtils.isNullOrEmpty(recordNo)) {
                recordsOfPourOperationsQueryWrapper.like("record_no", recordNo);
            }
            if (prechargeFurnaceId != null) {
                recordsOfPourOperationsQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId);
            }
            if (!StringUtils.isNullOrEmpty(furnaceNo)) {
                recordsOfPourOperationsQueryWrapper.like("furnace_no", furnaceNo);
            }
            if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
                recordsOfPourOperationsQueryWrapper.eq("type_of_steel", typeOfSteel);
            }
            if (!StringUtils.isNullOrEmpty(ingotCase)) {
                recordsOfPourOperationsQueryWrapper.eq("smelting_equipment", ingotCase);
            }
            if (!StringUtils.isNullOrEmpty(startTime)) {
                recordsOfPourOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                recordsOfPourOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
            }
            if (status != null) {
                recordsOfPourOperationsQueryWrapper.eq("status", status);
            }
            list = this.list(recordsOfPourOperationsQueryWrapper);
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException("没有找到浇注记录信息！", ResultCode.FAILED);
        }
        String fileName = "浇注记录.xlsx";
        String[] columnHeaders = {"审核状态", "记录编号", "配炉编号", "浇注温度", "炉号", "钢种", "浇注箱/根数", "锭型", "冶炼班组", "浇注工", "浇注时间", "审核人", "审核时间", "备注"};
        String[] fieldNames = {"status", "recordNo", "prechargeFurnaceId", "temperature", "furnaceNo", "typeOfSteel", "pourNum", "ingotCase", "classGroup", "operator", "pourTime", "assessor", "assessorTime", "remark"};
        try {
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void buildQueryWrapper(String ingotCase, String startTime, String endTime, Integer status, QueryWrapper<RecordsOfPourOperations> recordsOfPourOperationsQueryWrapper) {
        if (!StringUtils.isNullOrEmpty(ingotCase)) {
            recordsOfPourOperationsQueryWrapper.eq("ingot_case", ingotCase);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            recordsOfPourOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            recordsOfPourOperationsQueryWrapper.apply("UNIX_TIMESTAMP(operator_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
        }
        if (status != null) {
            recordsOfPourOperationsQueryWrapper.eq("status", status);
        }
    }

    @Override
    public boolean isBzz() {
        //获取登录用户权限
        List<Role> roles = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId());
        Set<String> rolesCode = roles.stream().map(Role::getRoleCode).collect(Collectors.toSet());
        //班组长标识
        String bzzBs = "JMAQ_BZZZ";
        boolean isBzz = false;
        for (String code : rolesCode) {
            if (code.endsWith(bzzBs)) {
                isBzz = true;
                break;
            }
        }
        return isBzz;
    }
}

