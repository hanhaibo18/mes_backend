package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.service.PdmDrawService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmDraw;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public CommonResult<List<PdmDraw>> getList(PdmDraw pdmDraw) {
        QueryWrapper<PdmDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isop", '1');
        queryWrapper.and(wrapper -> wrapper.eq("op_id", pdmDraw.getOpId()).or().eq("op_id", pdmDraw.getItemId() + "@" + pdmDraw.getItemId() + "@" + pdmDraw.getDataGroup()));
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmDraw.getDataGroup());
        List<PdmDraw> list = pdmDrawService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工艺图纸分页查询", notes = "工艺图纸分页查询")
    @ApiImplicitParam(name = "pdmDraw", value = "工序VO", required = true, dataType = "pdmDraw", paramType = "body")
    public CommonResult<IPage<PdmDraw>> getPageList(int page, int limit, PdmDraw pdmDraw) {
        QueryWrapper<PdmDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isop", '1');
        queryWrapper.and(wrapper -> wrapper.eq("op_id", pdmDraw.getOpId()).or().eq("op_id", pdmDraw.getItemId() + "@" + pdmDraw.getItemId() + "@" + pdmDraw.getDataGroup()));
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmDraw.getDataGroup());
        return CommonResult.success(pdmDrawService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "工艺图纸列表查询", notes = "工艺图纸列表查询")
    @GetMapping("/query/drawList/{itemId}/{dataGroup}")
    public List<PdmDraw> queryDraw(@PathVariable String itemId, @PathVariable String dataGroup) {
        return pdmDrawService.queryDraw(itemId, dataGroup);
    }

    @ApiOperation(value = "根据图号查询列表", notes = "根据图号查询列表")
    @ApiImplicitParam(name = "itemId", value = "图号", required = true, dataType = "String", paramType = "query")
    @GetMapping("/query/queryDrawList")
    public List<PdmDraw> queryDrawList(String itemId) {
        return pdmDrawService.queryDrawList(itemId);
    }
}
