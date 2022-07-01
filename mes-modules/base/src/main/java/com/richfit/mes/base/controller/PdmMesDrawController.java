package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmMesDrawService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmMesDraw;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Slf4j
@Api("图纸")
@RestController
@RequestMapping("/api/base/mes/pdmDraw")
public class PdmMesDrawController {

    @Autowired
    private PdmMesDrawService pdmMesDrawService;

    @PostMapping(value = "/query/list")
    @ApiOperation(value = "工艺图纸", notes = "工艺图纸查询")
    @ApiImplicitParam(name = "pdmDraw", value = "工序图纸VO", required = true, dataType = "PdmDraw", paramType = "body")
    public CommonResult<List<PdmMesDraw>> getList(PdmMesDraw pdmMesDraw) {
        QueryWrapper<PdmMesDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isop", '1');
        queryWrapper.and(wrapper -> wrapper.eq("op_id", pdmMesDraw.getOpId()).or().eq("op_id", pdmMesDraw.getItemId() + "@" + pdmMesDraw.getItemId() + "@" + pdmMesDraw.getDataGroup()));
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmMesDraw.getDataGroup());
        List<PdmMesDraw> list = pdmMesDrawService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工艺图纸分页查询", notes = "工艺图纸分页查询")
    @ApiImplicitParam(name = "pdmDraw", value = "工序VO", required = true, dataType = "pdmDraw", paramType = "body")
    public CommonResult<IPage<PdmMesDraw>> getPageList(int page, int limit, PdmMesDraw pdmMesDraw) {
        QueryWrapper<PdmMesDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isop", '1');
        queryWrapper.eq("op_id", pdmMesDraw.getOpId());
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmMesDraw.getItemId()), "item_id", pdmMesDraw.getItemId());
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmMesDraw.getDataGroup());
        return CommonResult.success(pdmMesDrawService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "工艺图纸列表查询", notes = "工艺图纸列表查询")
    @GetMapping("/query/drawList/{itemId}/{dataGroup}")
    public List<PdmMesDraw> queryDraw(@PathVariable String itemId, @PathVariable String dataGroup) {
        return pdmMesDrawService.queryDraw(itemId, dataGroup);
    }
}
