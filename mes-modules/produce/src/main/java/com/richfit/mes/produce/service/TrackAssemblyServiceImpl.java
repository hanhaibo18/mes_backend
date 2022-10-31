package com.richfit.mes.produce.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackAssemblyMapper;
import com.richfit.mes.produce.entity.AdditionalMaterialDto;
import com.richfit.mes.produce.entity.AssembleKittingVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import io.netty.util.internal.StringUtil;
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
    private BaseServiceClient baseServiceClient;

    @Override
    public IPage<TrackAssembly> queryTrackAssemblyPage(Page<TrackAssembly> page, String trackHeadId, String branchCode, String order, String orderCol) {
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
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateComplete(List<String> idList, String itemId) {
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
                assemblyBindingService.save(assemblyBinding);
            }
        }
        return isComplete;
    }

    @Override
    public Boolean unbindComplete(List<String> idList) {
        boolean isSucceed = false;
        for (String id : idList) {
            TrackAssembly trackAssembly = this.getById(id);
            if ("0".equals(trackAssembly.getIsKeyPart())) {
                trackAssembly.setNumberInstall(trackAssembly.getNumber() - trackAssembly.getNumberInstall());
                QueryWrapper<TrackAssemblyBinding> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("assembly_id", id);
                this.updateById(trackAssembly);
                isSucceed = assemblyBindingService.remove(queryWrapper);
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
    public ApplicationResult application(AdditionalMaterialDto additionalMaterialDto) {
        try {
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
            QueryWrapper<RequestNote> queryWrapperNote = new QueryWrapper<>();
            queryWrapperNote.likeRight("request_note_number", trackItem.getId());
            int count = requestNoteService.count(queryWrapperNote);
            //申请单号
            String id = trackItem.getId().substring(0, trackItem.getId().length() - 3);
            ingredient.setSqd(id + "@" + count);
            //车间编码
            ingredient.setGc(SecurityUtils.getCurrentUser().getTenantErpCode());
            //车间code
            ingredient.setCj(additionalMaterialDto.getBranchCode());
            //车间名称
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
            //单位 从哪获取
            lineLists.add(lineList);
            ingredient.setLineList(lineLists);
            //保存信息到本地
            saveNodeAndDetail(additionalMaterialDto, ingredient);
            ApplicationResult applicationForm = new ApplicationResult();
            try {
                applicationForm = anApplicationForm(ingredient);
            } catch (Exception e) {
                throw new GlobalException("申请单发送失败!", ResultCode.FAILED);
            }
            if ("N".equals(applicationForm.getRetCode())) {
                throw new GlobalException(applicationForm.getRetMsg(), ResultCode.FAILED);
            }
            return applicationForm;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    @Override
    public Page<TrackAssembly> getDeliveredDetail(Page<TrackAssembly> trackAssemblyPage, String id) {
        trackAssemblyMapper.getDeliveredDetail(trackAssemblyPage, id).getRecords().forEach(i -> {
            i.setLackQuantity(i.getOrderQuantity() - i.getQuantity());
        });
        return trackAssemblyMapper.getDeliveredDetail(trackAssemblyPage, id);
    }

    @Override
    public void addTrackAssemblyByTrackHead(TrackHead trackHead) {
        List<TrackAssembly> trackAssemblyList = pojectBomList(trackHead);
        for (TrackAssembly trackAssembly : trackAssemblyList) {
            trackAssembly.setBranchCode(trackHead.getBranchCode());
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
    List<TrackAssembly> pojectBomList(TrackHead trackHead) {
        List<TrackAssembly> trackAssemblyList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(trackHead.getProjectBomId())) {
            List<ProjectBom> projectBomList = baseServiceClient.getProjectBomPartByIdList(trackHead.getProjectBomId());
            Map<String, String> group = new HashMap<>();
            if (!StringUtil.isNullOrEmpty(trackHead.getProjectBomGroup())) {
                group = JSON.parseObject(trackHead.getProjectBomGroup(), Map.class);
            }
            for (ProjectBom pb : projectBomList) {
                TrackAssembly trackAssembly = new TrackAssembly();
                trackAssembly.setGrade(pb.getGrade());
                trackAssembly.setName(pb.getProdDesc());
                trackAssembly.setDrawingNo(pb.getDrawingNo());
                trackAssembly.setMaterialNo(pb.getMaterialNo());
                trackAssembly.setTrackHeadId(trackHead.getId());
                trackAssembly.setNumber(trackHead.getNumber() * pb.getNumber());
                trackAssembly.setIsKeyPart(pb.getIsKeyPart());
                trackAssembly.setTrackType(pb.getTrackType());
                trackAssembly.setWeight(Double.valueOf(pb.getWeight()));
                trackAssembly.setIsCheck(pb.getIsCheck());
                trackAssembly.setIsEdgeStore(pb.getIsEdgeStore());
                trackAssembly.setIsNeedPicking(pb.getIsNeedPicking());
                trackAssembly.setUnit(pb.getUnit());
                trackAssembly.setSourceType(pb.getSourceType());
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
}
