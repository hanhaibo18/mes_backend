package com.richfit.mes.base.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.ProductService;
import com.richfit.mes.base.service.ProductionBomService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 组织结构Controller
 */
@Slf4j
@Api("产品Bom管理")
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
    @ApiImplicitParam(name = "productionBom", value = "产品Bom", required = true, dataType = "Branch", paramType = "path")
    @PostMapping("/production_bom")
    public CommonResult<ProductionBom> addProduct(@RequestBody ProductionBom productionBom) {
        if (StringUtils.isNullOrEmpty(productionBom.getDrawingNo())) {
            return CommonResult.failed(BOM_DRAWING_NULL_MESSAGE);
        } else {

            QueryWrapper<ProductionBom> query = new QueryWrapper<>();
            query.eq("drawing_no", productionBom.getDrawingNo());
            List<ProductionBom> result = productionBomService.list(query);
            if (result != null && result.size() > 0) {
                return CommonResult.failed("相同版本的零部件图号已存在！");
            }

            UpdateWrapper<ProductionBom> bomQuery = new UpdateWrapper<>();
            bomQuery.set("is_current", "0");
            bomQuery.eq("drawing_no", productionBom.getDrawingNo());
            productionBomService.update(bomQuery);

            QueryWrapper<Product> productQuery = new QueryWrapper<>();
            productQuery.eq("drawing_no", productionBom.getDrawingNo());
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

    @ApiOperation(value = "分页查询产品Bom", notes = "根据图号、状态分页查询产品Bom")
    @GetMapping("/production_bom")
    public CommonResult<IPage<ProductionBom>> getProductionBom(String drawingNo, String branchCode, String order, String orderCol, String bomKey, int page, int limit) {

        QueryWrapper<ProductionBom> query = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            query.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            query.eq("branch_code", branchCode);
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
            query.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    query.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    query.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                query.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            if (!StringUtils.isNullOrEmpty(bomKey)) {
                query.orderByAsc("order_no");
            } else {
                query.orderByDesc("modify_time");
            }
        }

        return CommonResult.success(productionBomService.page(new Page<ProductionBom>(page, limit), query), BOM_SUCCESS_MESSAGE);

    }

    @ApiOperation(value = "分页查询产品Bom历史版本", notes = "根据图号查询产品Bom历史版本")
    @GetMapping("/production_bom/history")
    public CommonResult<IPage<ProductionBom>> getProductionBomHistory(String materialNo, String drawingNo, String mainDrawingNo, String branchCode, String bomKey, int page, int limit) {

        QueryWrapper<ProductionBom> query = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            query.eq("pb.material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            query.like("pb.drawing_no", drawingNo);
        }

        if (StringUtils.isNullOrEmpty(mainDrawingNo)) {
            query.and(wrapper -> wrapper.isNull("main_drawing_no").or().eq("main_drawing_no", ""));
        } else {
            query.eq("main_drawing_no", mainDrawingNo);
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

    @ApiOperation(value = "导入产品Bom", notes = "根据Excel文档导入产品Bom")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        //封装证件信息实体类
        String[] fieldNames = {"isImport", "orderNo", "branchCode", "grade", "mainDrawingNo", "drawingNo", "materialNo", "prodDesc", "sourceType", "weight", "texture", "number", "unit", "optName", "trackType",
                "isNumFrom", "isNeedPicking", "isKeyPart", "isEdgeStore", "isCheck", "remark"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成产品BOM实体类list
            List<ProductionBom> list = ExcelUtils.importExcel(excelFile, ProductionBom.class, fieldNames, 4, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);

            list = list.stream().filter(item -> !StringUtils.isNullOrEmpty(item.getDrawingNo()) &&
                    !StringUtils.isNullOrEmpty(item.getMaterialNo())).collect(Collectors.toList());

            /*
            List<String> drawingNoes = list.stream().map(l -> l.getDrawingNo()).collect(Collectors.toList());
            UpdateWrapper<ProductionBom> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("is_current", 0);
            updateWrapper.in("drawing_no", drawingNoes);
            updateWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            productionBomService.update(updateWrapper);
            */

            /*String drawingNo = list.get(0).getDrawingNo();

            List<ProductionBom> pList = productionBomService.list(new QueryWrapper<ProductionBom>().eq("drawing_no",drawingNo).eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId()).orderByDesc("version_no"));

            int versionNo = 1;

            if(pList != null  && pList.size() > 0 && pList.get(0).getVersionNo() > 0){
                versionNo += pList.get(0).getVersionNo();
            }

            int finalVersionNo = versionNo;*/
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String versionNo = format.format(new Date());

            String bomKey = UUID.randomUUID().toString();

            list.forEach(item -> {
                //item.setVersionNo(finalVersionNo);
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
                item.setBomKey(bomKey);
                if ("单件".equals(item.getTrackType())) {
                    item.setTrackType("0");
                } else if ("批次".equals(item.getTrackType())) {
                    item.setTrackType("1");
                }
                if ("否".equals(item.getIsCheck())) {
                    item.setIsCheck("0");
                } else if ("是".equals(item.getIsCheck())) {
                    item.setIsCheck("1");
                }
                if ("否".equals(item.getIsEdgeStore())) {
                    item.setIsEdgeStore("0");
                } else if ("是".equals(item.getIsEdgeStore())) {
                    item.setIsEdgeStore("1");
                }
                if ("否".equals(item.getIsNeedPicking())) {
                    item.setIsNeedPicking("0");
                } else if ("是".equals(item.getIsNeedPicking())) {
                    item.setIsNeedPicking("1");
                }

                /*Product product = productService.getOne(new QueryWrapper<Product>().eq("material_no", item.getMaterialNo()));
                if(product == null){ //判断导入的信息在物料表中是否存在，不存在则保存入物料表
                    product = new Product();
                    product.setBranchCode(item.getBranchCode());
                    product.setDrawingNo(item.getDrawingNo());
                    product.setMaterialNo(item.getMaterialNo());
                    //0：自制 1：外购 2：外协
                    if("自制件".equals(item.getObjectType())){
                        product.setObjectType("0");
                    } else if("外购件".equals(item.getObjectType())){
                        product.setObjectType("1");
                    } else if("外协件".equals(item.getObjectType())){
                        product.setObjectType("2");
                    }
                    //0铸件 1锻件 2精铸件 3成品/半成品
                    if("铸件".equals(item.getProductType())){
                        product.setMaterialType("0");
                    } else if("锻件".equals(item.getProductType())){
                        product.setMaterialType("1");
                    } else if("精铸件".equals(item.getProductType())){
                        product.setMaterialType("2");
                    } else if("成品/半成品".equals(item.getProductType())){
                        product.setMaterialType("3");
                    }
                    product.setProductName(item.getProductName());
                    product.setTexture(item.getTexture());
                    product.setUnit(item.getUnit());
                    product.setWeight(item.getWeight());
                    product.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    product.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    product.setCreateTime(new Date());
                    productService.save(product);
                }*/
            });

            boolean bool = productionBomService.saveBatch(list);
            if (bool) {
                return CommonResult.success(null, BOM_IMPORT_EXCEL_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BOM_FAILED_MESSAGE);
            }
        } catch (Exception e) {
            return CommonResult.failed(BOM_IMPORT_EXCEL_EXCEPTION_MESSAGE + e.getMessage());
        }
    }

    @Value("${excelTemp.pdmBomUrl}")
    private String bomUrl;

    /*@ApiOperation(value = "导出产品Bom", notes = "通过Excel文档导出产品Bom")
    @GetMapping("/export_excel")
    public void exportExcel(String bomKey, HttpServletResponse rsp) {
        try {
            QueryWrapper<ProductionBom> query = new QueryWrapper<>();
            if(!StringUtils.isNullOrEmpty(bomKey)){
                query.eq("pb.bom_key", bomKey);
            }
            query.orderByAsc("pb.order_no");

            List<ProductionBom> list = productionBomService.getProductionBomList(query);

            List<ProductionBom> topProduction = list.stream().filter(item -> item.getGrade().toUpperCase().equals("H")).collect(Collectors.toList());

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = topProduction.get(0).getDrawingNo() + "_" + format.format(new Date()) + ".xlsx";

            List<ProductionBom> result = new ArrayList<>();
            for(int i = 0; i<list.size(); i++){
                ProductionBom pb = list.get(i);
                if(pb.getObjectType() != null){
                    if(pb.getObjectType().equals("0")){
                        pb.setObjectType("自制件");
                    } else if(pb.getObjectType().equals("1")){
                        pb.setObjectType("外购件");
                    } else if(pb.getObjectType().equals("2")){
                        pb.setObjectType("外协件");
                    }
                }

                if(pb.getProductType() != null){
                    if(pb.getProductType().equals("0")){
                        pb.setProductType("铸件");
                    } else if(pb.getProductType().equals("1")){
                        pb.setProductType("锻件");
                    } else if(pb.getProductType().equals("2")){
                        pb.setProductType("精铸件");
                    } else if(pb.getProductType().equals("3")){
                        pb.setProductType("成品/半成品");
                    }
                }

                result.add(pb);
            }

            String[] columnHeaders = {"车间", "等级", "上级产品图号", "零部件图号", "版本号", "SAP物料编码", "零部件名称",  "物料类型", "重量(Kg)", "材质", "用量", "单位", "跟踪方式", "是否编号来源", "是否关键件", "是否仓储领料",  "实物配送区分", "是否齐套检查" ,"备注"};

            String[] fieldNames = {"branchCode","grade","mainDrawingNo","drawingNo","versionNo","materialNo","productName","productType","weight","texture","number","unit","trackType", "isNumFrom","isKeyPart","isNeedPicking","isEdgeStore","isCheck","remark"};

            //export
            ExcelUtils.exportExcel(fileName, result , columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }*/

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
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "查询零件接口")
    public CommonResult<List<ProductionBom>> getProductionBomByDrawingNoList(String drawingNo, String tenantId, String branchCode) {
        return CommonResult.success(productionBomService.getProductionBomByDrawingNoList(drawingNo, tenantId, branchCode));
    }

    @GetMapping("/issueBom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "发布BOM的ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "workPlanNo", value = "工作号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名称", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "发布BOM")
    public CommonResult<Boolean> issueBom(String id, String workPlanNo, String projectName, String tenantId, String branchCode) {
        return CommonResult.success(productionBomService.issueBom(id, workPlanNo, projectName, tenantId, branchCode));
    }


    @DeleteMapping("/deleteBom")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "发布BOM")
    public CommonResult<Boolean> deleteBom(String drawingNo, String tenantId, String branchCode) {
        return CommonResult.success(productionBomService.deleteBom(drawingNo, tenantId, branchCode));
    }


    @PutMapping("/updateBom")
    @ApiOperation(value = "修改Bom")
    public CommonResult<Boolean> updateBom(@RequestBody ProductionBom productionBom) {
        return CommonResult.success(productionBomService.updateBom(productionBom));
    }

    @PutMapping("/updateBomList")
    @ApiOperation(value = "修改零件bom")
    public CommonResult<Boolean> updateBomList(@RequestBody ProductionBom productionBom) {
        //TODO:未处理新增数据
        return CommonResult.success(productionBomService.updateBom(productionBom));
    }
}
