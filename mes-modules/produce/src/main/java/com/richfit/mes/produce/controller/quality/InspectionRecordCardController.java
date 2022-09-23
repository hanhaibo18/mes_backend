package com.richfit.mes.produce.controller.quality;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCardContent;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.quality.QualityTrackHead;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import com.richfit.mes.produce.service.quality.ProduceInspectionRecordCardService;
import com.richfit.mes.produce.utils.InspectionRecordCardUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhiqiang.lu
 * @Date: 2022.8.23
 * @LastEditors: zhiqiang.lu
 * @LastEditTime: 2022.9.21
 * @Description: 检验记录卡
 * @LastEdit: 添加注释
 */
@Slf4j
@Api(tags = "检验记录卡")
@RestController
@RequestMapping("/api/produce/inspection_record_card")
public class InspectionRecordCardController extends BaseController {
    @Autowired
    private TrackHeadFlowService trackFlowService;

    @Autowired
    private ProduceInspectionRecordCardService produceInspectionRecordCardService;


    @ApiOperation(value = "检测记录卡生产跟单分页", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询检测记录卡生产跟单信息")
    @GetMapping("/track_flow_page")
    public CommonResult<PageInfo<QualityTrackHead>> selectTrackFLowPage(@ApiParam(value = "开始时间") @RequestParam(required = false) String startDate,
                                                                        @ApiParam(value = "结束时间") @RequestParam(required = false) String endDate,
                                                                        @ApiParam(value = "检验记录卡审核状态  Y已审核 N审核不通过") @RequestParam(required = false) String isExamineCardData,
                                                                        @ApiParam(value = "检验记录卡生成状态  Y已生成") @RequestParam(required = false) String isCardData,
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
                                                                        @ApiParam(value = "工厂类型") @RequestParam(required = false) String classes,
                                                                        @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode,
                                                                        @ApiParam(value = "租户id") @RequestParam(required = false) String tenantId,
                                                                        @ApiParam(value = "页码") @RequestParam(required = false) int page,
                                                                        @ApiParam(value = "条数") @RequestParam(required = false) int limit) {
        Map<String, String> map = new HashMap<>();
        TrackFlow.param(startDate,
                endDate,
                isExamineCardData,
                isCardData,
                templateCode,
                status,
                isCompletionData,
                isCertificate,
                productNo,
                trackNo,
                workNo,
                drawingNo,
                batchNo,
                productionOrder,
                workPlanId,
                classes,
                branchCode,
                tenantId, map);
        PageHelper.startPage(page, limit);
        List<TrackHead> trackFlowList = trackFlowService.selectTrackFlowList(map);
        PageInfo<QualityTrackHead> trackFlowPage = new PageInfo(trackFlowList);
        log.debug("inspection_record_card trackFlowPage return is [{}]", trackFlowPage);
        return CommonResult.success(trackFlowPage);
    }

    @ApiOperation(value = "质量检测卡审核功能", notes = "质量检测卡审核功能")
    @PostMapping("/examine")
    public void examine(@ApiParam(value = "生成线id") @RequestParam(required = false) String flowId,
                        @ApiParam(value = "是否通过：Y通过，N不通过") @RequestParam(required = false) String approved) {
        trackFlowService.examineCard(flowId, approved);
        log.debug("inspection_record_card examine is return [{}]", flowId + ":" + approved);
    }

    @ApiOperation(value = "质量检测卡保存", notes = "质量检测卡保存")
    @PostMapping("/save")
    public void save(@ApiParam(value = "质量检测卡信息", required = true) @RequestBody ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCardService.saveProduceInspectionRecordCard(produceInspectionRecordCard);
        log.debug("inspection_record_card save is params [{}]", produceInspectionRecordCard);
    }

    @ApiOperation(value = "质量检测卡更新", notes = "质量检测卡更新")
    @PostMapping("/update")
    public void update(@ApiParam(value = "质量检测卡信息", required = true) @RequestBody ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCardService.updateProduceInspectionRecordCard(produceInspectionRecordCard);
        log.debug("inspection_record_card update is params [{}]", produceInspectionRecordCard);
    }

    @ApiOperation(value = "质量检测卡全部信息查询", notes = "质量检测卡全部信息查询")
    @GetMapping("/select")
    public CommonResult<ProduceInspectionRecordCard> select(@ApiParam(value = "质量检测卡id/flowID", required = true) @RequestParam String flowId) throws Exception {
        try {
            ProduceInspectionRecordCard produceInspectionRecordCard = produceInspectionRecordCardService.selectProduceInspectionRecordCard(flowId);
            log.debug("inspection_record_card select is params [{}]", flowId);
            log.debug("inspection_record_card select is return [{}]", produceInspectionRecordCard);
            return CommonResult.success(produceInspectionRecordCard);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("inspection_record_card select is error [{}]", e.getMessage());
            throw new Exception("信息所在工厂" + e.getMessage());
        }
    }

    @ApiOperation(value = "工序质量检测卡列表查询", notes = "工序质量检测卡列表查询")
    @GetMapping("/select_item_check_list")
    public CommonResult<List<ProduceInspectionRecordCardContent>> selectItemCheckList(@ApiParam(value = "质量检测卡id/flowID，如果itemId有值flowId可以为null", required = true) @RequestParam String flowId,
                                                                                      @ApiParam(value = "工序id，null为查询全部工序质检信息", required = true) @RequestParam String itemId) throws Exception {
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = produceInspectionRecordCardService.selectItemCheckList(flowId, itemId);
        log.debug("inspection_record_card select_item_check_list is params [{}]", flowId);
        log.debug("inspection_record_card select_item_check_list is return [{}]", produceInspectionRecordCardContentList);
        return CommonResult.success(produceInspectionRecordCardContentList);
    }

    @ApiOperation(value = "质量检测卡明细信息更新", notes = "质量检测卡明细信息更新")
    @PostMapping("/update_Track_check_detail")
    public void updateTrackCheckDetail(@ApiParam(value = "质量检测卡明细信息", required = true) @RequestBody ProduceInspectionRecordCardContent produceInspectionRecordCardContent) {
        produceInspectionRecordCardService.updateTrackCheckDetail(produceInspectionRecordCardContent);
        log.debug("inspection_record_card update is params [{}]", produceInspectionRecordCardContent);
    }

    @ApiOperation(value = "质量检测卡excel", notes = "质量检测卡excel")
    @GetMapping("/excel")
    public void excel(
            HttpServletResponse response,
            @ApiParam(value = "质量检测卡id/flowID", required = true) @RequestParam String flowId) throws Exception {
        try {
            ProduceInspectionRecordCard produceInspectionRecordCard = produceInspectionRecordCardService.selectProduceInspectionRecordCard(flowId);
            log.debug("inspection_record_card excel is params [{}]", flowId);
            log.debug("inspection_record_card excel is return [{}]", produceInspectionRecordCard);
            //excel封装工具类
            ExcelWriter writer = InspectionRecordCardUtil.excel(produceInspectionRecordCard);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=test.xlsx");
            writer.flush(outputStream, true);
            writer.close();
            IoUtil.close(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("inspection_record_card select is error [{}]", e.getMessage());
            throw new Exception("信息所在工厂" + e.getMessage());
        }
    }
}
