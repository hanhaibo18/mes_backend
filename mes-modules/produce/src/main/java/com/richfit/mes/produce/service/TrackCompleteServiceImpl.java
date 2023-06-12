package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.model.util.OptNameUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.OutsourceCompleteDto;
import com.richfit.mes.produce.entity.OutsourceDto;
import com.richfit.mes.produce.entity.QueryWorkingTimeVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.richfit.mes.produce.utils.ConcurrentUtil;
import com.richfit.mes.produce.utils.WorkHoursUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
public class TrackCompleteServiceImpl extends ServiceImpl<TrackCompleteMapper, TrackComplete> implements TrackCompleteService {

    @Autowired
    private TrackCompleteMapper trackCompleteMapper;

    @Autowired
    private TrackAssignService trackAssignService;
    @Autowired
    public PublicService publicService;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackAssignMapper trackAssignMapper;
    @Autowired
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Autowired
    private TrackCompleteCacheService trackCompleteCacheService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private TrackHeadFlowService trackFlowService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    public TrackCheckService trackCheckService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private LayingOffService layingOffService;
    @Autowired
    private ForgControlRecordService forgControlRecordService;
    @Autowired
    private RawMaterialRecordService rawMaterialRecordService;
    @Autowired
    private TrackItemMapper trackItemMapper;
    @Autowired
    private KnockoutService knockoutService;
    @Autowired
    private ModelingCoreService modelingCoreService;
    @Autowired
    private LayingOffCacheService layingOffCacheService;
    @Autowired
    private ForgControlRecordCacheService forgControlRecordCacheService;
    @Autowired
    private RawMaterialRecordCacheService rawMaterialRecordCacheService;
    @Autowired
    private ModelingCoreCacheService modelingCoreCacheService;
    @Autowired
    private KnockoutCacheService knockoutCacheService;
    @Autowired
    private PrechargeFurnaceAssignPersonService prechargeFurnaceAssignPersonService;
    @Autowired
    private RecordsOfSteelmakingOperationsService recordsOfSteelmakingOperationsService;
    @Autowired
    private RecordsOfPourOperationsService recordsOfPourOperationsService;
    @Autowired
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;
    @Autowired
    private TrackCertificateService trackCertificateService;

    @Resource
    private TrackAssemblyService assemblyService;

    public static final String END_START_WORK = "2";

    @Override
    public IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query) {
//        try {
//            deleteCompleteW();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return trackCompleteMapper.queryPage(page, query);
    }

    @Override
    public List<TrackComplete> queryList(String tiId, String branchCode, String order, String orderCol) {
        try {
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            if (!StringUtils.isNullOrEmpty(tiId)) {
                queryWrapper.eq("ti_id", tiId);
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            if (!StringUtils.isNullOrEmpty(orderCol)) {
                if (!StringUtils.isNullOrEmpty(order)) {
                    if ("desc".equals(order)) {
                        queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                    } else if ("asc".equals(order)) {
                        queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                    }
                } else {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc("modify_time");
            }
            List<TrackComplete> completes = trackCompleteMapper.queryList(queryWrapper);
            try {
                for (TrackComplete track : completes) {
                    CommonResult<TenantUserVo> tenantUserVo = systemServiceClient.queryByUserAccount(track.getUserId());
                    track.setUserName(tenantUserVo.getData().getEmplName());
                    //外协没有设备
                    if (StrUtil.isNotBlank(track.getDeviceId())) {
                        CommonResult<Device> device = baseServiceClient.getDeviceById(track.getDeviceId());
                        if (null != device.getData()) {
                            track.setDeviceName(device.getData().getName());
                        }
                    }
                    TrackItem trackItem = trackItemService.getById(track.getTiId());
                    //查询产品编号
                    TrackFlow trackFlow = trackFlowService.getById(trackItem.getFlowId());
                    track.setProdNo(trackFlow.getProductNo());
                    //查询产品名称
                    TrackHead trackHead = trackHeadService.getById(track.getTrackId());
                    track.setProductName(trackHead.getProductName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return completes;
        } catch (Exception e) {
            throw new GlobalException("信息获取异常", ResultCode.FAILED);
        }
    }

    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList, HttpServletRequest request) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        for (CompleteDto completeDto : completeDtoList) {
            if (StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
                return CommonResult.failed("质检人员不能为空");
            }
            if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
                return CommonResult.failed("报工人员不能为空");
            }
            if (StringUtils.isNullOrEmpty(completeDto.getTiId())) {
                return CommonResult.failed("工序Id不能为空");
            }
            TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
            trackItem.setPourTemperature(completeDto.getPourTemperature());
            //冶炼车间预装炉重复利用，特有字段下工序装炉，若该字段不为空则修改预装炉状态为未派工
            if (completeDto.getNextFurnace() != null && completeDto.getNextFurnace()) {
                QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
                itemQueryWrapper.eq("track_head_id", trackItem.getTrackHeadId());
                itemQueryWrapper.eq("original_opt_sequence", trackItem.getNextOptSequence());
                TrackItem nextItem = trackItemService.getOne(itemQueryWrapper);
                if (!ObjectUtil.isEmpty(nextItem)) {
                    //有下工序则修改预装炉状态为未派工
                    PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(trackItem.getPrechargeFurnaceId());
                    if (!ObjectUtil.isEmpty(prechargeFurnace)) {
                        prechargeFurnace.setAssignStatus(0);
                        prechargeFurnaceService.updateById(prechargeFurnace);
                    }
                } else {
                    //修改预装炉表状态为完工
                    if (!ObjectUtil.isEmpty(trackItem.getPrechargeFurnaceId())) {
                        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(trackItem.getPrechargeFurnaceId());
                        prechargeFurnace.setStatus(END_START_WORK);
                        prechargeFurnaceService.updateById(prechargeFurnace);
                    }
                }
            } else {
                //修改预装炉表状态为完工
                if (!ObjectUtil.isEmpty(trackItem.getPrechargeFurnaceId())) {
                    PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(trackItem.getPrechargeFurnaceId());
                    prechargeFurnace.setStatus(END_START_WORK);
                    prechargeFurnaceService.updateById(prechargeFurnace);
                }
            }
            //检验人
            trackItem.setQualityCheckBy(completeDto.getQcPersonId());
            //根据工序Id删除缓存表数据
            QueryWrapper<TrackCompleteCache> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ti_id", completeDto.getTiId());
            double number = 0.00;
            BigDecimal time = new BigDecimal(0);
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                //验证输入值是否合法
//                String s = this.verifyTrackComplete(trackComplete, trackItem, companyCode);
//                //如果返回值不等于空则代表验证不通过，将提示信息返回
//                if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
//                    return CommonResult.failed(s);
//                }

                trackComplete.setId(null);
                trackComplete.setAssignId(completeDto.getAssignId());
                trackComplete.setTiId(completeDto.getTiId());
                trackComplete.setTrackId(completeDto.getTrackId());
                trackComplete.setTrackNo(completeDto.getTrackNo());
                trackComplete.setProdNo(completeDto.getProdNo());
                trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackComplete.setCompleteTime(new Date());
                trackComplete.setDetectionResult("-");
                trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                if (!ObjectUtil.isEmpty(trackItem.getPrechargeFurnaceId())) {
                    trackComplete.setPrechargeFurnaceId(String.valueOf(trackItem.getPrechargeFurnaceId()));
                }
                number += trackComplete.getCompletedQty() == null ? 0 : trackComplete.getCompletedQty();
                time = time.add(new BigDecimal(trackComplete.getReportHours() == null ? 0.00 : trackComplete.getReportHours()));
            }
            Assign assign = trackAssignService.getById(completeDto.getAssignId());
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            //机加、装配需要判断报工数量，才去进行下工序处理
            if ("1".equals(trackHead.getClasses()) || "2".equals(trackHead.getClasses())) {
                //校验当前派工是否报工
                QueryWrapper<TrackComplete> queryWrapperComplete = new QueryWrapper<>();
                queryWrapperComplete.eq("assign_id", completeDto.getAssignId());
                int completeSize = this.count(queryWrapperComplete);
                if (completeSize > 0) {
                    throw new GlobalException("当前派工已经报工完成", ResultCode.FAILED);
                }
                //校验数量X单间额定工时 = 报工工时
                //单件工时*报工总数 != 上报总工时
                BigDecimal zjgs = new BigDecimal(trackItem.getSinglePieceHours() * number);
                if (!zjgs.setScale(2, BigDecimal.ROUND_HALF_UP).equals(time.setScale(2, BigDecimal.ROUND_HALF_UP))) {
                    throw new GlobalException("报工总数乘单件工时与上报总工时数值不匹配", ResultCode.FAILED);
                }
                //跟新工序完成数量
                trackItem.setCompleteQty(!Objects.isNull(trackItem.getCompleteQty()) ? trackItem.getCompleteQty() + number : number);
                //最后一次报工进行下工序激活
                if (queryIsComplete(assign)) {
                    //更改状态 标识当前工序完成
                    trackItem.setIsDoing(2);
                    trackItem.setIsOperationComplete(1);
                    trackItemService.updateById(trackItem);
                    trackCompleteCacheService.remove(queryWrapper);
                    //增加工序是否调度是否质检判断,不质检不调度进行下工序激活
                    if (trackItem.getIsExistQualityCheck() == 0 && trackItem.getIsExistScheduleCheck() == 0) {
                        //调用工序激活方法
                        Map<String, String> map = new HashMap<>(3);
                        map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                        map.put(IdEnum.TRACK_HEAD_ID.getMessage(), completeDto.getTrackId());
                        map.put(IdEnum.TRACK_ITEM_ID.getMessage(), completeDto.getTiId());
                        map.put(IdEnum.ASSIGN_ID.getMessage(), completeDto.getAssignId());
                        publicService.publicUpdateState(map, PublicCodeEnum.COMPLETE.getCode());
                    }
                }
                //派工状态设置为完成
                assign.setState(2);
                trackAssignService.updateById(assign);
                //外协报工最后一道工序校验所有关键件全部绑定完成
                if ("2".equals(trackHead.getClasses()) && trackItem.getNextOptSequence() == 0) {
                    //获取当前跟单下所有工序,并且根据opt_sequence倒序排序
                    QueryWrapper<TrackItem> queryWrapperItemList = new QueryWrapper<>();
                    queryWrapperItemList.eq("track_head_id", trackHead.getId());
                    queryWrapperItemList.orderByDesc("opt_sequence");
                    List<TrackItem> list = trackItemService.list(queryWrapperItemList);
                    //判断是不是最后一道工序
                    if (list.get(0).getOptSequence().equals(trackItem.getOptSequence())) {
                        //查询关键件 && 安装数量!=需要安装数量的
                        QueryWrapper<TrackAssembly> assemblyQueryWrapper = new QueryWrapper<>();
                        assemblyQueryWrapper.eq("track_head_id", trackHead.getId());
                        assemblyQueryWrapper.apply("number <> number_install");
                        assemblyQueryWrapper.eq("is_key_part", 1);
                        int count = assemblyService.count(assemblyQueryWrapper);
                        if (count > 0) {
                            throw new GlobalException("关键件为全部绑定,不允许最终完成", ResultCode.FAILED);
                        }
                    }
                }
            } else {
                //跟新工序完成数量
                trackItem.setCompleteQty(Double.parseDouble(String.valueOf(ObjectUtil.isEmpty(trackItem.getAssignableQty()) ? "0" : trackItem.getAssignableQty())));
                //更改状态 标识当前工序完成
                trackItem.setIsDoing(2);
                trackItem.setIsOperationComplete(1);
                trackItemService.updateById(trackItem);
                trackCompleteCacheService.remove(queryWrapper);
                //调用工序激活方法
                Map<String, String> map = new HashMap<>(3);
                map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                map.put(IdEnum.TRACK_HEAD_ID.getMessage(), completeDto.getTrackId());
                map.put(IdEnum.TRACK_ITEM_ID.getMessage(), completeDto.getTiId());
                map.put(IdEnum.ASSIGN_ID.getMessage(), completeDto.getAssignId());
                publicService.publicUpdateState(map, PublicCodeEnum.COMPLETE.getCode());
                //派工状态设置为完成
                assign.setState(2);
                trackAssignService.updateById(assign);

            }
            //修改预装炉派工表状态为完工
            if (!StringUtils.isNullOrEmpty(trackItem.getPrechargeFurnaceAssignId())) {
                PrechargeFurnaceAssign assignInfo = prechargeFurnaceAssignService.getById(trackItem.getPrechargeFurnaceAssignId());
                assignInfo.setIsDoing(END_START_WORK);
                assignInfo.setFinishTime(new Date());
                assignInfo.setCompleteStatus("1");
                assignInfo.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                prechargeFurnaceAssignService.updateById(assignInfo);
            }

            log.error(completeDto.getTrackCompleteList().toString());

            this.saveBatch(completeDto.getTrackCompleteList());

            //保存下料信息和锻造信息
            saveLayingOffAndForgControlRecord(completeDto);
            //删除下料和锻造缓存
            removeLayingOffAndForgControlRecordCache(completeDto);
            //保存造型/制芯工序报工信息
            saveModelingAndCore(completeDto);
            //删除造型/制芯工序报工信息缓存
            removeModelingAndCoreCache(completeDto);
            //保存扣箱工序报工信息
            saveKnockout(completeDto);
            //删除扣箱工序报工信息缓存
            removeKnockoutCache(completeDto);
            //保存原材料消耗信息
            saveRawMaterialRecord(completeDto);
            //删除原材料消耗信息缓存
            removeRawMaterialRecord(completeDto);

            //记录报工操作
            actionService.saveAction(ActionUtil.buildAction(
                    trackItem.getBranchCode(), "4", "2", "跟单报工，跟单号：" + completeDto.getTrackNo(),
                    OperationLogAspect.getIpAddress(request)));
        }
        return CommonResult.success(true);
    }

    private void removeRawMaterialRecord(CompleteDto completeDto) {
        QueryWrapper<RawMaterialRecordCache> queryWrapperRawMaterialRecordCache = new QueryWrapper<>();
        queryWrapperRawMaterialRecordCache.eq("item_id", completeDto.getTiId());
        rawMaterialRecordCacheService.remove(queryWrapperRawMaterialRecordCache);
    }

    private void removeKnockoutCache(CompleteDto completeDto) {
        QueryWrapper<KnockoutCache> queryWrapperKnockoutCache = new QueryWrapper<>();
        queryWrapperKnockoutCache.eq("item_id", completeDto.getTiId());
        knockoutCacheService.remove(queryWrapperKnockoutCache);
    }

    private void removeModelingAndCoreCache(CompleteDto completeDto) {
        QueryWrapper<ModelingCoreCache> queryWrapperModelingCore = new QueryWrapper<>();
        queryWrapperModelingCore.eq("item_id", completeDto.getTiId());
        modelingCoreCacheService.remove(queryWrapperModelingCore);
    }

    private void removeLayingOffAndForgControlRecordCache(CompleteDto completeDto) {
        //删除下料缓存
        QueryWrapper<LayingOffCache> queryWrapperLayingOffCache = new QueryWrapper<>();
        queryWrapperLayingOffCache.eq("item_id", completeDto.getTiId());
        layingOffCacheService.remove(queryWrapperLayingOffCache);
        //删除锻造缓存
        QueryWrapper<ForgControlRecordCache> queryWrapperForgControlRecordCache = new QueryWrapper<>();
        queryWrapperForgControlRecordCache.eq("item_id", completeDto.getTiId());
        forgControlRecordCacheService.remove(queryWrapperForgControlRecordCache);
    }

    void saveKnockout(CompleteDto completeDto) {
        if (!ObjectUtil.isEmpty(completeDto.getKnockout())) {
            //先删除该已保存过的
            QueryWrapper<Knockout> queryWrapperKnockout = new QueryWrapper<>();
            queryWrapperKnockout.eq("item_id", completeDto.getTiId());
            knockoutService.remove(queryWrapperKnockout);
            completeDto.getKnockout().setItemId(completeDto.getTiId());
            knockoutService.saveOrUpdate(completeDto.getKnockout());
        }
    }

    void saveModelingAndCore(CompleteDto completeDto) {
        if (!ObjectUtil.isEmpty(completeDto.getModelingCore())) {
            //先删除该已保存过的
            QueryWrapper<ModelingCore> queryWrapperModelingCore = new QueryWrapper<>();
            queryWrapperModelingCore.eq("item_id", completeDto.getTiId());
            modelingCoreService.remove(queryWrapperModelingCore);
            completeDto.getModelingCore().setItemId(completeDto.getTiId());
            modelingCoreService.saveOrUpdate(completeDto.getModelingCore());
        }
    }

    void saveRawMaterialRecord(CompleteDto completeDto) {
        if (!CollectionUtils.isEmpty(completeDto.getRawMaterialRecordList())) {
            //现根据item_id删除原有记录
            QueryWrapper<RawMaterialRecord> queryWrapperRawMaterialRecord = new QueryWrapper<>();
            queryWrapperRawMaterialRecord.eq("item_id", completeDto.getTiId());
            rawMaterialRecordService.remove(queryWrapperRawMaterialRecord);
            for (RawMaterialRecord rawMaterialRecord : completeDto.getRawMaterialRecordList()) {
                rawMaterialRecord.setItemId(completeDto.getTiId());
            }
            rawMaterialRecordService.saveBatch(completeDto.getRawMaterialRecordList());
        }
    }

    void saveLayingOffAndForgControlRecord(CompleteDto completeDto) {
        //记录下料信息
        if (!ObjectUtil.isEmpty(completeDto.getLayingOff())) {
            //先删除该已保存过的
            QueryWrapper<LayingOff> queryWrapperLayingOff = new QueryWrapper<>();
            queryWrapperLayingOff.eq("item_id", completeDto.getTiId());
            layingOffService.remove(queryWrapperLayingOff);
            completeDto.getLayingOff().setItemId(completeDto.getTiId());
            layingOffService.saveOrUpdate(completeDto.getLayingOff());
        }
        //记录锻造信息
        if (!CollectionUtils.isEmpty(completeDto.getForgControlRecordList())) {
            //现根据item_id删除原有记录
            QueryWrapper<ForgControlRecord> queryWrapperForgControlRecord = new QueryWrapper<>();
            queryWrapperForgControlRecord.eq("item_id", completeDto.getTiId());
            forgControlRecordService.remove(queryWrapperForgControlRecord);
            ForgControlRecord forgControlRecordBarInfo = new ForgControlRecord();
            forgControlRecordBarInfo.setType("2");
            forgControlRecordBarInfo.setBarForge(completeDto.getBarForge());
            forgControlRecordBarInfo.setItemId(completeDto.getTiId());
            ForgControlRecord forgControlRecordRemark = new ForgControlRecord();
            forgControlRecordRemark.setType("3");
            forgControlRecordRemark.setRemark(completeDto.getForgeRemark());
            forgControlRecordRemark.setItemId(completeDto.getTiId());
            for (ForgControlRecord forgControlRecord : completeDto.getForgControlRecordList()) {
                forgControlRecord.setType("1");
                forgControlRecord.setItemId(completeDto.getTiId());
            }
            completeDto.getForgControlRecordList().add(forgControlRecordRemark);
            completeDto.getForgControlRecordList().add(forgControlRecordBarInfo);
            forgControlRecordService.saveOrUpdateBatch(completeDto.getForgControlRecordList());
        }
    }

    /**
     * 功能描述: 判断当前报工是否是最后一次报工
     *
     * @param assign
     * @Author: xinYu.hou
     * @Date: 2022/10/10 16:01
     * @return: boolean
     **/
    private boolean queryIsComplete(Assign assign) {
        TrackItem trackItem = trackItemService.getById(assign.getTiId());
        //可派工数量不为0的时候 不进行判断
        if (trackItem.getAssignableQty() != 0) {
            return false;
        }
        QueryWrapper<Assign> query = new QueryWrapper<>();
        query.eq("ti_id", assign.getTiId());
        //state = 2 (已完工)
        query.notIn("state", 2);
        List<Assign> assignList = trackAssignService.list(query);
        //未完工数据超过1条返回false
        if (assignList.size() > 1) {
            return false;
        }
        //最后一道未报工的ID跟传入ID对比
        return assignList.get(0).getId().equals(assign.getId());
    }


    @Override
    public CommonResult<QueryWorkingTimeVo> queryDetails(String assignId, String tiId, Integer state, String classes) {
        if (StringUtils.isNullOrEmpty(tiId)) {
            return CommonResult.failed("工序Id不能为空");
        }
        if (StringUtils.isNullOrEmpty(assignId)) {
            return CommonResult.failed("派工Id不能为空");
        }
        QueryWorkingTimeVo queryWorkingTimeVo = new QueryWorkingTimeVo();
        Assign assign = trackAssignMapper.queryAssign(assignId);
        //给assignPersion赋值
        setAssignPersion(assignId, assign);

        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", tiId);

        List<TrackComplete> completeList = new ArrayList<>();
        List<ForgControlRecord> forgControlRecordList = new ArrayList<>();
        List<RawMaterialRecord> rawMaterialRecordList = new ArrayList<>();
        LayingOff layingOff = new LayingOff();
        ModelingCore modelingCore = new ModelingCore();
        Knockout knockout = new Knockout();
        TrackItem trackItem = trackItemService.getById(tiId);
        //state=0时从缓存取数据显示
        if (0 == state) {
            completeList = trackCompleteMapper.queryCompleteCache(queryWrapper);
            forgControlRecordList = forgControlRecordService.queryForgControlRecordCacheByItemId(tiId);
            forgControlRecordList = buildForgControlRecords(queryWorkingTimeVo, forgControlRecordList);
            layingOff = layingOffService.queryLayingOffCacheByItemId(tiId);
            rawMaterialRecordList = rawMaterialRecordService.queryrawMaterialRecordCacheByItemId(tiId);
            modelingCore = modelingCoreService.queryCacheByItemId(tiId);
            knockout = knockoutService.queryCacheByItemId(tiId);
            if (!CollectionUtils.isEmpty(completeList)) {
                queryWorkingTimeVo.setPourTemperature(completeList.get(0).getPourTemperature());
            }

        } else {
            completeList = this.list(queryWrapper);
            QueryWrapper<ForgControlRecord> forgControlRecordQueryWrapper = new QueryWrapper<>();
            forgControlRecordQueryWrapper.eq("item_id", tiId);
            forgControlRecordList = forgControlRecordService.list(forgControlRecordQueryWrapper);
            forgControlRecordList = buildForgControlRecords(queryWorkingTimeVo, forgControlRecordList);
            QueryWrapper<LayingOff> layingOffQueryWrapper = new QueryWrapper<>();
            layingOffQueryWrapper.eq("item_id", tiId);
            layingOff = layingOffService.getOne(layingOffQueryWrapper);
            QueryWrapper<RawMaterialRecord> rawMaterialRecordQueryWrapper = new QueryWrapper<>();
            rawMaterialRecordQueryWrapper.eq("item_id", tiId);
            rawMaterialRecordList = rawMaterialRecordService.list(rawMaterialRecordQueryWrapper);
            QueryWrapper<Knockout> knockoutQueryWrapper = new QueryWrapper<>();
            knockoutQueryWrapper.eq("item_id", tiId);
            knockout = knockoutService.getOne(knockoutQueryWrapper);
            QueryWrapper<ModelingCore> modelingCoreQueryWrapper = new QueryWrapper<>();
            modelingCoreQueryWrapper.eq("item_id", tiId);
            modelingCore = modelingCoreService.getOne(modelingCoreQueryWrapper);
            queryWorkingTimeVo.setPourTemperature(trackItem.getPourTemperature());
        }

        RecordsOfSteelmakingOperations recordsOfSteelmakingOperations = new RecordsOfSteelmakingOperations();
        RecordsOfPourOperations recordsOfPourOperations = new RecordsOfPourOperations();
        //该工序如果走的预装炉，根据预装炉id找对应的炼钢记录和浇注记录信息
        if (trackItem.getPrechargeFurnaceId() != null) {
            //根据预装炉id找炼钢记录
            QueryWrapper<RecordsOfSteelmakingOperations> recordsOfSteelmakingOperationsQueryWrapper = new QueryWrapper<>();
            recordsOfSteelmakingOperationsQueryWrapper.eq("precharge_furnace_id", trackItem.getPrechargeFurnaceId());
            recordsOfSteelmakingOperations = recordsOfSteelmakingOperationsService.getOne(recordsOfSteelmakingOperationsQueryWrapper);
            //根据预装炉id找浇注记录
            QueryWrapper<RecordsOfPourOperations> recordsOfPourOperationsQueryWrapper = new QueryWrapper<>();
            recordsOfPourOperationsQueryWrapper.eq("precharge_furnace_id", trackItem.getPrechargeFurnaceId());
            recordsOfPourOperations = recordsOfPourOperationsService.getOne(recordsOfPourOperationsQueryWrapper);
        }
        queryWorkingTimeVo.setTrackCompleteList(completeList);
        queryWorkingTimeVo.setAssign(assign);
        queryWorkingTimeVo.setQcPersonId(trackItem.getQualityCheckBy());
        queryWorkingTimeVo.setQualityCheckBranch(trackItem.getQualityCheckBranch());
        queryWorkingTimeVo.setLayingOff(layingOff);
        queryWorkingTimeVo.setForgControlRecordList(forgControlRecordList);
        queryWorkingTimeVo.setRawMaterialRecordList(rawMaterialRecordList);
        queryWorkingTimeVo.setKnockout(knockout);
        queryWorkingTimeVo.setModelingCore(modelingCore);
        queryWorkingTimeVo.setRecordsOfPourOperations(recordsOfPourOperations);
        queryWorkingTimeVo.setRecordsOfSteelmakingOperations(recordsOfSteelmakingOperations);
        return CommonResult.success(queryWorkingTimeVo);
    }

    /**
     * @param state
     * @return
     */
    @Override
    public CommonResult<QueryWorkingTimeVo> queryDetailsHot(Integer state, String furnaceId, String classes) {
        //查出炉内所有工序
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("precharge_furnace_id", furnaceId);
        //查出装炉内的跟单工序
        List<TrackItem> trackItems = trackItemMapper.selectList(itemQueryWrapper);
        if (!CollectionUtils.isEmpty(trackItems)) {
            List<String> itemsIds = trackItems.stream().map(x -> x.getId()).collect(Collectors.toList());
            //根据跟单工序id查出对应的派工数据
            QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
            assignQueryWrapper.in("ti_id", itemsIds);
            List<Assign> assigns = trackAssignMapper.query(assignQueryWrapper);
            Map<String, Assign> assignMap = assigns.stream().collect(Collectors.toMap(x -> x.getTiId(), x -> x));
            List<QueryWorkingTimeVo> list = new ArrayList<>();
            //取出其中一条的派工id和工序id查询报工详情信息
            return this.queryDetails(assignMap.get(itemsIds.get(0)).getId(), itemsIds.get(0), state, classes);
        } else {
            return CommonResult.failed("装炉内没有工序");
        }
    }

    private List<ForgControlRecord> buildForgControlRecords(QueryWorkingTimeVo queryWorkingTimeVo, List<ForgControlRecord> forgControlRecordList) {
        List<ForgControlRecord> barForgeInfo = forgControlRecordList.stream().filter(x -> "2".equals(x.getType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(barForgeInfo)) {
            queryWorkingTimeVo.setBarForge(barForgeInfo.get(0).getBarForge());
        }
        List<ForgControlRecord> remarkInfo = forgControlRecordList.stream().filter(x -> "3".equals(x.getType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(remarkInfo)) {
            queryWorkingTimeVo.setForgeRemark(remarkInfo.get(0).getRemark());
        }
        forgControlRecordList = forgControlRecordList.stream().filter(x -> "1".equals(x.getType())).collect(Collectors.toList());
        return forgControlRecordList;
    }


    //查询初始报工人员列表
    private void setAssignPersion(String assignId, Assign assign) {
        //‘/’全部派工人员查询
        if (assign.getUserId().contains("/")) {
            List<String> branchCodes = Arrays.asList(assign.getSiteId().split(","));
            List<TenantUserVo> data = systemServiceClient.queryUserByBranchCodes(branchCodes).getData();
            List<AssignPerson> assignPeople = new ArrayList<>();
            for (TenantUserVo datum : data) {
                AssignPerson assignPerson = new AssignPerson();
                assignPerson.setAssignId(assignId);
                assignPerson.setUserId(datum.getUserAccount());
                assignPerson.setUserName(datum.getEmplName());
                assignPerson.setRatioHours(datum.getRatioHours());
                assignPeople.add(assignPerson);
            }
            assign.setAssignPersons(assignPeople);
        } else {
            List<AssignPerson> assignPersons = trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assign.getId()));
            List<AssignPerson> assignPeople = new ArrayList<>();
            if (assignPersons.size() > 0) {
                List<String> userAccounts = assignPersons.stream().map(item -> item.getUserId()).collect(Collectors.toList());
                List<String> strings = new ArrayList<>();
                for (String userAccount : userAccounts) {
                    strings.addAll(Arrays.asList(userAccount.split(",")));
                }
                Map<String, TenantUserVo> userInfoMap = systemServiceClient.queryByUserAccountList(strings);
                for (TenantUserVo value : userInfoMap.values()) {
                    AssignPerson assignPerson = new AssignPerson();
                    assignPerson.setAssignId(assignId);
                    assignPerson.setUserId(value.getUserAccount());
                    assignPerson.setUserName(value.getEmplName());
                    assignPerson.setRatioHours(value.getRatioHours());
                    assignPeople.add(assignPerson);
                }
            }
            assign.setAssignPersons(assignPeople);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> updateComplete(CompleteDto completeDto) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        if (StringUtils.isNullOrEmpty(completeDto.getTiId())) {
            return CommonResult.failed("工序Id不能为空");
        }
        if (StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
            return CommonResult.failed("质检人员不能为空");
        }
        if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
            return CommonResult.failed("报工人员不能为空");
        }
        TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
        //检验人
        trackItem.setQualityCheckBy(completeDto.getQcPersonId());
        //根据工序Id先删除,在重新新增数据
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", completeDto.getTiId());
        this.remove(removeComplete);
        double numDouble = 0.00;
        Assign assign = trackAssignService.getById(completeDto.getAssignId());
        double intervalNumber = assign.getQty() + 0.0;
        for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
            //验证输入值是否合法
            String s = trackCompleteService.verifyTrackComplete(trackComplete, trackItem, companyCode);
            //如果返回值不等于空则代表验证不通过，将提示信息返回
            if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
                return CommonResult.failed(s);
            }
            trackComplete.setId(null);
            trackComplete.setAssignId(completeDto.getAssignId());
            trackComplete.setTiId(completeDto.getTiId());
            trackComplete.setTrackId(completeDto.getTrackId());
            trackComplete.setTrackNo(completeDto.getTrackNo());
            trackComplete.setProdNo(completeDto.getProdNo());
            trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            trackComplete.setCompleteTime(new Date());
            trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            numDouble += trackComplete.getCompletedQty() == null ? 0 : trackComplete.getCompletedQty();
        }
        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
        //机加、装配需要判断报工数量
        if ("1".equals(trackHead.getClasses()) || "2".equals(trackHead.getClasses())) {
            //报工数量判断
            if (numDouble > assign.getQty()) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得大于" + assign.getQty());
            }
            if (numDouble < intervalNumber - 0.01) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得少于" + +(intervalNumber - 0.01));
            }
            //跟新工序完成数量
            trackItem.setCompleteQty(trackItem.getCompleteQty() + numDouble);
        }
//        log.error(completeDto.getTrackCompleteList().toString());
        trackItemService.updateById(trackItem);
        //修改工序浇注温度
        if (completeDto.getPourTemperature() != null && completeDto.getPrechargeFurnaceAssignId() != null) {
            UpdateWrapper<TrackItem> itemUpdateWrapper = new UpdateWrapper<>();
            itemUpdateWrapper.eq("precharge_furnace_assign_id", completeDto.getPrechargeFurnaceAssignId()).set("pour_temperature", completeDto.getPourTemperature());
            trackItemService.update(itemUpdateWrapper);
        }
        //保存下料信息和锻造信息
        saveLayingOffAndForgControlRecord(completeDto);
        //保存原材料消耗信息
        saveRawMaterialRecord(completeDto);
        //保存打箱工序信息
        saveKnockout(completeDto);
        //保存造型/制芯工序报工信息
        saveModelingAndCore(completeDto);

        return CommonResult.success(this.saveBatch(completeDto.getTrackCompleteList()));
    }

    /**
     * @modifiedby mafeng02 2002-07-28 ,修改没有派工的外协和探伤下的处理
     * @Description 报工回滚
     */
    @Override
    public CommonResult<Boolean> rollBack(String id) {
        String msg = "";
        TrackComplete trackComplete = this.getById(id);
        Assign assign = new Assign();
        if (null != trackComplete.getAssignId()) {
            assign = trackAssignService.getById(trackComplete.getAssignId());
        }
        TrackItem trackItem = new TrackItem();
        if (null == assign) {
            trackItem = trackItemService.getById(trackComplete.getTiId());
        } else {
            trackItem = trackItemService.getById(assign.getTiId());
        }
        if (null == trackItem) {
            removeComplete(trackComplete.getTiId());
        } else {
            //若该工序开了合格证进行车间扭转且下车间状态为未开工，移除下车间跟单信息
            removeNextBranchInfo(trackItem);
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            if (!StringUtils.isNullOrEmpty(id)) {
                queryWrapper.eq("track_id", id);
            } else {
                queryWrapper.eq("track_id", "-1");
            }
            queryWrapper.orderByAsc("modify_time");
            List<TrackComplete> cs = trackCompleteService.list(queryWrapper);
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                msg += "跟单号已质检完成，报工无法取消！";
            }
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                msg += "跟单号已调度完成，报工无法取消！";
            }
            //判断后置工序是否已派工，否则不可回滚
            QueryWrapper<Assign> queryWrapperAssign = new QueryWrapper<Assign>();
            queryWrapperAssign.eq("track_id", trackItem.getTrackHeadId());
            List<Assign> assigns = trackAssignService.list(queryWrapperAssign);
            for (int j = 0; j < assigns.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(assigns.get(j).getTiId());
                if (cstrackItem.getFlowId().equals(trackItem.getFlowId()) && cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已派工，需要先取消后序工序");
                }
            }
            //判断后置工序是否已报工，否则不可回滚
            for (int j = 0; j < cs.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已报工，需要先取消后序工序");
                }
            }

            //将后置工序IS_CURRENT设置为否，状态为1
            List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("flow_id", trackItem.getFlowId()).orderByAsc("opt_sequence"));
            for (TrackItem trackItems : items) {
                if (trackItems.getOptSequence() > trackItem.getOptSequence() && trackItems.getIsCurrent() == 1) {
                    trackItems.setIsCurrent(0);
                    trackItems.setIsDoing(0);
                    trackItems.setIsFinalComplete("0");
                    trackItemService.updateById(trackItems);
                }
            }
            //将当前工序设置为激活
            if ("".equals(msg)) {
                UpdateWrapper<TrackItem> itemUpdateWrapper = new UpdateWrapper<>();
                itemUpdateWrapper.eq("id", trackItem.getId())
                        .set("is_doing", 0)
                        .set("is_current", 1)
                        .set("is_final_complete", "0")
                        .set("is_operation_complete", "0")
                        .set("precharge_furnace_id", null)
                        .set("final_complete_time", null);
                trackItemService.update(itemUpdateWrapper);
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackHead.setStatus("1");
                trackHeadService.updateById(trackHead);
                if (null != assign) {
                    assign.setAvailQty(assign.getQty());
                    assign.setState(0);
                    trackAssignService.updateById(assign);
                }
                removeComplete(trackComplete.getTiId());
            }
        }


        if ("".equals(msg)) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！" + msg);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void removeNextBranchInfo(TrackItem trackItem) {
        QueryWrapper<TrackCertificate> certificateQueryWrapper = new QueryWrapper<>();
        certificateQueryWrapper.eq("ti_id", trackItem.getId());
        TrackCertificate trackCertificate = trackCertificateService.getOne(certificateQueryWrapper);
        if (trackCertificate != null && trackCertificate.getNextThId() != null) {
            TrackHead trackHead = trackHeadService.getById(trackCertificate.getNextThId());
            if (trackHead != null && "0".equals(trackHead.getStatus())) {
                QueryWrapper<TrackFlow> flowQueryWrapper = new QueryWrapper<>();
                flowQueryWrapper.eq("track_head_id", trackHead.getId());
                trackFlowService.remove(flowQueryWrapper);

                QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
                itemQueryWrapper.eq("track_head_id", trackHead.getId());
                trackItemService.remove(itemQueryWrapper);

                trackHeadService.removeById(trackHead.getId());
            } else if (trackHead != null) {
                throw new GlobalException("扭转车间工序状态已变更，无法回滚！", ResultCode.FAILED);
            }
        }
    }

    @Override
    public String verifyTrackComplete(TrackComplete trackComplete, TrackItem trackItem, String companyCode) {
        StringBuffer massage = new StringBuffer();
        //根据数据字段配置，判断走那一套验证逻辑，宝石与北石字段不同
        if (Tenant.COMPANYCODE_BEISHI.equals(companyCode)) {
            //北石验证
            //验证实用固定机时是否填写正常,固定机时不能大于准结工时
            if (trackComplete.getActualFixHours() > trackItem.getPrepareEndHours()) {
                return massage.append("固定机时不能大于准结工时").toString();
            }
            //验证实用变动机时（正常班），实用变动机时（正常班）不能大于准结工时+额定工时
            if (trackComplete.getActualNomalHours() > trackItem.getPrepareEndHours() + trackItem.getSinglePieceHours()) {
                return massage.append("实用变动机时（正常班）不能大于准结工时+额定工时").toString();
            }
            //验证实用变动机时（加班），实用变动机时（加班）不能大于准结工时+额定工时
            if (trackComplete.getActualOverHours() > trackItem.getPrepareEndHours() + trackItem.getSinglePieceHours()) {
                return massage.append("实用变动机时（加班）不能大于准结工时+额定工时").toString();
            }
            //验证完成固定机时,完成固定机时不能大于准结工时
            if (trackComplete.getCompletedFixHours() > trackItem.getPrepareEndHours()) {
                return massage.append("完成固定机时不能大于准结工时").toString();
            }
            //验证完成变动机时，验证完成变动机时不能大于准结工时+额定工时
            if (trackComplete.getCompletedChangeHours() > trackItem.getPrepareEndHours() + trackItem.getSinglePieceHours()) {
                return massage.append("完成变动机时不能大于准结工时+额定工时").toString();
            }
        } else if (Tenant.COMPANYCODE_BAOSHI.equals(companyCode)) {
            //宝石验证
            if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                return massage.append("报工工时不能大于额定工时").toString();
            }
        }
        return massage.toString();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> saveOutsource(OutsourceCompleteDto outsource) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("track_head_id", outsource.getTrackHeadId());
        queryWrapper.in("product_no", outsource.getProdNoList());
        queryWrapper.eq("branch_code", outsource.getBranchCode());
        //产品对应的全部工序
        List<TrackItem> list = trackItemService.list(queryWrapper);
        List<TrackItem> result = new ArrayList<>();
        //获取正确的工序
        //此次报工对应的产品工序
        for (OutsourceDto outsourceDto : outsource.getOutsourceDtoList()) {
            List<TrackItem> collect = list.stream().filter(trackItem ->
                    StrUtil.isNotBlank(trackItem.getOptNo()) && StrUtil.isNotBlank(trackItem.getOptName()) && trackItem.getOptNo().equals(outsourceDto.getOptNo()) && OptNameUtil.optName(trackItem.getOptName()).equals(OptNameUtil.optName(outsourceDto.getOptName())) && trackItem.getIsCurrent() == 1
            ).collect(Collectors.toList());
            result.addAll(collect);
        }
        //获取对应的flow
        List<String> collect = result.stream().map(TrackItem::getFlowId).distinct().collect(Collectors.toList());
        //修改flow状态
        UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
        update.in("id", collect);
        update.set("status", "1");
        trackFlowService.update(update);
        //修改跟单状态
        UpdateWrapper<TrackHead> headUpdateWrapper = new UpdateWrapper<>();
        headUpdateWrapper.eq("id", list.get(0).getTrackHeadId());
        headUpdateWrapper.set("status", "1");
        trackHeadService.update(headUpdateWrapper);
        //检查是否跳工序报工
        this.checkOP(list, result);
        boolean bool = true;
        for (TrackItem trackItem : result) {
            TrackComplete trackComplete = new TrackComplete();
            BeanUtils.copyProperties(outsource.getTrackComplete(), trackComplete);
            if (StringUtils.isNullOrEmpty(trackItem.getStartDoingUser())) {
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(outsource.getTrackComplete().getUserId());
            }
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            trackItem.setCompleteQty(Double.valueOf(trackItem.getNumber()));
            trackItem.setAssignableQty(0);
            trackComplete.setTiId(trackItem.getId());
            trackComplete.setTrackId(trackItem.getTrackHeadId());
            trackComplete.setProdNo(trackItem.getProductNo());
            trackComplete.setAssignId("");
            trackComplete.setModifyTime(new Date());
            trackComplete.setCreateTime(new Date());
            trackComplete.setCompleteBy(outsource.getTrackComplete().getUserId());
            trackComplete.setCompleteTime(new Date());
            trackComplete.setUserId(SecurityUtils.getCurrentUser().getUsername());
            CommonResult<TenantUserVo> userVoCommonResult = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId());
            trackComplete.setUserName(userVoCommonResult.getData().getEmplName());
            trackComplete.setBranchCode(outsource.getBranchCode());
            trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            trackComplete.setCompletedQty(Double.valueOf(trackItem.getNumber()));
            trackComplete.setTrackNo(trackHead.getId());

            trackItem.setOperationCompleteTime(new Date());
            trackItem.setIsOperationComplete(1);
            trackItem.setIsDoing(2);
            trackItem.setQualityCheckBy(trackComplete.getQualityCheckBy());
            trackItem.setQualityCheckBranch(trackComplete.getQualityCheckBranch());
            bool = trackCompleteService.save(trackComplete);
            //判断是否需要质检和调度审核 再激活下工序
            boolean next = trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0);
            if (next) {
                trackItem.setIsFinalComplete("1");
                trackItem.setFinalCompleteTime(new Date());
            }
            trackItemService.updateById(trackItem);
            if (next) {
                Map<String, String> map = new HashMap<String, String>(1);
                map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                publicService.activationProcess(map);
            }
        }
        if (bool) {
            return CommonResult.success(bool, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }

    @Override
    public CommonResult<Boolean> saveOutsourceNew(OutsourceCompleteDto outsource) {
        //获取所有跟单工序
        QueryWrapper<TrackItem> queryWrapperAll = new QueryWrapper<>();
        queryWrapperAll.in("track_head_id", outsource.getTrackHeadId());
        queryWrapperAll.eq("branch_code", outsource.getBranchCode());
        //产品对应的全部工序
        List<TrackItem> list = trackItemService.list(queryWrapperAll);
        List<TrackItem> result = new ArrayList<>();
        //获取正确的工序
        //此次报工对应的产品工序
        for (OutsourceDto outsourceDto : outsource.getOutsourceDtoList()) {
            List<TrackItem> trackItemList = list.stream().filter(trackItem ->
                    StrUtil.isNotBlank(trackItem.getOptNo()) && StrUtil.isNotBlank(trackItem.getOptName()) && trackItem.getOptNo().equals(outsourceDto.getOptNo()) && OptNameUtil.optName(trackItem.getOptName()).equals(OptNameUtil.optName(outsourceDto.getOptName()))
            ).collect(Collectors.toList());
            result.addAll(trackItemList);
        }
        //工序连续性判断
        boolean nextJudge = nextOptSequenceJudge(result);
        if (nextJudge) {
            //先判断是不是最小工序报工
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
            queryWrapper.in("track_head_id", outsource.getTrackHeadId())
                    .eq("opt_type", "3")
                    .eq("is_operation_complete", 0)
                    .eq("is_current", 1)
                    .orderByDesc("next_opt_sequence");
            List<TrackItem> trackItems = trackItemService.list(queryWrapper);
            //最小值
            int min = trackItems.stream().mapToInt(TrackItem::getOptSequence).min().getAsInt();
            //查询需要报工数据有最小当前工序
            List<TrackItem> collect = result.stream().filter(item -> item.getOptSequence() == min).collect(Collectors.toList());
            //有大于最小值的不是最小工序报工,需要进行连续工序判断,和所有产品同时报工判断
            if (!collect.isEmpty()) {
                //先判断报工的是所有产品吗
                QueryWrapper<TrackFlow> flowQueryWrapper = new QueryWrapper<>();
                flowQueryWrapper.in("track_head_id", outsource.getTrackHeadId());
                //获取产品数量
                int count = trackFlowService.count(flowQueryWrapper);
                //总产品数大于报工产品数,不是只能报工所在产品的当前工序
                if (count > outsource.getProdNoList().size()) {
                    //过滤出 传入产品的当前工序
                    result = result.stream().filter(item -> item.getIsCurrent() == 1 && outsource.getProdNoList().contains(item.getProductNo())).collect(Collectors.toList());
                }
            } else {
//                //过滤掉已报工的数据&&过滤不在传入产品编号的数据
//                result = result.stream().filter(item -> item.getIsOperationComplete() == 0 && outsource.getProdNoList().contains(item.getProductNo())).collect(Collectors.toList());
                //没有最小当前工序 表示跳工序执行 抛出错误
                throw new GlobalException("未选中最小当前工序,请按照工序顺序选择工序", ResultCode.FAILED);
            }
        } else {
            //单间并行工序全都是当前工序会出现跳工序执行问题,过滤其中最小工序 仅对最小工序执行
            int min = result.stream().mapToInt(TrackItem::getOptSequence).min().getAsInt();
            result = result.stream().filter(item -> item.getIsOperationComplete() == 0 && item.getOptSequence() == min && item.getIsCurrent() == 1 && outsource.getProdNoList().contains(item.getProductNo())).collect(Collectors.toList());
        }
        //获取对应的flow
        List<String> collectFlow = result.stream().map(TrackItem::getFlowId).distinct().collect(Collectors.toList());
        //修改flow状态
        UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
        update.in("id", collectFlow);
        update.set("status", "1");
        trackFlowService.update(update);
        //修改跟单状态
        UpdateWrapper<TrackHead> headUpdateWrapper = new UpdateWrapper<>();
        headUpdateWrapper.eq("id", list.get(0).getTrackHeadId());
        headUpdateWrapper.set("status", "1");
        trackHeadService.update(headUpdateWrapper);
        //过滤需要调度或者质检的工序并从小到大排序
        boolean bool = true;
        for (TrackItem trackItem : result) {
            if (trackItem.getIsOperationComplete() == 1) {
                throw new GlobalException("工序:" + trackItem.getOptName() + "已报工,请刷新页面", ResultCode.FAILED);
            }
            TrackComplete trackComplete = new TrackComplete();
            BeanUtils.copyProperties(outsource.getTrackComplete(), trackComplete);
            if (StringUtils.isNullOrEmpty(trackItem.getStartDoingUser())) {
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(SecurityUtils.getCurrentUser().getUsername());
            }
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            trackItem.setCompleteQty(Double.valueOf(trackItem.getNumber()));
            trackItem.setAssignableQty(0);
            trackComplete.setTiId(trackItem.getId());
            trackComplete.setTrackId(trackItem.getTrackHeadId());
            trackComplete.setProdNo(trackItem.getProductNo());
            trackComplete.setAssignId("");
            trackComplete.setModifyTime(new Date());
            trackComplete.setCreateTime(new Date());
            trackComplete.setCompleteBy(outsource.getTrackComplete().getUserId());
            trackComplete.setCompleteTime(new Date());
            trackComplete.setUserId(SecurityUtils.getCurrentUser().getUsername());
            CommonResult<TenantUserVo> userVoCommonResult = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId());
            trackComplete.setUserName(userVoCommonResult.getData().getEmplName());
            trackComplete.setBranchCode(outsource.getBranchCode());
            trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            trackComplete.setCompletedQty(Double.valueOf(trackItem.getNumber()));
            trackComplete.setTrackNo(trackHead.getTrackNo());

            trackItem.setOperationCompleteTime(new Date());
            trackItem.setIsOperationComplete(1);
            trackItem.setIsDoing(2);
            trackItem.setQualityCheckBy(trackComplete.getQualityCheckBy());
            trackItem.setQualityCheckBranch(trackComplete.getQualityCheckBranch());
            bool = trackCompleteService.save(trackComplete);
            //所有工序的店庆工序修改成0
            trackItem.setIsCurrent(0);
            //判断是否需要质检和调度审核 再激活下工序
            boolean next = trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0);
            if (next) {
                trackItem.setIsFinalComplete("1");
                trackItem.setFinalCompleteTime(new Date());
            }
            //最后一道工序修改当前工序转台为1
            if (trackItem.getOptSequence().equals(result.get(result.size() - 1).getOptSequence())) {
                trackItem.setIsCurrent(1);
            }
            trackItemService.updateById(trackItem);
            //最后一道工序才能进行下工序激活
            if (trackItem.getOptSequence().equals(result.get(result.size() - 1).getOptSequence())) {
                if (next) {
                    Map<String, String> map = new HashMap<String, String>(1);
                    map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                    publicService.activationProcess(map);
                }
            }
        }
        if (bool) {
            return CommonResult.success(bool, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }


    private boolean nextOptSequenceJudge(List<TrackItem> result) {
        //分组并排序 自认顺序 从小到大
        Map<String, List<TrackItem>> map = result.stream().sorted(Comparator.comparing(TrackItem::getOptSequence)).collect(Collectors.groupingBy(TrackItem::getFlowId));
        //校验是否连续工序
        boolean optNext = false;
        for (List<TrackItem> trackItem : map.values()) {
            //默认赋值第一道工序参数,从第二道工序进行判断
            int optSequence = trackItem.get(0).getOptSequence();
            int next = trackItem.get(0).getNextOptSequence();
            Integer isParallel = trackItem.get(0).getOptParallelType();
            for (TrackItem item : trackItem) {
                //不判断第一道工序
                if (!trackItem.get(0).getId().equals(item.getId())) {
                    boolean isParallelSequence = optSequence == item.getOptSequence() - 1;
                    //上一道工序 和 当前工序并行,并且上到工序optSequence = 当前工序optSequence-1
                    if (isParallel == 1 && item.getOptParallelType() == 1 && isParallelSequence) {
                        //赋值当前工序参数 下次循环在进行判断
                        next = item.getNextOptSequence();
                        isParallel = item.getOptParallelType();
                        optSequence = item.getOptSequence();
                        //赋值连续工序
                        optNext = true;
                        continue;
                    }
                    //不是连续并行工序
                    if (item.getOriginalOptSequence() == next && isParallelSequence) {
                        //赋值当前工序参数 下次循环在进行判断
                        next = item.getNextOptSequence();
                        isParallel = item.getOptParallelType();
                        optSequence = item.getOptSequence();
                        //赋值连续工序
                        optNext = true;
                    }
                }
            }
        }
        return optNext;
    }

    /**
     * 检查是否跳工序报工
     *
     * @param list
     * @param result
     */
    private void checkOP(List<TrackItem> list, List<TrackItem> result) {
        //产品完整工序根据跟单分组
        Map<String, List<TrackItem>> groupByTrackHeadId = list.stream().collect(Collectors.groupingBy(TrackItem::getTrackHeadId));
        Map<String, Map<String, List<TrackItem>>> map1 = new HashMap<>();
        groupByTrackHeadId.forEach((x, y) -> {
            //根据产品编号分组
            Map<String, List<TrackItem>> groupByProductNo = y.stream().collect(Collectors.groupingBy(TrackItem::getProductNo));
            map1.put(x, groupByProductNo);
        });
        //选择的报工工序根据跟单分组
        Map<String, List<TrackItem>> resultGroupByTrackHeadId = result.stream().collect(Collectors.groupingBy(TrackItem::getTrackHeadId));
        Map<String, Map<String, List<TrackItem>>> map2 = new HashMap<>();
        resultGroupByTrackHeadId.forEach((x, y) -> {
            //根据产品编号分组
            Map<String, List<TrackItem>> groupByProductNo = y.stream().collect(Collectors.groupingBy(TrackItem::getProductNo));
            map2.put(x, groupByProductNo);
        });

        map2.forEach((x, y) -> {
            y.forEach((a, b) -> {
                //收集工序号并排序
                List<Integer> optNo = b.stream().map(c -> c.getOptSequence()).sorted().collect(Collectors.toList());
                //前面工序判断是否已报工
                //检查最小工序是否大于1
                if (optNo.get(0) > 1) {
                    //大于1则不是第一道工序
                    //拿到比当前工序小的工序并且没有完成报工的工序
                    List<TrackItem> smallItem = map1.get(x).get(a).stream().filter(d -> d.getOptSequence() < optNo.get(0) & d.getIsOperationComplete() == 0).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(smallItem)) {
                        //不为空则证明前面有工序没完成报工
                        throw new GlobalException("不能跳工序报工", ResultCode.FAILED);
                    }
                }
                //区间连续检查
                //检查工序号连续性
                for (int i = 0; i < optNo.size(); i++) {
                    //防止下表越界异常
                    if (i + 1 <= optNo.size() - 1) {
                        if (optNo.get(i + 1) - optNo.get(i) != 1) {
                            throw new GlobalException("报工工序号不连续,不能跳工序报工", ResultCode.FAILED);
                        }
                    }
                }
            });
        });
    }

    @Override
    public void knockoutLabel(HttpServletResponse response, String tiId) {
        //根据tiId获取跟id
        TrackItem trackItem = trackItemService.getById(tiId);
        if (trackItem == null) {
            throw new GlobalException("没有找到工序信息！", ResultCode.FAILED);
        }
        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
        if (trackHead == null) {
            throw new GlobalException("没有找到跟单信息！", ResultCode.FAILED);
        }
        //通过模板读入文件流
        ClassPathResource classPathResource = new ClassPathResource("excel/" + "knockoutLabel.xlsx");
        ExcelWriter writer;
        try {
            writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
            writer.writeCellValue("B2", trackHead.getProductName());
            writer.writeCellValue("D2", trackHead.getProductNo());
            writer.writeCellValue("B3", trackHead.getDrawingNo());
            writer.writeCellValue("D3", trackHead.getTexture());

            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = trackHead.getTrackNo() + "Label";
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"),
                    "ISO-8859-1") + ".xlsx");
            writer.flush(outputStream, true);
            IoUtil.close(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public IPage<PrechargeFurnaceAssign> prechargeFurnaceYl(Long prechargeFurnaceId, String texture, String startTime, String endTime, String workblankType, String status, int page, int limit, String order, String orderCol) {
        //获取当前用户分派的预装炉信息
        QueryWrapper<PrechargeFurnaceAssignPerson> assignPersonQueryWrapper = new QueryWrapper<>();
        assignPersonQueryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        List<PrechargeFurnaceAssignPerson> assignList = prechargeFurnaceAssignPersonService.list(assignPersonQueryWrapper);
        if (CollectionUtils.isEmpty(assignList)) {
            return new Page<>();
        }
        Set<String> prechargeFurnaceAssignIdList = assignList.stream().map(PrechargeFurnaceAssignPerson::getPrechargeFurnaceAssignId).collect(Collectors.toSet());
        QueryWrapper<PrechargeFurnaceAssign> assignQueryWrapper = new QueryWrapper<>();
        assignQueryWrapper.in("id", prechargeFurnaceAssignIdList);
        assignQueryWrapper.eq("workblank_type", workblankType);
        if (prechargeFurnaceId != null) {
            assignQueryWrapper.eq("furnace_id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(texture)) {
            assignQueryWrapper.eq("texture", texture);
        }
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(assignQueryWrapper, orderCol, order);
        } else {
            assignQueryWrapper.orderByDesc("modify_time");
        }
        //已报工时间筛选的是报工时间
        if (status.equals("2")) {
            assignQueryWrapper.eq("is_doing", 2);
            if (!StringUtils.isNullOrEmpty(startTime)) {
                assignQueryWrapper.apply("UNIX_TIMESTAMP(finish_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                assignQueryWrapper.apply("UNIX_TIMESTAMP(finish_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
            }
        } else {
            //未报工的时间筛选的是派工时间
            assignQueryWrapper.apply("(is_doing = 0 or (is_doing = 1 and start_doing_user = '" + SecurityUtils.getCurrentUser().getUsername() + "'))");
            if (!StringUtils.isNullOrEmpty(startTime)) {
                assignQueryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                assignQueryWrapper.apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
            }
        }
        return prechargeFurnaceAssignService.page(new Page<PrechargeFurnaceAssign>(page, limit), assignQueryWrapper);
    }

    @Override
    public List<TrackItem> getItemList(String prechargeFurnaceAssignId) {
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("precharge_furnace_assign_id", prechargeFurnaceAssignId);
        List<TrackItem> itemList = trackItemService.list(itemQueryWrapper);
        if (CollectionUtils.isEmpty(itemList)) {
            throw new GlobalException("该配炉没有添加工序！", ResultCode.FAILED);
        }
        for (TrackItem item : itemList) {
            Router router = baseServiceClient.getRouter(item.getRouterId()).getData();
            TrackHead trackHead = trackHeadService.getById(item.getTrackHeadId());
            QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
            assignQueryWrapper.eq("ti_id", item.getId());
            Assign assign = trackAssignService.getOne(assignQueryWrapper.last("limit 1"));
            item.setWeightMolten(router == null ? "0" : router.getWeightMolten());
            item.setPieceWeight(router == null ? "0" : String.valueOf(router.getWeight()));
            item.setProductName(trackHead.getProductName());
            item.setTrackNo(trackHead.getTrackNo());
            item.setWorkNo(trackHead.getWorkNo());
            item.setAssignId(assign == null ? null : assign.getId());
        }
        return itemList;
    }

    @Override
    public Map<String, Object> getPrechargeFurnaceMap(String workblankType, String branchCode, Long prechargeFurnaceId, String texture, String startTime, String endTime, int page, int limit, String order, String orderCol) {
        QueryWrapper<PrechargeFurnace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("workblank_type", workblankType).eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId())
                .eq("branch_code", branchCode).ne("status", 2);
        if (prechargeFurnaceId != null) {
            queryWrapper.eq("id", prechargeFurnaceId);
        }
        if (!StringUtils.isNullOrEmpty(texture)) {
            queryWrapper.eq("texture", texture);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
        }
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(queryWrapper, orderCol, order);
        } else {
            queryWrapper.orderByDesc("modify_time");
        }
        List<PrechargeFurnace> prechargeFurnaces = prechargeFurnaceService.list(queryWrapper);

        Page<PrechargeFurnace> total = prechargeFurnaceService.page(new Page<>(page, limit), queryWrapper);
        //原预装炉列表展示已派工的预装炉
        List<PrechargeFurnace> before = prechargeFurnaces.stream().filter(x -> x.getAssignStatus() == 1).collect(Collectors.toList());
        //变更后的预装炉列表展示未派工的预装炉
        List<PrechargeFurnace> after = prechargeFurnaces.stream().filter(x -> x.getAssignStatus() == 0).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("before", before);
        result.put("after", after);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean prechargeFurnaceChange(Long beforeId, Long afterId) {
        //先根据变更前的预装炉id找一条工序的派工信息
        //先找到该预装炉目前的派工信息
        QueryWrapper<PrechargeFurnaceAssign> furnaceAssignQueryWrapper = new QueryWrapper<>();
        furnaceAssignQueryWrapper.eq("furnace_id", beforeId).eq("complete_status", 0).last("limit 1");
        PrechargeFurnaceAssign prechargeFurnaceAssign = prechargeFurnaceAssignService.getOne(furnaceAssignQueryWrapper);
        if (ObjectUtil.isEmpty(prechargeFurnaceAssign)) {
            throw new GlobalException("原预装炉没有派工信息！", ResultCode.FAILED);
        }
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("precharge_furnace_id", beforeId).eq("precharge_furnace_assign_id", prechargeFurnaceAssign.getId());
        List<TrackItem> trackItemList = trackItemService.list(itemQueryWrapper);
        if (CollectionUtils.isEmpty(trackItemList)) {
            throw new GlobalException("原预装炉没有已派工工序信息！", ResultCode.FAILED);
        }
        String tiId = trackItemList.get(0).getId();
        QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
        assignQueryWrapper.eq("ti_id", tiId).last("limit 1");
        Assign assign = trackAssignService.getOne(assignQueryWrapper);
        if (ObjectUtil.isEmpty(assign)) {
            throw new GlobalException("原预装炉没有派工信息！", ResultCode.FAILED);
        }
        QueryWrapper<AssignPerson> assignPersonQueryWrapper = new QueryWrapper<>();
        assignPersonQueryWrapper.eq("assign_id", assign.getId());
        List<AssignPerson> assignPeople = trackAssignPersonMapper.selectList(assignPersonQueryWrapper);
        assign.setAssignPersons(assignPeople);
        if ("15".equals(prechargeFurnaceAssign.getOptType())) {
            //变更炼钢作业记录
            UpdateWrapper<RecordsOfSteelmakingOperations> steelmakingOperationsUpdateWrapper = new UpdateWrapper<>();
            steelmakingOperationsUpdateWrapper.eq("precharge_furnace_id", beforeId).set("precharge_furnace_id", afterId).set("status", null);
            recordsOfSteelmakingOperationsService.update(steelmakingOperationsUpdateWrapper);
        } else if ("16".equals(prechargeFurnaceAssign.getOptType())) {
            //变更炼浇注作业记录
            UpdateWrapper<RecordsOfPourOperations> pourOperationsUpdateWrapper = new UpdateWrapper<>();
            pourOperationsUpdateWrapper.eq("precharge_furnace_id", beforeId).set("precharge_furnace_id", afterId).set("status", null);
            recordsOfPourOperationsService.update(pourOperationsUpdateWrapper);
        }
        //设置原记录审核状态为null
        prechargeFurnaceAssign.setRecordStatus(null);
        prechargeFurnaceAssignService.updateById(prechargeFurnaceAssign);
        //根据派工信息和变更后预装炉id新建派工信息
        List<Long> ids = new ArrayList<>();
        ids.add(afterId);
        return prechargeFurnaceAssignService.furnaceAssign(assign, ids);
    }

    @Override
    public Map<String, Object> queryWorkHours(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo, String type) throws Exception {
        WorkHoursUtil workHoursUtil = new WorkHoursUtil();
        //1、根据条件查询报工信息
        List<TrackComplete> completes = workHoursUtil.getCompleteByFilter(systemServiceClient, trackCompleteMapper, trackNo, startTime, endTime, branchCode, workNo, userId, orderNo);
        //2、基本数据获取
        workHoursUtil.workHoursThread(systemServiceClient, trackHeadService, trackItemService, completes);
        //3、根据类型组装数据type（工厂、方法或者map数组），返回执行的数组（计算、数据封装）
        return workHoursUtil.workHoursCompletes(baseServiceClient, completes, type);
    }


    private Boolean removeComplete(String tiId) {
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", tiId);
        return this.remove(removeComplete);
    }

    //处理重复报工数据
    private void deleteComplete() {
        //错误问题工序20条
        List<String> trackItemList = trackItemMapper.queryBugTrackItemList();
        //查询工序所有报工记录
        QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<>();
        completeQueryWrapper.in("ti_id", trackItemList);
        completeQueryWrapper.orderByAsc("complete_time");
        List<TrackComplete> completeList = this.list(completeQueryWrapper);
        //查询所有工序信息
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.in("id", trackItemList);
        Map<String, TrackItem> itemMap = trackItemService.list(itemQueryWrapper).stream().collect(Collectors.toMap(TrackItem::getId, item -> item));
        //查询所有派工信息
        QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
        assignQueryWrapper.in("ti_id", trackItemList);
        Map<String, Assign> assignMap = trackAssignService.list(assignQueryWrapper).stream().collect(Collectors.toMap(Assign::getId, assign -> assign));
        //根据派工分组报工记录
        Map<String, List<TrackComplete>> listMap = completeList.stream().collect(Collectors.groupingBy(TrackComplete::getAssignId));
        //循环派工记录分组
        int i = 0;
        for (List<TrackComplete> trackCompleteList : listMap.values()) {
            //查询派工数量
            BigDecimal qty = BigDecimal.valueOf(assignMap.get(trackCompleteList.get(0).getAssignId()).getQty());
            //获取单件工时
            BigDecimal singlePieceHours = BigDecimal.valueOf(itemMap.get(trackCompleteList.get(0).getTiId()).getSinglePieceHours());
            //派工合计工时
            BigDecimal assignHours = singlePieceHours.multiply(qty).setScale(2, BigDecimal.ROUND_DOWN);
            //报工合计工时
            BigDecimal completeHours = BigDecimal.ZERO;
            //是否有合格信息
            boolean isRetain = false;
            //报工工序信息
            List<TrackComplete> isRetainList = new ArrayList<>();
            for (TrackComplete trackComplete : trackCompleteList) {
                completeHours = completeHours.add(BigDecimal.valueOf(trackComplete.getReportHours()));
                if (completeHours.compareTo(assignHours) == 0) {
                    isRetainList.add(trackComplete);
                    isRetain = true;
                    break;
                } else if (completeHours.compareTo(assignHours) == -1) {
                    isRetainList.add(trackComplete);
                } else {
                    isRetainList.clear();
                    completeHours = BigDecimal.ZERO;
                    completeHours = completeHours.add(BigDecimal.valueOf(trackComplete.getReportHours()));
                    isRetainList.add(trackComplete);
                }
            }
            //处理是否删除数据
            if (isRetain) {
                Map<String, TrackComplete> collecMap = isRetainList.stream().collect(Collectors.toMap(TrackComplete::getId, x -> x));
                for (TrackComplete trackComplete : trackCompleteList) {
                    if (null == collecMap.get(trackComplete.getId())) {
                        trackComplete.setIsRetain(2);
                    } else {
                        trackComplete.setIsRetain(1);
                    }
                }
            } else {
                trackCompleteList.forEach(complete -> complete.setIsRetain(3));
            }
            trackCompleteService.updateBatchById(trackCompleteList);
        }
    }

    private void deleteCompleteW() {
        //错误问题工序20条
        List<String> trackItemList = trackItemMapper.queryBugTrackItemList();
        //查询工序所有报工记录
        QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<>();
        completeQueryWrapper.in("ti_id", trackItemList);
        completeQueryWrapper.orderByAsc("complete_time");
        List<TrackComplete> completeList = this.list(completeQueryWrapper);
        //根据工序分组
        Map<String, List<TrackComplete>> listMap = completeList.stream().collect(Collectors.groupingBy(TrackComplete::getTiId));
        int i = 1;
        for (List<TrackComplete> trackCompleteList : listMap.values()) {
            for (TrackComplete trackComplete : trackCompleteList) {
                if (trackComplete.getId().equals(trackCompleteList.get(0).getId())) {
                    trackComplete.setIsRetain(1);
                } else {
                    trackComplete.setIsRetain(2);
                }
            }
            trackCompleteService.updateBatchById(trackCompleteList);
        }
    }
}
