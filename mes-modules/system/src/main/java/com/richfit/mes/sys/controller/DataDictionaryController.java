package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.sys.DataDictionary;
import com.richfit.mes.common.model.sys.DataDictionaryParam;
import com.richfit.mes.sys.service.DataDictionaryParamService;
import com.richfit.mes.sys.service.DataDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 数据字典表(SysDataDictionary)表控制层
 *
 * @author makejava
 * @since 2023-04-03 15:18:29
 */
@Slf4j
@Api(value = "数据字典", tags = {"数据字典"})
@RestController
@RequestMapping("/api/sys/data_dictionary")
public class DataDictionaryController extends BaseController {
    /**
     * 服务对象
     */
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private DataDictionaryParamService dataDictionaryParamService;


    @ApiOperation(value = "分页查询车间列表")
    @GetMapping("/page")
    public CommonResult<IPage<DataDictionary>> page(@ApiParam(value = "车间名称") String branchName, @ApiParam(value = "车间编码") String branchCode, int page, int limit) {
        QueryWrapper<DataDictionary> queryWrapper = new QueryWrapper<>();
        if (null != branchName) {
            queryWrapper.eq("branch_name", branchName);
        }
        if (null != branchCode) {
            queryWrapper.eq("branch_code", branchCode);
        }
        return CommonResult.success(dataDictionaryService.page(new Page<DataDictionary>(page, limit), queryWrapper));
    }


    @ApiOperation(value = "新增车间")
    @PostMapping("/add")
    public CommonResult<Boolean> add(@ApiParam(value = "数据字典") @RequestBody DataDictionary dataDictionary) {
        if (dataDictionary.getBranchCode() == null || dataDictionary.getBranchName() == null) {
            return CommonResult.failed("请校验填写信息正确！");
        }
        QueryWrapper<DataDictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_code", dataDictionary.getBranchCode());
        List<DataDictionary> dataDictionaries = dataDictionaryService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(dataDictionaries)) {
            return CommonResult.failed("该车间已存在！");
        }
        return CommonResult.success(dataDictionaryService.save(dataDictionary));
    }

    @ApiOperation(value = "修改车间")
    @PutMapping("/update")
    public CommonResult<Boolean> update(@ApiParam(value = "数据字典") @RequestBody DataDictionary dataDictionary) {
        if (dataDictionary.getBranchCode() == null || dataDictionary.getBranchName() == null) {
            return CommonResult.failed("请校验填写信息正确！");
        }
        return CommonResult.success(dataDictionaryService.updateById(dataDictionary));
    }

    @ApiOperation(value = "删除车间")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@ApiParam(value = "字典id") String id) {
        return CommonResult.success(dataDictionaryService.removeById(id));
    }

    @ApiOperation(value = "分页查询车间物料列表")
    @GetMapping("/param/page/{dictionary_id}")
    public CommonResult<IPage<DataDictionaryParam>> page(@PathVariable("dictionary_id") String id, @ApiParam(value = "物料编码") String materialNo, @ApiParam(value = "物料名称") String materialName, @ApiParam(value = "材质") String texture, @ApiParam(value = "物料规格") String specification, int page, int limit) {
        QueryWrapper<DataDictionaryParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictionary_id", id);
        if (null != materialNo) {
            queryWrapper.eq("material_no", materialNo);
        }
        if (null != materialName) {
            queryWrapper.eq("material_name", materialName);
        }
        if (null != texture) {
            queryWrapper.eq("texture", texture);
        }
        if (null != specification) {
            queryWrapper.eq("specifications", specification);
        }
        queryWrapper.orderByAsc("order_num");
        return CommonResult.success(dataDictionaryParamService.page(new Page<DataDictionaryParam>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "新增车间物料")
    @PostMapping("/param/add")
    public CommonResult<Boolean> add(@ApiParam(value = "物料参数") @RequestBody DataDictionaryParam dataDictionaryParam) {
        QueryWrapper<DataDictionaryParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_no", dataDictionaryParam.getMaterialNo());
        if (!dataDictionaryParamService.list(queryWrapper).isEmpty()) {
            return CommonResult.failed("物料编码已存在！");
        }
        return CommonResult.success(dataDictionaryParamService.save(dataDictionaryParam));
    }

    @ApiOperation(value = "修改车间物料")
    @PutMapping("/param/update")
    public CommonResult<Boolean> update(@ApiParam(value = "物料参数") @RequestBody DataDictionaryParam dataDictionaryParam) {
        if (dataDictionaryParam.getBranchCode() == null || dataDictionaryParam.getDictionaryId() == null
                || dataDictionaryParam.getMaterialName() == null || dataDictionaryParam.getMaterialNo() == null
                || dataDictionaryParam.getTexture() == null || dataDictionaryParam.getSpecifications() == null) {
            return CommonResult.failed("请校验填写信息正确！");
        }
        return CommonResult.success(dataDictionaryParamService.updateById(dataDictionaryParam));
    }

    @ApiOperation(value = "删除车间物料")
    @DeleteMapping("/param/delete")
    public CommonResult<Boolean> deleteParam(@ApiParam(value = "物料参数id") String id) {
        return CommonResult.success(dataDictionaryParamService.removeById(id));
    }

    @ApiOperation(value = "excel导入物料")
    @PostMapping("/import_excel")
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file, @RequestParam("dictionaryId") String id) {
        return CommonResult.success(dataDictionaryParamService.improtExcel(file, id));
    }

}

