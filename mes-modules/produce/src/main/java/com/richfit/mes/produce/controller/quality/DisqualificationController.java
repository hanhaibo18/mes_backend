package com.richfit.mes.produce.controller.quality;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.common.model.produce.DisqualificationFinalResult;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.quality.*;
import com.richfit.mes.produce.service.quality.DisqualificationAttachmentService;
import com.richfit.mes.produce.service.quality.DisqualificationFinalResultService;
import com.richfit.mes.produce.service.quality.DisqualificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DisqualificationController.java
 * @Author: Hou XinYu
 * @Description: 不合格品处理流程
 * @CreateTime: 2022年09月30日 14:40:00
 */
@Slf4j
@Api(value = "不合格品处理流程", tags = {"不合格品处理流程"})
@RestController
@RequestMapping("/api/produce/quality/disqualification")
public class DisqualificationController extends BaseController {

    @Resource
    private DisqualificationService disqualificationService;

    @Resource
    private DisqualificationAttachmentService attachmentService;

    @Resource
    private DisqualificationFinalResultService finalResultService;


    @ApiOperation(value = "待处理申请单", notes = "根据查询条件查询待处理申请单")
    @PostMapping("/queryInspector")
    public CommonResult<IPage<Disqualification>> queryInspector(@RequestBody QueryInspectorDto queryInspectorDto) {
        return CommonResult.success(disqualificationService.queryInspector(queryInspectorDto));
    }

    @ApiOperation(value = "该公司所有待处理申请单", notes = "根据查询条件查询待处理申请单")
    @PostMapping("/queryInspectorByCompany")
    public CommonResult<IPage<Disqualification>> queryInspectorByCompany(@RequestBody QueryInspectorDto queryInspectorDto) {
        return CommonResult.success(disqualificationService.queryInspectorByCompany(queryInspectorDto));
    }

    @ApiOperation(value = "创建/修改申请单", notes = "创建或修改不合格申请单接口")
    @PostMapping("/saveDisqualification")
    public CommonResult<Boolean> saveDisqualification(@RequestBody DisqualificationDto disqualification) {
        return CommonResult.success(disqualificationService.saveOrUpdateDisqualification(disqualification));
    }

    @ApiOperation(value = "关单", notes = "关闭申请单")
    @ApiImplicitParam(name = "id", value = "申请单Id", required = true, paramType = "query", dataType = "String")
    @GetMapping("/closeApplication")
    public CommonResult<Boolean> closeApplication(String id) {
        return CommonResult.success(disqualificationService.updateIsIssue(id));
    }

    @ApiOperation(value = "查询申请单信息(新)", notes = "根据工序Id查询申请单所用参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchCode", value = "车间", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "disqualificationId", value = "申请单Id", paramType = "query", dataType = "string")
    })
    @GetMapping("/queryItemNew")
    public CommonResult<DisqualificationItemVo> queryItem(String branchCode, String id) {
        return CommonResult.success(disqualificationService.inquiryRequestFormNew(id, branchCode));
    }

    @ApiOperation(value = "查询申请单信息", notes = "根据工序Id查询申请单所用参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchCode", value = "车间", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "disqualificationId", value = "申请单Id", paramType = "query", dataType = "string")
    })
    @GetMapping("/queryItem")
    public CommonResult<DisqualificationItemVo> queryItem(String tiId, String branchCode, String disqualificationId) {
        return CommonResult.success(disqualificationService.inquiryRequestForm(tiId, branchCode, disqualificationId));
    }

    @ApiOperation(value = "删除不合格记录", notes = "删除不合格记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "disqualificationId", value = "申请单Id", paramType = "query", dataType = "string")
    })
    @DeleteMapping("/delete/{disqualificationId}")
    public CommonResult<String> delete(@PathVariable String disqualificationId) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return CommonResult.failed("未检测到当前登录用户信息！");
        }
        Disqualification disqualification = disqualificationService.getById(disqualificationId);
        if (ObjectUtils.isEmpty(disqualification)) {
            return CommonResult.failed("没有找到该不合格记录！");
        } else if (!disqualification.getCreateBy().equals(currentUser.getUsername())) {
            return CommonResult.failed("您不能删除不是您创建的记录！");
        } else if (null != disqualification.getProcessSheetNo()) {
            return CommonResult.failed("已申请处理单号，不能删除！");
        }
        return CommonResult.success(disqualificationService.deleteById(disqualificationId));
    }

    @ApiOperation(value = "查询质量检测部", notes = "第一次提交申请单查询质量检测部人员")
    @GetMapping("/queryUser")
    public CommonResult<List<TenantUserVo>> queryUser() {
        return CommonResult.success(disqualificationService.queryUser());
    }

    @ApiOperation(value = "查询质量检测", notes = "质检人员查询不合格品处理单查询接口")
    @PostMapping("/queryCheck")
    public CommonResult<IPage<Disqualification>> queryCheck(@RequestBody QueryCheckDto queryCheckDto) {
        return CommonResult.success(disqualificationService.queryCheck(queryCheckDto));
    }

    @ApiOperation(value = "查询处理单位", notes = "查询质量检测部")
    @PostMapping("/query_deal_with")
    public CommonResult<IPage<Disqualification>> queryDealWith(@RequestBody QueryCheckDto queryCheckDto) {
        return CommonResult.success(disqualificationService.queryDealWith(queryCheckDto));
    }

    @ApiOperation(value = "查询责任裁决", notes = "查询责任裁决")
    @PostMapping("/query_responsibility")
    public CommonResult<IPage<Disqualification>> queryResponsibility(@RequestBody QueryCheckDto queryCheckDto) {
        return CommonResult.success(disqualificationService.queryResponsibility(queryCheckDto));
    }

    @ApiOperation(value = "查询技术裁决", notes = "查询技术裁决")
    @PostMapping("/query_technology")
    public CommonResult<IPage<Disqualification>> queryTechnology(@RequestBody QueryCheckDto queryCheckDto) {
        return CommonResult.success(disqualificationService.queryTechnology(queryCheckDto));
    }


    @ApiOperation(value = "保存最终结果", notes = "保存最终结果")
    @PostMapping("/saveFinalResult")
    public CommonResult<Boolean> saveFinalResult(@RequestBody DisqualificationFinalResultDto disqualificationFinalResult) {
        return CommonResult.success(finalResultService.saveDisqualificationFinalResult(disqualificationFinalResult));
    }

    @ApiOperation(value = "查询产品编号列表", notes = "查询产品编号列表")
    @GetMapping("/queryProductNoList")
    public CommonResult<List<Map<String, String>>> queryProductNoList(String trackHeadId) {
        return CommonResult.success(disqualificationService.queryProductNoList(trackHeadId));
    }

    @ApiOperation(value = "回滚", notes = "回滚")
    @GetMapping("/roll_back")
    public CommonResult<Boolean> rollBack(String id, Integer type) {
        return CommonResult.success(disqualificationService.rollBack(id, type));
    }

    @ApiOperation(value = "回滚All", notes = "回滚全部")
    @GetMapping("/roll_back_all")
    public CommonResult<Boolean> rollBackAll(String id) {
        return CommonResult.success(disqualificationService.rollBackAll(id));
    }

    @ApiOperation(value = "打回", notes = "打回")
    @GetMapping("/send_back")
    public CommonResult<Boolean> sendBack(String id, Integer type) {
        return CommonResult.success(disqualificationService.sendBack(id, type));
    }

    @ApiOperation(value = "查询上一次填写记录", notes = "查询上一次填写记录")
    @GetMapping("/query_last_time")
    public CommonResult<DisqualificationItemVo> queryLastTimeDataByCreateBy(String branchCode) {
        return CommonResult.success(disqualificationService.queryLastTimeDataByCreateBy(branchCode));
    }
    @ApiOperation(value = "不合格导出", notes = "不合格导出")
    @PostMapping("/export")
    public void exportDisqualification(HttpServletResponse rsp,@RequestBody List<Disqualification> disqualificationList) {
        try {
            if (!CollectionUtils.isNotEmpty(disqualificationList)) {
                return;
            }
            List<String> idList = disqualificationList.stream().map(e -> e.getId()).collect(Collectors.toList());
            QueryWrapper<DisqualificationFinalResult> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.in("id", idList);
            List<DisqualificationFinalResult> list = finalResultService.list(objectQueryWrapper);
            Map<String, DisqualificationFinalResult> resultMap = list.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
            if (CollectionUtils.isNotEmpty(list)) {
                // 读文件
                ClassPathResource classPathResource = new ClassPathResource("excel/" + "disqualificationTemplate.xlsx");
                ExcelWriter writer = null;
                writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
                writer.writeCellValue("A1", "共搜索到"+ disqualificationList.size() +"条符合条件的信息");
                writer.resetRow();
                writer.passRows(5);
                for (Disqualification disqualification: disqualificationList) {
                    disqualification.setUnitResponsibilityOutside(resultMap.get(disqualification.getId()).getUnitResponsibilityOutside());
                    disqualification.setUnitResponsibilityWithin(resultMap.get(disqualification.getId()).getUnitResponsibilityWithin());
                    disqualification.setTotalWeight(resultMap.get(disqualification.getId()).getTotalWeight());
                    disqualification.setUnitTreatmentOne(resultMap.get(disqualification.getId()).getUnitTreatmentOne());
                    disqualification.setUnitTreatmentTwo(resultMap.get(disqualification.getId()).getUnitTreatmentTwo());
                    disqualification.setDiscoverItem(resultMap.get(disqualification.getId()).getDiscoverItem());
                    disqualification.setDiscardTime(resultMap.get(disqualification.getId()).getDiscardTime());
                    disqualification.setReuseTime(resultMap.get(disqualification.getId()).getReuseTime());
                    disqualification.setAcceptDeviation(resultMap.get(disqualification.getId()).getAcceptDeviation());
                    disqualification.setRepairQualified(resultMap.get(disqualification.getId()).getRepairQualified());
                    disqualification.setScrap(resultMap.get(disqualification.getId()).getScrap());
                    disqualification.setSalesReturn(resultMap.get(disqualification.getId()).getSalesReturn());
                    disqualification.setSalesReturnLoss(resultMap.get(disqualification.getId()).getSalesReturnLoss());
                    disqualification.setTreatmentOneName(resultMap.get(disqualification.getId()).getTreatmentOneName());
                    disqualification.setTreatmentTwoName(resultMap.get(disqualification.getId()).getTreatmentTwoName());
                    disqualification.setResponsibilityName(resultMap.get(disqualification.getId()).getResponsibilityName());
                    disqualification.setTechnologyName(resultMap.get(disqualification.getId()).getTechnologyName());
                }
                int currentRow = writer.getCurrentRow();
                for (Disqualification disqualification: disqualificationList) {
                    writer.writeCellValue(1, currentRow, disqualification.getCreateBy());
                    writer.writeCellValue(2, currentRow, disqualification.getCreateTime());
                    writer.writeCellValue(3, currentRow, disqualification.getBranchCode());
                    writer.writeCellValue(4, currentRow, disqualification.getProcessSheetNo());
                    writer.writeCellValue(5, currentRow, disqualification.getMissiveBranch());
                    writer.writeCellValue(6, currentRow, disqualification.getUnitResponsibilityWithin());
                    writer.writeCellValue(7, currentRow, disqualification.getUnitResponsibilityOutside());
                    writer.writeCellValue(8, currentRow, disqualification.getWorkNo());
                    writer.writeCellValue(9, currentRow, disqualification.getProductName());
                    writer.writeCellValue(10, currentRow, disqualification.getPartName());
                    writer.writeCellValue(11, currentRow, disqualification.getPartDrawingNo());
                    writer.writeCellValue(12, currentRow, disqualification.getProductNo());
                    writer.writeCellValue(13, currentRow, disqualification.getPartMaterials());
                    writer.writeCellValue(14, currentRow, disqualification.getNumber());
                    writer.writeCellValue(15, currentRow, disqualification.getTotalWeight());
                    writer.writeCellValue(16, currentRow, disqualification.getDisqualificationCondition());
                    writer.writeCellValue(17, currentRow, disqualification.getQualityCheckBy());
                    writer.writeCellValue(18, currentRow, disqualification.getUnitTreatmentOne());
                    writer.writeCellValue(19, currentRow, disqualification.getUnitTreatmentTwo());
                    writer.writeCellValue(20, currentRow, disqualification.getDiscoverItem());
                    writer.writeCellValue(21, currentRow, disqualification.getReuseTime());
                    writer.writeCellValue(22, currentRow, disqualification.getAcceptDeviation());
                    writer.writeCellValue(23, currentRow, disqualification.getRepairQualified());
                    writer.writeCellValue(24, currentRow, disqualification.getScrap());
                    writer.writeCellValue(25, currentRow, disqualification.getSalesReturn());
                    writer.writeCellValue(26, currentRow, disqualification.getSalesReturnLoss());
                    writer.writeCellValue(27, currentRow, disqualification.getCloseTime());
                    writer.writeCellValue(28, currentRow, disqualification.getTreatmentOneName());
                    writer.writeCellValue(29, currentRow, disqualification.getTreatmentTwoName());
                    writer.writeCellValue(30, currentRow, disqualification.getResponsibilityName());
                    writer.writeCellValue(31, currentRow, disqualification.getTechnologyName());
                    currentRow++;

                }
                rsp.setContentType("application/octet-stream");
                rsp.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("不合格品处理单查询结果.xlsx", "UTF-8"));
                ServletOutputStream outputStream = rsp.getOutputStream();
                writer.flush(outputStream, true);
                IoUtil.close(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
