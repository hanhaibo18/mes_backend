package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmDrawService;
import com.richfit.mes.base.service.PdmOptionService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmOption;
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
 * @author rzw
 * @date 2022-01-12 13:27
 */
@Slf4j
@Api("工序")
@RestController
@RequestMapping("/api/base/pdmOption")
public class PdmOptionController {

    @Autowired
    private PdmOptionService pdmOptionService;

    @Autowired
    private PdmDrawService pdmDrawService;

    @GetMapping(value = "/query/list")
    @ApiOperation(value = "工序查询", notes = "工序查询")
    @ApiImplicitParam(name = "PdmOption", value = "工序VO", required = true, dataType = "PdmOption", paramType = "body")
    public CommonResult<List<PdmOption>> getList(PdmOption pdmOption) {
        QueryWrapper<PdmOption> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isNullOrEmpty(pdmOption.getOpNo()), "op_no", pdmOption.getOpNo())
                .like(!StringUtils.isNullOrEmpty(pdmOption.getName()), "name", pdmOption.getName())
                .eq(!StringUtils.isNullOrEmpty(pdmOption.getProcessId()), "process_id", pdmOption.getProcessId());
        queryWrapper.orderByAsc("op_no");
        List<PdmOption> list = pdmOptionService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工序分页查询", notes = "工序分页查询")
    @ApiImplicitParam(name = "PdmOption", value = "工序VO", required = true, dataType = "PdmOption", paramType = "body")
    public CommonResult<IPage<PdmOption>> getPageList(int page, int limit, PdmOption pdmOption) {
        QueryWrapper<PdmOption> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isNullOrEmpty(pdmOption.getOpNo()), "op_no", pdmOption.getOpNo())
                .like(!StringUtils.isNullOrEmpty(pdmOption.getName()), "name", pdmOption.getName())
                .eq(!StringUtils.isNullOrEmpty(pdmOption.getProcessId()), "process_id", pdmOption.getProcessId())
                .orderByAsc("op_no + 1")
                .eq("dataGroup", pdmOption.getDataGroup());
        return CommonResult.success(pdmOptionService.page(new Page<>(page, limit), queryWrapper));
    }

    @GetMapping("/queryOptionDraw/optionDrawPageList")
    @ApiOperation(value = "工序图纸分页查询", notes = "工序分页查询")
    @ApiImplicitParam(name = "pdmDraw", value = "图纸VO", required = true, dataType = "pdmDraw", paramType = "body")
    public CommonResult<IPage<PdmDraw>> optionDrawPageList(int page, int limit, PdmOption pdmOption) {
        QueryWrapper<PdmDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmOption.getId()), "op_id", pdmOption.getId());
        queryWrapper.orderByDesc("syc_time")
                .eq("dataGroup", pdmOption.getDataGroup());
        return CommonResult.success(pdmDrawService.page(new Page<>(page, limit), queryWrapper));
    }


}
