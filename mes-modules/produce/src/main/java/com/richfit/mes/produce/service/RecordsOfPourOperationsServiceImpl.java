package com.richfit.mes.produce.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RecordsOfPourOperationsMapper;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.entity.BatchAddScheduleDto;
import com.richfit.mes.produce.entity.CompleteDto;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    @Autowired
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;
    @Autowired
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Autowired
    private TrackAssignPersonService trackAssignPersonService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private TrackCheckService trackCheckService;

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
        UpdateWrapper<PrechargeFurnaceAssign> assignUpdateWrapper = new UpdateWrapper<>();
        assignUpdateWrapper.eq("furnace_id", prechargeFurnaceId).eq("opt_type", "16").set("record_status", 2);
        prechargeFurnaceAssignService.update(assignUpdateWrapper);
        return this.save(recordsOfPourOperations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RecordsOfPourOperations recordsOfPourOperations) {
        //先保存工序信息
        List<TrackItem> itemList = recordsOfPourOperations.getItemList();
        //计算保温完成时间
        for (TrackItem trackItem : itemList) {
            //浇注时间
            String pourTime = recordsOfPourOperations.getPourTime();
            //保温时间
            String holdTime = trackItem.getHoldTime();
            this.countHoldFinishedTime(trackItem, pourTime, holdTime);
        }

        trackItemService.updateBatchById(itemList);
        return this.updateById(recordsOfPourOperations);
    }


    /**
     * 计算并设置保温结束时间
     *
     * @param trackItem
     * @param pourTime  浇注时间
     * @param holdTime  保温时间
     */
    @Override
    public void countHoldFinishedTime(TrackItem trackItem, String pourTime, String holdTime) {
        if (!StringUtils.isNullOrEmpty(pourTime) && !StringUtils.isNullOrEmpty(holdTime)) {
            Date date = DateUtils.addDateForHour(DateUtils.parseDate(pourTime, "yyyy-MM-dd HH:mm:ss"), Integer.valueOf(holdTime));
            trackItem.setHoldFinishedTime(date);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean check(List<String> ids, int state) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String username = SecurityUtils.getCurrentUser().getUsername();
        QueryWrapper<RecordsOfPourOperations> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<RecordsOfPourOperations> recordsOfPourOperations = this.list(queryWrapper);
        Set<Long> furnaceId = recordsOfPourOperations.stream().map(RecordsOfPourOperations::getPrechargeFurnaceId).collect(Collectors.toSet());
        UpdateWrapper<PrechargeFurnaceAssign> assignUpdateWrapper = new UpdateWrapper<>();
        assignUpdateWrapper.in("furnace_id", furnaceId).eq("opt_type", "16").set("record_status", state);
        prechargeFurnaceAssignService.update(assignUpdateWrapper);
        for (RecordsOfPourOperations recordsOfPourOperation : recordsOfPourOperations) {
            recordsOfPourOperation.setAssessor(username);
            recordsOfPourOperation.setAssessorTime(date);
            recordsOfPourOperation.setStatus(state);
        }
        return this.updateBatchById(recordsOfPourOperations);
    }

    @Override
    public IPage<RecordsOfPourOperations> bzzcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, int page, int limit, String orderCol, String order) {
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
            recordsOfPourOperationsQueryWrapper.like("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfPourOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfPourOperationsQueryWrapper.like("type_of_steel", typeOfSteel);
        }
        buildQueryWrapper(ingotCase, startTime, endTime, status, recordsOfPourOperationsQueryWrapper);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(recordsOfPourOperationsQueryWrapper, orderCol, order);
        } else {
            recordsOfPourOperationsQueryWrapper.orderByDesc("modify_time");
        }
        return this.page(new Page<>(page, limit), recordsOfPourOperationsQueryWrapper);
    }

    @Override
    public IPage<RecordsOfPourOperations> czgcx(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String ingotCase, String startTime, String endTime, Integer status, int page, int limit, String orderCol, String order) {
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
            recordsOfPourOperationsQueryWrapper.like("precharge_furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(furnaceNo)) {
            recordsOfPourOperationsQueryWrapper.like("furnace_no", furnaceNo);
        }
        if (!StringUtils.isNullOrEmpty(typeOfSteel)) {
            recordsOfPourOperationsQueryWrapper.like("type_of_steel", typeOfSteel);
        }
        if (!StringUtils.isNullOrEmpty(ingotCase)) {
            recordsOfPourOperationsQueryWrapper.like("smelting_equipment", ingotCase);
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
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(recordsOfPourOperationsQueryWrapper, orderCol, order);
        } else {
            recordsOfPourOperationsQueryWrapper.orderByDesc("modify_time");
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

    @Override
    public Boolean delete(List<String> ids) {
        List<RecordsOfPourOperations> pourOperationsList = this.listByIds(ids);
        Set<Long> furnaceId = pourOperationsList.stream().map(RecordsOfPourOperations::getPrechargeFurnaceId).collect(Collectors.toSet());
        UpdateWrapper<PrechargeFurnaceAssign> assignUpdateWrapper = new UpdateWrapper<>();
        assignUpdateWrapper.in("furnace_id", furnaceId).eq("opt_type", "16").set("record_status", null);
        prechargeFurnaceAssignService.update(assignUpdateWrapper);
        return this.removeByIds(ids);
    }

    @Override
    public IPage<TrackItem> getItemByPrechargeFurnaceId(Long prechargeFurnaceId, int page, int limit) {
        //获取当前预装炉内装的工序信息
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("precharge_furnace_id", prechargeFurnaceId).eq("is_current", 1);
        List<TrackItem> trackItemList = trackItemService.list(queryWrapper);
        if (CollectionUtils.isEmpty(trackItemList)) {
            throw new GlobalException("该预装炉当前没有工序！", ResultCode.FAILED);
        }
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("is_current", 1).eq("opt_type", "15").isNull("precharge_furnace_id")
                .eq("branch_code", trackItemList.get(0).getBranchCode())
                .eq("tenant_id", trackItemList.get(0).getTenantId())
                .eq("is_exist_schedule_check", trackItemList.get(0).getIsExistScheduleCheck());
        return trackItemService.page(new Page<>(page, limit), itemQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addItem(Long prechargeFurnaceId, List<String> itemIds) {
        List<TrackItem> trackItems = trackItemService.listByIds(itemIds);
        //更新itemIds为下工序的id
        List<String> newItemIds = new ArrayList<>();
        for (TrackItem trackItem : trackItems) {
            QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
            trackItemQueryWrapper.eq("original_opt_sequence", trackItem.getNextOptSequence()).eq("flow_id", trackItem.getFlowId());
            List<TrackItem> list = trackItemService.list(trackItemQueryWrapper);
            newItemIds.addAll(list.stream().map(TrackItem::getId).collect(Collectors.toList()));
        }
        //预装炉信息
        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(prechargeFurnaceId);
        for (TrackItem trackItem : trackItems) {
            if (!trackItem.getIsExistScheduleCheck().equals(prechargeFurnace.getIsExistScheduleCheck())) {
                throw new GlobalException("新增工序调度状态需与预装炉保持一致！", ResultCode.FAILED);
            }
        }
        //浇注记录新增的产品是炼钢，先继承炼钢工序的派工、报工信息、调度
        {
            //查找该预装炉炼钢工序的预装炉派工信息
            LambdaQueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceAssignLambdaQueryWrapper = new LambdaQueryWrapper<>();
            prechargeFurnaceAssignLambdaQueryWrapper.eq(PrechargeFurnaceAssign::getFurnaceId, prechargeFurnaceId)
                    .eq(PrechargeFurnaceAssign::getOptType, "15").last("limit 1");
            PrechargeFurnaceAssign prechargeFurnaceAssign = prechargeFurnaceAssignService.getOne(prechargeFurnaceAssignLambdaQueryWrapper);
            //构造派工信息
            Assign assignInfoOfSteelmaking = getAssignInfoByPrechargeFurnaceIdAndOptType(prechargeFurnaceId, "15", itemIds);
            //进行派工和开工
            saveAssigenByAssigenInfoAndItemId(assignInfoOfSteelmaking, itemIds, prechargeFurnaceAssign);
            //构造报工信息
            List<CompleteDto> completeDtoList = buildComplateInfo(prechargeFurnaceId, itemIds, "15");
            //报工
            trackCompleteService.saveComplete(completeDtoList);
            //冶炼车间没有质检，若有调度审核，则进行调度审核
            if (prechargeFurnace.getIsExistScheduleCheck() == 1) {
                //构建调度审核信息
                BatchAddScheduleDto batchAddScheduleDto = buildBatchAddScheduleDto(prechargeFurnace, itemIds, "15");
                trackCheckService.batchAddSchedule(batchAddScheduleDto);
            }
        }
        //继承浇注工序的派工
        {
            //查找该预装炉浇注工序的预装炉派工信息
            LambdaQueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceAssignLambdaQueryWrapper = new LambdaQueryWrapper<>();
            prechargeFurnaceAssignLambdaQueryWrapper.eq(PrechargeFurnaceAssign::getFurnaceId, prechargeFurnaceId)
                    .eq(PrechargeFurnaceAssign::getOptType, "16").last("limit 1");
            PrechargeFurnaceAssign prechargeFurnaceAssign = prechargeFurnaceAssignService.getOne(prechargeFurnaceAssignLambdaQueryWrapper);
            //构造派工信息
            Assign assignInfoOfSteelmaking = getAssignInfoByPrechargeFurnaceIdAndOptType(prechargeFurnaceId, "16", newItemIds);
            //进行派工和开工
            saveAssigenByAssigenInfoAndItemId(assignInfoOfSteelmaking, newItemIds, prechargeFurnaceAssign);

        }

//        //查询该预装炉当前的派工id,获取派工信息
//        QueryWrapper<PrechargeFurnaceAssign> furnaceAssignQueryWrapper = new QueryWrapper<>();
//        furnaceAssignQueryWrapper.eq("furnace_id", prechargeFurnaceId).eq("complete_status", "0").ne("is_doing", "2").last("limit 1");
//        PrechargeFurnaceAssign furnaceAssign = prechargeFurnaceAssignService.getOne(furnaceAssignQueryWrapper);
//        if (ObjectUtils.isEmpty(furnaceAssign)) {
//            throw new GlobalException("该预装炉没有派工信息！", ResultCode.FAILED);
//        }
//        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
//        itemQueryWrapper.eq("precharge_furnace_id", prechargeFurnaceId).eq("precharge_furnace_assign_id", furnaceAssign.getId());
//        List<TrackItem> items = trackItemService.list(itemQueryWrapper);
//        if (CollectionUtils.isEmpty(items)) {
//            throw new GlobalException("该预装炉没有派工信息！", ResultCode.FAILED);
//        }
//        QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
//        assignQueryWrapper.eq("ti_id", items.get(0).getId()).last("limit 1");
//        Assign assign = trackAssignService.getOne(assignQueryWrapper);
//        QueryWrapper<AssignPerson> assignPersonQueryWrapper = new QueryWrapper<>();
//        assignPersonQueryWrapper.eq("assign_id", assign.getId());
//        List<AssignPerson> assignPeople = trackAssignPersonService.list(assignPersonQueryWrapper);
//        assign.setAssignPersons(assignPeople);
//
//        List<TrackItem> trackItemList = trackItemService.listByIds(itemIds);
//        //派工并开工
//        for (TrackItem trackItem : trackItemList) {
//            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
//            //派工数量校验
//            if (trackItem.getAssignableQty() < assign.getQty()) {
//                throw new GlobalException(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty(), ResultCode.FAILED);
//            }
//            trackItem.setAssignableQty(trackItem.getAssignableQty() - assign.getQty());
//            //可派工数量为0时 工序变为已派工状态
//            if (0 == trackItem.getAssignableQty()) {
//                trackItem.setIsSchedule(1);
//            }
//            //设置派工设备
//            trackItem.setDeviceId(assign.getDeviceId());
//            //锻造计算额定工时
//            if ("4".equals(trackHead.getClasses())) {
//                trackAssignService.calculationSinglePieceHours(trackHead, trackItem);
//            }
//            if (!StringUtils.isNullOrEmpty(trackHead.getStatus()) || "0".equals(trackHead.getStatus())) {
//                //将跟单状态改为在制
//                trackHead.setStatus("1");
//                trackHeadService.updateById(trackHead);
//                UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
//                update.set("status", "1");
//                update.eq("id", trackItem.getFlowId());
//                trackHeadFlowService.update(update);
//            }
//            //构造派工信息
//            constructAssignInfo(assign, trackItem, trackHead);
//            trackAssignService.save(assign);
//            //保存派工人员信息
//            for (AssignPerson person : assign.getAssignPersons()) {
//                person.setModifyTime(new Date());
//                person.setAssignId(assign.getId());
//                trackAssignPersonMapper.insert(person);
//            }
//            //保存预装炉派工人员信息
//            for (AssignPerson person : assign.getAssignPersons()) {
//                PrechargeFurnaceAssignPerson prechargeFurnaceAssignPerson = new PrechargeFurnaceAssignPerson();
//                prechargeFurnaceAssignPerson.setPrechargeFurnaceAssignId(furnaceAssign.getId());
//                prechargeFurnaceAssignPerson.setPrechargeFurnaceId(prechargeFurnaceId);
//                prechargeFurnaceAssignPerson.setUserId(person.getUserId());
//                prechargeFurnaceAssignPersonService.save(prechargeFurnaceAssignPerson);
//            }
//            //保存工序信息
//            trackItem.setPrechargeFurnaceAssignId(furnaceAssign.getId());
//            trackItem.setPrechargeFurnaceId(prechargeFurnaceId);
//            trackItemService.updateById(trackItem);
//
//            systemServiceClient.savenote(assign.getAssignBy(),
//                    "您有新的派工跟单需要报工！",
//                    assign.getTrackNo(),
//                    assign.getUserId().substring(0, assign.getUserId().length() - 1),
//                    assign.getBranchCode(),
//                    assign.getTenantId());
//        }
//        if (assign.getState() == 1) {
//            List<String> headIds = trackItemList.stream().map(TrackItem::getTrackHeadId).collect(Collectors.toList());
//            List<String> flowIds = trackItemList.stream().map(TrackItem::getFlowId).collect(Collectors.toList());
//            //将跟单状态改为在制
//            UpdateWrapper<TrackHead> trackHeadUpdateWrapper = new UpdateWrapper<>();
//            trackHeadUpdateWrapper.set("status", "1")
//                    .eq("status", "0")
//                    .in("id", headIds);
//            trackHeadService.update(trackHeadUpdateWrapper);
//            UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
//            update.set("status", "1")
//                    .eq("status", "0")
//                    .in("id", flowIds);
//            trackHeadFlowService.update(update);
//            UpdateWrapper<Assign> assignUpdate;
//            assignUpdate = new UpdateWrapper<>();
//            assignUpdate.set("state", "1")
//                    .eq("state", "0")
//                    .in("ti_id", itemIds);
//            trackAssignService.update(assignUpdate);
//            UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
//            trackItemUpdateWrapper.set("is_doing", 1)
//                    .set("start_doing_time", new Date())
//                    .set("start_doing_user", SecurityUtils.getCurrentUser().getUsername())
//                    .eq("is_doing", 0)
//                    .in("id", itemIds);
//            trackItemService.update(trackItemUpdateWrapper);
//        }
        return true;
    }

    private BatchAddScheduleDto buildBatchAddScheduleDto(PrechargeFurnace prechargeFurnace, List<String> itemIds, String optType) {
        LambdaQueryWrapper<TrackItem> trackItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        trackItemLambdaQueryWrapper.eq(TrackItem::getPrechargeFurnaceId, prechargeFurnace).eq(TrackItem::getOptType, optType);
        List<TrackItem> trackItemList = trackItemService.list(trackItemLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(trackItemList)) {
            throw new GlobalException("预装炉内没有工序编号为" + optType + "的工序！", ResultCode.FAILED);
        }
        BatchAddScheduleDto batchAddScheduleDto = new BatchAddScheduleDto();
        batchAddScheduleDto.setBranchCode(trackItemList.get(0).getBranchCode());
        batchAddScheduleDto.setIsPrepare(trackItemList.get(0).getIsPrepare());
        batchAddScheduleDto.setResult(trackItemList.get(0).getScheduleCompleteResult());
        batchAddScheduleDto.setTiId(itemIds);
        return batchAddScheduleDto;
    }

    private List<CompleteDto> buildComplateInfo(Long prechargeFurnaceId, List<String> itemIds, String optType) {
        LambdaQueryWrapper<TrackItem> trackItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        trackItemLambdaQueryWrapper.eq(TrackItem::getPrechargeFurnaceId, prechargeFurnaceId).eq(TrackItem::getOptType, optType)
                .notIn(TrackItem::getId, itemIds);
        List<TrackItem> trackItemList = trackItemService.list(trackItemLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(trackItemList)) {
            throw new GlobalException("预装炉内没有工序编号为" + optType + "的工序！", ResultCode.FAILED);
        }
        LambdaQueryWrapper<TrackComplete> trackCompleteLambdaQueryWrapper = new LambdaQueryWrapper<>();
        trackCompleteLambdaQueryWrapper.eq(TrackComplete::getTiId, trackItemList.get(0).getId());
        List<TrackComplete> trackCompletes = trackCompleteService.list(trackCompleteLambdaQueryWrapper);
        List<CompleteDto> completeDtoList = new ArrayList<>();
        for (String itemId : itemIds) {
            TrackItem trackItem = trackItemService.getById(itemId);
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            LambdaQueryWrapper<Assign> trackAssignServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            trackAssignServiceLambdaQueryWrapper.eq(Assign::getTiId, itemId).last("limit 1");
            Assign assign = trackAssignService.getOne(trackAssignServiceLambdaQueryWrapper);
            CompleteDto completeDto = new CompleteDto();
            completeDto.setAssignId(assign.getId());
            completeDto.setNextFurnace(true);
            completeDto.setProdNo(trackItem.getProductNo());
            completeDto.setQcPersonId(trackCompletes.get(0).getUserId());
            completeDto.setTiId(itemId);
            completeDto.setTrackCompleteList(trackCompletes);
            completeDto.setTrackId(trackItem.getTrackHeadId());
            completeDto.setTrackNo(trackHead.getTrackNo());
            completeDtoList.add(completeDto);
        }
        return completeDtoList;
    }

    private void saveAssigenByAssigenInfoAndItemId(Assign assign, List<String> itemIds, PrechargeFurnaceAssign furnaceAssign) {
        List<TrackItem> trackItemList = trackItemService.listByIds(itemIds);
        //派工
        for (TrackItem trackItem : trackItemList) {
            assign.setQty(trackItem.getAssignableQty());
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            //派工数量校验
            if (trackItem.getAssignableQty() < assign.getQty()) {
                throw new GlobalException(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty(), ResultCode.FAILED);
            }
            trackItem.setAssignableQty(trackItem.getAssignableQty() - assign.getQty());
            //可派工数量为0时 工序变为已派工状态
            if (0 == trackItem.getAssignableQty()) {
                trackItem.setIsSchedule(1);
            }
            //设置派工设备
            trackItem.setDeviceId(assign.getDeviceId());
            //锻造计算额定工时
            if ("4".equals(trackHead.getClasses())) {
                trackAssignService.calculationSinglePieceHours(trackHead, trackItem);
            }
            if (!StringUtils.isNullOrEmpty(trackHead.getStatus()) || "0".equals(trackHead.getStatus())) {
                //将跟单状态改为在制
                trackHead.setStatus("1");
                trackHeadService.updateById(trackHead);
                UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
                update.set("status", "1");
                update.eq("id", trackItem.getFlowId());
                trackHeadFlowService.update(update);
            }
            //构造派工信息
            constructAssignInfo(assign, trackItem, trackHead);
            trackAssignService.save(assign);
            //保存派工人员信息
            for (AssignPerson person : assign.getAssignPersons()) {
                person.setModifyTime(new Date());
                person.setAssignId(assign.getId());
                trackAssignPersonMapper.insert(person);
            }
            //保存预装炉派工人员信息
            for (AssignPerson person : assign.getAssignPersons()) {
                PrechargeFurnaceAssignPerson prechargeFurnaceAssignPerson = new PrechargeFurnaceAssignPerson();
                prechargeFurnaceAssignPerson.setPrechargeFurnaceAssignId(furnaceAssign.getId());
                prechargeFurnaceAssignPerson.setPrechargeFurnaceId(furnaceAssign.getFurnaceId());
                prechargeFurnaceAssignPerson.setUserId(person.getUserId());
                prechargeFurnaceAssignPersonService.save(prechargeFurnaceAssignPerson);
            }
            //保存工序信息
            trackItem.setPrechargeFurnaceAssignId(furnaceAssign.getId());
            trackItem.setPrechargeFurnaceId(furnaceAssign.getFurnaceId());
            trackItemService.updateById(trackItem);

            systemServiceClient.savenote(assign.getAssignBy(),
                    "您有新的派工跟单需要报工！",
                    assign.getTrackNo(),
                    assign.getUserId().substring(0, assign.getUserId().length() - 1),
                    assign.getBranchCode(),
                    assign.getTenantId());
        }
        //开工
        if (assign.getState() == 1) {
            List<String> headIds = trackItemList.stream().map(TrackItem::getTrackHeadId).collect(Collectors.toList());
            List<String> flowIds = trackItemList.stream().map(TrackItem::getFlowId).collect(Collectors.toList());
            //将跟单状态改为在制
            UpdateWrapper<TrackHead> trackHeadUpdateWrapper = new UpdateWrapper<>();
            trackHeadUpdateWrapper.set("status", "1")
                    .eq("status", "0")
                    .in("id", headIds);
            trackHeadService.update(trackHeadUpdateWrapper);
            UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
            update.set("status", "1")
                    .eq("status", "0")
                    .in("id", flowIds);
            trackHeadFlowService.update(update);
            UpdateWrapper<Assign> assignUpdate;
            assignUpdate = new UpdateWrapper<>();
            assignUpdate.set("state", "1")
                    .eq("state", "0")
                    .in("ti_id", itemIds);
            trackAssignService.update(assignUpdate);
            UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
            trackItemUpdateWrapper.set("is_doing", 1)
                    .set("start_doing_time", new Date())
                    .set("start_doing_user", SecurityUtils.getCurrentUser().getUsername())
                    .eq("is_doing", 0)
                    .in("id", itemIds);
            trackItemService.update(trackItemUpdateWrapper);
        }
    }

    private Assign getAssignInfoByPrechargeFurnaceIdAndOptType(Long prechargeFurnaceId, String optType, List<String> notInId) {
        LambdaQueryWrapper<TrackItem> trackItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        trackItemLambdaQueryWrapper.eq(TrackItem::getPrechargeFurnaceId, prechargeFurnaceId).eq(TrackItem::getOptType, optType)
                .notIn(TrackItem::getId, notInId);
        List<TrackItem> trackItemList = trackItemService.list(trackItemLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(trackItemList)) {
            throw new GlobalException("预装炉内没有工序类型编码为" + optType + "的工序", ResultCode.FAILED);
        }
        QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
        assignQueryWrapper.eq("ti_id", trackItemList.get(0).getId()).last("limit 1");
        Assign assign = trackAssignService.getOne(assignQueryWrapper);
        if (Objects.isNull(assign)) {
            throw new GlobalException("预装炉内工序类型编码为" + optType + "的工序没有派工信息！", ResultCode.FAILED);
        }
        QueryWrapper<AssignPerson> assignPersonQueryWrapper = new QueryWrapper<>();
        assignPersonQueryWrapper.eq("assign_id", assign.getId());
        List<AssignPerson> assignPeople = trackAssignPersonService.list(assignPersonQueryWrapper);
        assign.setAssignPersons(assignPeople);
        return assign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteItem(List<String> itemIds, Long prechargeFurnaceId) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_current", 1).eq("precharge_furnace_id", prechargeFurnaceId);
        List<TrackItem> list = trackItemService.list(queryWrapper);
        if (list != null && list.size() <= itemIds.size()) {
            throw new GlobalException("不能删除预装炉中所有工序！", ResultCode.FAILED);
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        List<TrackItem> lastTrackItems = null;
        for (String itemId : itemIds) {
            TrackItem trackItem = trackItemService.getById(itemId);
            QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
            trackItemQueryWrapper.eq("flow_id", trackItem.getFlowId()).eq("next_opt_sequence", trackItem.getOriginalOptSequence());
            lastTrackItems = trackItemService.list(trackItemQueryWrapper);
            //重置当前工序（浇注工序）信息
            trackItemService.resetStatus(itemId, 5, request);
            //回退至上工序（炼钢工序）
            trackItemService.backSequence(trackItem.getFlowId());
            //重置当前（炼钢）
            for (TrackItem lastTrackItem : lastTrackItems) {
                trackItemService.resetStatus(lastTrackItem.getId(), 5, request);
            }
            UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
            trackItemUpdateWrapper.set("precharge_furnace_id", null).set("precharge_furnace_assign_id", null)
                    .eq("flow_id", trackItem.getFlowId()).eq("next_opt_sequence", trackItem.getOriginalOptSequence());
            trackItemService.update(trackItemUpdateWrapper);
        }
        return true;
    }

    private void constructAssignInfo(Assign assign, TrackItem trackItem, TrackHead trackHead) {
        assign.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        if (null != SecurityUtils.getCurrentUser()) {
            assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
        }
        CommonResult<TenantUserVo> user = systemServiceClient.queryByUserId(assign.getAssignBy());
        assign.setAssignName(user.getData().getEmplName());
        assign.setAssignTime(new Date());
        assign.setModifyTime(new Date());
        assign.setCreateTime(new Date());
        assign.setAvailQty(assign.getQty());
        assign.setFlowId(trackItem.getFlowId());
        assign.setTiId(trackItem.getId());
        assign.setClasses(trackHead.getClasses());
        assign.setBranchCode(trackItem.getBranchCode());
        assign.setTenantId(trackItem.getTenantId());
        assign.setState(0);
        if (StringUtils.isNullOrEmpty(assign.getTrackId())) {
            assign.setTrackId(trackHead.getId());
        }
        if (StringUtils.isNullOrEmpty(assign.getTenantId())) {
            assign.setTenantId(trackHead.getTenantId());
        }
        //处理派工人员信息  (前端没有处理userId 和userName  assignPerson为派工人列表)
        if (StringUtils.isNullOrEmpty(assign.getUserId()) && !CollectionUtil.isEmpty(assign.getAssignPersons())) {
            StringBuilder userId = new StringBuilder();
            StringBuilder userName = new StringBuilder();
            for (AssignPerson assignPerson : assign.getAssignPersons()) {
                if (!StringUtils.isNullOrEmpty(String.valueOf(userId))) {
                    userId.append(",");
                    userName.append(",");
                }
                userId.append(assignPerson.getUserId());
                userName.append(assignPerson.getUserName());
            }
            assign.setUserId(String.valueOf(userId));
            assign.setEmplName(String.valueOf(userName));
        }
        boolean isAllUser = assign.getUserId().contains("/") ? true : false;
        if (isAllUser) {
            assign.setUserId("/");
            assign.setEmplName("/");
        }
    }
}

