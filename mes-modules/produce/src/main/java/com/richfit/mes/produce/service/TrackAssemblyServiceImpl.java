package com.richfit.mes.produce.service;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackAssemblyMapper;
import com.richfit.mes.produce.enmus.InspectionRecordTypeEnum;
import com.richfit.mes.produce.entity.AdditionalMaterialDto;
import com.richfit.mes.produce.entity.AssembleKittingVo;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.quality.InspectionPowerService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 马峰
 * @Description 产品装配服务
 */
@Service
public class TrackAssemblyServiceImpl extends ServiceImpl<TrackAssemblyMapper, TrackAssembly> implements TrackAssemblyService {

    @Resource
    private TrackAssemblyBindingService assemblyBindingService;
    @Resource
    private LineStoreMapper lineStoreMapper;
    @Resource
    private LineStoreService lineStoreService;
    @Resource
    private TrackHeadService trackHeadService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssignService trackAssignService;
    @Resource
    private WmsServiceClient wmsServiceClient;
    @Resource
    private RequestNoteDetailService requestNoteDetailService;
    @Resource
    private RequestNoteService requestNoteService;
    @Resource
    private TrackAssemblyMapper trackAssemblyMapper;
    @Resource
    private ApplicationNumberService numberService;

    @Resource
    private BaseServiceClient baseServiceClient;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;

    @Override
    public IPage<TrackAssembly> queryTrackAssemblyPage(Page<TrackAssembly> page, String trackHeadId, Boolean isKey, String branchCode, String order, String orderCol) {
        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        if (Boolean.TRUE.equals(isKey)) {
            queryWrapper.eq("is_key_part", 1);
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
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        IPage<TrackAssembly> trackAssemblyPage = this.page(page, queryWrapper);
        for (TrackAssembly trackAssembly : trackAssemblyPage.getRecords()) {
            Integer zpNumber = lineStoreMapper.selectTotalNum(trackAssembly.getMaterialNo(), branchCode, SecurityUtils.getCurrentUser().getTenantId());
            if (zpNumber != null) {
                trackAssembly.setNumberInventory(zpNumber);
            } else {
                trackAssembly.setNumberInventory(0);
            }
            trackAssembly.setNumberRemaining(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
            if (trackAssembly.getNumber() == trackAssembly.getNumberInstall()) {
                trackAssembly.setIsComplete(1);
            } else {
                trackAssembly.setIsComplete(0);
            }
            QueryWrapper<TrackAssemblyBinding> queryWrapperBinding = new QueryWrapper<>();
            queryWrapperBinding.eq("assembly_id", trackAssembly.getId());
            queryWrapperBinding.eq("is_binding", 1);
            trackAssembly.setAssemblyBinding(assemblyBindingService.list(queryWrapperBinding));
        }
        return trackAssemblyPage;
    }

    @Override
    public IPage<TrackAssembly> queryTrackHeadAssemblyPage(Page<TrackAssembly> page, String trackHeadId, String branchCode, String order, String orderCol) {
        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
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
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        IPage<TrackAssembly> trackAssemblyPage = this.page(page, queryWrapper);
        for (TrackAssembly trackAssembly : trackAssemblyPage.getRecords()) {
            trackAssembly.setNumberRemaining(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
            if (trackAssembly.getNumber() == trackAssembly.getNumberInstall()) {
                trackAssembly.setIsComplete(1);
            } else {
                trackAssembly.setIsComplete(0);
            }
            QueryWrapper<TrackAssemblyBinding> queryWrapperBinding = new QueryWrapper<>();
            queryWrapperBinding.eq("assembly_id", trackAssembly.getId());
            queryWrapperBinding.eq("is_binding", 1);
            trackAssembly.setAssemblyBinding(assemblyBindingService.list(queryWrapperBinding));
        }
        return trackAssemblyPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateComplete(List<String> idList, String itemId) {
        boolean isComplete = false;
        for (String id : idList) {
            TrackAssembly trackAssembly = this.getById(id);
            List<TrackAssemblyBinding> bindingList = assemblyBindingService.queryAssemblyBindingList(id);
            if ("0".equals(trackAssembly.getIsKeyPart()) && bindingList.isEmpty()) {
                trackAssembly.setNumberInstall(trackAssembly.getNumber());
                isComplete = this.updateById(trackAssembly);
                TrackAssemblyBinding assemblyBinding = new TrackAssemblyBinding();
                assemblyBinding.setPartDrawingNo(trackAssembly.getDrawingNo());
                assemblyBinding.setQuantity(trackAssembly.getNumber());
                assemblyBinding.setAssemblyId(id);
                assemblyBinding.setItemId(itemId);
                assemblyBinding.setIsBinding(1);
                TrackHead trackHead = trackHeadService.getById(trackAssembly.getTrackHeadId());
                Map<String, String> map = lineStoreService.partsBinding(trackAssembly.getTrackHeadId(), trackAssembly.getFlowId(), trackAssembly.getMaterialNo(), assemblyBinding.getQuantity(), trackHead.getBranchCode(), trackHead.getTenantId());
                assemblyBinding.setLineStoreId(map.get("success"));
                assemblyBindingService.save(assemblyBinding);
                if (StrUtil.isNotBlank(map.get("failureList"))) {
                    return CommonResult.failed("部分非关键件绑定失败,失败零件列表:" + map.get("failureList"));
                }
            }
        }
        return CommonResult.success(isComplete);
    }

    @Override
    public Boolean unbindComplete(List<String> idList) {
        boolean isSucceed = false;
        for (String id : idList) {
            TrackAssembly trackAssembly = this.getById(id);
            if ("0".equals(trackAssembly.getIsKeyPart())) {
                trackAssembly.setNumberInstall(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
                this.updateById(trackAssembly);
                QueryWrapper<TrackAssemblyBinding> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("assembly_id", id);
                List<TrackAssemblyBinding> bindingList = assemblyBindingService.list(queryWrapper);
                isSucceed = assemblyBindingService.remove(queryWrapper);
                lineStoreService.unbundling(bindingList.get(0).getLineStoreId());
            }
        }
        return isSucceed;
    }

    @Override
    public List<AssembleKittingVo> kittingExamine(String trackHeadId, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        queryWrapper.eq("is_check", "1");
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", tenantId);
        List<TrackAssembly> trackAssemblyList = this.list(queryWrapper);
        List<AssembleKittingVo> list = new ArrayList<>();
        for (TrackAssembly trackAssembly : trackAssemblyList) {
            AssembleKittingVo assemble = new AssembleKittingVo();
            assemble.setDrawingNo(trackAssembly.getDrawingNo());
            assemble.setMaterialNo(trackAssembly.getMaterialNo());
            assemble.setMaterialName(trackAssembly.getName());
            assemble.setUnit(trackAssembly.getUnit());
            //需要安装数量
            assemble.setNeedNumber(trackAssembly.getNumber());
            //安装数量
            assemble.setInstallNumber(trackAssembly.getNumberInstall());
            //线边库
            Integer integer = lineStoreMapper.selectTotalNum(trackAssembly.getMaterialNo(), branchCode, tenantId);
            if (integer != null) {
                assemble.setRepertoryNumber(integer);
            } else {
                assemble.setRepertoryNumber(0);
            }
            //可配送数量 WMS库存数量
            assemble.setDeliverableQuantity(queryMaterialCount(trackAssembly.getMaterialNo()));
            //已领取数量
            assemble.setAcquireNumber(0);
            //缺件数量
            assemble.setShortQuantity(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
            assemble.setIsNeedPicking(trackAssembly.getIsNeedPicking());
            assemble.setIsEdgeStore(trackAssembly.getIsEdgeStore());
            assemble.setIsKeyPart(trackAssembly.getIsKeyPart());
            list.add(assemble);
        }
        return list;
    }

    @Override
    public List<TrackAssembly> planKittingExamine(String trackHeadId, String branchCode, Boolean isComplete) {
        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<TrackAssembly> trackAssemblyList = this.list(queryWrapper);
        for (TrackAssembly trackAssembly : trackAssemblyList) {

            Integer zpNumber = lineStoreMapper.selectTotalNum(trackAssembly.getMaterialNo(), branchCode, SecurityUtils.getCurrentUser().getTenantId());
            if (zpNumber != null) {
                trackAssembly.setNumberInventory(zpNumber);
            } else {
                trackAssembly.setNumberInventory(0);
            }
            trackAssembly.setNumberRemaining(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
            if (trackAssembly.getNumber() == trackAssembly.getNumberInstall()) {
                trackAssembly.setIsComplete(1);
            } else {
                trackAssembly.setIsComplete(0);
            }
            QueryWrapper<TrackAssemblyBinding> queryWrapperBinding = new QueryWrapper<>();
            queryWrapperBinding.eq("assembly_id", trackAssembly.getId());
            queryWrapperBinding.eq("is_binding", 1);
            List<TrackAssemblyBinding> assemblyBindingList = assemblyBindingService.list(queryWrapperBinding);
            trackAssembly.setAssemblyBinding(assemblyBindingList);

            for (TrackAssemblyBinding assemblyBinding : assemblyBindingList) {
                QueryWrapper<TrackHead> queryWrapperHead = new QueryWrapper<>();
                queryWrapperHead.eq("product_no", assemblyBinding.getNumber());
                List<TrackHead> list = trackHeadService.list(queryWrapperHead);
                if (!list.isEmpty()) {
                    trackAssembly.setIsTrackHead("1");
                    break;
                } else {
                    trackAssembly.setIsTrackHead("0");
                }
            }
        }
        //控制是否部件级跟单
        if (Boolean.TRUE.equals(isComplete)) {
            trackAssemblyList = trackAssemblyList.stream().filter(trackAssembly -> "1".equals(trackAssembly.getIsTrackHead())).collect(Collectors.toList());
        }
        return trackAssemblyList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationResult application(AdditionalMaterialDto additionalMaterialDto) {
        TrackHead trackHead = trackHeadService.getById(additionalMaterialDto.getTrackHeadId());
        if (StrUtil.isBlank(trackHead.getProductionOrder())) {
            throw new GlobalException("无生产订单编号", ResultCode.FAILED);
        }
        TrackItem trackItem = trackItemService.getById(additionalMaterialDto.getTiId());
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", trackItem.getId());
        //查询派工工位信息
        Assign assign = trackAssignService.getOne(queryWrapper);
        IngredientApplicationDto ingredient = new IngredientApplicationDto();
        //申请单号保持唯一
        int number = numberService.queryApplicationNumber(trackItem.getId());
        QueryWrapper<RequestNote> queryWrapperNote = new QueryWrapper<>();
        queryWrapperNote.likeRight("request_note_number", number);
        int count = requestNoteService.count(queryWrapperNote);
        //申请单号
        ingredient.setSqd(number + "@" + count);
        //车间编码
        ingredient.setGc(SecurityUtils.getCurrentUser().getTenantErpCode());
        //车间code
        ingredient.setCj(additionalMaterialDto.getBranchCode());
        //车间名称
        CommonResult<Branch> branch = baseServiceClient.selectBranchByCodeAndTenantId(additionalMaterialDto.getBranchCode(), null);
        ingredient.setCjName(branch.getData().getBranchName());
        //工位
        ingredient.setGw(assign.getSiteId());
        //工位名称
        ingredient.setGwName(assign.getSiteName());
        //工序
        ingredient.setGx(trackItem.getId());
        //工序名称
        ingredient.setGxName(trackItem.getOptName());
        //生产订单编号
        ingredient.setScdd(trackHead.getProductionOrder());
        //跟单Id
        ingredient.setGd(trackHead.getId());
        //产品编号
        ingredient.setCp(trackHead.getProductNo());
        //产品名称
        ingredient.setCpName(trackHead.getProductName());
        //优先级
        ingredient.setYxj(Integer.parseInt(trackHead.getPriority()));
        //派工时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSS");
        ingredient.setPgsj(format.format(assign.getAssignTime()));
        //追加物料
        List<LineList> lineLists = new ArrayList<LineList>();
        LineList lineList = new LineList();
        lineList.setMaterialDesc(additionalMaterialDto.getMaterialName());
        lineList.setMaterialNum(additionalMaterialDto.getMaterialNo());
        lineList.setSwFlag(additionalMaterialDto.getIsEdgeStore());
        lineList.setQuantity(additionalMaterialDto.getCount());
        //单位
        lineList.setUnit(additionalMaterialDto.getUnit());
        lineLists.add(lineList);
        ingredient.setLineList(lineLists);
        //保存信息到本地
        saveNodeAndDetail(additionalMaterialDto, ingredient);
        ApplicationResult applicationForm = new ApplicationResult();
        //发送申请单
        applicationForm = anApplicationForm(ingredient);
        if ("N".equals(applicationForm.getRetCode())) {
            throw new GlobalException(applicationForm.getRetMsg(), ResultCode.FAILED);
        }
        return applicationForm;
    }

    @Override
    public Page<TrackAssembly> getDeliveredDetail(Page<TrackAssembly> trackAssemblyPage, String id) {
        trackAssemblyMapper.getDeliveredDetail(trackAssemblyPage, id).getRecords().forEach(i -> {
            i.setLackQuantity(i.getOrderQuantity() - i.getQuantity());
        });
        return trackAssemblyMapper.getDeliveredDetail(trackAssemblyPage, id);
    }

    @Override
    public void addTrackAssemblyByTrackHead(TrackHeadPublicDto trackHeadPublicDto) {
        List<TrackAssembly> trackAssemblyList = pojectBomList(trackHeadPublicDto);
        for (TrackAssembly trackAssembly : trackAssemblyList) {
            trackAssembly.setTrackHeadId(trackHeadPublicDto.getId());
            trackAssembly.setTrackNo(trackHeadPublicDto.getTrackNo());
            trackAssembly.setBranchCode(trackHeadPublicDto.getBranchCode());
            trackAssembly.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            trackAssembly.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            trackAssembly.setCreateTime(new Date());
            trackAssembly.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            trackAssembly.setModifyTime(new Date());
            trackAssembly.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            this.save(trackAssembly);
        }
    }

    /**
     * 功能描述: 添加跟单 装配列表数据整合，分组数据取值等
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/23 10:59
     **/
    List<TrackAssembly> pojectBomList(TrackHeadPublicDto trackHeadPublicDto) {
        List<TrackAssembly> trackAssemblyList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(trackHeadPublicDto.getProjectBomId())) {
            List<ProjectBom> projectBomList = baseServiceClient.getProjectBomPartByIdList(trackHeadPublicDto.getProjectBomId());
            Map<String, String> group = new HashMap<>();
            if (!StringUtil.isNullOrEmpty(trackHeadPublicDto.getProjectBomGroup())) {
                group = JSON.parseObject(trackHeadPublicDto.getProjectBomGroup(), Map.class);
            }
            for (ProjectBom pb : projectBomList) {
                TrackAssembly trackAssembly = new TrackAssembly();
                trackAssembly.setGrade(pb.getGrade());
                trackAssembly.setName(pb.getProdDesc());
                trackAssembly.setDrawingNo(pb.getDrawingNo());
                trackAssembly.setMaterialNo(pb.getMaterialNo());
                trackAssembly.setTrackHeadId(trackHeadPublicDto.getId());
                trackAssembly.setNumber(trackHeadPublicDto.getNumber() * pb.getNumber());
                trackAssembly.setIsKeyPart(pb.getIsKeyPart());
                trackAssembly.setTrackType(pb.getTrackType());
                if (pb.getWeight() != null) {
                    trackAssembly.setWeight(Double.valueOf(pb.getWeight()));
                }
                trackAssembly.setIsCheck(pb.getIsCheck());
                trackAssembly.setIsEdgeStore(pb.getIsEdgeStore());
                trackAssembly.setIsNeedPicking(pb.getIsNeedPicking());
                trackAssembly.setUnit(pb.getUnit());
                trackAssembly.setSourceType(pb.getSourceType());
                trackAssembly.setIsNumFrom(pb.getIsNumFrom());
                if (!StringUtil.isNullOrEmpty(pb.getBomGrouping())) {
                    if (pb.getId().equals(group.get(pb.getBomGrouping()))) {
                        trackAssemblyList.add(trackAssembly);
                    }
                } else {
                    trackAssemblyList.add(trackAssembly);
                }

            }
        }
        return trackAssemblyList;
    }


    private int queryMaterialCount(String materialNo) {
        return wmsServiceClient.queryMaterialCount(materialNo).getData();
    }

    private ApplicationResult anApplicationForm(IngredientApplicationDto ingredientApplicationDto) {
        return wmsServiceClient.anApplicationForm(ingredientApplicationDto).getData();
    }

    @Override
    public List<TrackAssembly> queryTrackAssemblyByTrackNo(String flowId) {
        QueryWrapper<TrackAssembly> wrapper = new QueryWrapper();
        wrapper.eq("flow_id", flowId);
        return this.list(wrapper);
    }

    /**
     * 保存申请单信息
     *
     * @param additionalMaterialDto
     * @param ingredient
     */
    private void saveNodeAndDetail(AdditionalMaterialDto additionalMaterialDto, IngredientApplicationDto ingredient) {
        //要保存的申请单信息
        RequestNote requestNote = new RequestNote();
        //要保存的物料信息集合
        List<RequestNoteDetail> requestNoteDetails = new ArrayList<>();
        //所属机构
        requestNote.setBranchCode(additionalMaterialDto.getBranchCode());
        //所属租户
        requestNote.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        //跟单id
        requestNote.setTrackHeadId(additionalMaterialDto.getTrackHeadId());
        TrackHead trackHead = trackHeadService.getById(additionalMaterialDto.getTrackHeadId());
        requestNote.setTrackNo(trackHead.getTrackNo());
        //工序id
        requestNote.setTrackItemId(ingredient.getGx());
        //申请单号
        requestNote.setRequestNoteNumber(ingredient.getSqd());
        //保存申请单
        requestNoteService.save(requestNote);
        //物料信息
        List<LineList> lineList = ingredient.getLineList();
        for (LineList line : lineList) {
            RequestNoteDetail requestNoteDetail = new RequestNoteDetail();
            requestNoteDetail.setBranchCode(ingredient.getCj());
            requestNoteDetail.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            //申请单Id
            requestNoteDetail.setNoteId(requestNote.getId());
            requestNoteDetail.setRequestNoteNumber(ingredient.getSqd());
            //图号
            requestNoteDetail.setDrawingNo(additionalMaterialDto.getDrawingNo());
            //物料编码
            requestNoteDetail.setMaterialNo(line.getMaterialNum());
            //物料名称
            requestNoteDetail.setMaterialName(line.getMaterialDesc());
            //数量
            requestNoteDetail.setNumber(line.getQuantity());
            requestNoteDetail.setReasonExplain(additionalMaterialDto.getExplain());
            requestNoteDetails.add(requestNoteDetail);
        }
        //保存物料信息
        requestNoteDetailService.saveBatch(requestNoteDetails);

    }


    @Autowired
    private InspectionPowerService inspectionPowerService;
    @Autowired
    private ProduceInspectionRecordMtService produceInspectionRecordMtService;
    @Autowired
    private ProduceInspectionRecordPtService produceInspectionRecordPtService;
    @Autowired
    private ProduceInspectionRecordRtService produceInspectionRecordRtService;
    @Autowired
    private ProduceInspectionRecordUtService produceInspectionRecordUtService;
    @Autowired
    private ProduceItemInspectInfoService produceItemInspectInfoService;

    /**
     * 根据装配清单信息修改产品编号
     * @param id   装配清单id
     * @param productNo   不带图号的产品编码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeProductNo(String id, String productNo,String branchCode) {
        try {
            Assign assign = trackAssignService.getById(id);
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            //新的产品编号
            String produceNoDesc =  trackItem.getDrawingNo() + " " + productNo;
            //校验产品编码是否重复
            QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("product_no", produceNoDesc);
            queryWrapper.eq("branch_code", branchCode);
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            List<TrackFlow> trackFlowList = trackHeadFlowService.list(queryWrapper);
            if (trackFlowList.size() > 0) {
                throw new GlobalException("产品编码已存在，不可以重复！", ResultCode.FAILED);
            }
            //校验单个生产流程的可以更改
            QueryWrapper<TrackFlow> queryWrapperFlow = new QueryWrapper<>();
            queryWrapperFlow.eq("track_head_id", trackItem.getTrackHeadId());
            TrackFlow trackFlow;
            try {
                trackFlow = trackHeadFlowService.getOne(queryWrapperFlow);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new GlobalException("异常提示跟单数据异常出现多个生产流程", ResultCode.FAILED);
            }
            //修改跟单、flow、item、探伤记录信息
            //旧的产品编号
            String produceNoDescOld =  trackFlow.getProductNo();
            //修改跟单分流数据
            trackFlow.setProductNo(produceNoDesc);
            trackHeadFlowService.updateById(trackFlow);
            //修改跟单数据
            UpdateWrapper<TrackHead> trackHeadUpdateWrapper = new UpdateWrapper<>();
            trackHeadUpdateWrapper.set("product_no",productNo)
                    .set("product_no_desc",produceNoDesc)
                    .eq("id",trackItem.getTrackHeadId());
            trackHeadService.update(trackHeadUpdateWrapper);
            //修改跟单工序item数据
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("flow_id", trackItem.getFlowId());
            updateWrapper.set("product_no", produceNoDesc);
            trackItemService.update(updateWrapper);
            //修改探伤记录信息
            updateProducrNoInspectRecordInfo(produceNoDesc, trackItem.getTrackHeadId(), produceNoDescOld);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("修改产品编码出现异常：" + e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 修改探伤记录信息
     * @param produceNoDesc
     * @param trackHeadId
     * @param produceNoDescOld
     */
    private void updateProducrNoInspectRecordInfo(String produceNoDesc, String trackHeadId, String produceNoDescOld) {
        QueryWrapper<InspectionPower> inspectionPowerQueryWrapper = new QueryWrapper<>();
        inspectionPowerQueryWrapper.eq("head_id",trackHeadId);
        List<InspectionPower> powers = inspectionPowerService.list(inspectionPowerQueryWrapper);
        List<String> powerIds = powers.stream().map(InspectionPower::getId).collect(Collectors.toList());
        if(powerIds.size()>0){
            QueryWrapper<ProduceItemInspectInfo> inspectInfoQueryWrapper = new QueryWrapper<>();
            inspectInfoQueryWrapper.in("power_id",powerIds);
            List<ProduceItemInspectInfo> inspects = produceItemInspectInfoService.list(inspectInfoQueryWrapper);

            for (ProduceItemInspectInfo inspect : inspects) {
                if (InspectionRecordTypeEnum.MT.getType().equals(inspect.getTempType())) {
                    ProduceInspectionRecordMt mt = produceInspectionRecordMtService.getById(inspect.getInspectRecordId());
                    if(!StringUtils.isNullOrEmpty(mt.getProductNo())){
                        String replace = mt.getProductNo().replace(produceNoDescOld, produceNoDesc);
                        mt.setProductNo(replace);
                        produceInspectionRecordMtService.updateById(mt);
                    }
                } else if (InspectionRecordTypeEnum.PT.getType().equals(inspect.getTempType())) {
                    ProduceInspectionRecordPt pt = produceInspectionRecordPtService.getById(inspect.getInspectRecordId());
                    if(!StringUtils.isNullOrEmpty(pt.getProductNo())){
                        String replace = pt.getProductNo().replace(produceNoDescOld, produceNoDesc);
                        pt.setProductNo(replace);
                        produceInspectionRecordPtService.updateById(pt);
                    }
                } else if (InspectionRecordTypeEnum.RT.getType().equals(inspect.getTempType())) {
                    ProduceInspectionRecordRt rt = produceInspectionRecordRtService.getById(inspect.getInspectRecordId());
                    if(!StringUtils.isNullOrEmpty(rt.getProductNo())){
                        String replace = rt.getProductNo().replace(produceNoDescOld, produceNoDesc);
                        rt.setProductNo(replace);
                        produceInspectionRecordRtService.updateById(rt);
                    }
                } else if (InspectionRecordTypeEnum.UT.getType().equals(inspect.getTempType())) {
                    ProduceInspectionRecordUt ut = produceInspectionRecordUtService.getById(inspect.getInspectRecordId());
                    if(!StringUtils.isNullOrEmpty(ut.getProductNo())){
                        String replace = ut.getProductNo().replace(produceNoDescOld, produceNoDesc);
                        ut.setProductNo(replace);
                        produceInspectionRecordUtService.updateById(ut);
                    }
                }
            }
        }

    }
}
