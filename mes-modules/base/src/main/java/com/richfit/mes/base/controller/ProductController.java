package com.richfit.mes.base.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.enmus.MaterialTypeEnum;
import com.richfit.mes.base.provider.ProduceServiceClient;
import com.richfit.mes.base.service.ProductService;
import com.richfit.mes.base.service.ProductionBomService;
import com.richfit.mes.base.service.RouterService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 组织结构Controller
 */
@Slf4j
@Api(value = "物料管理接口", tags = {"物料管理接口"})
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
    @Resource
    private ProduceServiceClient produceServiceClient;
    @Autowired
    private ProductionBomService productionBomService;

    @Autowired
    private RouterService routerService;

    @ApiOperation(value = "根据excel导入物料")
    @PostMapping("/importMaterialByExcle")
    public CommonResult<String> importMaterialByExcle(@ApiParam(value = "excel文件") @RequestParam("file") MultipartFile file,
                                    @ApiParam(value = "tenantId") @RequestParam String tenantId,
                                    @ApiParam(value = "branchCode") @RequestParam String branchCode) {

        return CommonResult.success(productService.importMaterialByExcle(file,tenantId,branchCode));
    }


    @ApiOperation(value = "新增物料", notes = "新增物料信息")
    @ApiImplicitParam(name = "product", value = "物料", required = true, dataType = "Branch", paramType = "path")
    @PostMapping("/product")
    public CommonResult<Product> addProduct(@RequestBody Product product) {
        if (StringUtils.isNullOrEmpty(product.getMaterialNo())) {
            return CommonResult.failed(PRODUCT_MATERIAL_NO_NULL_MESSAGE);
        } else {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
            queryWrapper.eq("material_no", product.getMaterialNo());
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            Product oldProduct = productService.getOne(queryWrapper);
            if (oldProduct != null && !StringUtils.isNullOrEmpty(oldProduct.getId())) {
                return CommonResult.failed("物料编码已存在！");
            } else {
                product.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                product.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                boolean bool = productService.save(product);
                if (bool) {
                    return CommonResult.success(product, PRODUCT_SUCCESS_MESSAGE);
                } else {
                    return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
                }
            }

        }
    }

    @ApiOperation(value = "修改物料", notes = "修改物料信息")
    @ApiImplicitParam(name = "product", value = "物料", required = true, dataType = "Branch", paramType = "path")
    @PutMapping("/product")
    public CommonResult<Product> updateProduct(@RequestBody Product product, String oldMaterialNo) {

        if (StringUtils.isNullOrEmpty(product.getMaterialNo())) {
            return CommonResult.failed(PRODUCT_MATERIAL_NO_NULL_MESSAGE);
        } else {
            if (!product.getMaterialNo().equals(oldMaterialNo)) {
                QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
                queryWrapper.eq("material_no", product.getMaterialNo());
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                Product oldProduct = productService.getOne(queryWrapper);
                if (oldProduct != null && !StringUtils.isNullOrEmpty(oldProduct.getId())) {
                    return CommonResult.failed("物料编码已存在！");
                }
            }
            Product byId = productService.getById(product.getId());
            ArrayList<String> materialCodes = new ArrayList<>();
            materialCodes.add(byId.getMaterialNo());
            ArrayList<String> drawingNos = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(byId.getDrawingNo())) {
                drawingNos.add(byId.getDrawingNo());
            }
            List<TrackHead> trackHeads = produceServiceClient.getTrackHeadByMaterialCodeAndDrawingNo(materialCodes, drawingNos, SecurityUtils.getCurrentUser().getTenantId()).getData();
            Map<String, TrackHead> materialNoMap = trackHeads.stream().collect(Collectors.toMap(x -> x.getMaterialNo(), x -> x, (value1, value2) -> value2));
            Map<String, TrackHead> drawingNoMap = trackHeads.stream().collect(Collectors.toMap(x -> x.getDrawingNo(), x -> x, (value1, value2) -> value2));
            //判断物料号是否修改
            if (!byId.getMaterialNo().equals(product.getMaterialNo())) {
                //物料号有改动时检查物料号有没有订单
                CommonResult<List<Order>> listCommonResult = produceServiceClient.queryByMaterialCode(materialCodes, SecurityUtils.getCurrentUser().getTenantId());
                List<Order> orders = listCommonResult.getData();
                if (orders.size() > 0) {
                    throw new GlobalException("物料号 " + byId.getMaterialNo() + " 存在订单,不能修改该物料号", ResultCode.FAILED);
                }
                //物料号有改动时检查是否存在该物料的跟单
                if (!ObjectUtil.isEmpty(materialNoMap.get(byId.getMaterialNo()))) {
                    throw new GlobalException("物料 " + byId.getMaterialNo() + " 存在跟单,不能修改该物料编号", ResultCode.FAILED);
                }
            }

            if (byId.getDrawingNo() != null && !byId.getDrawingNo().equals(product.getDrawingNo())) {
                //物料号有变动时,检查有没有该物料图号的跟单
                if (!ObjectUtil.isEmpty(drawingNoMap.get(byId.getDrawingNo()))) {
                    throw new GlobalException("物料图号 " + byId.getDrawingNo() + " 存在跟单,不能修改该物料图号", ResultCode.FAILED);
                }
            }


            product.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            product.setModifyTime(new Date());
            boolean bool = productService.updateById(product);
            if (bool) {
                return CommonResult.success(product, PRODUCT_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除物料", notes = "根据物料ID删除物料")
    @ApiImplicitParam(name = "id", value = "物料ID", required = true, dataType = "List<String>", paramType = "path")
    @DeleteMapping("/product")
    public CommonResult<Product> deleteProductById(@RequestBody List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return CommonResult.failed(PRODUCT_ID_NULL_MESSAGE);
        } else {
            List<Product> productList = productService.listByIds(ids);
            List<String> materialCodes = productList.stream().map(x -> x.getMaterialNo()).collect(Collectors.toList());
            List<String> drawingNos = productList.stream().map(x -> x.getDrawingNo()).collect(Collectors.toList());

            //检查物料号有没有订单,
            CommonResult<List<Order>> listCommonResult = produceServiceClient.queryByMaterialCode(materialCodes, SecurityUtils.getCurrentUser().getTenantId());
            List<Order> orders = listCommonResult.getData();
            if (orders.size() > 0) {
                throw new GlobalException("物料 " + orders.get(0).getMaterialCode() + " 存在订单,不能删除该物料", ResultCode.FAILED);
            }
            //检查物料号或者图号有没有跟单,
            List<TrackHead> trackHeads = produceServiceClient.getTrackHeadByMaterialCodeAndDrawingNo(materialCodes, drawingNos, SecurityUtils.getCurrentUser().getTenantId()).getData();
            Map<String, TrackHead> materialNoMap = trackHeads.stream().collect(Collectors.toMap(x -> x.getMaterialNo(), x -> x, (value1, value2) -> value2));
            Map<String, TrackHead> drawingNoMap = trackHeads.stream().collect(Collectors.toMap(x -> x.getDrawingNo(), x -> x, (value1, value2) -> value2));
            for (String materialCode : materialCodes) {
                if (!ObjectUtil.isEmpty(materialNoMap.get(materialCode))) {
                    throw new GlobalException("物料 " + materialCode + " 存在跟单,不能删除该物料", ResultCode.FAILED);
                }
            }
            for (String drawingNo : drawingNos) {
                if (!ObjectUtil.isEmpty(drawingNoMap.get(drawingNo))) {
                    throw new GlobalException("物料图号 " + drawingNo + " 存在跟单,不能删除该物料", ResultCode.FAILED);
                }
            }
            boolean bool = productService.removeByIds(ids);
            if (bool) {
                return CommonResult.success(null, PRODUCT_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "分页查询物料", notes = "根据图号、物料编码等参数分页查询物料")
    @GetMapping("/product")
    public CommonResult<IPage<Product>> selectProduct(@ApiParam(value = "页码", required = true) @RequestParam(defaultValue = "1") int page,
                                                      @ApiParam(value = "条数", required = true) @RequestParam(defaultValue = "10") int limit,
                                                      @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                      @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo,
                                                      @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                                                      @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
                                                      @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol,
                                                      @ApiParam(value = "产品名称") @RequestParam(required = false) String productName,
                                                      @ApiParam(value = "反向查询物料类型") @RequestParam(required = false, defaultValue = "false") Boolean material_type_reverse) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "p.drawing_no", drawingNo);
            //queryWrapper.like("p.drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("p.material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            if (material_type_reverse) {
                queryWrapper.ne("p.material_type", materialType);
            } else {
                queryWrapper.eq("p.material_type", materialType);
            }

        }
        if (!StringUtils.isNullOrEmpty(productName)) {
            queryWrapper.like("p.product_name", productName);
        }
        // queryWrapper.eq("p.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if (order.equals("desc")) {
                    queryWrapper.orderByDesc("p." + StrUtil.toUnderlineCase(orderCol));
                } else if (order.equals("asc")) {
                    queryWrapper.orderByAsc("p." + StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc("p." + StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("p.modify_time");
        }
        //只查询当前租户下的物料数据
        queryWrapper.eq("p.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        IPage<Product> result = productService.selectProduct(new Page<Product>(page, limit), queryWrapper);
//        List<Product> data = result.getRecords();
//        result.setRecords(findBomAndRouterByProduct(data));
        return CommonResult.success(result, PRODUCT_SUCCESS_MESSAGE);
    }

    private List<Product> findBomAndRouterByProduct(List<Product> data) {
        List<Product> newData = new ArrayList<>();
        for (Product product : data) {
            Product newProduct = product;
            // 查询物料是否有对应的BOM
            QueryWrapper<ProductionBom> bomQuery = new QueryWrapper<>();
            // pb.drawing_no = p.drawing_no  and pb.is_current = '1' and pb.grade = 'H' and main_drawing_no  is NULL
            DrawingNoUtil.queryLike(bomQuery, "drawing_no", product.getDrawingNo());
            //bomQuery.eq("drawing_no", product.getDrawingNo());
            bomQuery.eq("is_current", "1");
            bomQuery.eq("grade", "H");
            bomQuery.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            bomQuery.isNull("main_drawing_no");
            // 查询物料是否有对应的工艺
            // p.drawing_no = r.router_no and p.tenant_id = r.tenant_id and r.status = 1
            QueryWrapper<Router> routerQuery = new QueryWrapper<>();
            routerQuery.eq("router_no", product.getDrawingNo());
            routerQuery.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            routerQuery.eq("is_active", "1");

            List<Router> routerList = routerService.list(routerQuery);
            newProduct.setHaveBom(productionBomService.count(bomQuery));
            if (routerList.size() > 0) {
                Router router = routerList.get(0);
                newProduct.setHaveRouter(1);
                if (!StringUtils.isNullOrEmpty(router.getType())) {
                    newProduct.setRouterType(Integer.parseInt(router.getType()));
                }
            }
            newData.add(newProduct);
        }
        return newData;
    }

    @ApiOperation(value = "查询物料", notes = "根据输入内容查询物料")
    @GetMapping("/product/searchList")
    public CommonResult<List<Product>> selectByMaterialNoOrDrawingNo(String inputKey, String materialType, Boolean isEqualType) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(inputKey)) {
/*

            inputKey = "%" + inputKey.replaceAll("-", "") + "%";
            queryWrapper.apply("(material_no like {0} or replace(drawing_no,'-','') like {0})", inputKey);
*/

            queryWrapper.apply("(material_no like {0} or " + DrawingNoUtil.queryLikeSql("drawing_no", inputKey) + ")", inputKey);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            if (isEqualType != null) {
                if (isEqualType) {
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

    @ApiOperation(value = "查询物料", notes = "根据输入内容查询物料")
    @GetMapping("/product/list")
    public CommonResult<List<Product>> selectProductList(String inputKey, String drawingNo, String materialType, Boolean isEqualType) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(inputKey)) {
            queryWrapper.and(wrapper -> wrapper.like("material_no", inputKey).or().like("product_name", inputKey));
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            if (isEqualType != null) {
                if (isEqualType) {
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

    @ApiOperation(value = "查询物料(分页)", notes = "根据输入内容查询物料(分页)")
    @GetMapping("/product/list/page")
    public CommonResult<List<Product>> selectProductListPage(int page, int limit, String inputKey, String drawingNo, String materialType, Boolean isEqualType) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(inputKey)) {
            queryWrapper.and(wrapper -> wrapper.like("material_no", inputKey).or().like("product_name", inputKey));
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            if (isEqualType != null) {
                if (isEqualType) {
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
        IPage<Product> result = productService.selectProduct(new Page<Product>(page, limit), queryWrapper);
        return CommonResult.success(result.getRecords(), PRODUCT_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询物料", notes = "根据输入内容查询物料")
    @GetMapping("/product/findList")
    public CommonResult<List<Product>> selectProductList2(String materialNo, String drawingNo, String materialType, Boolean isEqualType) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            if (isEqualType != null) {
                if (isEqualType) {
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


    @ApiOperation(value = "查询物料", notes = "根据物料号图号查询物料(订单同步校验用)")
    @GetMapping("/selectOrderProduct")
    public List<Product> selectOrderProduct(String materialNo, String drawingNo) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.eq("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
        }
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        queryWrapper.eq("material_type", "3");
        return productService.list(queryWrapper);
    }

    @ApiOperation(value = "查询物料", notes = "根据物料号图号查询物料")
    @GetMapping("/product/listByNo")
    public CommonResult<List<Product>> selectProduct(String materialNo, String drawingNo, String materialType) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.eq("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType) && materialType.equals("0")) {
            queryWrapper.ne("material_type", "3");
        } else if (!StringUtils.isNullOrEmpty(materialType) && materialType.equals("1")) {
            queryWrapper.eq("material_type", "3");
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryEq(queryWrapper, "drawing_no", drawingNo);
        }
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        queryWrapper.orderByDesc("create_time");
        return CommonResult.success(productService.list(queryWrapper), PRODUCT_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "导入物料", notes = "根据Excel文档导入物料")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        //封装证件信息实体类
        String[] fieldNames = {"materialNo", "materialDate", "materialType", "materialDesc", "drawingNo", "productName", "weight", "unit", "convertScale", "convertUnit"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<Product> list = ExcelUtils.importExcel(excelFile, Product.class, fieldNames, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);

            list = list.stream().filter(item -> item.getMaterialNo() != null).collect(Collectors.toList());
            list.forEach(item -> {
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
            });

            boolean bool = productService.saveBatch(list);
            if (bool) {
                return CommonResult.success(null, PRODUCT_IMPORT_EXCEL_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(PRODUCT_FAILED_MESSAGE);
            }
        } catch (Exception e) {
            return CommonResult.failed(PRODUCT_EXCEPTION_MESSAGE + e.getMessage());
        }
    }

    @ApiOperation(value = "导出物料信息", notes = "通过Excel文档导出物料信息")
    @GetMapping("/export_excel")
    public void exportExcel(String drawingNo, String materialNo, String materialType, HttpServletResponse rsp) {
        try {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(materialNo)) {
                queryWrapper.like("material_no", materialNo);
            }
            if (!StringUtils.isNullOrEmpty(materialType)) {
                queryWrapper.eq("material_type", materialType);
            }
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.orderByDesc("create_time");
            List<Product> list = productService.list(queryWrapper);

            for (Product product : list) {
                if (product.getObjectType() != null) {
                    switch (product.getObjectType()) {
                        case "0":
                            product.setObjectType("自制");
                            break;
                        case "1":
                            product.setObjectType("外购");
                            break;
                        case "2":
                            product.setObjectType("外协");
                            break;
                        default:
                            product.setObjectType("--");
                            break;
                    }

                }

                //物料类型编码转文字
                product.setMaterialType(MaterialTypeEnum.getName(product.getMaterialType()));

            }


            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "物料信息_" + format.format(new Date()) + ".xlsx";


            String[] columnHeaders = {"SAP物料编号", "图号", "产品名称", "物料日期", "类型", "制造类型", "物料描述", "材质", "单重", "单位"};

            String[] fieldNames = {"materialNo", "drawingNo", "productName", "materialDate", "materialType", "objectType", "materialDesc", "texture", "weight", "unit"};

            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @ApiOperation(value = "通过物料号码查询物流信息，用于分布式调用", notes = "通过物料号码查询物流信息，用于分布式调用")
    @GetMapping("/list_by_material_no")
    public List<Product> listByMaterialNo(@ApiParam(value = "物料号码", required = true) @RequestParam String materialNo) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.eq("material_no", materialNo);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("create_time");
        return productService.list(queryWrapper);
    }

    @ApiOperation(value = "查询物料材质", notes = "查询物料材质")
    @GetMapping("/getProductTexture")
    public CommonResult<List<String>> selectProductTexture() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        queryWrapper.select("texture");
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.groupBy("texture");
        List<Product> productList = productService.list(queryWrapper);
        List<String> textureList = productList.stream().filter(x -> x != null && !x.getTexture().equals("")).map(x -> x.getTexture()).collect(Collectors.toList());
        return CommonResult.success(textureList, PRODUCT_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "修复物料数据列表中的物料类型错乱问题", notes = "修复物料数据列表中的物料类型错乱问题")
    @GetMapping("/renovateData")
    public CommonResult renovateData() {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("0", " D");
        paramMap.put("1", " Z");
        paramMap.put("2", " JZ");
        paramMap.put("4", " X");
        paramMap.put("5", " MX");
        paramMap.put("6", " 半");

        QueryWrapper<Product> cpWrapper = new QueryWrapper<Product>();//构造成品修改条件
        cpWrapper.select("id");
        cpWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        cpWrapper.apply("(material_type !='3' or material_type is null)");

        Set<String> strings = paramMap.keySet();
        for (String s : strings) {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
            queryWrapper.select("id");
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.likeLeft("material_desc", paramMap.get(s));
            queryWrapper.apply("(material_type !='" + s + "' or material_type is null)");
            //拼接成品参数
            cpWrapper.apply("material_desc not like '%" + paramMap.get(s) + "'");
            //查出错乱数据的id
            List<Object> idList = productService.listObjs(queryWrapper);
            //大于一千条分页操作
            if (idList.size() > 1000) {
                int page = idList.size() / 1000;
                int oldPage = idList.size() / 1000;
                if (idList.size() % 1000 > 0) page += 1;//不能整除页数加1
                for (int i = 0; i < page; i++) {
                    int index = i == 0 ? i : 1000 * i;//起始下标
                    int toIndex = i == 0 ? 999 : 1000 * i + 999;//结尾下标
                    log.info(index + "  index-----------------  toindex" + toIndex);
                    List<Object> objects = null;
                    if (i < oldPage) {
                        objects = idList.subList(index, toIndex);
                    } else if (i == oldPage) {//尾页
                        objects = idList.subList(index, idList.size());
                    }
                    if (!CollectionUtils.isEmpty(objects)) {
                        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.set("material_type", s);
                        updateWrapper.in("id", objects);
                        productService.update(updateWrapper);//修改错乱数据
                    }
                }
            } else {
                //小于一千条直接修改
                if (!CollectionUtils.isEmpty(idList)) {
                    UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("material_type", s);
                    updateWrapper.in("id", idList);
                    productService.update(updateWrapper);//修改错乱数据
                }
            }
        }
        //查出成品错乱数据的id
        List<Object> idList = productService.listObjs(cpWrapper);
        if (idList.size() > 1000) {
            int page = idList.size() / 1000;
            int oldPage = idList.size() / 1000;
            if (idList.size() % 1000 > 0) page += 1;//不能整除页数加1
            for (int i = 0; i < page; i++) {
                int index = i == 0 ? i : 1000 * i;//起始下标
                int toIndex = i == 0 ? 999 : 1000 * i + 999;//结尾下标
                System.out.println(index + "  index-----------------  toindex" + toIndex);
                List<Object> objects = null;
                if (i < oldPage) {
                    objects = idList.subList(index, toIndex);
                } else if (i == oldPage) {//尾页
                    objects = idList.subList(index, idList.size());
                }
                if (!CollectionUtils.isEmpty(objects)) {
                    UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("material_type", 3);
                    updateWrapper.in("id", objects);
                    productService.update(updateWrapper);//修改成品错乱数据
                }
            }
        } else {
            //小于一千条直接修改
            if (!CollectionUtils.isEmpty(idList)) {
                UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("material_type", 3);
                updateWrapper.in("id", idList);
                productService.update(updateWrapper);//修改成品错乱数据
            }
        }
        return CommonResult.success("操作成功");
    }


    @ApiOperation(value = "根据产品名称模糊查询物料信息", notes = "根据产品名称模糊查询物料信息")
    @GetMapping("/list_by_product_name")
    public CommonResult<List<Product>> listByProductName(@ApiParam(value = "页码", required = true) @RequestParam(defaultValue = "1") int page,
                                           @ApiParam(value = "条数", required = true) @RequestParam(defaultValue = "10") int limit,
                                           @ApiParam(value = "产品名称", required = false) @RequestParam String productName) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>();
        if (!StringUtils.isNullOrEmpty(productName)) {
            queryWrapper.like("product_name", productName);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        IPage<Product> result=productService.page(new Page<Product>(page, limit),queryWrapper);
        return CommonResult.success(result.getRecords());
    }

}
