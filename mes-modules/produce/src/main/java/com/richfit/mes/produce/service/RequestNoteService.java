package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.model.produce.LineList;
import com.richfit.mes.common.model.produce.RequestNote;
import com.richfit.mes.common.model.produce.TrackHead;

import java.util.List;

public interface RequestNoteService extends IService<RequestNote> {

    boolean saveRequestNote(IngredientApplicationDto ingredient, List<LineList> lineLists, String branchCode);

    boolean saveRequestNoteNew(IngredientApplicationDto ingredient, TrackHead trackHead, String branchCode);

    /**
     * 申请单上传wms
     *
     * @param ids
     * @return
     */
    CommonResult<Boolean> uploadRequestNote(List<String> ids);
}
