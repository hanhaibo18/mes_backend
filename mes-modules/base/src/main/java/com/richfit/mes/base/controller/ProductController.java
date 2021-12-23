package com.richfit.mes.base.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.ProductService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
@Api("物料管理")
@RestController
@RequestMapping("/api/base/product")
public class ProductController extends BaseController {

    public static String PRODUCT_MATERIAL_NO_NULL_MESSAGE = "机构ID不能为空！";
    public static String PRODUCT_ID_NULL_MESSAGE = "机构ID不能为空！";
    public static String PRODUCT_SUCCESS_MESSAGE = "操作成功!";
    public static String PRODUCT_FAILED_MESSAGE = "操作失败，请重试！";
    public static String PRODUCT_EXCEPTION_MESSAGE = "操作失败：";
    public static String PRODUCT_IMPORT_EXCEL_SUCCESS_MESSAGE = "导入成功!";

    @Autowired
    private ProductService productService;

    @ApiOperation(value = "新增物料", notes = "新增物料信息")
    @ApiImplicitParam(name = "product", value = "物料", required = true, dataType = "Branch", paramType = "path")
    @PostMapping("/product")
    public CommonResult<Product> addProduct(@RequestBody Product product){
        if(StringUtils.isNullOrEmpty(product.getMaterialNo())){
            return CommonResult.failed(PRODUCT_MATERIAL_NO_NULL_MESSAGE);
        } else {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
            queryWrapper.eq("material_no", product.getMaterialNo());
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            Product oldProduct = productService.getOne(queryWrapper);
            if(oldProduct != null && !StringUtils.isNullOrEmpty(oldProduct.getId())){
                return CommonResult.failed("物料编码已存在！");
            } else {
                product.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                product.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                boolean bool = productService.save(product);
                if(bool){
                    return CommonResult.success(product,PRODUCT_SUCCESS_MESSAGE);
                } else {
                    return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
                }
            }

        }
    }

    @ApiOperation(value = "修改物料", notes = "修改物料信息")
    @ApiImplicitParam(name = "product", value = "物料", required = true, dataType = "Branch", paramType = "path")
    @PutMapping("/product")
    public CommonResult<Product> updateProduct(@RequestBody Product product, String oldMaterialNo){
        if(StringUtils.isNullOrEmpty(product.getMaterialNo())){
            return CommonResult.failed(PRODUCT_MATERIAL_NO_NULL_MESSAGE);
        } else {
            if(!product.getMaterialNo().equals(oldMaterialNo)){
                QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
                queryWrapper.eq("material_no", product.getMaterialNo());
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                Product oldProduct = productService.getOne(queryWrapper);
                if(oldProduct != null && !StringUtils.isNullOrEmpty(oldProduct.getId())){
                    return CommonResult.failed("物料编码已存在！");
                }
            }
            product.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            product.setModifyTime(new Date());
            boolean bool = productService.updateById(product);
            if(bool){
                return CommonResult.success(product,PRODUCT_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除物料", notes = "根据物料ID删除物料")
    @ApiImplicitParam(name = "id", value = "物料ID", required = true, dataType = "List<String>", paramType = "path")
    @DeleteMapping("/product")
    public CommonResult<Product> deleteProductById(@RequestBody List<String> ids){
        if(ids == null || ids.size() == 0){
            return CommonResult.failed(PRODUCT_ID_NULL_MESSAGE);
        } else {
            boolean bool = productService.removeByIds(ids);
            if(bool){
                return CommonResult.success(null, PRODUCT_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "分页查询物料", notes = "根据图号、物料编码分页查询物料")
    @GetMapping("/product")
    public CommonResult<IPage<Product>> selectProduct(String drawingNo, String materialNo, String materialType, String order , String orderCol, int page, int limit){
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if(!StringUtils.isNullOrEmpty(drawingNo)){
            queryWrapper.like("p.drawing_no", "%" + drawingNo + "%");
        }
        if(!StringUtils.isNullOrEmpty(materialNo)){
            queryWrapper.like("p.material_no", "%" + materialNo + "%");
        }
        if(!StringUtils.isNullOrEmpty(materialType)){
            queryWrapper.eq("p.material_type", materialType);
        }
        queryWrapper.eq("p.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        if(!StringUtils.isNullOrEmpty(orderCol)){
            if(!StringUtils.isNullOrEmpty(order)){
                if(order.equals("desc")){
                    queryWrapper.orderByDesc("p." + StrUtil.toUnderlineCase(orderCol));
                } else if (order.equals("asc")){
                    queryWrapper.orderByAsc("p." + StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc("p." + StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("p.modify_time");
        }


        return CommonResult.success(productService.selectProduct(new Page<Product>(page, limit), queryWrapper), PRODUCT_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询物料", notes = "根据输入内容查询物料")
    @GetMapping("/product/list")
    public CommonResult<List<Product>> selectProductList(String inputKey, String drawingNo, String materialType, Boolean isEqualType){
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if(!StringUtils.isNullOrEmpty(inputKey)){
            queryWrapper.and(wrapper -> wrapper.like("material_no", "%" + inputKey + "%").or().like("product_name", "%" + inputKey + "%"));
        }
        if(!StringUtils.isNullOrEmpty(drawingNo)){
            queryWrapper.eq("drawing_no", drawingNo);
        }
        if(!StringUtils.isNullOrEmpty(materialType)){
            if(isEqualType != null){
                if(isEqualType){
                    queryWrapper.eq("material_type", materialType);
                } else {
                    queryWrapper.ne("material_type", materialType);
                }
            } else {
                queryWrapper.eq("material_type", materialType);
            }
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("create_time");

        return CommonResult.success(productService.list(queryWrapper), PRODUCT_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询物料", notes = "根据物料号图号查询物料")
    @GetMapping("/product/listByNo")
    public CommonResult<List<Product>> selectProduct(String materialNo, String drawingNo, String materialType){
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if(!StringUtils.isNullOrEmpty(materialNo)){
            queryWrapper.eq("material_no", materialNo);
        }
        if(!StringUtils.isNullOrEmpty(materialType) && materialType.equals("0")){
            queryWrapper.ne("material_type", "3");
        } else if (!StringUtils.isNullOrEmpty(materialType) && materialType.equals("1")){
            queryWrapper.eq("material_type", "3");
        }
        if(!StringUtils.isNullOrEmpty(drawingNo)){
            queryWrapper.eq("drawing_no", drawingNo);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("create_time");
        return CommonResult.success(productService.list(queryWrapper), PRODUCT_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "导入物料", notes = "根据Excel文档导入物料")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        //封装证件信息实体类
        String[] fieldNames = {"materialNo","materialDate","materialType","materialDesc","drawingNo","productName","weight","unit","convertScale","convertUnit"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"),tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<Product> list =  ExcelUtils.importExcel(excelFile, Product.class, fieldNames, 1,0,0,tempName.toString());
            FileUtils.delete(excelFile);

            list = list.stream().filter(item -> item.getMaterialNo() != null).collect(Collectors.toList());
            list.forEach(item->{
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
            });

            boolean bool = productService.saveBatch(list);
            if(bool){
                return CommonResult.success(null, PRODUCT_IMPORT_EXCEL_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
            }
        }catch (Exception e) {
            return CommonResult.failed(PRODUCT_EXCEPTION_MESSAGE + e.getMessage());
        }
    }

    @ApiOperation(value = "导出物料信息", notes = "通过Excel文档导出物料信息")
    @GetMapping("/export_excel")
    public void exportExcel(String drawingNo, String materialNo, String materialType, HttpServletResponse rsp) {
        try {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
            if(!StringUtils.isNullOrEmpty(drawingNo)){
                queryWrapper.like("p.drawing_no", "%" + drawingNo + "%");
            }
            if(!StringUtils.isNullOrEmpty(materialNo)){
                queryWrapper.like("p.material_no", "%" + materialNo + "%");
            }
            if(!StringUtils.isNullOrEmpty(materialType)){
                queryWrapper.eq("p.material_type", materialType);
            }
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.orderByDesc("create_time");
            List<Product> list = productService.list(queryWrapper);


            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName =   "物料信息_" + format.format(new Date()) + ".xlsx";



            String[] columnHeaders = {"SAP物料编号", "图号", "产品名称", "物料日期", "类型", "制造类型", "物料描述", "材质", "单重","单位"};

            String[] fieldNames = {"materialNo","drawingNo","productName","materialDate","materialType","objectType","materialDesc","texture", "weight","unit"};

            //export
            ExcelUtils.exportExcel(fileName, list , columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}