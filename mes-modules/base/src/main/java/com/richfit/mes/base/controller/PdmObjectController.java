package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmObjectService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-12 13:27
 */
@Slf4j
@Api("工装")
@RestController
@RequestMapping("/api/base/pdmObject")
public class PdmObjectController {

    @Autowired
    private PdmObjectService pdmObjectService;

    @GetMapping("/query/list")
    @ApiOperation(value = "工装列表查询", notes = "工装列表查询")
    @ApiImplicitParam(name = "pdmObject", value = "工装VO", required = true, dataType = "pdmObject", paramType = "body")
    public CommonResult<List<PdmObject>> getList(PdmObject pdmObject) {
        QueryWrapper<PdmObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmObject.getOpId()), "op_id", pdmObject.getOpId());
        queryWrapper.orderByDesc("rev").eq("dataGroup", pdmObject.getDataGroup());
        return CommonResult.success(pdmObjectService.list(queryWrapper));
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工装分页查询", notes = "工装分页查询")
    @ApiImplicitParam(name = "pdmObject", value = "工装VO", required = true, dataType = "pdmObject", paramType = "body")
    public CommonResult<IPage<PdmObject>> getPageList(int page, int limit, PdmObject pdmObject) {
        QueryWrapper<PdmObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmObject.getOpId()), "op_id", pdmObject.getOpId());
        queryWrapper.orderByDesc("rev").eq("dataGroup", pdmObject.getDataGroup());
        return CommonResult.success(pdmObjectService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "根据图号查询工装列表", notes = "根据图号查询工装列表")
    @GetMapping("/query/pdmList/{drawing}/{dataGroup}")
    public List<PdmObject> queryIndustrialAssembly(@PathVariable String drawing, @PathVariable String dataGroup) {
        return pdmObjectService.queryIndustrialAssembly(drawing, dataGroup);
    }

    @GetMapping("/query/selectFixtureList")
    @ApiOperation(value = "根据工序id查询工装列表", notes = "根据工序Id查询工装列表")
    @ApiImplicitParam(name = "optId", value = "工序Id", dataType = "String", paramType = "query")
    public CommonResult<List<PdmObject>> selectFixtureList(String optId) {
        return CommonResult.success(pdmObjectService.selectFixtureList(optId));
    }

}
