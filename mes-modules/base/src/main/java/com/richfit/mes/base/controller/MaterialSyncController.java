package com.richfit.mes.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.base.entity.MaterialSyncDto;
import com.richfit.mes.base.service.MaterialSyncService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: MaterialSyncController.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月10日 17:10:00
 */
@Slf4j
@Api("物料同步")
@RestController
@RequestMapping("/api/base/material")
public class MaterialSyncController {

    @Resource
    private MaterialSyncService materialSyncService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 功能描述: 查询物料同步信息
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/2/10 17:20
     * @return: List<Product>
     **/
    @ApiOperation(value = "查询物料同步信息", notes = "根据查询条件返回物料信息")
    @GetMapping("/query/synchronization_page")
    public CommonResult<List<Product>> queryProductSync(BasePageDto<String> queryDto) {
        MaterialSyncDto materialSyncDto = new MaterialSyncDto();
        try {
            materialSyncDto = objectMapper.readValue(queryDto.getParam(), MaterialSyncDto.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return CommonResult.success(materialSyncService.queryProductSync(materialSyncDto));
    }

    /**
     * 功能描述: 同步物料信息
     *
     * @param productList
     * @Author: xinYu.hou
     * @Date: 2022/2/10 17:22
     * @return: CommonResult<Boolean>
     **/
    @ApiOperation(value = "保存物料", notes = "保存物料信息")
    @PostMapping("/synchronization_save")
    public CommonResult<Boolean> saveProductSync(@RequestBody List<Product> productList) {
        return materialSyncService.saveProductSync(productList);
    }

    @ApiOperation(value = "导出物料信息", notes = "通过Excel文档导出订单信息")
    @GetMapping("/export_excel")
    public void exportExcel(BasePageDto<String> queryDto, HttpServletResponse rsp) {
        MaterialSyncDto materialSyncDto = new MaterialSyncDto();
        try {
            materialSyncDto = objectMapper.readValue(queryDto.getParam(), MaterialSyncDto.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        List<Product> list = materialSyncService.queryProductSync(materialSyncDto);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = "物料同步_" + format.format(new Date()) + ".xlsx";

        String[] columnHeaders = {"物料编号", "物料描述", "图号", "工厂编码", "单位"};
        String[] fieldNames = {"materialNo", "materialDesc", "drawingNo", "branchCode", "unit",};
        //export
        try {
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
