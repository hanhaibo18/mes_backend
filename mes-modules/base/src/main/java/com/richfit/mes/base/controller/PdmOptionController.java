package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmOptionService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.DrawingApply;
import com.richfit.mes.common.model.base.PdmOption;
import com.richfit.mes.common.model.base.PdmProcess;
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
@Api("工序")
@RestController
@RequestMapping("/api/base/pdmOption")
public class PdmOptionController {

    @Autowired
    private PdmOptionService pdmOptionService;

    @PostMapping(value = "/query/list")
    @ApiOperation(value = "工序查询", notes = "工序查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工序VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<List<PdmOption>> getList(PdmOption pdmOption){
        QueryWrapper<PdmOption> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isNullOrEmpty(pdmOption.getOpNo())
                ,"op_no"
                ,pdmOption.getOpNo());
        queryWrapper.orderByAsc("op_no");
        List<PdmOption> list = pdmOptionService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @PostMapping("/query/pageList")
    @ApiOperation(value = "工序分页查询", notes = "工序分页查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工序VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<IPage<PdmOption>> getPageList(int page, int limit,PdmOption pdmOption){
        QueryWrapper<PdmOption> queryWrapper = new QueryWrapper<PdmOption>();
        queryWrapper.like(!StringUtils.isNullOrEmpty(pdmOption.getOpNo())
                ,"op_no"
                ,"%" + pdmOption.getOpNo() + "%");
        queryWrapper.orderByAsc("op_no");
        return CommonResult.success(pdmOptionService.page(new Page<PdmOption>(page, limit), queryWrapper));
    }


}
