package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmObjectService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工装分页查询", notes = "工装分页查询")
    @ApiImplicitParam(name = "pdmObject", value = "工装VO", required = true, dataType = "pdmObject", paramType = "body")
    public CommonResult<IPage<PdmObject>> getPageList(int page, int limit, PdmObject pdmObject){
        QueryWrapper<PdmObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmObject.getOpId()),"op_id",pdmObject.getOpId());
        queryWrapper.orderByDesc("rev").eq("dataGroup",pdmObject.getDataGroup());
        return CommonResult.success(pdmObjectService.page(new Page<>(page, limit), queryWrapper));
    }




}
