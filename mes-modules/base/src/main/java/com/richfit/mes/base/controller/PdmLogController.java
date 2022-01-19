package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.PdmDrawService;
import com.richfit.mes.base.service.PdmLogService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmLog;
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
@Api("pdm日志")
@RestController
@RequestMapping("/api/base/pdmLog")
public class PdmLogController {

    @Autowired
    private PdmLogService pdmLogService;


    @GetMapping("/page/list")
    @ApiOperation(value = "pdm日志", notes = "pdm日志")
    public CommonResult<IPage<PdmLog>> getPageList(int page, int limit, String type,
                                                   String par,
                                                   String queryTimeStart,
                                                   String queryTimeEnd){
        return CommonResult.success(pdmLogService.queryPageList(page,limit,type,par,queryTimeStart,queryTimeEnd));
    }
}
