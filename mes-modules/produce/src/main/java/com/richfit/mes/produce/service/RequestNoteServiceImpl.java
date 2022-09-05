package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.RequestNoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class RequestNoteServiceImpl extends ServiceImpl<RequestNoteMapper, RequestNote> implements RequestNoteService {

    @Resource
    private TrackAssemblyService trackAssemblyService;

    @Resource
    private RequestNoteDetailService requestNoteDetailService;

    @Resource
    private TrackHeadService trackHeadService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRequestNote(IngredientApplicationDto ingredient, List<LineList> lineLists) {
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
        requestNote.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
        //所属租户
        requestNote.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        boolean save = this.save(requestNote);
        if (save) {
            lineLists.forEach(i -> {
                RequestNoteDetail requestNoteDetail = new RequestNoteDetail();
                //申请单id
                requestNoteDetail.setNoteId(requestNote.getId());
                requestNoteDetail.setMaterialNo(i.getMaterialNum());
                requestNoteDetail.setMaterialName(i.getMaterialDesc());
                QueryWrapper<TrackAssembly> wrapper = new QueryWrapper<>();
                wrapper.eq("material_no", requestNoteDetail.getMaterialNo());
                wrapper.eq("track_head_id", requestNote.getTrackHeadId());
                TrackAssembly one = trackAssemblyService.getOne(wrapper);
                requestNoteDetail.setDrawingNo(one.getDrawingNo());
                requestNoteDetail.setUnit(one.getUnit());
                requestNoteDetail.setNumber(i.getQuantity());
                requestNoteDetail.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
                requestNoteDetail.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                requestNoteDetail.setIsNeedPicking(one.getIsNeedPicking());
                requestNoteDetail.setIsKeyPart(one.getIsKeyPart());
                requestNoteDetail.setIsEdgeStore(one.getIsEdgeStore());
                requestNoteDetailService.save(requestNoteDetail);
            });
            return true;
        } else {
            return false;
        }
    }
}
