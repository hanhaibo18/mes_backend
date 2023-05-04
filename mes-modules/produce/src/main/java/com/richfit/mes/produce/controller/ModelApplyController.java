package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.produce.ModelApply;
import com.richfit.mes.produce.service.ModelApplyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (ModelApply)表控制层
 *
 * @author makejava
 * @since 2023-04-23 14:49:09
 */
@RestController
@RequestMapping("/api/produce/model_apply")
public class ModelApplyController extends ApiController {
    /**
     * 服务对象
     */
    @Autowired
    private ModelApplyService modelApplyService;

    /**
     * 根据图号请求模型
     *
     * @param branchCode 车间编码
     * @return
     */
    @ApiOperation(value = "根据图号版本号请求模型")
    @PostMapping("/apply_model")
    public CommonResult<Boolean> applyModel(@ApiParam(value = "车间编码") @RequestParam String branchCode,
                                            @ApiParam(value = "工序信息") @RequestBody List<TrackItem> itemInfo) {
        return modelApplyService.applyModel(branchCode, itemInfo);
    }

    /**
     * 分页展示模型请求
     */
    @ApiOperation(value = "分页展示模型请求")
    @GetMapping("/get_model_apply")
    public CommonResult<Page<ModelApply>> getModelApply(@ApiParam(value = "展示标识：未配送0，已配送1") @RequestParam(defaultValue = "0") int sign,
                                                        @ApiParam(value = "车间编码") @RequestParam String branchCode,
                                                        @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                        @ApiParam(value = "请求日期开始") @RequestParam(required = false) String startTime,
                                                        @ApiParam(value = "请求日期结束") @RequestParam(required = false) String endTime,
                                                        @ApiParam(value = "页数") @RequestParam(defaultValue = "1") int page,
                                                        @ApiParam(value = "每页数量") @RequestParam(defaultValue = "10") int limit) {
        return modelApplyService.getPageInfo(sign,branchCode,drawingNo,startTime,endTime,page,limit);
    }

    @ApiOperation(value = "配送确认")
    @PostMapping("/delivery")
    public CommonResult<Boolean> delivery(@ApiParam(value = "模型请求实体") @RequestBody List<ModelApply> modelApplyList) {
        return CommonResult.success(modelApplyService.delivery(modelApplyList));
    }

    @ApiOperation(value = "模型退库")
    @PostMapping("/sent_back")
    public CommonResult<Boolean> sendBack(@ApiParam(value = "模型请求实体") @RequestBody List<ModelApply> modelApplyList) {
        return CommonResult.success(modelApplyService.sendBack(modelApplyList));
    }

    @ApiOperation(value = "模型车间主动配送")
    @PostMapping("/delivery_active")
    public CommonResult<Boolean> deliveryActive(@ApiParam("模型实体") @RequestBody List<HotModelStore> hotModelStoreList){
        return CommonResult.success(modelApplyService.deliveryActive(hotModelStoreList));
    }

}

