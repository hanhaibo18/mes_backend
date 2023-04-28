package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.wms.ApplyLineList;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RequestNoteMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.enmus.MaterialTypeEnum;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class RequestNoteServiceImpl extends ServiceImpl<RequestNoteMapper, RequestNote> implements RequestNoteService {

    @Resource
    private TrackAssemblyService trackAssemblyService;

    @Resource
    private RequestNoteDetailService requestNoteDetailService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private RequestNoteMapper requestNoteMapper;

    @Resource
    private TrackHeadService trackHeadService;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private TrackHeadMapper trackHeadMapper;

    @Resource
    private WmsServiceClient wmsServiceClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRequestNote(IngredientApplicationDto ingredient, List<LineList> lineLists, String branchCode) {
        RequestNote requestNote = new RequestNote();
        requestNote.setId(UUID.randomUUID().toString().replace("-", ""));
        //跟单Id
        requestNote.setTrackHeadId(ingredient.getGd());
        //存入跟单编号
        TrackHead trackHead = trackHeadService.getById(ingredient.getGd());
        requestNote.setTrackNo(trackHead.getTrackNo());
        //工序Id
        requestNote.setTrackItemId(ingredient.getGx());
        //申请单号
        requestNote.setRequestNoteNumber(ingredient.getSqd());
        //所属机构
        requestNote.setBranchCode(branchCode);
        //所属租户
        requestNote.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        boolean save = this.save(requestNote);
        if (save) {
            lineLists.forEach(i -> {
                RequestNoteDetail requestNoteDetail = new RequestNoteDetail();
                //申请单id
                requestNoteDetail.setNoteId(requestNote.getId());
                requestNoteDetail.setRequestNoteNumber(ingredient.getSqd());
                requestNoteDetail.setMaterialNo(i.getMaterialNum());
                requestNoteDetail.setMaterialName(i.getMaterialDesc());
                QueryWrapper<TrackAssembly> wrapper = new QueryWrapper<>();
                wrapper.eq("material_no", requestNoteDetail.getMaterialNo());
                wrapper.eq("track_head_id", requestNote.getTrackHeadId());
                List<TrackAssembly> list = trackAssemblyService.list(wrapper);
                requestNoteDetail.setDrawingNo(list.get(0).getDrawingNo());
                requestNoteDetail.setUnit(list.get(0).getUnit());
                requestNoteDetail.setNumber(i.getQuantity());
                requestNoteDetail.setRequestNoteNumber(ingredient.getSqd());
                requestNoteDetail.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
                requestNoteDetail.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                requestNoteDetail.setIsNeedPicking(list.get(0).getIsNeedPicking());
                requestNoteDetail.setIsKeyPart(list.get(0).getIsKeyPart());
                requestNoteDetail.setIsEdgeStore(list.get(0).getIsEdgeStore());
                requestNoteDetailService.save(requestNoteDetail);
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean saveRequestNoteNew(IngredientApplicationDto ingredient, TrackHead trackHead, String branchCode) {
        RequestNote requestNote = new RequestNote();
        requestNote.setId(UUID.randomUUID().toString().replace("-", ""));
        //跟单Id
        requestNote.setTrackHeadId(ingredient.getGd());
        //存入跟单编号
        requestNote.setTrackNo(trackHead.getTrackNo());
        //工序Id
        requestNote.setTrackItemId(ingredient.getGx());
        //申请单号
        requestNote.setRequestNoteNumber(ingredient.getSqd());
        //所属机构
        requestNote.setBranchCode(branchCode);
        //所属租户
        requestNote.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        boolean save = this.save(requestNote);

        QueryWrapper<TrackAssembly> assemblyQueryWrapper = new QueryWrapper<>();
        assemblyQueryWrapper.eq("track_head_id", trackHead.getId());
        List<TrackAssembly> assemblyList = trackAssemblyService.list(assemblyQueryWrapper);

        assemblyList.forEach(assembly -> {
            RequestNoteDetail requestNoteDetail = new RequestNoteDetail();
            //申请单id
            requestNoteDetail.setNoteId(requestNote.getId());
            requestNoteDetail.setRequestNoteNumber(ingredient.getSqd());
            requestNoteDetail.setMaterialNo(assembly.getMaterialNo());
            //根据物料号查询物料
            List<Product> list = baseServiceClient.listByMaterialNo(assembly.getMaterialNo());
            if (!CollectionUtils.isEmpty(list)) {
                requestNoteDetail.setMaterialName(list.get(0).getProductName());
            }
            requestNoteDetail.setDrawingNo(assembly.getDrawingNo());
            requestNoteDetail.setUnit(assembly.getUnit());
            requestNoteDetail.setNumber(Double.valueOf(assembly.getNumber()));
            requestNoteDetail.setRequestNoteNumber(ingredient.getSqd());
            requestNoteDetail.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
            requestNoteDetail.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            requestNoteDetail.setIsNeedPicking(assembly.getIsNeedPicking());
            requestNoteDetail.setIsKeyPart(assembly.getIsKeyPart());
            requestNoteDetail.setIsEdgeStore(assembly.getIsEdgeStore());
            requestNoteDetailService.save(requestNoteDetail);
        });
        return save;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> uploadRequestNote(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return CommonResult.failed("未勾选中数据");
        }
        // 申请单
        List<RequestNote> requestNoteList = requestNoteMapper.selectBatchIds(ids);
        List<String> idList = requestNoteList.stream().map(RequestNote::getId).collect(Collectors.toList());
        List<String> trackHeadIdList = requestNoteList.stream().map(RequestNote::getTrackHeadId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(requestNoteList)) {
            QueryWrapper<RequestNoteDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("note_id", idList);
            // 申请单详情
            List<RequestNoteDetail> requestNoteDetailList = requestNoteDetailService.list(queryWrapper);
            if (!CollectionUtils.isEmpty(requestNoteDetailList)) {
                QueryWrapper<TrackHead> trackHeadQueryWrapper = new QueryWrapper<>();
                trackHeadQueryWrapper.in("id", trackHeadIdList);
                // 跟单表
                List<TrackHead> trackHeadList = trackHeadService.list(trackHeadQueryWrapper);
                if (!CollectionUtils.isEmpty(trackHeadList)) {
                    requestNoteList.forEach(e -> {
                        LambdaQueryWrapper<TrackHead> requestNoteQueryWrapper = new LambdaQueryWrapper<>();
                        requestNoteQueryWrapper.eq(TrackHead::getId, e.getTrackHeadId());
                        TrackHead trackHead = trackHeadMapper.selectOne(requestNoteQueryWrapper);
                        if (StringUtils.isEmpty(trackHead)) {
                            e.setWorkNo(null);
                            e.setProductionOrder(null);
                        } else {
                            e.setWorkNo(trackHead.getWorkNo());
                            e.setProductionOrder(trackHead.getProductionOrder());
                        }
                    });
                    Set<String> materialNoSet = requestNoteDetailList.stream().map(RequestNoteDetail::getMaterialNo).collect(Collectors.toSet());
                    ProductTypeDto productTypeDto = new ProductTypeDto();
                    productTypeDto.setMaterialNoSet(materialNoSet);
                    // 物料管理表
                    Map<String, Product> productMap = baseServiceClient.selectConditionProduct(productTypeDto).getData().stream().collect(Collectors.toMap(Product::getMaterialNo, x -> x, (value1, value2) -> value2));
                    // 查询所有的租户信息
                    Map<String, Tenant> tenantMap = systemServiceClient.queryTenantAllList().getData().stream().collect(Collectors.toMap(Tenant::getId, x -> x, (value1, value2) -> value2));
                    if(!CollectionUtils.isEmpty(tenantMap)) {
                        List<ApplyListUpload> uploadList = new ArrayList<>();
                        for (RequestNote requestNote : requestNoteList) {
                            ApplyListUpload applyListUpload = new ApplyListUpload();
                            applyListUpload.setId(requestNote.getId());
                            applyListUpload.setApplyNum(requestNote.getRequestNoteNumber());
                            applyListUpload.setWorkshop(requestNote.getBranchCode());
                            if (StringUtils.isEmpty(requestNote.getTenantId())) {
                                applyListUpload.setWorkCode(null);
                            } else {
                                applyListUpload.setWorkCode(tenantMap.get(requestNote.getTenantId()).getTenantErpCode());
                            }
                            applyListUpload.setJobNo(requestNote.getWorkNo());
                            applyListUpload.setProdNum(requestNote.getProductionOrder());
                            applyListUpload.setCreateBy(requestNote.getCreateBy());
                            applyListUpload.setCreateTime(requestNote.getCreateTime());
                            int num = 0;
                            List<ApplyLineList> applyLineList = new ArrayList<>();
                            for (RequestNoteDetail requestNoteDetail : requestNoteDetailList) {
                                ApplyLineList applyLine = new ApplyLineList();
                                if (requestNote.getId().equals(requestNoteDetail.getNoteId())) {
                                    applyLine.setApplyId(requestNoteDetail.getNoteId());
                                    applyLine.setId(requestNoteDetail.getId());
                                    applyLine.setLineNum(num + 1);
                                    applyLine.setMaterialNum(requestNoteDetail.getMaterialNo());
                                    applyLine.setMaterialDesc(requestNoteDetail.getMaterialName());
                                    applyLine.setUnit(requestNoteDetail.getUnit());
                                    applyLine.setQuantity(requestNoteDetail.getNumber());
                                    if (StringUtils.isEmpty(requestNoteDetail.getMaterialNo())) {
                                        applyLine.setMaterialType(null);
                                    } else {
                                        applyLine.setMaterialType(MaterialTypeEnum.getName(productMap.get(requestNoteDetail.getMaterialNo()).getMaterialType()));
                                    }
                                    applyLine.setCrucialFlag(requestNoteDetail.getIsKeyPart());
                                    applyLineList.add(applyLine);
                                    num ++;
                                }
                            }
                            applyListUpload.setLineList(applyLineList);
                            uploadList.add(applyListUpload);
                        }
                        wmsServiceClient.applyListUpload(uploadList);
                        return CommonResult.success(true, "申请单上传成功");
                    }
                }
            }
        }
        return CommonResult.failed("申请单上传失败，请稍后再试");
    }
}
