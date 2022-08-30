package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmMesDrawService;
import com.richfit.mes.base.service.PdmMesOptionService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmMesDraw;
import com.richfit.mes.common.model.base.PdmMesOption;
import com.richfit.mes.common.model.base.PdmMesProcess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Slf4j
@Api("工序")
@RestController
@RequestMapping("/api/base/mes/pdmOption")
public class PdmMesOptionController {

    @Autowired
    private PdmMesOptionService pdmMesOptionService;

    @Autowired
    private PdmMesDrawService pdmMesDrawService;

    @GetMapping(value = "/query/list")
    @ApiOperation(value = "工序查询", notes = "工序查询")
    @ApiImplicitParam(name = "PdmOption", value = "工序VO", required = true, dataType = "PdmOption", paramType = "body")
    public CommonResult<List<PdmMesOption>> getList(PdmMesOption pdmMesOption) {
        QueryWrapper<PdmMesOption> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isNullOrEmpty(pdmMesOption.getOpNo()), "op_no", pdmMesOption.getOpNo())
                .like(!StringUtils.isNullOrEmpty(pdmMesOption.getName()), "name", pdmMesOption.getName())
                .eq(!StringUtils.isNullOrEmpty(pdmMesOption.getProcessId()), "process_id", pdmMesOption.getProcessId())
                .orderByAsc("op_no + 1")
                .eq("dataGroup", pdmMesOption.getDataGroup());
        List<PdmMesOption> list = pdmMesOptionService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工序分页查询", notes = "工序分页查询")
    @ApiImplicitParam(name = "PdmOption", value = "工序VO", required = true, dataType = "PdmOption", paramType = "body")
    public CommonResult<IPage<PdmMesOption>> getPageList(int page, int limit, PdmMesOption pdmOption) {
        QueryWrapper<PdmMesOption> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isNullOrEmpty(pdmOption.getOpNo()), "op_no", pdmOption.getOpNo())
                .like(!StringUtils.isNullOrEmpty(pdmOption.getName()), "name", pdmOption.getName())
                .eq(!StringUtils.isNullOrEmpty(pdmOption.getProcessId()), "process_id", pdmOption.getProcessId())
                .orderByAsc("op_no + 1")
                .eq("dataGroup", pdmOption.getDataGroup());
        return CommonResult.success(pdmMesOptionService.page(new Page<>(page, limit), queryWrapper));
    }

    @GetMapping("/queryOptionDraw/optionDrawList")
    @ApiOperation(value = "工序图纸列表查询", notes = "工序图纸列表查询")
    @ApiImplicitParam(name = "pdmDraw", value = "图纸VO", required = true, dataType = "pdmDraw", paramType = "body")
    public CommonResult<List<PdmMesDraw>> optionDrawList(PdmMesOption pdmMesOption) {
        QueryWrapper<PdmMesDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmMesOption.getId()), "op_id", pdmMesOption.getId());
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmMesOption.getDataGroup());
        return CommonResult.success(pdmMesDrawService.list(queryWrapper));
    }

    @GetMapping("/queryOptionDraw/optionDrawPageList")
    @ApiOperation(value = "工序图纸分页查询", notes = "工序分页查询")
    @ApiImplicitParam(name = "pdmDraw", value = "图纸VO", required = true, dataType = "pdmDraw", paramType = "body")
    public CommonResult<IPage<PdmMesDraw>> optionDrawPageList(int page, int limit, PdmMesProcess pdmMesOption) {
        QueryWrapper<PdmMesDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmMesOption.getId()), "op_id", pdmMesOption.getId());
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmMesOption.getDataGroup());
        return CommonResult.success(pdmMesDrawService.page(new Page<>(page, limit), queryWrapper));
    }

    @GetMapping("/queryOptionDraw/queryOptionDraw")
    @ApiOperation(value = "工序图纸分页查询", notes = "工序分页查询")
    @ApiImplicitParam(name = "id", value = "工序Id", dataType = "String", paramType = "query")
    public CommonResult<PdmMesOption> queryOptionDraw(String id) {
        return CommonResult.success(pdmMesOptionService.getById(id));
    }
}
