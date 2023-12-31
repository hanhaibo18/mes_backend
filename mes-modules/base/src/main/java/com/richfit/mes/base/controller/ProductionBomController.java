package com.richfit.mes.base.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.entity.DeleteProductionBomDto;
import com.richfit.mes.base.service.ProductService;
import com.richfit.mes.base.service.ProductionBomService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 产品Bom管理Controller
 */
@Slf4j
@Api(value = "产品Bom管理", tags = {"产品Bom管理"})
@RestController
@RequestMapping("/api/base/production_bom")
public class ProductionBomController extends BaseController {

    public static String BOM_DRAWING_NULL_MESSAGE = "产品图号不能为空！";
    public static String BOM_SUCCESS_MESSAGE = "操作成功!";
    public static String BOM_FAILED_MESSAGE = "操作失败，请重试！";
    public static String BOM_IMPORT_EXCEL_SUCCESS_MESSAGE = "导入成功!";
    public static String BOM_IMPORT_EXCEL_EXCEPTION_MESSAGE = "操作失败：";

    @Autowired
    private ProductionBomService productionBomService;

    @Autowired
    private ProductService productService;

    @ApiOperation(value = "新增产品Bom", notes = "新增产品Bom")
    @Deprecated
    @ApiImplicitParam(name = "productionBom", value = "产品Bom", required = true, dataType = "Branch", paramType = "path")
    @PostMapping("/production_bom")
    public CommonResult<ProductionBom> addProduct(@RequestBody ProductionBom productionBom) {
        if (StringUtils.isNullOrEmpty(productionBom.getDrawingNo())) {
            return CommonResult.failed(BOM_DRAWING_NULL_MESSAGE);
        } else {

            QueryWrapper<ProductionBom> query = new QueryWrapper<>();
            DrawingNoUtil.queryEq(query, "drawing_no", productionBom.getDrawingNo());
            List<ProductionBom> result = productionBomService.list(query);
            if (result != null && result.size() > 0) {
                return CommonResult.failed("相同版本的零部件图号已存在！");
            }

            UpdateWrapper<ProductionBom> bomQuery = new UpdateWrapper<>();
            bomQuery.set("is_current", "0");
            bomQuery.eq("drawing_no", productionBom.getDrawingNo());
            productionBomService.update(bomQuery);

            QueryWrapper<Product> productQuery = new QueryWrapper<>();
            DrawingNoUtil.queryEq(productQuery, "drawing_no", productionBom.getDrawingNo());
            productQuery.eq("material_no", productionBom.getMaterialNo());
            List<Product> result2 = productService.list(productQuery);
            if (result2 == null || result2.size() == 0) {
                return CommonResult.failed("输入的物料编号不存在！");
            }

            productionBom.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            productionBom.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            productionBom.setCreateTime(new Date());
            boolean bool = productionBomService.save(productionBom);
            if (bool) {
                return CommonResult.success(productionBom, BOM_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BOM_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "新增产品Bom", notes = "新增产品Bom")
    @ApiImplicitParam(name = "productionBom", value = "产品Bom", required = true, dataType = "ProductionBom", paramType = "path")
    @PostMapping("/save_production_bom")
    public CommonResult<ProductionBom> saveProductBom(@RequestBody ProductionBom productionBom) {
        return productionBomService.saveProductionBom(productionBom);
    }

    @ApiOperation(value = "修改产品Bom", notes = "修改产品Bom")
    @ApiImplicitParam(name = "productionBom", value = "产品Bom", required = true, dataType = "ProductionBom", paramType = "path")
    @PutMapping("/update_production_bom")
    public CommonResult<ProductionBom> updateProductBom(@RequestBody ProductionBom productionBom) {
        //产品BOM修改，保证图号唯一
        QueryWrapper<ProductionBom> query = new QueryWrapper<>();
        DrawingNoUtil.queryEq(query, "drawing_no", productionBom.getDrawingNo());
        List<ProductionBom> result = productionBomService.list(query);
        List<ProductionBom> collect = result.stream().filter(e ->
                e.getDrawingNo().equals(productionBom.getDrawingNo()) &&
                        !e.getId().equals(productionBom.getId())).collect(Collectors.toList());
        if (collect.size() > 0) {
            return CommonResult.failed("相同版本的零部件图号已存在！");
        }
        productionBom.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        boolean bool = productionBomService.updateById(productionBom);
        if (bool) {
            return CommonResult.success(productionBom, BOM_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(BOM_FAILED_MESSAGE);
        }
    }

    @DeleteMapping("/deletePartBom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "删除单条零件BOM")
    public CommonResult<Boolean> deletePartBom(String id, String drawingNo, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return CommonResult.success(productionBomService.deletePartBom(id, drawingNo, tenantId, branchCode));
    }


    @ApiOperation(value = "发布", notes = "发布")
    @Deprecated
    @PostMapping("/production_bom/publish")
    public CommonResult<ProductionBom> pushProduct(@RequestBody ProductionBom productionBom) {
        boolean bool = productionBomService.updateStatus(productionBom);
        if (bool) {
            return CommonResult.success(productionBom, BOM_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(BOM_FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "修改产品Bom", notes = "修改产品Bom")
    @Deprecated
    @ApiImplicitParam(name = "productionBom", value = "产品Bom", required = true, dataType = "Branch", paramType = "path")
    @PutMapping("/production_bom")
    public CommonResult<ProductionBom> updateProduct(@RequestBody ProductionBom productionBom) {
        productionBom.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        productionBom.setModifyTime(new Date());
        boolean bool = productionBomService.updateById(productionBom);
        if (bool) {
            return CommonResult.success(productionBom, BOM_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(BOM_FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "批量修改产品Bom", notes = "批量修改产品Bom")
    @Deprecated
    @ApiImplicitParam(name = "productionBoms", value = "产品Bom数组", required = true, dataType = "Branch", paramType = "path")
    @PutMapping("/production_boms")
    public CommonResult<String> updateProducts(@RequestBody ProductionBom[] productionBoms) {
        for (ProductionBom productionBom : productionBoms) {
            productionBom.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            productionBom.setModifyTime(new Date());
            productionBomService.updateById(productionBom);

        }
        return CommonResult.success("", BOM_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "删除产品Bom", notes = "根据产品图号删除产品Bom")
    @Deprecated
    @ApiImplicitParam(name = "id", value = "物料ID", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/production_bom")
    public CommonResult<ProductionBom> deleteProductionBomById(@RequestBody List<String> bomKeys) {
        if (bomKeys == null || bomKeys.size() == 0) {
            return CommonResult.failed(BOM_DRAWING_NULL_MESSAGE);
        } else {
            QueryWrapper<ProductionBom> query = new QueryWrapper<ProductionBom>();
            query.and(wrapper -> wrapper.in("bom_key", bomKeys));
            query.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            boolean bool = productionBomService.remove(query);
            if (bool) {
                return CommonResult.success(null, BOM_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BOM_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "分页查询产品Bom历史版本", notes = "根据图号查询产品Bom历史版本")
    @GetMapping("/production_bom/history")
    public CommonResult<IPage<ProductionBom>> getProductionBomHistory(String materialNo, String drawingNo, String mainDrawingNo, String branchCode, String bomKey, int page, int limit) {
        QueryWrapper<ProductionBom> query = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            query.eq("pb.material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(query, "pb.drawing_no", drawingNo);
        }

        if (StringUtils.isNullOrEmpty(mainDrawingNo)) {
            query.and(wrapper -> wrapper.isNull("main_drawing_no").or().eq("main_drawing_no", ""));
        } else {
            DrawingNoUtil.queryLike(query, "main_drawing_no", mainDrawingNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            query.eq("pb.branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(bomKey)) {
            query.eq("pb.bom_key", bomKey);
        }
        List<GrantedAuthority> authorities = new ArrayList<>(SecurityUtils.getCurrentUser().getAuthorities());
        boolean isAdmin = false;
        for (GrantedAuthority authority : authorities) {
            //超级管理员 ROLE_12345678901234567890000000000000
            if ("ROLE_12345678901234567890000000000000".equals(authority.getAuthority())) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) {
            query.eq("pb.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        query.orderByDesc("pb.modify_time");
        return CommonResult.success(productionBomService.getProductionBomHistory(new Page<ProductionBom>(page, limit), query), BOM_SUCCESS_MESSAGE);

    }

    @Value("${excelTemp.pdmBomUrl}")
    private String bomUrl;

    @ApiOperation(value = "导出产品Bom", notes = "通过Excel文档导出产品Bom")
    @GetMapping("/export_excel")
    public void exportExcel(String bomKey, HttpServletResponse rsp) {
        try {
            ClassPathResource resource = new ClassPathResource(bomUrl);
            HSSFWorkbook wb = new HSSFWorkbook(resource.getInputStream());
            HSSFSheet sheet = wb.getSheet("泵业制造BOM");

            QueryWrapper<ProductionBom> query = new QueryWrapper<>();
            if (!StringUtils.isNullOrEmpty(bomKey)) {
                query.eq("pb.bom_key", bomKey);
            }
            query.orderByAsc("pb.order_no");

            List<ProductionBom> list = productionBomService.getProductionBomList(query);

            List<ProductionBom> topProduction = list.stream().filter(item -> item.getGrade().toUpperCase().equals("H")).collect(Collectors.toList());

            String drawingNo = topProduction.get(0).getDrawingNo();

            HSSFRow drawTitle = sheet.getRow(1);
            drawTitle.getCell(7).setCellValue(drawingNo);
            List<ProductionBom> result = new ArrayList<>();
            int index = 4;

            for (ProductionBom bom : result) {
                HSSFRow row = sheet.getRow(index);
                int orderNo = bom.getOrderNo() != null ? bom.getOrderNo() : 0;
                row.getCell(1).setCellValue(orderNo);
                row.getCell(2).setCellValue(bom.getBranchCode());
                if (bom.getDrawingNo().equals(drawingNo)) {
                    row.getCell(3).setCellValue("H");
                    drawTitle.getCell(9).setCellValue(bom.getMaterialNo());
                    HSSFRow nameTitle = sheet.getRow(2);
                    nameTitle.getCell(7).setCellValue(bom.getProdDesc());
                } else {
                    row.getCell(3).setCellValue("L");
                    row.getCell(4).setCellValue(drawingNo);
                }
                row.getCell(5).setCellValue(bom.getDrawingNo());
                row.getCell(6).setCellValue(bom.getMaterialNo());
                row.getCell(7).setCellValue(bom.getProdDesc());
                row.getCell(8).setCellValue(bom.getSourceType());
                Float weight = bom.getWeight() != null ? bom.getWeight() : 0;
                row.getCell(9).setCellValue(weight);
                row.getCell(10).setCellValue(bom.getTexture());
                int number = bom.getNumber() != null ? bom.getNumber() : 0;
                row.getCell(11).setCellValue(number);
                row.getCell(12).setCellValue(bom.getUnit());

                row.getCell(14).setCellValue("0".equals(bom.getTrackType()) ? "单件" : "批次");
                row.getCell(16).setCellValue("0".equals(bom.getIsEdgeStore()) ? "否" : "是");
                row.getCell(17).setCellValue("0".equals(bom.getIsKeyPart()) ? "否" : "是");
                row.getCell(18).setCellValue("0".equals(bom.getIsNeedPicking()) ? "否" : "是");
                row.getCell(19).setCellValue("0".equals(bom.getIsCheck()) ? "否" : "是");
                row.getCell(20).setCellValue(bom.getRemark());
                index++;
            }

            rsp.setCharacterEncoding("UTF-8");
            rsp.setContentType("application/octet-stream");
            //默认Excel名称
            rsp.setHeader("Content-disposition",
                    String.format("attachment;filename=%s", URLEncoder.encode("产品BOM.xls", "UTF-8")));
            rsp.flushBuffer();
            wb.write(rsp.getOutputStream());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @GetMapping("/queryPart")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "查询零件接口")
    public CommonResult<List<ProductionBom>> getProductionBomByDrawingNoList(String drawingNo, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return CommonResult.success(productionBomService.getProductionBomByDrawingNoList(drawingNo, tenantId, branchCode));
    }

    @GetMapping("/issueBom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "发布BOM的ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名称", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "发布BOM")
    public CommonResult<Boolean> issueBom(String id, String workPlanNo, String projectName, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return productionBomService.issueBom(id, workPlanNo, projectName, tenantId, branchCode);
    }

    @PostMapping("/auto/issue_bom")
    @ApiOperation(value = "自动发布BOM")
    public CommonResult autoIssueBom(@ApiParam(value = "项目bom发布条件", required = true) @RequestBody List<Map<String, String>> list) {
        String errmessage = "";
        for (Map<String, String> map : list) {
            String drawingNo = map.get("drawingNo");
            String projectName = map.get("projectName");
            String branchCode = map.get("branchCode");
            String tenantId = SecurityUtils.getCurrentUser().getTenantId();
            String workNo = map.get("projectBomWork");
            if (StrUtil.isBlank(workNo)) {
                workNo = map.get("workNo");
            }
            if (StrUtil.isBlank(workNo)) {
                errmessage += "drawingNo：" + drawingNo + "workNo:工作号不能为空；";
                break;
            }
            QueryWrapper<ProductionBom> query = new QueryWrapper<>();
            DrawingNoUtil.queryEq(query, "drawing_no", drawingNo);
            query.eq("branch_code", branchCode);
            query.eq("grade", "H");
            query.eq("tenant_id", tenantId);
            List<ProductionBom> productionBoms = productionBomService.list(query);
            if (CollectionUtils.isEmpty(productionBoms)) {
                errmessage += "drawingNo：" + drawingNo + "workNo:" + workNo + ",没有找到产品bom；";
                break;
            }
            ProductionBom productionBom = productionBoms.get(0);
            CommonResult commonResult = productionBomService.issueBom(productionBom.getId(), workNo, projectName, tenantId, branchCode);
            if (commonResult.getStatus() == 500) {
                errmessage += "drawingNo：" + drawingNo + "workNo:" + workNo + "," + commonResult.getMessage() + "；";
            }
        }
        return CommonResult.success(errmessage);
    }


    @DeleteMapping("/deleteBom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "删除BOM")
    public CommonResult<Boolean> deleteBom(String drawingNo, String branchCode) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return CommonResult.success(productionBomService.deleteBom(drawingNo, tenantId, branchCode));
    }

    @DeleteMapping("/deleteBomList")
    @ApiOperation(value = "删除多条BOM")
    public CommonResult<Boolean> deleteBom(@RequestBody DeleteProductionBomDto deleteProductionBomDto) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        boolean deleteBom = false;
        for (String drawingNo : deleteProductionBomDto.getDrawingNoList()) {
            deleteBom = productionBomService.deleteBom(drawingNo, tenantId, deleteProductionBomDto.getBranchCode());
        }
        return CommonResult.success(deleteBom);
    }


    @PutMapping("/updateBom")
    @ApiOperation(value = "修改Bom")
    public CommonResult<Boolean> updateBom(@RequestBody ProductionBom productionBom) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        productionBom.setTenantId(tenantId);
        return CommonResult.success(productionBomService.updateBom(productionBom));
    }

    @PutMapping("/updateBomList")
    @ApiOperation(value = "修改零件bom")
    public CommonResult<Boolean> updateBomList(@RequestBody ProductionBom productionBom) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        productionBom.setTenantId(tenantId);
        return CommonResult.success(productionBomService.updateBom(productionBom));
    }


    @ApiOperation(value = "分页查询产品Bom", notes = "根据图号、状态分页查询产品Bom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "order", value = "排序方式", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "orderCol", value = "排序字段", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, paramType = "query", dataType = "int")
    })
    @GetMapping("/production_bom")
    public CommonResult<IPage<ProductionBom>> getProductionBomPage(String drawingNo, String branchCode, String order, String orderCol, int page, int limit) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        return CommonResult.success(productionBomService.getProductionBomPage(drawingNo, tenantId, branchCode, order, orderCol, page, limit));
    }


    @ApiOperation(value = "导入产品Bom", notes = "根据Excel文档导入产品Bom")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult newImportExcel(@ApiIgnore HttpServletRequest request, @RequestParam("file") MultipartFile file, String branchCode) throws IOException {
        return productionBomService.newImportExcel(file, branchCode);
    }


    @ApiOperation(value = "导出BOM到Excel", notes = "导出BOM到Excel")
    @ApiImplicitParam(name = "idList", value = "idList", paramType = "query", allowMultiple = true, dataType = "List<String>")
    @PostMapping("/newExportExcel")
    public void newExportExcel(@RequestBody List<String> idList, HttpServletResponse rsp) {
        productionBomService.exportExcel(idList, rsp);
    }

    @ApiOperation(value = "导出产品BOM(ERP)", notes = "导出产品BOM(ERP)")
    @ApiImplicitParam(name = "id", value = "id", paramType = "query", dataType = "String")
    @GetMapping("/exportExcelERP")
    public void exportExcelERP(String id, HttpServletResponse rsp) {
        productionBomService.exportExcelERP(id, rsp);
    }
}
