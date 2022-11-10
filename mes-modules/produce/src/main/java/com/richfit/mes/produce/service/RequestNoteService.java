package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.model.produce.LineList;
import com.richfit.mes.common.model.produce.RequestNote;

import java.util.List;

public interface RequestNoteService extends IService<RequestNote> {

    boolean saveRequestNote(IngredientApplicationDto ingredient, List<LineList> lineLists, String branchCode);
}
