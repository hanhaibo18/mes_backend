package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.model.produce.HotModelStoreExportExcelVo;
import com.richfit.mes.common.model.produce.HotModelStoreQueryVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.HotModelStoreService;
import com.richfit.mes.produce.utils.DrawingNoUtil;
import com.richfit.mes.produce.utils.OrderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 张盘石
 * @Description 模型库Controller
 */
@Slf4j
@Api(value = "模型库", tags = {"模型库"})
@RestController
@RequestMapping("/api/produce/hot_model_store")
public class HotModelStoreController extends BaseController {

    @Autowired
    public HotModelStoreService hotModelStoreService;

    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败，请重试！";
    public static String ModelDrawingNo_ISNULL_MESSAGE = "操作失败，图号不能重复！";
    public static String TenantId_NULL_MESSAGE = "租戶ID不能为空！";

    @ApiOperation(value = "新增模型库", notes = "新增模型库")
    @PostMapping("/hot_model_store")
    public CommonResult addHotModelStore(@RequestBody HotModelStore hotModelStore) {
        try {
            QueryWrapper<HotModelStore> hotModelStoreQueryWrapper = new QueryWrapper<>();
            DrawingNoUtil.queryEq(hotModelStoreQueryWrapper,"model_drawing_no", hotModelStore.getModelDrawingNo());
            List<HotModelStore> list = hotModelStoreService.list(hotModelStoreQueryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                return CommonResult.failed(ModelDrawingNo_ISNULL_MESSAGE);
            }
            if (StringUtils.isEmpty(hotModelStore.getTenantId())) {
                return CommonResult.failed(TenantId_NULL_MESSAGE);
            }
            hotModelStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            hotModelStore.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            hotModelStore.setCreateTime(new Date());
            hotModelStore.setModifyTime(new Date());
            hotModelStoreService.save(hotModelStore);
            return CommonResult.success(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return CommonResult.failed(FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "修改模型库", notes = "修改模型库")
    @PutMapping("/hot_model_store")
    public CommonResult updateHotModelStore(@RequestBody HotModelStore hotModelStore) {
        try {
            QueryWrapper<HotModelStore> hotModelStoreQueryWrapper = new QueryWrapper<>();
            DrawingNoUtil.queryEq(hotModelStoreQueryWrapper,"model_drawing_no", hotModelStore.getModelDrawingNo());
            hotModelStoreQueryWrapper.ne("id", hotModelStore.getId());
            List<HotModelStore> list = hotModelStoreService.list(hotModelStoreQueryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                return CommonResult.failed(ModelDrawingNo_ISNULL_MESSAGE);
            }
            if (StringUtils.isEmpty(hotModelStore.getTenantId())) {
                return CommonResult.failed(TenantId_NULL_MESSAGE);
            }
            hotModelStore.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            hotModelStore.setModifyTime(new Date());
            hotModelStoreService.updateById(hotModelStore);
            return CommonResult.success(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return CommonResult.success(FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "删除模型库", notes = "删除模型库")
    @DeleteMapping("/hot_model_store")
    public CommonResult deleteHotModelStore(@RequestBody List<String> ids) {
        try {
            hotModelStoreService.removeByIds(ids);
            return CommonResult.success(SUCCESS_MESSAGE);
        } catch (Exception e) {
            return CommonResult.success(FAILED_MESSAGE);
        }
    }


    @ApiOperation(value = "查询模型库", notes = "查询模型库")
    @GetMapping("/hot_model_store")
    public CommonResult<IPage<HotModelStore>> selectHotModelStore(HotModelStoreQueryVo hotModelStorQueryVo) {
        if (StringUtils.isEmpty(hotModelStorQueryVo.getTenantId())) {
            return CommonResult.failed(TenantId_NULL_MESSAGE);
        }
        QueryWrapper<HotModelStore> queryWrapper = new QueryWrapper<HotModelStore>();
        if (StringUtils.isNotEmpty(hotModelStorQueryVo.getModelName())) {
            queryWrapper.like("model_name", hotModelStorQueryVo.getModelName());
        }
        if (StringUtils.isNotEmpty(hotModelStorQueryVo.getModelDrawingNo())) {
            DrawingNoUtil.queryLike(queryWrapper,"model_drawing_no", hotModelStorQueryVo.getModelDrawingNo());
        }
        if (hotModelStorQueryVo.getModelType() != null) {
            queryWrapper.eq("model_type", hotModelStorQueryVo.getModelType());
        }
        queryWrapper.eq("tenant_id", hotModelStorQueryVo.getTenantId());
        queryWrapper.orderByDesc("create_time");
        //排序工具
        OrderUtil.query(queryWrapper, hotModelStorQueryVo.getOrderCol(), hotModelStorQueryVo.getOrder());
        return CommonResult.success(hotModelStoreService.page(new Page<HotModelStore>(hotModelStorQueryVo.getPage(), hotModelStorQueryVo.getLimit()), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "导入模型库", notes = "根据Excel文档导入模型库")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file, String tenantId) {
        return hotModelStoreService.importExcel(file, tenantId);
    }

    @ApiOperation(value = "导出模型库信息", notes = "通过Excel文档导出模型信息")
    @GetMapping("/export_excel")
    public void exportExcel(HotModelStore hotModelStor, HttpServletResponse rsp) {
        try {
            QueryWrapper<HotModelStore> queryWrapper = new QueryWrapper<HotModelStore>();
            if (StringUtils.isNotEmpty(hotModelStor.getModelName())) {
                queryWrapper.like("model_name", hotModelStor.getModelName());
            }
            if (StringUtils.isNotEmpty(hotModelStor.getModelDrawingNo())) {
                DrawingNoUtil.queryLike(queryWrapper,"model_drawing_no", hotModelStor.getModelDrawingNo());
            }
            if (hotModelStor.getModelType() != null) {
                queryWrapper.eq("model_type", hotModelStor.getModelType());
            }
            queryWrapper.eq("tenant_id", hotModelStor.getTenantId());
            queryWrapper.orderByDesc("create_time");
            List<HotModelStore> list = hotModelStoreService.list(queryWrapper);
            List<HotModelStoreExportExcelVo> hotModelStores = new ArrayList<>();

            for (HotModelStore hotModel : list) {
                HotModelStoreExportExcelVo hotModelStoreExportExcelVo = new HotModelStoreExportExcelVo();
                BeanUtils.copyProperties(hotModel, hotModelStoreExportExcelVo);
                hotModelStoreExportExcelVo.setNormalNum(hotModel.getNormalNum() != null ? hotModel.getNormalNum() + "" : "");
                hotModelStoreExportExcelVo.setScrapNum(hotModel.getScrapNum() != null ? hotModel.getScrapNum() + "" : "");

                if (hotModel.getModelType() != null) {
                    switch (hotModel.getModelType()) {
                        case 0:
                            hotModelStoreExportExcelVo.setModelType("一次性");
                            break;
                        case 1:
                            hotModelStoreExportExcelVo.setModelType("重复性");
                            break;
                        default:
                            hotModelStoreExportExcelVo.setModelType("");
                            break;
                    }

                }
                hotModelStores.add(hotModelStoreExportExcelVo);


            }


            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "模型库信息_" + format.format(new Date()) + ".xlsx";


            String[] columnHeaders = {"模型名称", "模型类型", "模型数量(正常)", "模型图号", "货位号", "模型数量(报废)", "模型备注"};

            String[] fieldNames = {"modelName", "modelType", "normalNum", "modelDrawingNo", "locationNo", "scrapNum", "modelRemark"};

            //export
            ExcelUtils.exportExcel(fileName, hotModelStores, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
