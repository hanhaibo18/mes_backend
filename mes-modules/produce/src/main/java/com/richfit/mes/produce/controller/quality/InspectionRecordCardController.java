package com.richfit.mes.produce.controller.quality;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.quality.QualityTrackHead;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhiqiang.lu
 * @Date: 2022/8/23 9:33
 */
@Slf4j
@Api(tags = "检验记录卡")
@RestController
@RequestMapping("/api/produce/inspection_record_card")
public class InspectionRecordCardController extends BaseController {
    @Autowired
    private TrackHeadFlowService trackFlowService;

    @ApiOperation(value = "检测记录卡生产跟单分页", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询检测记录卡生产跟单信息")
    @GetMapping("/track_flow_page")
    public CommonResult<PageInfo<QualityTrackHead>> selectTrackFLowPage(@ApiParam(value = "开始时间") @RequestParam(required = false) String startDate,
                                                                        @ApiParam(value = "结束时间") @RequestParam(required = false) String endDate,
                                                                        @ApiParam(value = "打印模板编码") @RequestParam(required = false) String templateCode,
                                                                        @ApiParam(value = "跟单状态") @RequestParam(required = false) String status,
                                                                        @ApiParam(value = "完工资料生成") @RequestParam(required = false) String isCompletionData,
                                                                        @ApiParam(value = "合格证生成/Y以生产 N未生成") @RequestParam(required = false) String isCertificate,
                                                                        @ApiParam(value = "产品编码") @RequestParam(required = false) String productNo,
                                                                        @ApiParam(value = "跟单编码") @RequestParam(required = false) String trackNo,
                                                                        @ApiParam(value = "工作号") @RequestParam(required = false) String workNo,
                                                                        @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                                        @ApiParam(value = "炉批号") @RequestParam(required = false) String batchNo,
                                                                        @ApiParam(value = "生成订单号") @RequestParam(required = false) String productionOrder,
                                                                        @ApiParam(value = "计划id") @RequestParam(required = false) String workPlanId,
                                                                        @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode,
                                                                        @ApiParam(value = "页码") @RequestParam(required = false) int page,
                                                                        @ApiParam(value = "条数") @RequestParam(required = false) int limit) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("templateCode", templateCode);
        map.put("status", status);
        map.put("isCompletionData", isCompletionData);
        map.put("isCertificate", isCertificate);
        map.put("productNo", productNo);
        map.put("trackNo", trackNo);
        map.put("workNo", workNo);
        map.put("drawingNo", drawingNo);
        map.put("batchNo", batchNo);
        map.put("productionOrder", productionOrder);
        map.put("workPlanId", workPlanId);
        map.put("branchCode", branchCode);
        PageHelper.startPage(page, limit);
        List<TrackHead> trackFlowList = trackFlowService.selectTrackFlowList(map);
        PageInfo<QualityTrackHead> trackFlowPage = new PageInfo(trackFlowList);
        return CommonResult.success(trackFlowPage);
    }
}
