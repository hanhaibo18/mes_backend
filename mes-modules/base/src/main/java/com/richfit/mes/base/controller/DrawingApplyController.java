package com.richfit.mes.base.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.entity.param.ExamineDrawingApplyParam;
import com.richfit.mes.base.service.DrawingApplyService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.DrawingApply;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 图纸申请Controller
 */
@Slf4j
@Api("图纸申请控制器")
@RestController
@RequestMapping("/api/base/drawing_apply")
public class DrawingApplyController extends BaseController {

    public static String DRAWING_APPLY_NO_NULL_MESSAGE = "图号不能为空！";
    public static String DRAWING_APPLY_ID_NULL_MESSAGE = "图纸申请ID不能为空！";
    public static String DRAWING_APPLY_SUCCESS_MESSAGE = "操作成功!";
    public static String DRAWING_APPLY_FAILED_MESSAGE = "操作失败，请重试！";
    public static String DRAWING_APPLY_EXCEPTION_MESSAGE = "操作失败：";
    public static String DRAWING_APPLY_IMPORT_EXCEL_SUCCESS_MESSAGE = "导入成功!";

    @Autowired
    private DrawingApplyService drawingApplyService;

    @ApiOperation(value = "新增图纸申请", notes = "新增图纸申请")
    @PostMapping("/manage")
    public CommonResult<DrawingApply> addDrawingApply(@RequestBody DrawingApply drawingApply) {
        if (StringUtils.isNullOrEmpty(drawingApply.getDrawingNo())) {
            return CommonResult.failed(DRAWING_APPLY_NO_NULL_MESSAGE);
        } else {
            QueryWrapper<DrawingApply> queryWrapper = new QueryWrapper<DrawingApply>();
            queryWrapper.eq("drawing_no", drawingApply.getDrawingNo());
            queryWrapper.eq("branch_code", drawingApply.getBranchCode());
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            DrawingApply oldApply = drawingApplyService.getOne(queryWrapper);
            if (oldApply != null && !StringUtils.isNullOrEmpty(oldApply.getId())) {
                return CommonResult.failed("已有该图号的申请！");
            } else {
                drawingApply.setStatus("0");
                drawingApply.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                drawingApply.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                boolean bool = drawingApplyService.save(drawingApply);
                if (bool) {
                    return CommonResult.success(drawingApply, DRAWING_APPLY_SUCCESS_MESSAGE);
                } else {
                    return CommonResult.failed(DRAWING_APPLY_FAILED_MESSAGE);
                }
            }
        }
    }

    @ApiOperation(value = "导入图纸申请", notes = "根据Excel文档导入导入图纸申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/importExcelDrawingApply")
    public CommonResult importExcelDrawingApply(HttpServletRequest request, @RequestParam("file") MultipartFile file, String branchCode) {
        return drawingApplyService.importExcelDrawingApply(file, branchCode);
    }

    @ApiOperation(value = "修改图纸申请", notes = "修改图纸申请")
    @PutMapping("/manage")
    public CommonResult<DrawingApply> updateDrawingApply(@RequestBody DrawingApply drawingApply, String oldDrawingNo) {
        if (StringUtils.isNullOrEmpty(drawingApply.getDrawingNo())) {
            return CommonResult.failed(DRAWING_APPLY_NO_NULL_MESSAGE);
        } else {
            if (!drawingApply.getDrawingNo().equals(oldDrawingNo)) {
                QueryWrapper<DrawingApply> queryWrapper = new QueryWrapper<DrawingApply>();
                queryWrapper.eq("drawing_no", drawingApply.getDrawingNo());
                queryWrapper.eq("branch_code", drawingApply.getBranchCode());
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                DrawingApply oldApply = drawingApplyService.getOne(queryWrapper);
                if (oldApply != null && !StringUtils.isNullOrEmpty(oldApply.getId())) {
                    return CommonResult.failed("已有该图号的申请！");
                }
            }
            drawingApply.setStatus("0");
            drawingApply.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            drawingApply.setModifyTime(new Date());
            boolean bool = drawingApplyService.updateById(drawingApply);
            if (bool) {
                return CommonResult.success(drawingApply, DRAWING_APPLY_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(DRAWING_APPLY_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "审批图纸申请", notes = "修改图纸申请")
    @GetMapping("/examine")
    public CommonResult<Boolean> examineDrawingApply(String id, Integer status, String reason) {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(DRAWING_APPLY_ID_NULL_MESSAGE);
        } else {

            UpdateWrapper<DrawingApply> wrapper = new UpdateWrapper<>();
            wrapper.set("status", status);
            wrapper.set("reason", reason);
            wrapper.set("review_by", SecurityUtils.getCurrentUser().getUsername());
            wrapper.set("review_time", new Date());

            wrapper.eq("id", id);

            boolean bool = drawingApplyService.update(wrapper);
            return CommonResult.success(bool, DRAWING_APPLY_SUCCESS_MESSAGE);
        }
    }

    @ApiOperation(value = "审批图纸申请批量处理", notes = "审批图纸申请批量处理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reason", value = "驳回原因", paramType = "query", dataType = "string")
    })
    @PostMapping("/examineBatch")
    public CommonResult examineDrawingApplyBatch(@RequestBody ExamineDrawingApplyParam param) {
        try {
            List<String> ids = (List) param.getIdList();
            if (ObjectUtils.isEmpty(ids)) {
                return CommonResult.failed(DRAWING_APPLY_ID_NULL_MESSAGE);
            } else {
                UpdateWrapper<DrawingApply> wrapper = new UpdateWrapper<>();
                wrapper.set("status", param.getStatus());
                wrapper.set("reason", param.getReason());
                wrapper.set("review_by", SecurityUtils.getCurrentUser().getUsername());
                wrapper.set("review_time", new Date());
                wrapper.in("id", ids);
                boolean bool = drawingApplyService.update(wrapper);

            }
        } catch (Exception e) {
            log.error("批量审核异常了", e);
        }
        return CommonResult.success(DRAWING_APPLY_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "删除物料", notes = "根据物料ID删除图纸申请")
    @DeleteMapping("/manage")
    public CommonResult<DrawingApply> deleteDrawingApplyById(@RequestBody List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return CommonResult.failed(DRAWING_APPLY_ID_NULL_MESSAGE);
        } else {
            boolean bool = drawingApplyService.removeByIds(ids);
            if (bool) {
                return CommonResult.success(null, DRAWING_APPLY_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(DRAWING_APPLY_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "分页查询图纸申请", notes = "根据图号、状态分页查询图纸申请")
    @GetMapping("/manage")
    public CommonResult<PageInfo<DrawingApply>> selectDrawingApply(String drawingNo, String status, String order, String orderCol, int page, int limit, String dataGroup) {
//        QueryWrapper<DrawingApply> queryWrapper = new QueryWrapper<DrawingApply>();
//        if (!StringUtils.isNullOrEmpty(drawingNo)) {
//            queryWrapper.like("drawing_no", drawingNo);
//        }
//        if (null != status) {
//            queryWrapper.eq("status", status);
//        }
//
//        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
//        queryWrapper.eq("datagroup", dataGroup);
//        if (!StringUtils.isNullOrEmpty(orderCol)) {
//            if (!StringUtils.isNullOrEmpty(order)) {
//                if ("desc".equals(order)) {
//                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
//                } else if ("asc".equals(order)) {
//                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
//                }
//            } else {
//                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
//            }
//        } else {
//            queryWrapper.orderByDesc("modify_time");
//        }
        DrawingApply drawingApply = new DrawingApply();
        if (!StrUtil.isBlank(drawingNo)) {
            drawingApply.setDrawingNo(drawingNo.replaceAll("-", ""));
        }
        drawingApply.setStatus(status);
        drawingApply.setDataGroup(dataGroup);
        PageHelper.startPage(page, limit);
        if (!StrUtil.isBlank(orderCol)) {
            PageHelper.orderBy(orderCol + " " + order);
        }
        List<DrawingApply> trackFlowList = drawingApplyService.list(drawingApply);
        PageInfo<DrawingApply> trackFlowPage = new PageInfo(trackFlowList);
        return CommonResult.success(trackFlowPage, DRAWING_APPLY_SUCCESS_MESSAGE);
    }


    @ApiOperation(value = "导入图纸申请", notes = "根据Excel文档导入图纸申请")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_manage")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        //封装证件信息实体类
        String[] fieldNames = {"drawingNo", "drawingDesc", "branchCode", "remark", "pdmDrawingNo"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<DrawingApply> list = ExcelUtils.importExcel(excelFile, DrawingApply.class, fieldNames, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);

            list = list.stream().filter(item -> item.getDrawingNo() != null).collect(Collectors.toList());
            list.forEach(item -> {
                item.setStatus("0");
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
            });

            boolean bool = drawingApplyService.saveBatch(list);
            if (bool) {
                return CommonResult.success(null, DRAWING_APPLY_IMPORT_EXCEL_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(DRAWING_APPLY_FAILED_MESSAGE);
            }
        } catch (Exception e) {
            return CommonResult.failed(DRAWING_APPLY_EXCEPTION_MESSAGE + e.getMessage());
        }
    }

    @ApiOperation(value = "导出图纸申请", notes = "通过Excel文档导出图纸申请")
    @GetMapping("/export_manage")
    public void exportExcel(String drawingNo, Integer status, HttpServletResponse rsp) {
        try {
            QueryWrapper<DrawingApply> queryWrapper = new QueryWrapper<DrawingApply>();
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                queryWrapper.like("drawing_no", drawingNo);
            }
            if (null != status) {
                queryWrapper.eq("status", status);
            }
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.orderByDesc("create_time");
            List<DrawingApply> list = drawingApplyService.list(queryWrapper);


            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "图纸申请信息_" + format.format(new Date()) + ".xlsx";


            String[] columnHeaders = {"图号", "PDM图号", "描述", "状态", "工艺数量", "图纸数量", "BOM数量", "工艺", "备注"};

            String[] fieldNames = {"drawingNo", "pdmDrawingNo", "drawingDesc", "status", "routerNum", "drawingNum", "bomNum", "router", "remark"};

            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
