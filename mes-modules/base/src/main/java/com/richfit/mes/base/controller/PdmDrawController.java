package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmDrawService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmOption;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-12 13:27
 */
@Slf4j
@Api("图纸")
@RestController
@RequestMapping("/api/base/pdmDraw")
public class PdmDrawController {

    @Autowired
    private PdmDrawService pdmDrawService;

    @PostMapping(value = "/query/list")
    @ApiOperation(value = "工艺图纸", notes = "工艺图纸查询")
    @ApiImplicitParam(name = "pdmDraw", value = "工序图纸VO", required = true, dataType = "PdmDraw", paramType = "body")
    public CommonResult<List<PdmDraw>> getList(PdmDraw pdmDraw){
        QueryWrapper<PdmDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmDraw.getItemId()),"item_id",pdmDraw.getItemId());
        queryWrapper.orderByDesc("syc_time");
        List<PdmDraw> list = pdmDrawService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @PostMapping("/query/pageList")
    @ApiOperation(value = "工艺图纸分页查询", notes = "工艺图纸分页查询")
    @ApiImplicitParam(name = "pdmDraw", value = "工序VO", required = true, dataType = "pdmDraw", paramType = "body")
    public CommonResult<IPage<PdmDraw>> getPageList(int page, int limit,PdmDraw pdmDraw){
        QueryWrapper<PdmDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmDraw.getItemId()),"item_id",pdmDraw.getItemId());
        queryWrapper.orderByDesc("syc_time");
        return CommonResult.success(pdmDrawService.page(new Page<>(page, limit), queryWrapper));
    }




}
