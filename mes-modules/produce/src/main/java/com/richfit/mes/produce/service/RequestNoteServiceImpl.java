package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RequestNoteMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;


@Service
@Transactional
public class RequestNoteServiceImpl extends ServiceImpl<RequestNoteMapper, RequestNote> implements RequestNoteService {

    @Resource
    private TrackAssemblyService trackAssemblyService;

    @Resource
    private RequestNoteDetailService requestNoteDetailService;

    @Resource
    private TrackHeadService trackHeadService;

    @Resource
    private BaseServiceClient baseServiceClient;

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
    public boolean saveRequestNoteInfo(ApplyListUpload applyListUpload, TrackHead trackHead,TrackItem trackItem, String branchCode) {
        RequestNote requestNote = new RequestNote();
        //MES申请单ID 唯一
        requestNote.setId(applyListUpload.getId());
        //跟单Id
        requestNote.setTrackHeadId(trackHead.getId());
        //存入跟单编号
        requestNote.setTrackNo(trackHead.getTrackNo());
        //工序Id
        requestNote.setTrackItemId(trackItem.getId());
        //申请单号
        requestNote.setRequestNoteNumber(applyListUpload.getApplyNum());
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
            //申请单号
            requestNoteDetail.setRequestNoteNumber(applyListUpload.getApplyNum());
            // 物料号
            requestNoteDetail.setMaterialNo(assembly.getMaterialNo());
            //根据物料号查询物料
            List<Product> list = baseServiceClient.listByMaterialNo(assembly.getMaterialNo());
            if (!CollectionUtils.isEmpty(list)) {
                requestNoteDetail.setMaterialName(list.get(0).getProductName());
            }
            requestNoteDetail.setDrawingNo(assembly.getDrawingNo());
            requestNoteDetail.setUnit(assembly.getUnit());
            requestNoteDetail.setNumber(Double.valueOf(assembly.getNumber()));
            requestNoteDetail.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
            requestNoteDetail.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            requestNoteDetail.setIsNeedPicking(assembly.getIsNeedPicking());
            requestNoteDetail.setIsKeyPart(assembly.getIsKeyPart());
            requestNoteDetail.setIsEdgeStore(assembly.getIsEdgeStore());
            requestNoteDetailService.save(requestNoteDetail);
        });
        return save;
    }

}
