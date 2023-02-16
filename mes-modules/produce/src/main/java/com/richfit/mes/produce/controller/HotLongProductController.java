package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.HotLongProduct;
import com.richfit.mes.common.model.produce.HotLongProductQueryVo;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.HotLongProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 张盘石
 * @Description 长周期产品清单Controller
 */
@Slf4j
@Api(value = "长周期产品清单", tags = {"长周期产品清单"})
@RestController
@RequestMapping("/api/produce/hot_long_product")
public class HotLongProductController extends BaseController {

    @Autowired
    public HotLongProductService hotLongProductService;

    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败，请重试！";
    public static String productDrawingNoANDVERSION_ISNULL_MESSAGE = "操作失败，图号和版本号不能同时重复！";
    public static String TenantId_NULL_MESSAGE = "租戶ID不能为空！";

    @ApiOperation(value = "新增长周期产品清单", notes = "长周期产品清单")
    @PostMapping("/hot_long_product")
    public CommonResult addHotLongProduct(@RequestBody HotLongProduct hotLongProduct) {
        try {
            QueryWrapper<HotLongProduct> hotModelStoreQueryWrapper = new QueryWrapper<>();
            hotModelStoreQueryWrapper.eq("product_drawing_no", hotLongProduct.getProductDrawingNo());
            //hotModelStoreQueryWrapper.eq("version", hotLongProduct.getVersion());
            List<HotLongProduct> list = hotLongProductService.list(hotModelStoreQueryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                return CommonResult.failed(productDrawingNoANDVERSION_ISNULL_MESSAGE);
            }
            if (StringUtils.isEmpty(hotLongProduct.getTenantId())) {
                return CommonResult.failed(TenantId_NULL_MESSAGE);
            }
            hotLongProduct.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            hotLongProduct.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            hotLongProduct.setCreateTime(new Date());
            hotLongProduct.setModifyTime(new Date());
            hotLongProductService.save(hotLongProduct);
            return CommonResult.success(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return CommonResult.failed(FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "修改长周期产品清单", notes = "修改长周期产品清单")
    @PutMapping("/hot_long_product")
    public CommonResult updateHotLongProduct(@RequestBody HotLongProduct hotLongProduct) {
        try {
            QueryWrapper<HotLongProduct> hotModelStoreQueryWrapper = new QueryWrapper<>();
            hotModelStoreQueryWrapper.eq("product_drawing_no", hotLongProduct.getProductDrawingNo());
           // hotModelStoreQueryWrapper.eq("version", hotLongProduct.getVersion());
            hotModelStoreQueryWrapper.ne("id", hotLongProduct.getId());
            List<HotLongProduct> list = hotLongProductService.list(hotModelStoreQueryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                return CommonResult.failed(productDrawingNoANDVERSION_ISNULL_MESSAGE);
            }
            if (StringUtils.isEmpty(hotLongProduct.getTenantId())) {
                return CommonResult.failed(TenantId_NULL_MESSAGE);
            }
            hotLongProduct.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            hotLongProduct.setModifyTime(new Date());
            hotLongProductService.updateById(hotLongProduct);
            return CommonResult.success(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return CommonResult.success(FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "删除长周期产品清单", notes = "删除长周期产品清单")
    @DeleteMapping("/hot_long_product")
    public CommonResult deleteHotLongProduct(@RequestBody List<String> ids) {
        try {
            hotLongProductService.removeByIds(ids);
            return CommonResult.success(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return CommonResult.success(FAILED_MESSAGE);
        }
    }


    @ApiOperation(value = "查询长周期产品清单", notes = "查询长周期产品清单")
    @GetMapping("/hot_long_product")
    public CommonResult<IPage<HotLongProduct>> selectHotLongProduct(HotLongProductQueryVo hotLongProductQueryVo) {
        if (StringUtils.isEmpty(hotLongProductQueryVo.getTenantId())) {
            return CommonResult.failed(TenantId_NULL_MESSAGE);
        }
        QueryWrapper<HotLongProduct> queryWrapper = new QueryWrapper<HotLongProduct>();
        if (StringUtils.isNotEmpty(hotLongProductQueryVo.getProductName())) {
            queryWrapper.like("product_name", hotLongProductQueryVo.getProductName());
        }
        if (StringUtils.isNotEmpty(hotLongProductQueryVo.getProductDrawingNo())) {
            queryWrapper.like("product_drawing_no", hotLongProductQueryVo.getProductDrawingNo());
        }
        queryWrapper.eq("tenant_id", hotLongProductQueryVo.getTenantId());
        //排序工具
        OrderUtil.query(queryWrapper, hotLongProductQueryVo.getOrderCol(), hotLongProductQueryVo.getOrder());
        queryWrapper.orderByDesc("create_time");
        return CommonResult.success(hotLongProductService.page(new Page<HotLongProduct>(hotLongProductQueryVo.getPage(), hotLongProductQueryVo.getLimit()), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "导入长周期产品清单", notes = "根据Excel文档导入长周期产品清单")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file, String tenantId) {
        return hotLongProductService.importExcel(file, tenantId);
    }

    @ApiOperation(value = "导出长周期产品清单", notes = "通过Excel文档导出长周期产品清单")
    @GetMapping("/export_excel")
    public void exportExcel(HotLongProduct hotLongProduct, HttpServletResponse rsp) {
        try {
            QueryWrapper<HotLongProduct> queryWrapper = new QueryWrapper<HotLongProduct>();
            if (StringUtils.isNotEmpty(hotLongProduct.getProductName())) {
                queryWrapper.like("product_name", hotLongProduct.getProductName());
            }
            if (StringUtils.isNotEmpty(hotLongProduct.getProductDrawingNo())) {
                queryWrapper.like("product_drawing_no", hotLongProduct.getProductDrawingNo());
            }
            queryWrapper.eq("tenant_id", hotLongProduct.getTenantId());
            queryWrapper.orderByDesc("create_time");
            List<HotLongProduct> list = hotLongProductService.list(queryWrapper);

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "长周期产品清单_" + format.format(new Date()) + ".xlsx";


            String[] columnHeaders = {"产品名称", "产品图号", "版本号"};

            String[] fieldNames = {"productName", "productDrawingNo", "version"};

            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
