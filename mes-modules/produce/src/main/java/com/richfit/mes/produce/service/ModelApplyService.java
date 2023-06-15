package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.produce.ModelApply;

import java.util.List;

/**
 * (ModelApply)表服务接口
 *
 * @author makejava
 * @since 2023-04-23 14:49:09
 */
public interface ModelApplyService extends IService<ModelApply> {

    CommonResult<Boolean> applyModel(String branchCode, List<TrackItem> itemInfo);

    Boolean delivery(List<ModelApply> modelApplyList);

    Boolean sendBack(List<ModelApply> modelApplyList);

    CommonResult<Page<ModelApply>> getPageInfo(int sign, String branchCode, String drawingNo, String startTime, String endTime, int page, int limit);

    Boolean deliveryActive(List<HotModelStore> hotModelStoreList);

    CommonResult<Boolean> applyModelNew(String branchCode, List<TrackItem> itemInfo);

    CommonResult<Page<ModelApply>> getPageInfoNew(int sign, String branchCode, String drawingNo, String startTime, String endTime, int page, int limit);

    Boolean deliveryNew(String modelId, String modelApplyId);
}

