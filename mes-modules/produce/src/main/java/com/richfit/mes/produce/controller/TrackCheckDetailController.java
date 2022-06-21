package com.richfit.mes.produce.controller;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.TrackCheckDetail;
import com.richfit.mes.produce.service.TrackCheckDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述: 工序质检明细
 *
 * @Author: zhiqiang.lu
 * @Date: 2022/6/21 10:25
 **/
@Slf4j
@Api(tags = "工序质检明细")
@RestController
@RequestMapping("/api/produce/trackcheckdetail")
public class TrackCheckDetailController extends BaseController {

    @Autowired
    public TrackCheckDetailService trackCheckDetailService;

    /**
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @ApiOperation(value = "查询工序质检项目", notes = "通过工序id查询当前工序质检项目")
    @GetMapping("/select/tiid")
    public CommonResult<List<TrackCheckDetail>> selectByTiId(@ApiParam(value = "工序id", required = true) @RequestParam String tiId) {
        return CommonResult.success(trackCheckDetailService.selectByTiId(tiId));
    }
}
