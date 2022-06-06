package com.richfit.mes.produce.controller.store;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.LineStoreService;
import com.richfit.mes.produce.service.OrderService;
import com.richfit.mes.produce.service.PurchaseOrderService;
import com.richfit.mes.produce.service.TrackHeadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

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
 * @Description 库存Controller
 */
@Slf4j
@Api(value = "库存管理", tags = {"库存管理接口"})
@RestController
@RequestMapping("/api/produce/line_store")
public class LineStoreController extends BaseController {

    private final static String WORKBLANK_NULL_MESSAGE = "编号不能为空!";
    private final static String CODE_EXITS = "编号已存在！";
    private final static String MATERIAL_CODE_NULL_MSG = "物料编号不能为空！";
    private final static String DRAWING_NO_NULL_MSG = "图号不能为空！";

    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败！";

    @Autowired
    private LineStoreService lineStoreService;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @ApiOperation(value = "入库", notes = "毛坯或半成品/成品入库")
    @PostMapping("/line_store")
    public CommonResult<LineStore> addLineStore(@ApiParam(value = "料单详情") @RequestBody LineStore lineStore,
                                                @ApiParam(value = "启始序列号") @RequestParam(required = false) Integer startNo,
                                                @ApiParam(value = "终止序列号") @RequestParam(required = false) Integer endNo,
                                                @ApiParam(value = "前缀字段") @RequestParam(required = false) String suffixNo,
                                                @ApiParam(value = "自动匹配生产订单") @RequestParam Boolean isAutoMatchProd,
                                                @ApiParam(value = "自动匹配采购订单") @RequestParam Boolean isAutoMatchPur,
                                                @ApiParam(value = "所选分公司") @RequestParam String branchCode) throws Exception {
        if (StringUtils.isNullOrEmpty(lineStore.getWorkblankNo())) {
            return CommonResult.failed(WORKBLANK_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(lineStore.getMaterialNo())) {
            return CommonResult.failed(MATERIAL_CODE_NULL_MSG);
        } else if (StringUtils.isNullOrEmpty(lineStore.getDrawingNo())) {
            return CommonResult.failed(DRAWING_NO_NULL_MSG);

            //校验编号是否已存在，如存在，返回报错信息
        } else if (lineStoreService.checkCodeExist(lineStore, startNo, endNo, suffixNo)) {
            String message = lineStore.getMaterialType().equals(0) ? "毛坯" : "零（部）件";
            return CommonResult.failed(message + CODE_EXITS);
        } else {
            boolean bool = lineStoreService.addStore(lineStore, startNo, endNo, suffixNo, isAutoMatchProd, isAutoMatchPur, branchCode);
            if (bool) {
                return CommonResult.success(lineStore, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }

    }

    @ApiOperation(value = "修改入库信息", notes = "修改入库信息")
    @PutMapping("/line_store")
    public CommonResult<LineStore> updateLineStore(@ApiParam(value = "料单详情") @RequestBody LineStore lineStore) {
        if (StringUtils.isNullOrEmpty(lineStore.getWorkblankNo())) {
            return CommonResult.failed(WORKBLANK_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(lineStore.getDrawingNo())) {
            return CommonResult.failed(DRAWING_NO_NULL_MSG);
        } else {
            boolean bool = false;
            lineStore.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            lineStore.setModifyTime(new Date());
            bool = lineStoreService.updateById(lineStore);

            if (bool) {
                return CommonResult.success(lineStore, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除入库信息", notes = "删除入库信息")
    @DeleteMapping("/line_store")
    public CommonResult deleteLineStore(@ApiParam(value = "料单Id数组") @RequestBody List<String> ids) {
        boolean bool = lineStoreService.removeByIds(ids);
        if (bool) {
            return CommonResult.success(true, SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "分页查询入库信息", notes = "根据图号、合格证号、物料编号分页查询入库信息")
    @GetMapping("/line_store")
    public CommonResult<IPage<LineStore>> selectLineStore(@ApiParam(value = "料单Id") @RequestParam(required = false) String id,
                                                          @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo,
                                                          @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                                                          @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                          @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                          @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                          @ApiParam(value = "料单状态") @RequestParam(required = false) String status,
                                                          @ApiParam(value = "跟踪方式") @RequestParam(required = false) String trackType,
                                                          @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
                                                          @ApiParam(value = "排序字段") @RequestParam(required = false) String orderCol,
                                                          @ApiParam(value = "页码") @RequestParam int page,
                                                          @ApiParam(value = "每页记录数") @RequestParam int limit,
                                                          @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.eq("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.like("workblank_no", workblankNo);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if (order.equals("desc")) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if (order.equals("asc")) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("modify_time");
        }
        return CommonResult.success(lineStoreService.page(new Page<LineStore>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询入库总览", notes = "根据物料号查询入库总览")
    @GetMapping("/line_store/group")
    public CommonResult<IPage<LineStoreSum>> selectLineStoreGroup(@ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                                  @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo,
                                                                  @ApiParam(value = "页码") @RequestParam int page,
                                                                  @ApiParam(value = "每页数") @RequestParam int limit,
                                                                  @ApiParam(value = "当前分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
//        if (!StringUtils.isNullOrEmpty(materialType)) {
//            queryWrapper.eq("material_type", materialType);
//        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);

        queryWrapper.orderByAsc("drawing_no");
        return CommonResult.success(lineStoreService.selectGroup(new Page<LineStore>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询入库信息", notes = "根据图号、合格证号、物料编号查询入库信息")
    @GetMapping("/line_store/list")
    public CommonResult<List<LineStore>> selectLineStoreList(@ApiParam(value = "料单Id") @RequestParam(required = false) String id,
                                                             @ApiParam(value = "料单类型") @RequestParam(required = false) String materialType,
                                                             @ApiParam(value = "物料码") @RequestParam(required = false) String materialNo,
                                                             @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                             @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                             @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                             @ApiParam(value = "跟踪方式") @RequestParam(required = false) String trackType,
                                                             @ApiParam(value = "已用数量") @RequestParam(required = false) Integer userNum,
                                                             @ApiParam(value = "料单状态") @RequestParam(required = false) String status,
                                                             @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.like("workblank_no", workblankNo);
        }
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (userNum != null && userNum > 0) {
            queryWrapper.ge("number - use_num", userNum);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.orderByDesc("create_time");
        return CommonResult.success(lineStoreService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "物料完工", notes = "物料完工")
    @GetMapping("/line_store/finish")
    public CommonResult<Boolean> finishProduct(@ApiParam(value = "跟单号") @RequestParam String trackNo,
                                               @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        queryWrapper.eq("track_no", trackNo);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        TrackHead trackHead = trackHeadService.getOne(queryWrapper);
        boolean bool = lineStoreService.changeStatus(trackHead);
        if (bool) {
            return CommonResult.success(bool, SUCCESS_MESSAGE);
        }
        return CommonResult.failed(FAILED_MESSAGE);
    }

    @ApiOperation(value = "修改状态", notes = "修改产品状态")
    @GetMapping("/line_store/change_status")
    public CommonResult<Boolean> changeStatus(@ApiParam(value = "毛坯号") @RequestParam String workblankNo,
                                              @ApiParam(value = "状态") @RequestParam String status,
                                              @ApiParam(value = "分公司") @RequestParam String branchCode) {
        UpdateWrapper<LineStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", status);
        updateWrapper.eq("workblank_no", workblankNo);
        updateWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        updateWrapper.eq("branch_code", branchCode);
        boolean bool = lineStoreService.update(updateWrapper);
        if (bool) {
            return CommonResult.success(bool, SUCCESS_MESSAGE);
        }
        return CommonResult.failed(FAILED_MESSAGE);
    }

    @ApiOperation(value = "查询产品使用的毛坯信息", notes = "根据图号查询产品使用的毛坯信息")
    @GetMapping("/line_store/workblank")
    public CommonResult<LineStore> selectWorkblankByTrackNo(@ApiParam(value = "跟单号") @RequestParam String trackNo,
                                                            @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        queryWrapper.eq("track_no", trackNo);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        TrackHead trackHead = trackHeadService.getOne(queryWrapper);
        LineStore lineStore = new LineStore();
        if (trackHead != null && !StringUtils.isNullOrEmpty(trackHead.getUserProductNo())) {
            QueryWrapper<LineStore> wrapper = new QueryWrapper<LineStore>();
            wrapper.eq("workblank_no", trackHead.getUserProductNo());
            wrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            lineStore = lineStoreService.getOne(wrapper);
        }
        return CommonResult.success(lineStore, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "分页查询产品装配信息", notes = "根据跟踪类型，产品编号分页查询产品装配信息")
    @GetMapping("/line_store/product")
    public CommonResult<IPage<LineStore>> selectLineStoreByProduct(@ApiParam(value = "跟踪类型") @RequestParam(required = false) String trackType,
                                                                   @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                                                                   @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                                   @ApiParam(value = "状态") @RequestParam(required = false) String status,
                                                                   @ApiParam(value = "页码") @RequestParam int page,
                                                                   @ApiParam(value = "条数") @RequestParam int limit,
                                                                   @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("ls.track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.like("ls.workblank_no", workblankNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("ls.material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("ls.status", status);
        }
        queryWrapper.eq("ls.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("ls.branch_code", branchCode);
        return CommonResult.success(lineStoreService.selectLineStoreByProduce(new Page<LineStore>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }


    @ApiOperation(value = "导入料单", notes = "根据Excel文档导入物料")
    @PostMapping("/import_excel")
    public CommonResult importExcel(@ApiIgnore HttpServletRequest request,
                                    @ApiParam(value = "文件") @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        //封装证件信息实体类
        String[] fieldNames = {"workblankNo", "materialNo", "materialName", "productName", "materialDesc", "materialType", "drawingNo", "texture", "weight", "trackType", "number", "certificateNo", "workNo", "materialSource", "batchNo", "testBarType", "testBarNumber", "productionOrder", "purchaseOrder", "contractNo", "replaceMaterial", "beforehandAssigned", "prevTrackNum"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<LineStore> list = ExcelUtils.importExcel(excelFile, LineStore.class, fieldNames, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);

            list = list.stream().filter(item -> item.getMaterialNo() != null).collect(Collectors.toList());
            QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
            List<LineStore> lineStores = new ArrayList<>();
            list.forEach(item -> {
                if (item.getMaterialType().equals("毛坯")) {
                    item.setMaterialType("0");
                } else if (item.getMaterialType().equals("半成品/成品")) {
                    item.setMaterialType("1");
                }
                if (item.getTrackType().equals("单件")) {
                    item.setTrackType("0");
                    item.setNumber(1);
                } else if (item.getTrackType().equals("批次")) {
                    item.setTrackType("1");
                    if (item.getNumber() <= 0) {
                        item.setNumber(1);
                    }
                }

                item.setUseNum(0);
                item.setStatus("1");
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                item.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
                item.setInTime(new Date());

                queryWrapper.eq("workblank_no", item.getWorkblankNo());
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

                List<LineStore> l = lineStoreService.list(queryWrapper);
                if (l == null || l.size() == 0) {
                    CommonResult<List<Product>> commonResult = baseServiceClient.selectProduct(item.getMaterialNo(), item.getDrawingNo(), item.getMaterialType());
                    List<Product> pList = commonResult.getData();
                    if (pList != null && pList.size() > 0) {
                        lineStores.add(item);
                    }
                }
            });

            boolean bool = lineStoreService.saveBatch(lineStores);
            if (bool) {
                return CommonResult.success(null, "导入成功!");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        } catch (Exception e) {
            return CommonResult.failed("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "导出库存信息", notes = "通过Excel文档导出库存信息")
    @GetMapping("/export_excel")
    public void exportExcel(@ApiParam(value = "物料号") @RequestParam(required = false) String materialNo, @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                            @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo, @ApiParam(value = "合格证编号") @RequestParam(required = false) String certificateNo,
                            @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo, @ApiParam(value = "状态") @RequestParam(required = false) String status,
                            @ApiParam(value = "分公司") @RequestParam String branchCode, @ApiIgnore HttpServletResponse rsp) {
        try {
            QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
            if (!StringUtils.isNullOrEmpty(materialNo)) {
                queryWrapper.eq("material_no", materialNo);
            }
            if (!StringUtils.isNullOrEmpty(materialType)) {
                queryWrapper.eq("material_type", materialType);
            }
            if (!StringUtils.isNullOrEmpty(status)) {
                queryWrapper.eq("status", status);
            }
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                queryWrapper.like("drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(certificateNo)) {
                queryWrapper.like("certificate_no", certificateNo);
            }
            if (!StringUtils.isNullOrEmpty(workblankNo)) {
                queryWrapper.like("workblank_no", workblankNo);
            }


            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.eq("branch_code", branchCode);
            queryWrapper.orderByDesc("create_time");
            List<LineStore> list = lineStoreService.list(queryWrapper);
            for (LineStore lineStore : list) {
                if ("0".equals(lineStore.getMaterialType()) && lineStore.getMaterialType() != null) {
                    lineStore.setMaterialType("毛坯");
                } else if ("1".equals(lineStore.getMaterialType()) && lineStore.getMaterialType() != null) {
                    lineStore.setMaterialType("半成品/成品");
                }
                if ("0".equals(lineStore.getTrackType()) && lineStore.getTrackType() != null) {
                    lineStore.setTrackType("单件");
                } else if ("1".equals(lineStore.getTrackType()) && lineStore.getTrackType() != null) {
                    lineStore.setTrackType("批次");
                }
                if ("0".equals(lineStore.getTestBarType()) && lineStore.getTestBarType() != null) {
                    lineStore.setTestBarType("普通试棒");
                } else if ("1".equals(lineStore.getTestBarType()) && lineStore.getTestBarType() != null) {
                    lineStore.setTestBarType("基尔试棒");
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "物料信息_" + format.format(new Date()) + ".xlsx";


            String[] columnHeaders = {"编号", "物料号", "物料名称", "产品名称", "物料描述", "物料类型", "图号", "材质", "重量", "跟踪类型", "数量", "合格证编号", "工作号", "物料来源", "炉号", "试棒类型", "试棒数量", "生产订单编号", "采购订单编号", "合同编号", "代用材料", "预先派工", "上工序跟单号"};

            String[] fieldNames = {"workblankNo", "materialNo", "materialName", "productName", "materialDesc", "materialType", "drawingNo", "texture", "weight", "trackType", "number", "certificateNo", "workNo", "materialSource", "batchNo", "testBarType", "testBarNumber", "productionOrder", "purchaseOrder", "contractNo", "replaceMaterial", "beforehandAssigned", "prevTrackNum"};

            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
