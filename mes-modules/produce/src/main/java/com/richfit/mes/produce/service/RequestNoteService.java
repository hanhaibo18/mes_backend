package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.model.wms.MaterialRequisitionUpload;

import java.util.List;

public interface RequestNoteService extends IService<RequestNote> {

    boolean saveRequestNote(IngredientApplicationDto ingredient, List<LineList> lineLists, String branchCode);

    boolean saveRequestNoteNew(List<MaterialRequisitionUpload> materialRequisitionUploads, TrackHead trackHead, String branchCode, String tiId);

    boolean saveRequestNoteInfo(ApplyListUpload applyListUpload, TrackHead trackHead, TrackItem trackItem, String branchCode);

}
